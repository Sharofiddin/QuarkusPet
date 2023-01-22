package uz.leran;

import uz.leran.events.Overdrawn;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class OverdrawnDeserializer extends JsonbDeserializer<Overdrawn> {

	public OverdrawnDeserializer() {
		super(Overdrawn.class);
	}
}
