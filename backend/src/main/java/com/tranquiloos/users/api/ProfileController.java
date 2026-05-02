package com.tranquiloos.users.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tranquiloos.users.application.ProfileService;

@RestController
@RequestMapping("/api/v1/me/profile")
public class ProfileController {

	private final ProfileService profileService;

	public ProfileController(ProfileService profileService) {
		this.profileService = profileService;
	}

	@GetMapping
	ProfileResponse getProfile() {
		return profileService.getCurrentProfile();
	}

	@PutMapping
	ProfileResponse updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
		return profileService.updateCurrentProfile(request);
	}
}
