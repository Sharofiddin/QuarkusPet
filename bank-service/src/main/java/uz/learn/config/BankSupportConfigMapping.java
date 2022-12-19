package uz.learn.config;

import javax.validation.constraints.Size;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "bank-support-mapping")
public interface BankSupportConfigMapping {

	@Size(min = 12, max = 12)
	String phone();

	String email();

	Business business();

	interface Business {

		@Size(min = 12, max = 12)
		String phone();

		String email();
	}
}
