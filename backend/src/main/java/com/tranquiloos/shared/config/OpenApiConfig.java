package com.tranquiloos.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {

	@Bean
	OpenAPI tranquiloosOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("TranquiloOS API")
						.version("0.0.1")
						.description("Foundation API for the TranquiloOS MVP."));
	}
}
