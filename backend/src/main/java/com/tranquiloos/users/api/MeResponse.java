package com.tranquiloos.users.api;

public record MeResponse(
		Long id,
		String email,
		String displayName,
		String city,
		String currency) {
}
