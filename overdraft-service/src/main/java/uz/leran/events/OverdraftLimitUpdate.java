package uz.leran.events;

import java.math.BigDecimal;

public class OverdraftLimitUpdate {
	private Long accountNumber;
	private BigDecimal newOverDraftLimit;
	public Long getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}
	public BigDecimal getNewOverDraftLimit() {
		return newOverDraftLimit;
	}
	public void setNewOverDraftLimit(BigDecimal newOverDraftLimit) {
		this.newOverDraftLimit = newOverDraftLimit;
	}
}
