package com.tranquiloos.users.application;

import org.springframework.stereotype.Component;

import com.tranquiloos.shared.error.ResourceNotFoundException;
import com.tranquiloos.users.infrastructure.AppUserRepository;

@Component
public class CurrentUserProvider {

	private static final String LOCAL_USER_EMAIL = "local@tranquiloos.dev";

	private final AppUserRepository appUserRepository;

	public CurrentUserProvider(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	public Long currentUserId() {
		return appUserRepository.findByEmail(LOCAL_USER_EMAIL)
				.orElseThrow(() -> new ResourceNotFoundException("Local user seed was not found"))
				.getId();
	}
}
