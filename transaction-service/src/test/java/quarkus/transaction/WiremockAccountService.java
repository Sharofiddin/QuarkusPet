package quarkus.transaction;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockAccountService implements QuarkusTestResourceLifecycleManager {

	private WireMockServer wireMockServer;

	@Override
	public Map<String, String> start() {
		wireMockServer = new WireMockServer();
		wireMockServer.start();

		stubFor(get(urlEqualTo("/accounts/121212/balance")).willReturn(
				aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withBody("100.20")));
		stubFor(post(urlEqualTo("/accounts/121212/transaction")).willReturn(noContent()));
		return Collections.singletonMap("account-service/mp-rest/url", wireMockServer.baseUrl());
	}

	@Override
	public void stop() {
		if(null != wireMockServer) {
			wireMockServer.stop();
		}
	}

}
