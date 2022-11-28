package uz.learn;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import uz.learn.objects.Account;

@Path("/accounts")
public class AccountResource {
	
	private Set<Account> accounts = new HashSet<>();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Account> allAccounts(){
		return accounts;
	}
	
	@GET
	@Path(("/{accountNumber}"))
	@Produces(MediaType.APPLICATION_JSON)
	public Account getAccount(@PathParam("accountNumber") Long accountNumber) {
		Optional<Account> response = accounts.stream().filter(acc->acc.getAccountNumber().equals(accountNumber)).findFirst();
		return response.orElseThrow(()->new NotFoundException("Account with id of " + accountNumber + " does not exist."));
	}
	
	@PostConstruct
	void setup(){
		accounts.add(new Account(121L, 131L, "Gallus Zakaria",  BigDecimal.valueOf(75.0)));
		accounts.add(new Account(122L, 132L, "Norbert Thijmen",  BigDecimal.valueOf(75.0)));
		accounts.add(new Account(123L, 133L, "Frode Aleks",  BigDecimal.valueOf(75.0)));
		accounts.add(new Account(124L, 134L, "Gautvin Marianne",  BigDecimal.valueOf(75.0)));
	}
}
