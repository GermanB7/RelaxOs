package com.tranquiloos.shared.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

	private final JwtService jwtService = new JwtService(
			new ObjectMapper(),
			"test_secret_for_jwt_service_32_chars",
			1440);

	@Test
	void tokenGeneratedAndValidates() {
		String token = jwtService.generateToken(42L, "user@example.com");

		AuthenticatedUser user = jwtService.validateAndExtract(token);

		assertThat(token).contains(".");
		assertThat(user.id()).isEqualTo(42L);
		assertThat(user.email()).isEqualTo("user@example.com");
	}
}
