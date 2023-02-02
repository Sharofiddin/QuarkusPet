package quarkus.accounts;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.annotations.Blocking;
import quarkus.accounts.objects.Account;
import quarkus.accounts.objects.OverdraftLimitUpdate;
import quarkus.accounts.objects.Overdrawn;
import quarkus.accounts.repository.AccountRepository;

@Path("/accounts")
public class AccountResource {
	
	private static Logger log = LoggerFactory.getLogger(AccountResource.class);
	
	@Inject
	AccountRepository accountRepository;

	@Inject
	@Channel("account-overdrawn")
	Emitter<Overdrawn> emitter;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Account> allAccounts() {
		return accountRepository.listAll();
	}

	@GET
	@Path("/{acctNumber}/balance")
	public BigDecimal getBalance(@PathParam("acctNumber") Long accountNumber) {
		Account account = accountRepository.findByAccountNumber(accountNumber);

		if (account == null) {
			throw new WebApplicationException("Account with " + accountNumber + " does not exist.", 404);
		}

		return account.getBalance();
	}

	@POST
	@Path("{accountNumber}/transaction")
	@Transactional
	public Map<String, List<String>> transact(@Context HttpHeaders headers,
			@PathParam("accountNumber") Long accountNumber, BigDecimal amount) {
		Account entity = accountRepository.findByAccountNumber(accountNumber);

		if (entity == null) {
			throw new WebApplicationException("Account with " + accountNumber + " does not exist.", 404);
		}

		if (!entity.isWithdrawalPossible()) {
			throw new WebApplicationException("Account is overdrawn, no further withdrawals permitted", 409);
		}

		entity.setBalance(entity.getBalance().add(amount));
		return headers.getRequestHeaders();
	}

	@GET
	@Path("/{accountNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public Account getAccount(@PathParam("accountNumber") Long accountNumber) {
		try {
			return accountRepository.findByAccountNumber(accountNumber);
		} catch (NoResultException e) {
			throw new WebApplicationException("Account with id of " + accountNumber + " does not exist.", 404);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response createAccount(Account account) {
		if (account.getAccountNumber() == null) {
			throw new WebApplicationException("Account id is not specified", 400);
		}
		accountRepository.persist(account);
		return Response.status(201).entity(account).build();
	}

	@PUT
	@Path("/{accountNumber}/withdrawal")
	@Transactional
	public CompletionStage<Account> withdrawal(@PathParam("accountNumber") Long accountNumber, String amount) {
		Account entity = getAccount(accountNumber);
		if (!entity.isWithdrawalPossible()) {
			throw new WebApplicationException("Account is overdrawn, no further withdrawals permitted", 409);
		}
		entity.withdrawFunds(new BigDecimal(amount));

		if (entity.getBalance().compareTo(BigDecimal.ZERO) < 0) {
			entity.markOverdrawn();
			Overdrawn payload = new Overdrawn(entity.getAccountNumber(), entity.getCustomerNumber(),
					entity.getBalance(), entity.getOverdraftLimit());
			log.warn("Account overdrawn, payload={}", payload);
			return emitter.send(payload).thenCompose(empty -> CompletableFuture.completedFuture(entity));
		}
		accountRepository.persist(entity);
		return CompletableFuture.completedStage(entity);
	}

	@PUT
	@Path("/{accountNumber}/deposit")
	@Transactional
	public Account deposit(@PathParam("accountNumber") Long accountNumber, String amount) {
		Account account = getAccount(accountNumber);
		account.addFunds(new BigDecimal(amount));
		return account;
	}

	@DELETE
	@Path("/{accountNumber}")
	@Transactional
	public Response delete(@PathParam("accountNumber") Long accountNumber) {
		accountRepository.delete(accountRepository.findByAccountNumber(accountNumber));
		return Response.noContent().build();
	}

	@Incoming("overdraft-update")
	@Transactional
	@Blocking
	public void processOverdraftUpdate(OverdraftLimitUpdate overdraftLimitUpdate) {
		Account entity = accountRepository.findByAccountNumber(overdraftLimitUpdate.getAccountNumber());
		entity.setOverdraftLimit(overdraftLimitUpdate.getNewOverDraftLimit());
	}

	@Provider
	public static class ErrorMapper implements ExceptionMapper<Exception> {

		@Override
		public Response toResponse(Exception exception) {
			int code = 500;
			if (exception instanceof WebApplicationException webException) {
				code = webException.getResponse().getStatus();
			}
			JsonObjectBuilder entityBuilder = Json.createObjectBuilder()
					.add("exceptiontype", exception.getClass().getName()).add("code", code);
			if (exception.getMessage() != null) {
				entityBuilder.add("error", exception.getMessage());
			}

			return Response.status(code).entity(entityBuilder.build()).build();
		}

	}
}
