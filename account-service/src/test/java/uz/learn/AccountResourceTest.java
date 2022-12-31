package uz.learn;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.IsEmptyString.emptyString;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import uz.learn.objects.Account;
import uz.learn.objects.AccountStatus;

@QuarkusTest
@QuarkusTestResource(value = H2DatabaseTestResource.class)
@TestMethodOrder(OrderAnnotation.class)
class AccountResourceTest {

	private static final int SIZE = 6;

	@DisplayName("Retrieve all accounts")
	@Test
	@Order(1)
	void testRetrieveAll() {
		Response result = given().when().get("/accounts").then().statusCode(200).body(containsString("Frode Aleks"),
				containsString("Gautvin Marianne"), containsString("Gallus Zakaria"), containsString("Norbert Thijmen"))
				.extract().response();
		List<Account> accounts = result.jsonPath().getList("$");
		assertThat(accounts, not(empty()));
		assertThat(accounts, hasSize(SIZE));
	}

	@DisplayName("Get account by id")
	@Test
	@Order(2)
	void testGetAccountById() {
		Account account = given().when().get("/accounts/{accountNumber}", 777888999L).then().statusCode(200).extract()
				.as(Account.class);

		assertThat(account.getAccountNumber(), equalTo(777888999L));
		assertThat(account.getCustomerName(), equalTo("Gallus Zakaria"));
		assertThat(account.getBalance(), equalTo(BigDecimal.valueOf(480.56)));
		assertThat(account.getAccountStatus(), equalTo(AccountStatus.OPEN));
	}

	@DisplayName("Create account test")
	@Test
	@Order(3)
	void testCreateAccount() {
		Account newAccount = new Account();
		newAccount.setAccountNumber(88899977L);
		newAccount.setAccountStatus(AccountStatus.OPEN);
		newAccount.setBalance(BigDecimal.valueOf(100.05f));
		newAccount.setCustomerName("Kelechi Oghenekevwe");
		newAccount.setCustomerNumber(18144L);
		Account returnedAccount = given().contentType(ContentType.JSON).body(newAccount).post("/accounts").then()
				.statusCode(201).extract().as(Account.class);
		assertThat(returnedAccount, notNullValue());
		assertThat(returnedAccount, equalTo(newAccount));
		Response result = given().when().get("/accounts").then().statusCode(200)
				.body(containsString("Frode Aleks"), containsString("Gautvin Marianne"),
						containsString("Gallus Zakaria"), containsString("Norbert Thijmen"),
						containsString("Yusef Rubab"))
				.extract().response();
		List<Account> accounts = result.jsonPath().getList("$");
		assertThat(accounts, not(empty()));
		assertThat(accounts, hasSize(SIZE+1));
	}

	@DisplayName("Delete account test")
	@Test
	@Order(4)
	void testDeleteAccount() {
		Response response = given().delete("/accounts/{accountNumber}", 88899977L).then().statusCode(204).extract()
				.response();
		assertThat(response.getBody().asString(), emptyString());
		Response result = given().when().get("/accounts").then().statusCode(200)
				.body(containsString("Frode Aleks"), containsString("Gautvin Marianne"),
						containsString("Gallus Zakaria"), containsString("Norbert Thijmen"),
						not(containsString("Kelechi Oghenekevwe")))
				.extract().response();
		List<Account> accounts = result.jsonPath().getList("$");
		assertThat(accounts, not(empty()));
		assertThat(accounts, hasSize(SIZE));
	}

	@DisplayName("withdrawal account test")
	@Test
	@Order(5)
	void testAccountWithdrawal() {
		Account account = given().body("25").put("/accounts/{accountNumber}/withdrawal", 123456789L).then().statusCode(200)
				.extract().as(Account.class);
		assertThat(account.getBalance(), comparesEqualTo(BigDecimal.valueOf(525.78)));
		Account accountAfterWithdrawal = given().when().get("/accounts/{accountNumber}", 123456789L).then().statusCode(200)
				.extract().as(Account.class);
		assertThat(account.getBalance(), comparesEqualTo(accountAfterWithdrawal.getBalance()));
	}

	@DisplayName("Deposit account test")
	@Test
	@Order(6)
	void testAccountDeposit() {
		Account account = given().body("25").put("/accounts/{accountNumber}/deposit", 123456789L).then().statusCode(200)
				.extract().as(Account.class);
		assertThat(account.getBalance(), comparesEqualTo(BigDecimal.valueOf(550.78)));
		Account accountAfterWithdrawal = given().when().get("/accounts/{accountNumber}", 123456789L).then().statusCode(200)
				.extract().as(Account.class);
		assertThat(account.getBalance(), comparesEqualTo(accountAfterWithdrawal.getBalance()));
	}

}
