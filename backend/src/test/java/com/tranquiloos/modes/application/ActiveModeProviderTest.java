package com.tranquiloos.modes.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import com.tranquiloos.modes.domain.AlertPolicy;
import com.tranquiloos.modes.domain.IntensityLevel;
import com.tranquiloos.modes.domain.ModeActivationStatus;
import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.modes.domain.PurchasePolicy;
import com.tranquiloos.modes.domain.RoutinePolicy;
import com.tranquiloos.modes.domain.SpendingPolicy;
import com.tranquiloos.modes.infrastructure.AdaptiveModeEntity;
import com.tranquiloos.modes.infrastructure.AdaptiveModeJpaRepository;
import com.tranquiloos.modes.infrastructure.ModeActivationEntity;
import com.tranquiloos.modes.infrastructure.ModeActivationJpaRepository;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ActiveModeProviderTest {

	@Test
	void returnsActiveModeIfExists() {
		CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
		ModeActivationJpaRepository activationRepository = mock(ModeActivationJpaRepository.class);
		AdaptiveModeJpaRepository modeRepository = mock(AdaptiveModeJpaRepository.class);
		ActiveModeProvider provider = new ActiveModeProvider(currentUserProvider, activationRepository, modeRepository);
		when(currentUserProvider.currentUserId()).thenReturn(1L);
		when(activationRepository.findFirstByUserIdAndStatusOrderByActivatedAtDesc(1L, ModeActivationStatus.ACTIVE))
				.thenReturn(Optional.of(activation()));
		when(modeRepository.findById(1L)).thenReturn(Optional.of(mode()));

		var active = provider.currentPolicy();

		assertThat(active).isPresent();
		assertThat(active.get().modeCode()).isEqualTo(ModeCode.WAR_MODE);
	}

	@Test
	void returnsEmptyIfNoActiveMode() {
		CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
		ModeActivationJpaRepository activationRepository = mock(ModeActivationJpaRepository.class);
		AdaptiveModeJpaRepository modeRepository = mock(AdaptiveModeJpaRepository.class);
		ActiveModeProvider provider = new ActiveModeProvider(currentUserProvider, activationRepository, modeRepository);
		when(currentUserProvider.currentUserId()).thenReturn(1L);
		when(activationRepository.findFirstByUserIdAndStatusOrderByActivatedAtDesc(1L, ModeActivationStatus.ACTIVE))
				.thenReturn(Optional.empty());

		assertThat(provider.currentPolicy()).isEmpty();
		assertThat(provider.currentSummary().hasActiveMode()).isFalse();
	}

	@Test
	void calculatesDaysRemaining() {
		ActiveModeProvider provider = new ActiveModeProvider(null, null, null);

		assertThat(provider.daysRemaining(Instant.now().plusSeconds(3 * 24 * 60 * 60))).isBetween(2L, 3L);
		assertThat(provider.daysRemaining(Instant.now().minusSeconds(10))).isZero();
	}

	private AdaptiveModeEntity mode() {
		AdaptiveModeEntity mode = new AdaptiveModeEntity();
		ReflectionTestUtils.setField(mode, "id", 1L);
		ReflectionTestUtils.setField(mode, "code", ModeCode.WAR_MODE);
		ReflectionTestUtils.setField(mode, "name", "Modo Guerra");
		ReflectionTestUtils.setField(mode, "intensityLevel", IntensityLevel.HIGH);
		ReflectionTestUtils.setField(mode, "spendingPolicy", SpendingPolicy.STRICT);
		ReflectionTestUtils.setField(mode, "alertPolicy", AlertPolicy.STRICT);
		ReflectionTestUtils.setField(mode, "purchasePolicy", PurchasePolicy.FREEZE_NON_ESSENTIAL);
		ReflectionTestUtils.setField(mode, "routinePolicy", RoutinePolicy.STRICT);
		return mode;
	}

	private ModeActivationEntity activation() {
		ModeActivationEntity activation = new ModeActivationEntity();
		ReflectionTestUtils.setField(activation, "id", 1L);
		activation.setUserId(1L);
		activation.setModeId(1L);
		activation.setStatus(ModeActivationStatus.ACTIVE);
		activation.setActivatedAt(Instant.now());
		return activation;
	}
}
