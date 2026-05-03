package com.tranquiloos.home.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateHomeRoadmapRequest(
		@JsonProperty("scenarioId") Long scenarioId) {
}
