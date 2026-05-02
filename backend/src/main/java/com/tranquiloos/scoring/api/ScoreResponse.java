package com.tranquiloos.scoring.api;

import java.time.Instant;
import java.util.List;

import com.tranquiloos.scoring.domain.ConfidenceLevel;
import com.tranquiloos.scoring.domain.ScoreStatus;

public record ScoreResponse(
		Long snapshotId,
		Long scenarioId,
		int score,
		ScoreStatus status,
		ConfidenceLevel confidenceLevel,
		String summary,
		List<ScoreFactorResponse> factors,
		List<RiskFactorResponse> risks,
		Instant createdAt) {
}
