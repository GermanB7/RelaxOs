package com.tranquiloos.scoring.api;

import java.math.BigDecimal;

public record ScoreFactorResponse(
		String key,
		String label,
		String valueText,
		int impact,
		BigDecimal weight,
		String explanation) {
}
