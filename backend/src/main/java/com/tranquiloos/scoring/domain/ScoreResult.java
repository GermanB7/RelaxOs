package com.tranquiloos.scoring.domain;

import java.util.List;

public record ScoreResult(
		int score,
		ScoreStatus status,
		ConfidenceLevel confidenceLevel,
		String summary,
		List<ScoreFactor> factors,
		List<RiskFactor> risks) {
}
