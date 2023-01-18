package quarkus.transaction.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionServiceFallbackHandler implements FallbackHandler<Response> {
	private static Logger log = LoggerFactory.getLogger(TransactionServiceFallbackHandler.class);

	@Override
	public Response handle(ExecutionContext context) {
		Response response = null;
		String name = null;
		if (context.getFailure().getCause() == null) {
			name = context.getFailure().getClass().getSimpleName();
		} else {
			name = context.getFailure().getCause().getClass().getSimpleName();
		}
		switch (name) {
		case "BulkheadException":
			response = Response.status(Status.TOO_MANY_REQUESTS).build();
			break;
		case "TimeoutException":
			response = Response.status(Status.GATEWAY_TIMEOUT).build();
			break;
		case "CircuitBreakerOpenException":
			response = Response.status(Status.SERVICE_UNAVAILABLE).build();
			break;
		case "ResteasyWebApplicationException",
		     "WebApplicationException", 
		     "HttpHostConnectException":
			response = Response.status(Status.BAD_GATEWAY).build();
			break;
		default:
			response = Response.status(Status.NOT_IMPLEMENTED).build();
			break;
		}
			log.info("******** {} : {} *******" , context.getMethod().getName() , name );
			return response;
		}
	}
