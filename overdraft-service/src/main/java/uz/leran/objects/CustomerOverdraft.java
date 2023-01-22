package uz.leran.objects;

import java.util.HashMap;
import java.util.Map;

public class CustomerOverdraft {
	private Long customerNumber;
	private int totalOverdrawnEvents = 0;
	private Map<Long, AccountOverdraft> accountOverdrafts = new HashMap<>();

	public Long getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(Long customerNumber) {
		this.customerNumber = customerNumber;
	}

	public int getTotalOverdrawnEvents() {
		return totalOverdrawnEvents;
	}

	public void setTotalOverdrawnEvents(int totalOverdrawnEvents) {
		this.totalOverdrawnEvents = totalOverdrawnEvents;
	}

	public void incTotalOverdrawnEvents() {
		this.totalOverdrawnEvents++;
	}

	public Map<Long, AccountOverdraft> getAccountOverdrafts() {
		return accountOverdrafts;
	}

	public void setAccountOverdrafts(Map<Long, AccountOverdraft> accountOverdrafts) {
		this.accountOverdrafts = accountOverdrafts;
	}
}
