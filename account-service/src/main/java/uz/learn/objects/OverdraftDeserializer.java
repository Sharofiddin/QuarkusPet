package uz.learn.objects;

import java.lang.reflect.Type;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class OverdraftDeserializer implements JsonbDeserializer<OverdraftLimitUpdate> {
	
	@Override
	public OverdraftLimitUpdate deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
		OverdraftLimitUpdate result = new OverdraftLimitUpdate();

		while (parser.hasNext()) {
			Event event = parser.next();
			if (event == JsonParser.Event.KEY_NAME && parser.getString().equals("accountNumber")) {
				parser.next();
				result.setAccountNumber(parser.getLong());
			} else if (event == JsonParser.Event.KEY_NAME && parser.getString().equals("newOverDraftLimit")) {
				parser.next();
				result.setNewOverDraftLimit(parser.getBigDecimal());
			}
		}

		return result;
	}

}
