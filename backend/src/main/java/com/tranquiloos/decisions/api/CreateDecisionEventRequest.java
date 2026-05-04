package com.tranquiloos.decisions.api;

import com.tranquiloos.decisions.domain.DecisionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDecisionEventRequest(
		Long scenarioId,
		@NotNull DecisionType decisionType,
		@NotBlank @Size(max = 500) String question,
		@Size(max = 120) String chosenOption,
		Integer scoreBefore,
		Integer scoreAfter,
		@Size(max = 1000) String reason,
		String contextJson) {
}
