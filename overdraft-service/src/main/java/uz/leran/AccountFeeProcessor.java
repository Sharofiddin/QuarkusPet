package uz.leran;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import uz.leran.events.AccountFee;
import uz.leran.events.Overdrawn;
import uz.leran.objects.CustomerOverdraft;

public class AccountFeeProcessor {
	@Incoming("customer-overdrafts")
	@Outgoing("overdraft-fee")
	public AccountFee processOverdraftFee(Message<Overdrawn> message) {
		Overdrawn payload = message.getPayload();
		CustomerOverdraft customerOverdraft = message.getMetadata(CustomerOverdraft.class)
				.orElseThrow(() -> new IllegalStateException("Metadata invalid"));
		AccountFee accountFee = new AccountFee();
		accountFee.setAccountNumber(payload.getAccountNumber());
		accountFee.setOverdraftFee(determineFee(customerOverdraft.getTotalOverdrawnEvents(),
				customerOverdraft.getAccountOverdrafts().get(payload.getAccountNumber()).getNumberOverdrawnEvents()));
		return accountFee;
	}

	private BigDecimal determineFee(int customerOverdrawnTimes, int accountOverdrawnTimes) {
		return new BigDecimal((5 * accountOverdrawnTimes) + (10 * customerOverdrawnTimes)).setScale(2,
				RoundingMode.HALF_UP);
	}
}
