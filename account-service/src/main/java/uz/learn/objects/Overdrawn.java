package uz.learn.objects;

import java.math.BigDecimal;

public class Overdrawn {
	private Long accountNumber; 
	private Long customerNumber; 
	private BigDecimal balance;
	private BigDecimal overdraftLimit;
	public Overdrawn(Long accountNumber, Long customerNumber, BigDecimal balance, BigDecimal overdraftLimit) {
		this.accountNumber = accountNumber; 
		this.customerNumber = customerNumber; 
		this.balance = balance;
		this.overdraftLimit = overdraftLimit;
	}
	public Long getAccountNumber() {
		return accountNumber;
	}
	public Long getCustomerNumber() {
		return customerNumber;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public BigDecimal getOverdraftLimit() {
		return overdraftLimit;
	}
	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}
	public void setCustomerNumber(Long customerNumber) {
		this.customerNumber = customerNumber;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public void setOverdraftLimit(BigDecimal overdraftLimit) {
		this.overdraftLimit = overdraftLimit;
	}
	@Override
	public String toString() {
		return "Overdrawn [accountNumber=" + accountNumber + ", customerNumber=" + customerNumber + ", balance="
				+ balance + ", overdraftLimit=" + overdraftLimit + "]";
	}

}
