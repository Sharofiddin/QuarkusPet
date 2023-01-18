package quarkus.transaction;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockAccountService implements QuarkusTestResourceLifecycleManager {

	private WireMockServer wireMockServer;

	private static final String SERVER_ERROR_1 = "CB Fail 1";
	private static final String SERVER_ERROR_2 = "CB Fail 2";
	private static final String CB_OPEN_1 = "CB Open 1";
	private static final String CB_OPEN_2 = "CB Open 2";
	private static final String CB_OPEN_3 = "CB Open 3";
	private static final String CB_SUCCES_1 = "CB Succes 1";
	private static final String CB_SUCCES_2 = "CB Succes 2";

	@Override
	public Map<String, String> start() {
		wireMockServer = new WireMockServer(WireMockConfiguration.options().port(7080));
		wireMockServer.start();
		WireMock.configureFor(wireMockServer.port());

		mockAccountService();
		mockTimeout();
		mockCircuitBreaker();
		return Map.of("account-service/mp-rest/url", wireMockServer.baseUrl(), "account.service",
				wireMockServer.baseUrl());
	}

	protected void mockAccountService() {
		stubFor(get(urlEqualTo("/accounts/121212/balance")).willReturn(
				aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withBody("100.20")));
		stubFor(post(urlEqualTo("/accounts/121212/transaction")).willReturn(
				aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON).withStatus(200).withBody("{}")));
	}

	protected void mockTimeout() {
		stubFor(get(urlEqualTo("/accounts/123456/balance"))
				.willReturn(aResponse().withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON).withStatus(200)
						.withFixedDelay(200).withBody("200.00")));
		stubFor(get(urlEqualTo("/accounts/456789/balance")).willReturn(aResponse()
				.withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON).withStatus(200).withBody("200.00")));
	}

	protected void mockCircuitBreaker() {
		createCircuitBreakerStub(Scenario.STARTED, SERVER_ERROR_1, "100", 200);
		createCircuitBreakerStub(SERVER_ERROR_1, SERVER_ERROR_2, "200", 502);
		createCircuitBreakerStub(SERVER_ERROR_2, CB_OPEN_1, "300", 502);
		createCircuitBreakerStub(CB_OPEN_1, CB_OPEN_2, "400", 200);
		createCircuitBreakerStub(CB_OPEN_2, CB_OPEN_3, "500", 200);
		createCircuitBreakerStub(CB_OPEN_3, CB_SUCCES_1, "600", 200);
		createCircuitBreakerStub(CB_SUCCES_1, CB_SUCCES_2, "700", 200);
	}

	void createCircuitBreakerStub(String currentState, String nextState, String response, int status) {
		stubFor(post(urlEqualTo("/accounts/444666/transaction")).inScenario("circuitbreake")
				.whenScenarioStateIs(currentState).willSetStateTo(nextState)
				.willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON).withStatus(status)
						.withBody(Json.createObjectBuilder(Map.of("amount", List.of(response))).build().toString())));
	}

	@Override
	public void stop() {
		if (null != wireMockServer) {
			wireMockServer.stop();
		}
	}

}
