package uz.learn;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import uz.learn.config.BankSupportConfig;

@Path("/bank")
public class BankResource {

	@ConfigProperty(name = "bank.name", defaultValue = "Bank of Default")
	private String name;

	@ConfigProperty(name = "app.mobileBanking")
	private Optional<Boolean> mobileBanking;

	@ConfigProperties(prefix = "bank-support")
	BankSupportConfig bankSupportConfig;

	@GET
	@Path("/name")
	@Produces(MediaType.TEXT_PLAIN)
	public String name() {
		return name;
	}

	@GET
	@Path("/mobileBanking")
	@Produces(MediaType.TEXT_PLAIN)
	public Boolean mobileBnaking() {
		return mobileBanking.orElse(false);
	}

	@GET
	@Path("/support")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> support() {
		Map<String, String> support = new HashMap<>();
		support.put("email", bankSupportConfig.email);
		support.put("phone", bankSupportConfig.getPhone());
		return support;
	}
}