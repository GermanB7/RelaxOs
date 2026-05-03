package com.tranquiloos.users.application;

import java.time.Instant;
import java.util.Locale;

import com.tranquiloos.shared.error.ResourceConflictException;
import com.tranquiloos.shared.error.UnauthorizedException;
import com.tranquiloos.shared.security.JwtService;
import com.tranquiloos.users.api.AuthResponse;
import com.tranquiloos.users.api.LoginRequest;
import com.tranquiloos.users.api.MeResponse;
import com.tranquiloos.users.api.RegisterRequest;
import com.tranquiloos.users.domain.UserStatus;
import com.tranquiloos.users.infrastructure.AppUserEntity;
import com.tranquiloos.users.infrastructure.AppUserRepository;
import com.tranquiloos.users.infrastructure.UserProfileEntity;
import com.tranquiloos.users.infrastructure.UserProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private final AppUserRepository userRepository;
	private final UserProfileRepository profileRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final CurrentUserProvider currentUserProvider;

	public AuthService(
			AppUserRepository userRepository,
			UserProfileRepository profileRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService,
			CurrentUserProvider currentUserProvider) {
		this.userRepository = userRepository;
		this.profileRepository = profileRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.currentUserProvider = currentUserProvider;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String email = normalizeEmail(request.email());
		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new ResourceConflictException("Email is already registered");
		}
		AppUserEntity user = new AppUserEntity();
		user.setEmail(email);
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setStatus(UserStatus.ACTIVE);
		user.setAuthProvider("LOCAL");
		AppUserEntity savedUser = userRepository.save(user);

		UserProfileEntity profile = new UserProfileEntity(savedUser.getId());
		profile.setDisplayName(request.displayName());
		profile.setCity(request.city());
		profile.setCurrency(defaultCurrency(request.currency()));
		profileRepository.save(profile);

		return authResponse(savedUser, profile);
	}

	@Transactional
	public AuthResponse login(LoginRequest request) {
		AppUserEntity user = userRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
				.orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
		if (user.getStatus() != UserStatus.ACTIVE || user.getPasswordHash() == null
				|| !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new UnauthorizedException("Invalid email or password");
		}
		user.setLastLoginAt(Instant.now());
		AppUserEntity savedUser = userRepository.save(user);
		UserProfileEntity profile = profileRepository.findById(savedUser.getId())
				.orElseGet(() -> profileRepository.save(new UserProfileEntity(savedUser.getId())));
		return authResponse(savedUser, profile);
	}

	@Transactional(readOnly = true)
	public MeResponse me() {
		Long userId = currentUserProvider.currentUserId();
		AppUserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new UnauthorizedException("Authenticated user was not found"));
		UserProfileEntity profile = profileRepository.findById(userId)
				.orElse(new UserProfileEntity(userId));
		return toMeResponse(user, profile);
	}

	private AuthResponse authResponse(AppUserEntity user, UserProfileEntity profile) {
		return new AuthResponse(jwtService.generateToken(user.getId(), user.getEmail()), "Bearer", toMeResponse(user, profile));
	}

	private MeResponse toMeResponse(AppUserEntity user, UserProfileEntity profile) {
		return new MeResponse(user.getId(), user.getEmail(), profile.getDisplayName(), profile.getCity(), profile.getCurrency());
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private String defaultCurrency(String currency) {
		return currency == null || currency.isBlank() ? "COP" : currency;
	}
}
