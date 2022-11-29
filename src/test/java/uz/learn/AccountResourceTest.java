package uz.learn;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import uz.learn.objects.Account;

@QuarkusTest
class AccountResourceTest {

	@Test
	void testRetrieveAll() {
		Response result =
				given()
				.when().get("/accounts")
				.then().statusCode(200)
				.body(containsString("Frode Aleks"), 
						containsString("Gautvin Marianne"),
						containsString("Gallus Zakaria"),
						containsString("Norbert Thijmen"))
				.extract().response();
		List<Account> accounts = result.jsonPath().getList("$");
		assertThat(accounts, not(empty()));
		assertThat(accounts, hasSize(4));
	}

}
