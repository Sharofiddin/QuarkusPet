package uz.learn;

import java.math.BigDecimal;
import java.util.List;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import uz.learn.objects.Account;
import uz.learn.repository.AccountRepository;

@Path("/accounts")
public class AccountResource {

	@Inject
	AccountRepository accountRepository;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Account> allAccounts() {
		return accountRepository.listAll();
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
	public Account withdrawal(@PathParam("accountNumber") Long accountNumber, String amount) {
		Account entity = getAccount(accountNumber);
		entity.withdrawFunds(new BigDecimal(amount));
		return entity;
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
