package uz.learn;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uz.learn.events.AccountFee;
import uz.learn.events.Overdrawn;
import uz.learn.models.CustomerOverdraft;

@ApplicationScoped
public class AccountFeeProcessor {
	private static Logger log = LoggerFactory.getLogger(AccountFeeProcessor.class);
	@Incoming("customer-overdrafts")
	@Outgoing("overdraft-fee")
	public AccountFee processOverdraftFee(Message<Overdrawn> message) {
		log.info("customer-overdrafts message consumed");

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
