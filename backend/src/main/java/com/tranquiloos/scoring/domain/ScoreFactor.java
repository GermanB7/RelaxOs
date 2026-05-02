package com.tranquiloos.scoring.domain;

import java.math.BigDecimal;

public record ScoreFactor(
		String key,
		String label,
		String valueText,
		int impact,
		BigDecimal weight,
		String explanation) {
}
