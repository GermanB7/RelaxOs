package com.tranquiloos.recommendations.application;

import org.springframework.stereotype.Component;

import com.tranquiloos.recommendations.api.DecisionEventResponse;
import com.tranquiloos.recommendations.api.RecommendationResponse;
import com.tranquiloos.recommendations.infrastructure.DecisionEventEntity;
import com.tranquiloos.recommendations.infrastructure.RecommendationEntity;

@Component
class RecommendationMapper {

	RecommendationResponse toResponse(RecommendationEntity entity) {
		return new RecommendationResponse(
				entity.getId(),
				entity.getUserId(),
				entity.getScenarioId(),
				entity.getScoreSnapshotId(),
				entity.getType(),
				entity.getSeverity(),
				entity.getPriority(),
				entity.getTitle(),
				entity.getMessage(),
				entity.getActionLabel(),
				entity.getActionType(),
				entity.getSourceRuleKey(),
				entity.getStatus(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}

	DecisionEventResponse toResponse(DecisionEventEntity entity) {
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
				entity.getCreatedAt());
	}
}
