package com.tranquiloos.shared.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
class CorsConfig {

	@Bean
	CorsConfigurationSource corsConfigurationSource(
			@Value("${app.cors.allowed-origins:http://localhost:5173}") String allowedOrigins,
			Environment environment) {
		List<String> parsedOrigins = parseCsv(allowedOrigins);
		if (isProd(environment) && parsedOrigins.contains("*")) {
			throw new IllegalStateException("Wildcard CORS origins are not allowed in prod");
		}
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(parsedOrigins);
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(false);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	private List<String> parseCsv(String value) {
		return Arrays.stream(value.split(","))
				.map(String::trim)
				.filter(origin -> !origin.isBlank())
				.toList();
	}

	private boolean isProd(Environment environment) {
		return Arrays.asList(environment.getActiveProfiles()).contains("prod");
	}
}
