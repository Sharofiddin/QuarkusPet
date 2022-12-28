package uz.learn.objects;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
@Entity
@NamedQuery(name = "Accounts.findAll", query="SELECT a FROM Account a ORDER BY a.accountNumber")
@NamedQuery(name = "Accounts.findByAccountNumber", query="SELECT a FROM Account a WHERE a.accountNumber=:accountNumber")
public class Account {
	@Id
	@SequenceGenerator(name="accountsSequence", sequenceName = "account_id_seq", initialValue = 10, allocationSize = 1)
	@GeneratedValue(generator = "accountsSequence", strategy = GenerationType.SEQUENCE)
	private Long id;
	private Long accountNumber;
	private String customerName;
	private Long customerNumber;
	private BigDecimal balance;
	private AccountStatus accountStatus = AccountStatus.OPEN;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(Long customerNumber) {
		this.customerNumber = customerNumber;
	}

	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public void markOverdrawn() {
		this.accountStatus = AccountStatus.OVERDRAWN;
	}
	
	public void removeOverdrawnStatus() {
		this.accountStatus = AccountStatus.OPEN;
	}
	
	public void close() {
		this.accountStatus = AccountStatus.CLOSED;
		this.balance = BigDecimal.ZERO;
	}
	
	public void withdrawFunds(BigDecimal amount) {
		balance = balance.subtract(amount);
	}
	
	public void addFunds(BigDecimal amount) {
		balance = balance.add(amount);
	}
	

	public Long getAccountNumber() {
		return accountNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}	
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof Account acc) {
			return acc.getAccountNumber().equals(accountNumber) && acc.customerNumber.equals(customerNumber);
		}else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(accountNumber,customerNumber);
	}
}
