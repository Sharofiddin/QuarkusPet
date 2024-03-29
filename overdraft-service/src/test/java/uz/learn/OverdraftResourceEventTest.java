package uz.learn;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySink;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySource;
import uz.learn.events.AccountFee;
import uz.learn.events.Overdrawn;

@QuarkusTestResource(InmemoryLifecyclemanager.class)
@QuarkusTest
class OverdraftResourceEventTest {

	@Inject
	@Any
	InMemoryConnector connector;

	@Test
	void testOverdraftEvent() {
		InMemorySource<Overdrawn> overdraftSource = connector.source("account-overdrawn");
		InMemorySink<AccountFee> overdraftSink = connector.sink("overdraft-fee");

		Overdrawn overdrawn = new Overdrawn();
		overdrawn.setAccountNumber(121212L);
		overdrawn.setCustomerNumber(212121L);
		overdrawn.setBalance(BigDecimal.valueOf(-185.00d));
		overdrawn.setOverdraftLimit(BigDecimal.valueOf(-200.00d));

		overdraftSource.send(overdrawn);

		await().atMost(3, TimeUnit.SECONDS).until(() -> overdraftSink.received().size() == 1);

		Message<AccountFee> message = overdraftSink.received().get(0);
		assertThat(message, Matchers.notNullValue());
		AccountFee payload = message.getPayload();
		assertThat(payload, Matchers.notNullValue());
		
		assertThat(payload.getAccountNumber(), equalTo(121212L));
		assertThat(payload.getOverdraftFee(), equalTo(new BigDecimal("15.00")));
		
		overdrawn = new Overdrawn();
		overdrawn.setAccountNumber(343434L);
		overdrawn.setCustomerNumber(434343L);
		overdrawn.setBalance(BigDecimal.valueOf(-98.00d));
		overdrawn.setOverdraftLimit(BigDecimal.valueOf(-200.00d));

		overdraftSource.send(overdrawn);

		await().atMost(3, TimeUnit.SECONDS).until(() -> overdraftSink.received().size() == 2);

		message = overdraftSink.received().get(1);
		assertThat(message, Matchers.notNullValue());
		payload = message.getPayload();
		assertThat(payload, Matchers.notNullValue());
		
		assertThat(payload.getAccountNumber(), equalTo(343434L));
		assertThat(payload.getOverdraftFee(), equalTo(new BigDecimal("15.00")));
		
		overdrawn = new Overdrawn();
		overdrawn.setAccountNumber(121212L);
		overdrawn.setCustomerNumber(212121L);
		overdrawn.setBalance(BigDecimal.valueOf(-80.00d));
		overdrawn.setOverdraftLimit(BigDecimal.valueOf(-200.00d));

		overdraftSource.send(overdrawn);

		await().atMost(3, TimeUnit.SECONDS).until(() -> overdraftSink.received().size() == 3);

		message = overdraftSink.received().get(2);
		assertThat(message, Matchers.notNullValue());
		payload = message.getPayload();
		assertThat(payload, Matchers.notNullValue());
		
		assertThat(payload.getAccountNumber(), equalTo(121212L));
		assertThat(payload.getOverdraftFee(), equalTo(new BigDecimal("30.00")));
		
		overdrawn = new Overdrawn();
		overdrawn.setAccountNumber(343434L);
		overdrawn.setCustomerNumber(212121L);
		overdrawn.setBalance(BigDecimal.valueOf(-80.00d));
		overdrawn.setOverdraftLimit(BigDecimal.valueOf(-200.00d));

		overdraftSource.send(overdrawn);

		await().atMost(3, TimeUnit.SECONDS).until(() -> overdraftSink.received().size() == 4);

		message = overdraftSink.received().get(3);
		assertThat(message, Matchers.notNullValue());
		payload = message.getPayload();
		assertThat(payload, Matchers.notNullValue());
		
		assertThat(payload.getAccountNumber(), equalTo(343434L));
		assertThat(payload.getOverdraftFee(), equalTo(new BigDecimal("35.00")));
	}

}
