package quarkus.transaction;

import static io.restassured.RestAssured.given;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
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
	
	@Test
	void testCircuitBreaker() {
		RequestSpecification request = given().body("145.00").contentType(ContentType.JSON);
		request.post("/transactions/api/444666").then().statusCode(200);
		request.post("/transactions/api/444666").then().statusCode(502);
		request.post("/transactions/api/444666").then().statusCode(502);
		
		//circuit breaker opened
		request.post("/transactions/api/444666").then().statusCode(503);
		request.post("/transactions/api/444666").then().statusCode(503);
		
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		request.post("/transactions/api/444666").then().statusCode(200);
		request.post("/transactions/api/444666").then().statusCode(200);
		request.post("/transactions/api/444666").then().statusCode(200);
	}
}
