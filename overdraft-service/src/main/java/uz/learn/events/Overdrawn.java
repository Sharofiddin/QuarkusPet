package uz.learn.events;

import java.math.BigDecimal;

public class Overdrawn {
	private Long accountNumber;
	private Long customerNumber;
	private BigDecimal balance ;
	private BigDecimal overdraftLimit;
	public Long getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}
	public Long getCustomerNumber() {
		return customerNumber;
	}
	public void setCustomerNumber(Long customerNumber) {
		this.customerNumber = customerNumber;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public BigDecimal getOverdraftLimit() {
		return overdraftLimit;
	}
	public void setOverdraftLimit(BigDecimal overdraftLimit) {
		this.overdraftLimit = overdraftLimit;
	}
	
}
