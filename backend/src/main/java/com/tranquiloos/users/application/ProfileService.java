package com.tranquiloos.users.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tranquiloos.users.api.ProfileResponse;
import com.tranquiloos.users.api.UpdateProfileRequest;
import com.tranquiloos.users.infrastructure.UserProfileEntity;
import com.tranquiloos.users.infrastructure.UserProfileRepository;

@Service
public class ProfileService {

	private final CurrentUserProvider currentUserProvider;
	private final UserProfileRepository profileRepository;

	public ProfileService(CurrentUserProvider currentUserProvider, UserProfileRepository profileRepository) {
		this.currentUserProvider = currentUserProvider;
		this.profileRepository = profileRepository;
	}

	@Transactional(readOnly = true)
	public ProfileResponse getCurrentProfile() {
		return toResponse(getOrCreateProfile(currentUserProvider.currentUserId()));
	}

	@Transactional
	public ProfileResponse updateCurrentProfile(UpdateProfileRequest request) {
		UserProfileEntity profile = getOrCreateProfile(currentUserProvider.currentUserId());
		profile.setDisplayName(request.displayName());
		profile.setCity(request.city());
		profile.setCurrency(request.currency() == null || request.currency().isBlank() ? "COP" : request.currency());
		profile.setMonthlyIncome(request.monthlyIncome());
		return toResponse(profileRepository.save(profile));
	}

	private UserProfileEntity getOrCreateProfile(Long userId) {
		return profileRepository.findById(userId).orElseGet(() -> profileRepository.save(new UserProfileEntity(userId)));
	}

	private ProfileResponse toResponse(UserProfileEntity profile) {
		return new ProfileResponse(profile.getDisplayName(), profile.getCity(), profile.getCurrency(), profile.getMonthlyIncome());
	}
}
