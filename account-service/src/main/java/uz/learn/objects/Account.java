package uz.learn.objects;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
@Entity
public class Account {
	@Id
	 @SequenceGenerator(name = "accountsSequence", sequenceName = "accounts_id_seq",
	    allocationSize = 1, initialValue = 10)
	  @GeneratedValue(strategy = GenerationType.SEQUENCE,
	    generator = "accountsSequence")
	private Long id;
	
	@Column(name = "account_number")
	private Long accountNumber;
	
	@Column(name = "customer_name")
	private String customerName;
	
	@Column(name = "customer_number")
	private Long customerNumber;
	
	private BigDecimal balance;
	
	@Column(name = "overdraft_limit")
	private BigDecimal overdraftLimit;
	
	@Column(name = "account_status")
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
	
	
	public BigDecimal getOverdraftLimit() {
		return overdraftLimit;
	}

	public void setOverdraftLimit(BigDecimal overdraftLimit) {
		this.overdraftLimit = overdraftLimit;
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
