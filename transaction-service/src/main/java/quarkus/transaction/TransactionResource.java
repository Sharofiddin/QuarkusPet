package quarkus.transaction;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.annotation.ConcurrentGauge;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quarkus.transaction.events.AccountFee;
import quarkus.transaction.exception.AccountExceptionMapper;
import quarkus.transaction.exception.AccountNotFoundException;
import quarkus.transaction.exception.TransactionServiceFallbackHandler;
import quarkus.transaction.object.Account;
import quarkus.transaction.object.Transaction;
import quarkus.transaction.object.TransactionStatus;
import quarkus.transaction.repository.TransactionRepository;

@Path("/transactions")
public class TransactionResource {

	@ConfigProperty(name = "account.service", defaultValue = "http://localhost:8080")
	String accountServiceUrl;
	
	private static Logger log = LoggerFactory.getLogger(TransactionResource.class);
	
	@Inject
	@Metric(name="deposits", 
	description="Deposit histogram")
	Histogram depositHistogram;
	
	@Inject
	@RestClient
	AccountService accountService;

	@Inject
	TransactionRepository transactionRepository;
	
	void updateDespositHistogram(BigDecimal dollars) {
		depositHistogram.update(dollars.longValue());
		log.info("histogram: {}", depositHistogram.getSum());
	}
	
	@GET
	@Path("/")
	public List<Transaction> getAllTransactions() {
		return transactionRepository.listAll();
	}

	@POST
	@Path("/{accountNumber}")
	@ConcurrentGauge(
			name = "concurrentBlockingTransactions",
			absolute = true,
			description = "Number of concurrent transactions using blocking API"
			)
	
	@Transactional
	public Map<String, List<String>> newTransaction(@PathParam("accountNumber") Long accountNumber, BigDecimal amount) {
		Transaction transaction = createNewTransaction(accountNumber, amount);
		try {
			Map<String, List<String>> transactionResponse = accountService.transact(accountNumber, amount);
			transaction.makeSucces();
			updateDespositHistogram(amount);
			return transactionResponse;
		} catch (Exception e) {
			log.error("Exception", e);
			return makeError(e, transaction);
		}
	}

	@POST
	@Path("/async/{accountNumber}")
	@Transactional
	public CompletionStage<Map<String, List<String>>> newTransactionAsync(
			@PathParam("accountNumber") Long accountNumber, BigDecimal amount) {
		Transaction transaction = createNewTransaction(accountNumber, amount);
		CompletionStage<Map<String, List<String>>> response = accountService.transactAsync(accountNumber, amount)
				.exceptionallyAsync(e -> makeError(e, transaction));
		transaction.makeSucces();
		updateDespositHistogram(amount);
		return response;
	}

	@POST
	@Path("/api/{accountNumber}")
	@Transactional
	@Bulkhead(1)
	@CircuitBreaker(requestVolumeThreshold = 3, failureRatio = 0.66, delay = 5, delayUnit = ChronoUnit.SECONDS, successThreshold = 2)
	@Fallback(value = TransactionServiceFallbackHandler.class)
	public Response newTransactionWithApi(@PathParam("accountNumber") Long accountNumber, BigDecimal amount)
			throws IllegalStateException, RestClientDefinitionException, MalformedURLException {
		Transaction transaction = createNewTransaction(accountNumber, amount);
		AccountServiceProgrammatic accountServiceProgrammatic = RestClientBuilder.newBuilder()
				.baseUrl(new URL(accountServiceUrl)).connectTimeout(500, TimeUnit.MILLISECONDS)
				.readTimeout(1500, TimeUnit.MILLISECONDS).register(AccountExceptionMapper.class)
				.register(AccountRequestFilter.class).build(AccountServiceProgrammatic.class);
		String reponse = accountServiceProgrammatic.transact(accountNumber, amount);
		transaction.makeSucces();
		updateDespositHistogram(amount);
		return Response.ok(reponse).build();
	}

	@POST
	@Path("/api/async/{accountNumber}")
	@Transactional
	public CompletionStage<Map<String, List<String>>> newTransactionWithApiAsync(
			@PathParam("accountNumber") Long accountNumber, BigDecimal amount)
			throws IllegalStateException, RestClientDefinitionException, MalformedURLException {
		Transaction transaction = createNewTransaction(accountNumber, amount);
		transaction.setStatus(TransactionStatus.SUCCESS);
		updateDespositHistogram(amount);
		AccountServiceProgrammatic accountServiceProgrammatic = RestClientBuilder.newBuilder()
				.baseUrl(new URL(accountServiceUrl)).connectTimeout(500, TimeUnit.MILLISECONDS)
				.readTimeout(1500, TimeUnit.MILLISECONDS).register(AccountRequestFilter.class)
				.build(AccountServiceProgrammatic.class);
		return accountServiceProgrammatic.transactAsyncWithApi(accountNumber, amount)
				.exceptionally(e -> makeError(e, transaction));

	}

	@GET
	@Path("/{accountNumber}/balance")
	@Timeout(100)
	@Fallback(value = TransactionServiceFallbackHandler.class)
	@Retry(retryOn = TimeoutException.class, delay = 100, maxRetries = 3, jitter = 25)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBalance(@PathParam("accountNumber") Long accountNumber) {
		return Response.ok(accountService.getBalance(accountNumber)).build();
	}
	
	@Incoming("overdraft-fee")
	@Transactional
	public void processAccountFee(AccountFee accountFee) {
		Long accountNumber = accountFee.getAccountNumber();
		BigDecimal amount = accountFee.getOverdraftFee();
		newTransaction(accountNumber,  amount.subtract(amount.multiply(BigDecimal.valueOf(2))));
	}

	private Transaction createNewTransaction(Long accountNumber, BigDecimal amount) {
		Transaction transaction = new Transaction();
		transaction.setAccountNumber(accountNumber);
		transaction.setAmount(amount);
		transactionRepository.persist(transaction);
		return transaction;
	}

	private Map<String, List<String>> makeError(Throwable e, Transaction transaction) {
		transaction.setStatus(TransactionStatus.ERROR);
		transaction.setUpdated(LocalDateTime.now());
		Map<String, List<String>> response = new HashMap<>();
		response.put("Exception - " + e.getClass(), Collections.singletonList(e.getMessage()));
		return response;
	}

	public Map<String, List<String>> bulkheadNewTrxWithApi(Long accountNumber, BigDecimal amount) {
		return Map.of("Error", List.of("TOO_MANY_REQUESTS"));
	}

	public Response timeoutFallbackGetBalance(Long accountNumber) {
		return Response.status(Status.GATEWAY_TIMEOUT).build();
	}
	
	@Path("/{acctNumber}/withdrawal")
	@PUT
	public Account withdrawal(@PathParam("acctNumber") Long accountNumber, String amount) throws AccountNotFoundException {
		return accountService.withdrawal(accountNumber, amount);
	}
	
	@Path("/{acctNumber}/deposit")
	@PUT
	public Response deposit(@PathParam("acctNumber") Long accountNumber, String amount)  {
		try {
			accountService.deposit(accountNumber, amount);
			return Response.ok().build();
		} catch (AccountNotFoundException e) {
			return Response.serverError().entity(e).build();
		}
	}
}