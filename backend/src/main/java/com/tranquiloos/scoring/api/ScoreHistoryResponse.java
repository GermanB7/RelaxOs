package com.tranquiloos.scoring.api;

import java.time.Instant;

import com.tranquiloos.scoring.domain.ConfidenceLevel;
import com.tranquiloos.scoring.domain.ScoreStatus;

public record ScoreHistoryResponse(
		Long snapshotId,
		Long scenarioId,
		int score,
		ScoreStatus status,
		ConfidenceLevel confidenceLevel,
		String summary,
		Instant createdAt) {
}
