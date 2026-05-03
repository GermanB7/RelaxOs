package com.tranquiloos.users.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Email @Size(max = 255) String email,
		@NotBlank @Size(min = 8, max = 120) String password,
		@Size(max = 120) String displayName,
		@Size(max = 120) String city,
		@Size(max = 10) String currency) {
}
