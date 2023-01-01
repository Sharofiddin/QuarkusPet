package quarkus.transaction;

import java.math.BigDecimal;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/accounts")
@RegisterRestClient(configKey = "account-service")
@Produces(MediaType.APPLICATION_JSON)
public interface AccountService {
	
	@GET
	@Path("/{accountNumber}/balance")
	BigDecimal getBalance(@PathParam("accountNumber") Long accountNumber);
	
	@POST
	@Path("{accountNumber}/transaction")
	void transact(@PathParam("accountNumber") Long accountNumber, BigDecimal amount);
	
	

}
