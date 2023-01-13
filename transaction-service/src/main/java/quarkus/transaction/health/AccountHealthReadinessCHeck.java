package quarkus.transaction.health;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import quarkus.transaction.AccountService;

@Readiness
public class AccountHealthReadinessCHeck implements HealthCheck {

	private static final String ACCOUNT_SERVICE_CHECK = "AccountServiceCheck";
	@Inject
	@RestClient
	AccountService accountService;
	BigDecimal balance;

	@Override
	public HealthCheckResponse call() {
		try {
			balance = accountService.getBalance(999999999L);
		} catch (WebApplicationException e) {
			balance = new BigDecimal(Integer.MIN_VALUE);
			if (e.getResponse().getStatus() >= 500) {
				return HealthCheckResponse.named(ACCOUNT_SERVICE_CHECK).withData("exception", e.toString()).down()
						.build();
			}
		}
		return HealthCheckResponse.named(ACCOUNT_SERVICE_CHECK).withData("balance", balance.toString()).up().build();
	}

}
