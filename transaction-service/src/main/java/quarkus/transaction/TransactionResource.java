package quarkus.transaction;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/transactions")
public class TransactionResource {

	@ConfigProperty(name = "account.service", defaultValue = "http://localhost:8080")
	private String accountServiceUrl;

	@Inject
	@RestClient
	AccountService accountService;

	@POST
	@Path("/{accountNumber}")
	public Map<String, List<String>> newTransaction(@PathParam("accountNumber") Long accountNumber, BigDecimal amount) {
		return accountService.transact(accountNumber, amount);
	}

	@POST
	@Path("/async/{accountNumber}")
	public CompletionStage<Map<String, List<String>>> newTransactionAsync(
			@PathParam("accountNumber") Long accountNumber, BigDecimal amount) {
		return accountService.transactAsync(accountNumber, amount);
	}

	@POST
	@Path("/api/{accountNumber}")
	public Map<String, List<String>> newTransactionWithApi(@PathParam("accountNumber") Long accountNumber,
			BigDecimal amount) throws IllegalStateException, RestClientDefinitionException, MalformedURLException {
		AccountServiceProgrammatic accountServiceProgrammatic = RestClientBuilder.newBuilder()
				.baseUrl(new URL(accountServiceUrl))
				.connectTimeout(500, TimeUnit.MILLISECONDS)
				.readTimeout(1500, TimeUnit.MILLISECONDS)
				.register(AccountRequestFilter.class)
				.build(AccountServiceProgrammatic.class);
		return accountServiceProgrammatic.transact(accountNumber, amount);
	}

	@POST
	@Path("/api/async/{accountNumber}")
	public CompletionStage<Map<String, List<String>>> newTransactionWithApiAsync(
			@PathParam("accountNumber") Long accountNumber, BigDecimal amount)
			throws IllegalStateException, RestClientDefinitionException, MalformedURLException {
		AccountServiceProgrammatic accountServiceProgrammatic = RestClientBuilder.newBuilder()
				.baseUrl(new URL(accountServiceUrl)).connectTimeout(500, TimeUnit.MILLISECONDS)
				.readTimeout(1500, TimeUnit.MILLISECONDS)
				.register(AccountRequestFilter.class)
				.build(AccountServiceProgrammatic.class);
		return accountServiceProgrammatic.transactAsync(accountNumber, amount);

	}
}