package quarkus.accounts.repository;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import quarkus.accounts.objects.Account;

@ApplicationScoped
public class AccountRepository implements PanacheRepository<Account>{
	public Account findByAccountNumber(Long accountNumber) {
		return find("account_number = ?1", accountNumber).firstResult();
	}
}
