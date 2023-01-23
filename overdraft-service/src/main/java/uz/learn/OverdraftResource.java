package uz.learn;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import uz.learn.events.OverdraftLimitUpdate;
import uz.learn.events.Overdrawn;
import uz.learn.models.AccountOverdraft;
import uz.learn.models.CustomerOverdraft;

@Path("/overdraft")
public class OverdraftResource {

	private Map<Long, CustomerOverdraft> customerOverdrafts = new HashMap<>();

	@Inject
	@Channel("overdraft-update")
	Emitter<OverdraftLimitUpdate> overdraftLimitUpdateEmitter;

	@Incoming("account-overdrawn")
	@Outgoing("customer-overdrafts")
	public Message<Overdrawn> overdraftNotification(Message<Overdrawn> message) {
		Overdrawn payload = message.getPayload();
		CustomerOverdraft customerOverdraft = customerOverdrafts.computeIfAbsent(payload.getCustomerNumber(), key -> {
			CustomerOverdraft overdraft = new CustomerOverdraft();
			overdraft.setCustomerNumber(payload.getCustomerNumber());
			return overdraft;
		});

		AccountOverdraft accountOverdraft = customerOverdraft.getAccountOverdrafts()
				.computeIfAbsent(payload.getAccountNumber(), key -> {
					AccountOverdraft overdraft = new AccountOverdraft();
					overdraft.setAccountNumber(payload.getAccountNumber());
					return overdraft;
				});

		customerOverdraft.incTotalOverdrawnEvents();
		accountOverdraft.setCurrentOverdraft(payload.getOverdraftLimit());
		accountOverdraft.incOverdraft();

		return message.addMetadata(customerOverdraft);
	}

	@PUT
	@Path("/{accountNumber}")
	public void updateOverdraftLimit(@PathParam("accountNumber") Long accountNumber, BigDecimal newLimit) {
		OverdraftLimitUpdate limitUpdate = new OverdraftLimitUpdate();
		limitUpdate.setAccountNumber(accountNumber);
		limitUpdate.setNewOverDraftLimit(newLimit);
		overdraftLimitUpdateEmitter.send(limitUpdate);
	}

	@GET
	@Path("/")
	public List<AccountOverdraft> getAllAccountOverdrafts() {
		return customerOverdrafts.values().stream().flatMap(co -> co.getAccountOverdrafts().values().stream()).toList();
	}
}
