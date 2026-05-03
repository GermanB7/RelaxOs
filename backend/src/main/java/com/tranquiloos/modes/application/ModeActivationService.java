package com.tranquiloos.modes.application;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.modes.api.ActivateModeRequest;
import com.tranquiloos.modes.api.ActiveModeSummaryResponse;
import com.tranquiloos.modes.api.EndModeRequest;
import com.tranquiloos.modes.api.ModeActivationResponse;
import com.tranquiloos.modes.domain.ModeActivationStatus;
import com.tranquiloos.modes.infrastructure.AdaptiveModeEntity;
import com.tranquiloos.modes.infrastructure.AdaptiveModeJpaRepository;
import com.tranquiloos.modes.infrastructure.ModeActivationEntity;
import com.tranquiloos.modes.infrastructure.ModeActivationJpaRepository;
import com.tranquiloos.recommendations.infrastructure.DecisionEventEntity;
import com.tranquiloos.recommendations.infrastructure.DecisionEventJpaRepository;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.shared.error.ResourceNotFoundException;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModeActivationService {

	private final CurrentUserProvider currentUserProvider;
	private final AdaptiveModeJpaRepository adaptiveModeRepository;
	private final ModeActivationJpaRepository modeActivationRepository;
	private final DecisionEventJpaRepository decisionEventRepository;
	private final ScenarioService scenarioService;
	private final ActiveModeProvider activeModeProvider;
	private final ObjectMapper objectMapper;

	public ModeActivationService(
			CurrentUserProvider currentUserProvider,
			AdaptiveModeJpaRepository adaptiveModeRepository,
			ModeActivationJpaRepository modeActivationRepository,
			DecisionEventJpaRepository decisionEventRepository,
			ScenarioService scenarioService,
			ActiveModeProvider activeModeProvider,
			ObjectMapper objectMapper) {
		this.currentUserProvider = currentUserProvider;
		this.adaptiveModeRepository = adaptiveModeRepository;
		this.modeActivationRepository = modeActivationRepository;
		this.decisionEventRepository = decisionEventRepository;
		this.scenarioService = scenarioService;
		this.activeModeProvider = activeModeProvider;
		this.objectMapper = objectMapper;
	}

	@Transactional(readOnly = true)
	public ActiveModeSummaryResponse activeMode() {
		return activeModeProvider.currentSummary();
	}

	@Transactional
	public ModeActivationResponse activate(ActivateModeRequest request) {
		Long userId = currentUserProvider.currentUserId();
		if (request.scenarioId() != null) {
			scenarioService.findCurrentUserScenario(request.scenarioId());
		}
		AdaptiveModeEntity mode = adaptiveModeRepository.findByCodeAndActiveTrue(request.modeCode())
				.orElseThrow(() -> new ResourceNotFoundException("Adaptive mode was not found"));
		ModeActivationEntity previous = modeActivationRepository
				.findFirstByUserIdAndStatusOrderByActivatedAtDesc(userId, ModeActivationStatus.ACTIVE)
				.orElse(null);
		String previousModeCode = null;
		if (previous != null) {
			previousModeCode = adaptiveModeRepository.findById(previous.getModeId())
					.map(previousMode -> previousMode.getCode().name())
					.orElse(null);
			endActivation(previous);
			modeActivationRepository.save(previous);
		}

		ModeActivationEntity activation = new ModeActivationEntity();
		activation.setUserId(userId);
		activation.setModeId(mode.getId());
		activation.setScenarioId(request.scenarioId());
		activation.setObjective(request.objective());
		activation.setIntensityLevel(request.intensityLevel() == null ? mode.getIntensityLevel() : request.intensityLevel());
		activation.setActivatedAt(Instant.now());
		activation.setExpiresAt(expiresAt(mode, request.durationDays()));
		activation.setStatus(ModeActivationStatus.ACTIVE);
		activation.setNotes(request.notes());
		ModeActivationEntity saved = modeActivationRepository.save(activation);
		Map<String, Object> context = new HashMap<>();
		context.put("modeCode", mode.getCode().name());
		context.put("activationId", saved.getId());
		context.put("durationDays", resolvedDuration(mode, request.durationDays()));
		context.put("previousModeCode", previousModeCode);
		createDecisionEvent(userId, saved, mode, "MODE_ACTIVATED", "Activate adaptive mode", mode.getCode().name(), request.objective(), context);
		return toResponse(saved, mode);
	}

	@Transactional
	public void endActive(EndModeRequest request) {
		Long userId = currentUserProvider.currentUserId();
		ModeActivationEntity activation = modeActivationRepository
				.findFirstByUserIdAndStatusOrderByActivatedAtDesc(userId, ModeActivationStatus.ACTIVE)
				.orElseThrow(() -> new ResourceNotFoundException("Active mode was not found"));
		AdaptiveModeEntity mode = adaptiveModeRepository.findById(activation.getModeId())
				.orElseThrow(() -> new ResourceNotFoundException("Adaptive mode was not found"));
		endActivation(activation);
		modeActivationRepository.save(activation);
		Map<String, Object> context = new HashMap<>();
		context.put("modeCode", mode.getCode().name());
		context.put("activationId", activation.getId());
		createDecisionEvent(userId, activation, mode, "MODE_ENDED", "End active adaptive mode", mode.getCode().name(), request == null ? null : request.reason(), context);
	}

	@Transactional(readOnly = true)
	public List<ModeActivationResponse> history(Long scenarioId) {
		Long userId = currentUserProvider.currentUserId();
		return (scenarioId == null
				? modeActivationRepository.findTop50ByUserIdOrderByActivatedAtDesc(userId)
				: modeActivationRepository.findTop50ByUserIdAndScenarioIdOrderByActivatedAtDesc(userId, scenarioId))
				.stream()
				.map(activation -> toResponse(activation, adaptiveModeRepository.findById(activation.getModeId())
						.orElseThrow(() -> new ResourceNotFoundException("Adaptive mode was not found"))))
				.toList();
	}

	private void endActivation(ModeActivationEntity activation) {
		activation.setStatus(ModeActivationStatus.ENDED);
		activation.setEndedAt(Instant.now());
	}

	private Instant expiresAt(AdaptiveModeEntity mode, Integer durationDays) {
		return Instant.now().plus(resolvedDuration(mode, durationDays), ChronoUnit.DAYS);
	}

	private int resolvedDuration(AdaptiveModeEntity mode, Integer durationDays) {
		if (durationDays != null) {
			return durationDays;
		}
		return mode.getRecommendedMaxDays() == null ? 14 : mode.getRecommendedMaxDays();
	}

	private ModeActivationResponse toResponse(ModeActivationEntity activation, AdaptiveModeEntity mode) {
		return new ModeActivationResponse(
				activation.getId(),
				mode.getCode(),
				mode.getName(),
				activation.getScenarioId(),
				activation.getObjective(),
				activation.getIntensityLevel() == null ? mode.getIntensityLevel() : activation.getIntensityLevel(),
				mode.getSpendingPolicy(),
				mode.getAlertPolicy(),
				mode.getPurchasePolicy(),
				mode.getRoutinePolicy(),
				activation.getStatus(),
				activation.getActivatedAt(),
				activation.getExpiresAt(),
				activation.getEndedAt(),
				activation.getNotes());
	}

	private void createDecisionEvent(
			Long userId,
			ModeActivationEntity activation,
			AdaptiveModeEntity mode,
			String decisionType,
			String question,
			String chosenOption,
			String reason,
			Map<String, Object> context) {
		DecisionEventEntity event = new DecisionEventEntity();
		event.setUserId(userId);
		event.setScenarioId(activation.getScenarioId());
		event.setRecommendationId(null);
		event.setDecisionType(decisionType);
		event.setQuestion(question);
		event.setChosenOption(chosenOption);
		event.setScoreBefore(null);
		event.setScoreAfter(null);
		event.setReason(reason);
		Map<String, Object> contextWithMode = new HashMap<>(context);
		contextWithMode.put("modeName", mode.getName());
		event.setContextJson(toJson(contextWithMode));
		decisionEventRepository.save(event);
	}

	private String toJson(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException exception) {
			return "{}";
		}
	}
}
