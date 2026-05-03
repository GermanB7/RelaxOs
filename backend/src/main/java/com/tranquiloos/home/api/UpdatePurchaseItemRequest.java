package com.tranquiloos.home.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record UpdatePurchaseItemRequest(
		@JsonProperty("name") String name,
		@JsonProperty("category") String category,
		@JsonProperty("tier") String tier,
		@JsonProperty("estimatedPrice") BigDecimal estimatedPrice,
		@JsonProperty("actualPrice") BigDecimal actualPrice,
		@JsonProperty("priority") Integer priority,
		@JsonProperty("link") String link,
		@JsonProperty("notes") String notes) {
}
