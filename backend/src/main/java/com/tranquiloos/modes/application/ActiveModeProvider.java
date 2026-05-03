package com.tranquiloos.modes.application;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import com.tranquiloos.modes.api.ActiveModeSummaryResponse;
import com.tranquiloos.modes.domain.ModeActivationStatus;
import com.tranquiloos.modes.domain.ModePolicySnapshot;
import com.tranquiloos.modes.infrastructure.AdaptiveModeJpaRepository;
import com.tranquiloos.modes.infrastructure.ModeActivationEntity;
import com.tranquiloos.modes.infrastructure.ModeActivationJpaRepository;
import com.tranquiloos.shared.error.ResourceNotFoundException;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActiveModeProvider {

	private final CurrentUserProvider currentUserProvider;
	private final ModeActivationJpaRepository modeActivationRepository;
	private final AdaptiveModeJpaRepository adaptiveModeRepository;

	public ActiveModeProvider(
			CurrentUserProvider currentUserProvider,
			ModeActivationJpaRepository modeActivationRepository,
			AdaptiveModeJpaRepository adaptiveModeRepository) {
		this.currentUserProvider = currentUserProvider;
		this.modeActivationRepository = modeActivationRepository;
		this.adaptiveModeRepository = adaptiveModeRepository;
	}

	@Transactional(readOnly = true)
	public Optional<ModePolicySnapshot> currentPolicy() {
		Long userId = currentUserProvider.currentUserId();
		return modeActivationRepository.findFirstByUserIdAndStatusOrderByActivatedAtDesc(userId, ModeActivationStatus.ACTIVE)
				.map(this::toPolicySnapshot);
	}

	@Transactional(readOnly = true)
	public ActiveModeSummaryResponse currentSummary() {
		Long userId = currentUserProvider.currentUserId();
		return modeActivationRepository.findFirstByUserIdAndStatusOrderByActivatedAtDesc(userId, ModeActivationStatus.ACTIVE)
				.map(this::toSummary)
				.orElseGet(ActiveModeSummaryResponse::empty);
	}

	ModePolicySnapshot toPolicySnapshot(ModeActivationEntity activation) {
		var mode = adaptiveModeRepository.findById(activation.getModeId())
				.orElseThrow(() -> new ResourceNotFoundException("Adaptive mode was not found"));
		return new ModePolicySnapshot(
				activation.getId(),
				mode.getCode(),
				mode.getName(),
				activation.getIntensityLevel() == null ? mode.getIntensityLevel() : activation.getIntensityLevel(),
				mode.getSpendingPolicy(),
				mode.getAlertPolicy(),
				mode.getPurchasePolicy(),
				mode.getRoutinePolicy());
	}

	ActiveModeSummaryResponse toSummary(ModeActivationEntity activation) {
		var mode = adaptiveModeRepository.findById(activation.getModeId())
				.orElseThrow(() -> new ResourceNotFoundException("Adaptive mode was not found"));
		return new ActiveModeSummaryResponse(
				true,
				activation.getId(),
				mode.getCode(),
				mode.getName(),
				activation.getObjective(),
				activation.getIntensityLevel() == null ? mode.getIntensityLevel() : activation.getIntensityLevel(),
				mode.getSpendingPolicy(),
				mode.getAlertPolicy(),
				mode.getPurchasePolicy(),
				mode.getRoutinePolicy(),
				activation.getScenarioId(),
				activation.getActivatedAt(),
				activation.getExpiresAt(),
				daysRemaining(activation.getExpiresAt()),
				ModeGuidance.guidance(mode.getCode()));
	}

	Long daysRemaining(Instant expiresAt) {
		if (expiresAt == null) {
			return null;
		}
		long days = Duration.between(Instant.now(), expiresAt).toDays();
		return Math.max(days, 0);
	}
}
