package com.tranquiloos.recommendations.application;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tranquiloos.recommendations.api.DecisionEventResponse;
import com.tranquiloos.recommendations.api.RecommendationResponse;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationStatus;
import com.tranquiloos.recommendations.infrastructure.RecommendationEntity;
import com.tranquiloos.recommendations.infrastructure.DecisionEventJpaRepository;
import com.tranquiloos.recommendations.infrastructure.RecommendationJpaRepository;
import com.tranquiloos.users.application.CurrentUserProvider;

@Service
public class GetRecommendationsService {

	private final CurrentUserProvider currentUserProvider;
	private final RecommendationJpaRepository recommendationRepository;
	private final DecisionEventJpaRepository decisionEventRepository;
	private final RecommendationMapper mapper;

	public GetRecommendationsService(
			CurrentUserProvider currentUserProvider,
			RecommendationJpaRepository recommendationRepository,
			DecisionEventJpaRepository decisionEventRepository,
			RecommendationMapper mapper) {
		this.currentUserProvider = currentUserProvider;
		this.recommendationRepository = recommendationRepository;
		this.decisionEventRepository = decisionEventRepository;
		this.mapper = mapper;
	}

	@Transactional(readOnly = true)
	public List<RecommendationResponse> listRecommendations(RecommendationStatus status, Long scenarioId) {
		Long userId = currentUserProvider.currentUserId();
		RecommendationStatus resolvedStatus = status == null ? RecommendationStatus.OPEN : status;
		return (scenarioId == null
				? recommendationRepository.findByUserIdAndStatusOrderByPriorityAscSeverityDescCreatedAtDesc(userId, resolvedStatus)
				: recommendationRepository.findByUserIdAndScenarioIdAndStatusOrderByPriorityAscSeverityDescCreatedAtDesc(userId, scenarioId, resolvedStatus))
				.stream()
				.sorted(recommendationOrder())
				.map(mapper::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<RecommendationResponse> topOpenRecommendations() {
		return recommendationRepository.findByUserIdAndStatusOrderByPriorityAscSeverityDescCreatedAtDesc(
						currentUserProvider.currentUserId(),
						RecommendationStatus.OPEN)
				.stream()
				.sorted(recommendationOrder())
				.limit(5)
				.map(mapper::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<DecisionEventResponse> listDecisionEvents(Long scenarioId) {
		Long userId = currentUserProvider.currentUserId();
		return (scenarioId == null
				? decisionEventRepository.findTop50ByUserIdOrderByCreatedAtDesc(userId)
				: decisionEventRepository.findTop50ByUserIdAndScenarioIdOrderByCreatedAtDesc(userId, scenarioId))
				.stream()
				.map(mapper::toResponse)
				.toList();
	}

	private Comparator<RecommendationEntity> recommendationOrder() {
		return Comparator
				.comparing(RecommendationEntity::getPriority)
				.thenComparing((RecommendationEntity recommendation) -> severityRank(recommendation.getSeverity()), Comparator.reverseOrder())
				.thenComparing(RecommendationEntity::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
	}

	private int severityRank(RecommendationSeverity severity) {
		return switch (severity) {
			case CRITICAL -> 4;
			case HIGH -> 3;
			case MEDIUM -> 2;
			case LOW -> 1;
		};
	}
}
