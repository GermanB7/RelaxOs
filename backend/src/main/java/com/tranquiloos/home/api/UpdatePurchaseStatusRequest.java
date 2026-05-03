package com.tranquiloos.home.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record UpdatePurchaseStatusRequest(
		@JsonProperty("status") String status,
		@JsonProperty("actualPrice") BigDecimal actualPrice,
		@JsonProperty("reason") String reason) {
}
