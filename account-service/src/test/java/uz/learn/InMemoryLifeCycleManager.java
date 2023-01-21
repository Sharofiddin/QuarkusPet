package uz.learn;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;

public class InMemoryLifeCycleManager implements QuarkusTestResourceLifecycleManager {

	@Override
	public Map<String, String> start() {
		Map<String, String> env = new HashMap<>();
		env.putAll(InMemoryConnector.switchIncomingChannelsToInMemory("overdraft-update"));
		env.putAll(InMemoryConnector.switchOutgoingChannelsToInMemory("account-overdrawn"));
		return env;
	}

	@Override
	public void stop() {
		InMemoryConnector.clear();
	}

}
