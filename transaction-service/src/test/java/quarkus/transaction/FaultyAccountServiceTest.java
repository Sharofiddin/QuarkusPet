package quarkus.transaction;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;

import javax.ws.rs.core.MediaType;
@QuarkusTest
@QuarkusTestResource(WiremockAccountService.class)
class FaultyAccountServiceTest {

	@Test
	void testTimeout() {
		given().contentType(MediaType.APPLICATION_JSON)
		.get("/transactions/123456/balance")
		.then().statusCode(504);
		
		given().contentType(MediaType.APPLICATION_JSON)
		.get("/transactions/456789/balance")
		.then().statusCode(200);
	}

}
