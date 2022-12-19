package uz.learn.objects;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
	public Long accountNumber;
	public String customerName;
	public Long customerNumber;
	public BigDecimal balance;
	public AccountStatus accountStatus = AccountStatus.OPEN;

	public Account(Long accountNumber, Long customerNumber, String customerName, BigDecimal balance) {
		this.accountNumber = accountNumber;
		this.customerName = customerName;
		this.customerNumber = customerNumber;
		this.balance = balance;
	}

	public Account() {
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
