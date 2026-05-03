package com.tranquiloos.users.api;

public record AuthResponse(
		String token,
		String tokenType,
		MeResponse user) {
}
