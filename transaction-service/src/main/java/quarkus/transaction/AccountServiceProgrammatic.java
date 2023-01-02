package quarkus.transaction;

import java.math.BigDecimal;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public interface AccountServiceProgrammatic {


	@POST
	@Path("/{accountNumber}/transaction")
	void transact(@PathParam("accountNumber") Long accountNumber, BigDecimal amount);

	@POST
	@Path("/{accountNumber}/transaction")
	CompletionStage<Void> transactAsync(@PathParam("accountNumber") Long accountNumber, BigDecimal amount);

}
