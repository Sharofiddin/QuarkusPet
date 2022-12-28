package uz.learn;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

@Path("/accounts")
public class AccountResource {

	@Inject
	EntityManager entityManager;

	private Set<Account> accounts = new HashSet<>();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Account> allAccounts() {
		return entityManager.createNamedQuery("Accounts.findAll", Account.class).getResultList();
	}

	@GET
	@Path("/{accountNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public Account getAccount(@PathParam("accountNumber") Long accountNumber) {
		try {
			return entityManager.createNamedQuery("Accounts.findByAccounNumber", Account.class)
					.setParameter("accountNumber", accountNumber).getSingleResult();
		} catch (NoResultException e) {
			throw new WebApplicationException("Account with id of " + accountNumber + " does not exist.", 404);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createAccount(Account account) {
		if (account.getAccountNumber() == null) {
			throw new WebApplicationException("Account id is not specified", 400);
		}
		accounts.add(account);
		return Response.status(201).entity(account).build();
	}

	@PUT
	@Path("/{accountNumber}/withdrawal")
	public Account withdrawal(@PathParam("accountNumber") Long accountNumber, String amount) {
		Account account = getAccount(accountNumber);
		account.withdrawFunds(new BigDecimal(amount));
		return account;
	}

	@PUT
	@Path("/{accountNumber}/deposit")
	public Account deposit(@PathParam("accountNumber") Long accountNumber, String amount) {
		Account account = getAccount(accountNumber);
		account.addFunds(new BigDecimal(amount));
		return account;
	}

	@DELETE
	@Path("/{accountNumber}")
	public Response delete(@PathParam("accountNumber") Long accountNumber) {
		accounts.removeIf(acc -> acc.getAccountNumber().equals(accountNumber));
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
