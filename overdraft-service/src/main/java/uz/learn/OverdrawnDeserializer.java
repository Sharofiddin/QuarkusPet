package uz.learn;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import uz.learn.events.Overdrawn;

public class OverdrawnDeserializer extends JsonbDeserializer<Overdrawn> {

	public OverdrawnDeserializer() {
		super(Overdrawn.class);
	}
}
