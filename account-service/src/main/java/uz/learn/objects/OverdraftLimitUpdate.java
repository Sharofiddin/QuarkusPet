package uz.learn.objects;

import java.math.BigDecimal;

public class OverdraftLimitUpdate {

	private long accountNumber;

	private BigDecimal newOverDraftLimit;

	public long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getNewOverDraftLimit() {
		return newOverDraftLimit;
	}

	public void setNewOverDraftLimit(BigDecimal newOverDraftLimit) {
		this.newOverDraftLimit = newOverDraftLimit;
	}

}
