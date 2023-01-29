package quarkus.transaction;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import quarkus.transaction.events.AccountFee;

public class OverdraftFeeDeserializer extends JsonbDeserializer<AccountFee> {
	public OverdraftFeeDeserializer() {
		super(AccountFee.class);
	}
}
