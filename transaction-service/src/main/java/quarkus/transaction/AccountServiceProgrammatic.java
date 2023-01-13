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

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@ClientHeaderParam(name = "Class-Level-Header", value = "Class level header value (api)")
public interface AccountServiceProgrammatic {


	@POST
	@Path("/{accountNumber}/transaction")
	Map<String, List<String>> transact(@PathParam("accountNumber") Long accountNumber, BigDecimal amount);

	@POST
	@Path("/{accountNumber}/transaction")
	@ClientHeaderParam(name = "Method-Level-Header", value = "{generateValue}")
	CompletionStage<Map<String, List<String>>> transactAsyncWithApi(@PathParam("accountNumber") Long accountNumber, BigDecimal amount);
	
	default String generateValue() {
		return "Method level header value async (api)";
	}
}
