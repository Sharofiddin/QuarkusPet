package quarkus.transaction;

import static io.restassured.RestAssured.given;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(value = WiremockAccountService.class)
class TransactionResourceTest {

    @Test
    void testTransaction() {
        given()
        .body("150.45")
        .contentType(MediaType.APPLICATION_JSON)
          .when().post("/transactions/{accountNumber}", 121212L)
          .then()
             .statusCode(200);
    }
    
}