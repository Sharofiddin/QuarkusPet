package uz.learn.models;

import java.math.BigDecimal;

public class AccountOverdraft {
	private Long accountNumber;
	private BigDecimal currentOverdraft;
	private int numberOverdrawnEvents = 0;

	public Long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getCurrentOverdraft() {
		return currentOverdraft;
	}

	public void setCurrentOverdraft(BigDecimal currentOverdraft) {
		this.currentOverdraft = currentOverdraft;
	}

	public int getNumberOverdrawnEvents() {
		return numberOverdrawnEvents;
	}

	public void setNumberOverdrawnEvents(int numberOverdrawnEvents) {
		this.numberOverdrawnEvents = numberOverdrawnEvents;
	}

	public void incOverdraft() {
		numberOverdrawnEvents++;
	}
}
