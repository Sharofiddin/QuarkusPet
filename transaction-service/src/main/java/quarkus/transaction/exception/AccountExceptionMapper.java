package quarkus.transaction.exception;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class AccountExceptionMapper implements ResponseExceptionMapper<AccountNotFoundException> {

	@Override
	public AccountNotFoundException toThrowable(Response response) {
		return new AccountNotFoundException("Account not found");
	}

	@Override
	public boolean handles(int status, MultivaluedMap<String, Object> headers) {
		return status == 404;
	}

}
