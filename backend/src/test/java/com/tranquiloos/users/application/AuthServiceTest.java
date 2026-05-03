package com.tranquiloos.users.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.shared.error.ResourceConflictException;
import com.tranquiloos.shared.error.UnauthorizedException;
import com.tranquiloos.shared.security.JwtService;
import com.tranquiloos.users.api.LoginRequest;
import com.tranquiloos.users.api.RegisterRequest;
import com.tranquiloos.users.domain.UserStatus;
import com.tranquiloos.users.infrastructure.AppUserEntity;
import com.tranquiloos.users.infrastructure.AppUserRepository;
import com.tranquiloos.users.infrastructure.UserProfileEntity;
import com.tranquiloos.users.infrastructure.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private AppUserRepository userRepository;

	@Mock
	private UserProfileRepository profileRepository;

	@Mock
	private CurrentUserProvider currentUserProvider;

	private PasswordEncoder passwordEncoder;
	private AuthService authService;

	@BeforeEach
	void setUp() {
		passwordEncoder = new BCryptPasswordEncoder();
		authService = new AuthService(
				userRepository,
				profileRepository,
				passwordEncoder,
				new JwtService(new ObjectMapper(), "test_secret_for_auth_service_32_chars", 1440),
				currentUserProvider);
	}

	@Test
	void registerCreatesUserWithHashedPassword() {
		when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
		when(userRepository.save(any(AppUserEntity.class))).thenAnswer(invocation -> {
			AppUserEntity user = invocation.getArgument(0);
			ReflectionTestUtils.setField(user, "id", 10L);
			return user;
		});
		when(profileRepository.save(any(UserProfileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		var response = authService.register(new RegisterRequest("USER@example.com", "strong-password", "Juan", "Bogota", null));

		assertThat(response.token()).isNotBlank();
		assertThat(response.user().id()).isEqualTo(10L);
		assertThat(response.user().email()).isEqualTo("user@example.com");
	}

	@Test
	void duplicateEmailFails() {
		when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(true);

		assertThatThrownBy(() -> authService.register(new RegisterRequest("user@example.com", "strong-password", null, null, null)))
				.isInstanceOf(ResourceConflictException.class);
	}

	@Test
	void loginWithValidCredentialsReturnsToken() {
		AppUserEntity user = user("user@example.com", passwordEncoder.encode("strong-password"), UserStatus.ACTIVE);
		when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));
		when(userRepository.save(user)).thenReturn(user);
		when(profileRepository.findById(10L)).thenReturn(Optional.of(profile(10L)));

		var response = authService.login(new LoginRequest("user@example.com", "strong-password"));

		assertThat(response.token()).isNotBlank();
		assertThat(user.getLastLoginAt()).isNotNull();
	}

	@Test
	void loginInvalidPasswordFails() {
		AppUserEntity user = user("user@example.com", passwordEncoder.encode("strong-password"), UserStatus.ACTIVE);
		when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> authService.login(new LoginRequest("user@example.com", "wrong-password")))
				.isInstanceOf(UnauthorizedException.class);
	}

	@Test
	void inactiveUserCannotLogin() {
		AppUserEntity user = user("user@example.com", passwordEncoder.encode("strong-password"), UserStatus.INACTIVE);
		when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> authService.login(new LoginRequest("user@example.com", "strong-password")))
				.isInstanceOf(UnauthorizedException.class);
	}

	private AppUserEntity user(String email, String passwordHash, UserStatus status) {
		AppUserEntity user = new AppUserEntity();
		ReflectionTestUtils.setField(user, "id", 10L);
		user.setEmail(email);
		user.setPasswordHash(passwordHash);
		user.setStatus(status);
		return user;
	}

	private UserProfileEntity profile(Long userId) {
		UserProfileEntity profile = new UserProfileEntity(userId);
		profile.setDisplayName("Juan");
		profile.setCity("Bogota");
		profile.setCurrency("COP");
		return profile;
	}
}
