package uz.learn;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import uz.learn.objects.Account;
import uz.learn.objects.AccountStatus;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class AccountResourceTest {

	@DisplayName("Retrieve all accounts")
	@Test
	@Order(1)
	void testRetrieveAll() {
		Response result = given().when().get("/accounts").then().statusCode(200).body(containsString("Frode Aleks"),
				containsString("Gautvin Marianne"), containsString("Gallus Zakaria"), containsString("Norbert Thijmen"))
				.extract().response();
		List<Account> accounts = result.jsonPath().getList("$");
		assertThat(accounts, not(empty()));
		assertThat(accounts, hasSize(4));
	}

	@DisplayName("Get account by id")
	@Test
	@Order(2)
	void testGetAccountById() {
		Account account = given().when().get("/accounts/{accountNumber}", 121).then().statusCode(200).extract()
				.as(Account.class);

		assertThat(account.getAccountNumber(), equalTo(121L));
		assertThat(account.getCustomerName(), equalTo("Gallus Zakaria"));
		assertThat(account.getBalance(), equalTo(BigDecimal.valueOf(75.0)));
		assertThat(account.getAccountStatus(), equalTo(AccountStatus.OPEN));
	}

	@DisplayName("Create account test")
	@Test
	@Order(3)
	void testCreateAccount() {
		Account newAccount = new Account(125L, 135L, "Yusef Rubab", BigDecimal.valueOf(180.0d));
		Account returnedAccount = given().contentType(ContentType.JSON).body(newAccount).post("/accounts").then().statusCode(201).extract()
				.as(Account.class);
		assertThat(returnedAccount, notNullValue());
		assertThat(returnedAccount, equalTo(newAccount));
		Response result = given().when().get("/accounts").then().statusCode(200)
				.body(containsString("Frode Aleks"), containsString("Gautvin Marianne"),
						containsString("Gallus Zakaria"), containsString("Norbert Thijmen"),
						containsString("Yusef Rubab"))
				.extract().response();
		List<Account> accounts = result.jsonPath().getList("$");
		assertThat(accounts, not(empty()));
		assertThat(accounts, hasSize(5));
	}
}
