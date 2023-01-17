package quarkus.transaction;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockAccountService implements QuarkusTestResourceLifecycleManager {

	private WireMockServer wireMockServer;

	@Override
	public Map<String, String> start() {
		wireMockServer = new WireMockServer(WireMockConfiguration.options().port(7080));
		wireMockServer.start();
		WireMock.configureFor(wireMockServer.port());
		
		mockAccountService();
		mockTimeout();
		return Collections.singletonMap("account-service/mp-rest/url", wireMockServer.baseUrl());
	}
	
	protected void mockAccountService() {
		stubFor(get(urlEqualTo("/accounts/121212/balance")).willReturn(
				aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withBody("100.20")));
		stubFor(post(urlEqualTo("/accounts/121212/transaction")).willReturn(
				aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON).withStatus(200).withBody("{}")));
	}
	
	protected void mockTimeout() {
		stubFor(get(urlEqualTo("/accounts/123456/balance")).willReturn(
				aResponse()
				.withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON)
				.withStatus(200)
				.withFixedDelay(200)
				.withBody("200.00")));
		stubFor(get(urlEqualTo("/accounts/456789/balance")).willReturn(
				aResponse()
				.withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON)
				.withStatus(200)
				.withBody("200.00")));
	}
	@Override
	public void stop() {
		if (null != wireMockServer) {
			wireMockServer.stop();
		}
	}

}
