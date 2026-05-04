package com.tranquiloos.decisions.application;

import java.util.List;

import com.tranquiloos.decisions.api.CreateDecisionEventRequest;
import com.tranquiloos.decisions.api.DecisionEventResponse;
import com.tranquiloos.decisions.domain.DecisionType;
import com.tranquiloos.recommendations.infrastructure.DecisionEventEntity;
import com.tranquiloos.recommendations.infrastructure.DecisionEventJpaRepository;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.shared.error.ResourceNotFoundException;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DecisionEventService {

	private final CurrentUserProvider currentUserProvider;
	private final ScenarioService scenarioService;
	private final DecisionEventJpaRepository decisionEventRepository;

	public DecisionEventService(
			CurrentUserProvider currentUserProvider,
			ScenarioService scenarioService,
			DecisionEventJpaRepository decisionEventRepository) {
		this.currentUserProvider = currentUserProvider;
		this.scenarioService = scenarioService;
		this.decisionEventRepository = decisionEventRepository;
	}

	@Transactional(readOnly = true)
	public List<DecisionEventResponse> listCurrentUserEvents() {
		return decisionEventRepository.findTop50ByUserIdOrderByCreatedAtDesc(currentUserProvider.currentUserId())
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public DecisionEventResponse getCurrentUserEvent(Long id) {
		return toResponse(decisionEventRepository.findByIdAndUserId(id, currentUserProvider.currentUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Decision event was not found")));
	}

	@Transactional(readOnly = true)
	public List<DecisionEventResponse> listCurrentUserScenarioEvents(Long scenarioId) {
		scenarioService.findCurrentUserScenario(scenarioId);
		return decisionEventRepository.findTop50ByUserIdAndScenarioIdOrderByCreatedAtDesc(currentUserProvider.currentUserId(), scenarioId)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public DecisionEventResponse createManualEvent(CreateDecisionEventRequest request) {
		Long userId = currentUserProvider.currentUserId();
		if (request.scenarioId() != null) {
			scenarioService.findCurrentUserScenario(request.scenarioId());
		}
		return toResponse(saveEvent(
				userId,
				request.scenarioId(),
				null,
				request.decisionType(),
				request.question(),
				request.chosenOption(),
				request.scoreBefore(),
				request.scoreAfter(),
				request.reason(),
				request.contextJson()));
	}

	@Transactional
	public DecisionEventEntity saveEvent(
			Long userId,
			Long scenarioId,
			Long recommendationId,
			DecisionType decisionType,
			String question,
			String chosenOption,
			Integer scoreBefore,
			Integer scoreAfter,
			String reason,
			String contextJson) {
		DecisionEventEntity event = new DecisionEventEntity();
		event.setUserId(userId);
		event.setScenarioId(scenarioId);
		event.setRecommendationId(recommendationId);
		event.setDecisionType(decisionType.name());
		event.setQuestion(question);
		event.setChosenOption(chosenOption);
		event.setScoreBefore(scoreBefore);
		event.setScoreAfter(scoreAfter);
		event.setReason(reason);
		event.setContextJson(contextJson);
		return decisionEventRepository.save(event);
	}

	public DecisionEventResponse toResponse(DecisionEventEntity entity) {
		return new DecisionEventResponse(
				entity.getId(),
				entity.getUserId(),
				entity.getScenarioId(),
				entity.getRecommendationId(),
				entity.getDecisionType(),
				entity.getQuestion(),
				entity.getChosenOption(),
				entity.getScoreBefore(),
				entity.getScoreAfter(),
				entity.getReason(),
				entity.getContextJson(),
				entity.getCreatedAt());
	}
}
