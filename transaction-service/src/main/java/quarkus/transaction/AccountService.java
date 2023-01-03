package quarkus.transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "account-service")
@ClientHeaderParam(name = "class-level-param", value = "AccouuntService interface")
public interface AccountService {

	@POST
	@Path("/{accountNumber}/transaction")
	Map<String, List<String>> transact(@PathParam("accountNumber") Long accountNumber, BigDecimal amount);

	@POST
	@Path("/{accountNumber}/transaction")
	@ClientHeaderParam(name="method-lvl-param", value = "{generated}")
	CompletionStage<Map<String, List<String>>> transactAsync(@PathParam("accountNumber") Long accountNumber, BigDecimal amount);
	
	default String generated() {
		return "Value generated for method async call";
	}
}
