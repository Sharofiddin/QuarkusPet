package quarkus.transaction;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import quarkus.transaction.exception.AccountExceptionMapper;
import quarkus.transaction.object.Transaction;
import quarkus.transaction.object.TransactionStatus;
import quarkus.transaction.repository.TransactionRepository;

@Path("/transactions")
public class TransactionResource {

	@ConfigProperty(name = "account.service", defaultValue = "http://localhost:8080")
	String accountServiceUrl;

	@Inject
	@RestClient
	AccountService accountService;

	@Inject
	TransactionRepository transactionRepository;

	@GET
	@Path("/")
	public List<Transaction> getAllTransactions() {
		return transactionRepository.listAll();
	}

	@POST
	@Path("/{accountNumber}")
	@Transactional
	public Map<String, List<String>> newTransaction(@PathParam("accountNumber") Long accountNumber, BigDecimal amount) {
		Transaction transaction = createNewTransaction(accountNumber, amount);
		try {
			Map<String, List<String>> transactionResponse = accountService.transact(accountNumber, amount);
			transaction.makeSucces();
			return transactionResponse;
		} catch (Exception e) {
			return makeError(e);
		}
	}

	@POST
	@Path("/async/{accountNumber}")
	@Transactional
	public CompletionStage<Map<String, List<String>>> newTransactionAsync(
			@PathParam("accountNumber") Long accountNumber, BigDecimal amount) {
		try {
			Transaction transaction = createNewTransaction(accountNumber, amount);
			CompletionStage<Map<String, List<String>>> response = accountService.transactAsync(accountNumber, amount);
			transaction.makeSucces();
			return response;
		} catch (Exception e) {
			return CompletableFuture.supplyAsync(() -> makeError(e));
		}
	}

	@POST
	@Path("/api/{accountNumber}")
	@Transactional
	@Bulkhead(1)
	public Map<String, List<String>> newTransactionWithApi(@PathParam("accountNumber") Long accountNumber,
			BigDecimal amount) throws IllegalStateException, RestClientDefinitionException, MalformedURLException {
		try {
			Transaction transaction = createNewTransaction(accountNumber, amount);
			AccountServiceProgrammatic accountServiceProgrammatic = RestClientBuilder.newBuilder()
					.baseUrl(new URL(accountServiceUrl)).connectTimeout(500, TimeUnit.MILLISECONDS)
					.readTimeout(1500, TimeUnit.MILLISECONDS).register(AccountExceptionMapper.class)
					.register(AccountRequestFilter.class).build(AccountServiceProgrammatic.class);
			Map<String, List<String>> reponse = accountServiceProgrammatic.transact(accountNumber, amount);
			transaction.makeSucces();
			return reponse;
		} catch (Exception e) {
			return makeError(e);
		}
	}

	@POST
	@Path("/api/async/{accountNumber}")
	@Transactional
	public CompletionStage<Map<String, List<String>>> newTransactionWithApiAsync(
			@PathParam("accountNumber") Long accountNumber, BigDecimal amount)
			throws IllegalStateException, RestClientDefinitionException, MalformedURLException {
			Transaction transaction = createNewTransaction(accountNumber, amount);
			transaction.setStatus(TransactionStatus.SUCCESS);
			AccountServiceProgrammatic accountServiceProgrammatic = RestClientBuilder.newBuilder()
					.baseUrl(new URL(accountServiceUrl)).connectTimeout(500, TimeUnit.MILLISECONDS)
					.readTimeout(1500, TimeUnit.MILLISECONDS).register(AccountRequestFilter.class)
					.build(AccountServiceProgrammatic.class);
			return  accountServiceProgrammatic
					.transactAsyncWithApi(accountNumber, amount)
					.exceptionally(this::makeError);
		
	}

	private Transaction createNewTransaction(Long accountNumber, BigDecimal amount) {
		Transaction transaction = new Transaction();
		transaction.setAccountNumber(accountNumber);
		transaction.setAmount(amount);
		transactionRepository.persist(transaction);
		return transaction;
	}

	private Map<String, List<String>> makeError(Throwable e) {
		Map<String, List<String>> response = new HashMap<>();
		response.put("Exception - " + e.getClass(), Collections.singletonList(e.getMessage()));
		return response;
	}

}