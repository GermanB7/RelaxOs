package com.tranquiloos.recommendations.application;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.recommendations.api.RecommendationActionRequest;
import com.tranquiloos.recommendations.api.RecommendationResponse;
import com.tranquiloos.recommendations.domain.RecommendationStatus;
import com.tranquiloos.recommendations.infrastructure.DecisionEventEntity;
import com.tranquiloos.recommendations.infrastructure.DecisionEventJpaRepository;
import com.tranquiloos.recommendations.infrastructure.RecommendationEntity;
import com.tranquiloos.recommendations.infrastructure.RecommendationJpaRepository;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotRepository;
import com.tranquiloos.shared.error.ResourceNotFoundException;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecommendationActionService {

	private final CurrentUserProvider currentUserProvider;
	private final RecommendationJpaRepository recommendationRepository;
	private final DecisionEventJpaRepository decisionEventRepository;
	private final ScoreSnapshotRepository scoreSnapshotRepository;
	private final RecommendationMapper mapper;
	private final ObjectMapper objectMapper;

	public RecommendationActionService(
			CurrentUserProvider currentUserProvider,
			RecommendationJpaRepository recommendationRepository,
			DecisionEventJpaRepository decisionEventRepository,
			ScoreSnapshotRepository scoreSnapshotRepository,
			RecommendationMapper mapper,
			ObjectMapper objectMapper) {
		this.currentUserProvider = currentUserProvider;
		this.recommendationRepository = recommendationRepository;
		this.decisionEventRepository = decisionEventRepository;
		this.scoreSnapshotRepository = scoreSnapshotRepository;
		this.mapper = mapper;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public RecommendationResponse accept(Long recommendationId, RecommendationActionRequest request) {
		return transition(recommendationId, RecommendationStatus.ACCEPTED, "RECOMMENDATION_ACCEPTED", request);
	}

	@Transactional
	public RecommendationResponse postpone(Long recommendationId, RecommendationActionRequest request) {
		return transition(recommendationId, RecommendationStatus.POSTPONED, "RECOMMENDATION_POSTPONED", request);
	}

	@Transactional
	public RecommendationResponse dismiss(Long recommendationId, RecommendationActionRequest request) {
		return transition(recommendationId, RecommendationStatus.DISMISSED, "RECOMMENDATION_DISMISSED", request);
	}

	private RecommendationResponse transition(
			Long recommendationId,
			RecommendationStatus newStatus,
			String decisionType,
			RecommendationActionRequest request) {
		Long userId = currentUserProvider.currentUserId();
		RecommendationEntity recommendation = recommendationRepository.findByIdAndUserId(recommendationId, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Recommendation was not found"));
		RecommendationStatus previousStatus = recommendation.getStatus();
		recommendation.setStatus(newStatus);
		RecommendationEntity saved = recommendationRepository.save(recommendation);
		createDecisionEvent(saved, previousStatus, newStatus, decisionType, request == null ? null : request.reason());
		return mapper.toResponse(saved);
	}

	private void createDecisionEvent(
			RecommendationEntity recommendation,
			RecommendationStatus previousStatus,
			RecommendationStatus newStatus,
			String decisionType,
			String reason) {
		DecisionEventEntity event = new DecisionEventEntity();
		event.setUserId(recommendation.getUserId());
		event.setScenarioId(recommendation.getScenarioId());
		event.setRecommendationId(recommendation.getId());
		event.setDecisionType(decisionType);
		event.setQuestion(recommendation.getTitle());
		event.setChosenOption(newStatus.name());
		event.setScoreBefore(scoreBefore(recommendation));
		event.setScoreAfter(null);
		event.setReason(reason);
		event.setContextJson(toJson(Map.of(
				"recommendationId", recommendation.getId(),
				"sourceRuleKey", recommendation.getSourceRuleKey(),
				"actionType", recommendation.getActionType(),
				"previousStatus", previousStatus.name(),
				"newStatus", newStatus.name())));
		decisionEventRepository.save(event);
	}

	private Integer scoreBefore(RecommendationEntity recommendation) {
		if (recommendation.getScoreSnapshotId() == null) {
			return null;
		}
		return scoreSnapshotRepository.findById(recommendation.getScoreSnapshotId())
				.map(snapshot -> snapshot.getScore())
				.orElse(null);
	}

	private String toJson(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException exception) {
			return "{}";
		}
	}
}
