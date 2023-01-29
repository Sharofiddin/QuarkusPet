package quarkus.transaction;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;

import java.math.BigDecimal;
import java.util.List;

import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySource;
import quarkus.transaction.events.AccountFee;
import quarkus.transaction.object.Transaction;

@QuarkusTest
@QuarkusTestResource(WiremockAccountService.class)
@QuarkusTestResource(InMemoryLifecycleManager.class)
@QuarkusTestResource(value = H2DatabaseTestResource.class)
class OverdraftFeeEventTest {

	@Inject
	@Any
	InMemoryConnector connector;

	@Test
	void testOverdraftFeeEvent() {
		InMemorySource<AccountFee> source = connector.source("overdraft-fee");
		AccountFee accountFee = new AccountFee();
		accountFee.setAccountNumber(121212L);
		accountFee.setOverdraftFee(BigDecimal.valueOf(15.00d));
		Response result = RestAssured.given().get("/transactions/").then().statusCode(200).extract().response();
		List<Transaction> transactions = result.jsonPath().getList("$", Transaction.class);
		int cnt = transactions.size();
		source.send(accountFee);
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		result = RestAssured.given().get("/transactions/").then().statusCode(200).extract().response();
		transactions = result.jsonPath().getList("$", Transaction.class);
		assertThat(transactions, CoreMatchers.is(notNullValue()));
		assertThat(transactions, Matchers.hasSize(cnt + 1));
		Transaction transaction = transactions.get(transactions.size()-1);

		assertThat(transaction.getAccountNumber(), equalTo(121212L));
		assertThat(transaction.getAmount(), comparesEqualTo(BigDecimal.valueOf(-15.00d)));
	}

}
