package com.tranquiloos.comparison.api;

import jakarta.validation.constraints.Size;

public record SelectScenarioRequest(
		@Size(max = 1000) String reason) {
}
