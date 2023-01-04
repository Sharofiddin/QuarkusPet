package quarkus.transaction;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

public class AccountRequestFilter implements ClientRequestFilter {

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		Method invokedMethod = (Method) requestContext
				.getProperty("org.eclipse.microprofile.rest.client.invokedMethod");
		requestContext.getHeaders().add("Invoked-Client-Method", invokedMethod.getName());
	}

}
