package quarkus.transaction;

import static io.restassured.RestAssured.given;

import javax.ws.rs.core.MediaType;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(value = WiremockAccountService.class)
@QuarkusTestResource(value = H2DatabaseTestResource.class)
class TransactionResourceTest {

    @Test
    void testTransaction() {
        given()
        .body("150.45")
        .contentType(MediaType.APPLICATION_JSON)
          .when().post("/transactions/{accountNumber}", 121212L)
          .then()
             .statusCode(Matchers.anyOf(Matchers.is(200), Matchers.is(204)));
    }
    
}