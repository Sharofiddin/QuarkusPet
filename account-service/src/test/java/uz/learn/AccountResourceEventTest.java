package uz.learn;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;

import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySink;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySource;
import uz.learn.objects.Account;
import uz.learn.objects.AccountStatus;
import uz.learn.objects.OverdraftLimitUpdate;
import uz.learn.objects.Overdrawn;

@QuarkusTest
@QuarkusTestResource(InMemoryLifeCycleManager.class)
class AccountResourceEventTest {

	@Inject
	@Any
	InMemoryConnector connector;

	@Test
	void testOverdraftEvent() {
		InMemorySink<Overdrawn> overdraftSink = connector.sink("account-overdrawn");

		Account account = given().get("/accounts/{accountNumber}", 777888999).then().statusCode(200).extract()
				.as(Account.class);
		BigDecimal withdrawal = new BigDecimal("23.82");
		BigDecimal balance = account.withdrawFunds(withdrawal);

		account = given().contentType(ContentType.JSON).body(withdrawal.toString()).when()
				.put("/accounts/{accountNumber}/withdrawal", 777888999).then().statusCode(200).extract()
				.as(Account.class);

		assertThat(overdraftSink.received().size(), equalTo(0));

		withdrawal = new BigDecimal(6000);
		balance = account.withdrawFunds(withdrawal);

		account = given().contentType(ContentType.JSON).body(withdrawal.toString()).when()
				.put("/accounts/{accounNumber}/withdrawal", 777888999).then().statusCode(200).extract()
				.as(Account.class);

		assertThat(account.getAccountStatus(), equalTo(AccountStatus.OVERDRAWN));
		assertThat(account.getBalance(), equalTo(balance));

		assertThat(overdraftSink.received().size(), equalTo(1));

		Message<Overdrawn> message = overdraftSink.received().get(0);
		assertThat(message, notNullValue());
		Overdrawn event = message.getPayload();

		assertThat(event.getAccountNumber(), equalTo(777888999L));
		assertThat(event.getCustomerNumber(), equalTo(98765L));
		assertThat(event.getBalance(), equalTo(balance));
		assertThat(event.getOverdraftLimit(), equalTo(new BigDecimal("-200.00")));
	}

	@Test
	void testOverdraftUpdate() {
		InMemorySource<OverdraftLimitUpdate> source = connector.source("overdraft-update");

		Account account = given().get("/accounts/{accountNumber}", 777888999L)
				 .then().statusCode(200).extract()
				.as(Account.class);
		assertThat(account.getOverdraftLimit(), equalTo(new BigDecimal("-200.00")));
		
		OverdraftLimitUpdate limitUpdate = new OverdraftLimitUpdate();
		limitUpdate.setAccountNumber(777888999L);
		limitUpdate.setNewOverDraftLimit(new BigDecimal("-600.00"));
		source.send(limitUpdate);
		
		account = given().get("/accounts/{accountNumber}", 777888999L)
				 .then().statusCode(200).extract()
				.as(Account.class);
		
		assertThat(account.getOverdraftLimit(), equalTo(new BigDecimal("-600.00")));
		
	}
}
