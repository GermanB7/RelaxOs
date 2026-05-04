package com.tranquiloos.decisions.api;

import java.time.Instant;

public record DecisionEventResponse(
		Long id,
		Long userId,
		Long scenarioId,
		Long recommendationId,
		String decisionType,
		String question,
		String chosenOption,
		Integer scoreBefore,
		Integer scoreAfter,
		String reason,
		String contextJson,
		Instant createdAt) {
}
