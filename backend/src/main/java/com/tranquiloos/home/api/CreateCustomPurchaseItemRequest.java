package com.tranquiloos.home.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCustomPurchaseItemRequest(
		@JsonProperty("scenarioId") Long scenarioId,
		@JsonProperty("name") String name,
		@JsonProperty("category") String category,
		@JsonProperty("tier") String tier,
		@JsonProperty("estimatedPrice") java.math.BigDecimal estimatedPrice,
		@JsonProperty("priority") Integer priority,
		@JsonProperty("link") String link,
		@JsonProperty("notes") String notes) {
}
