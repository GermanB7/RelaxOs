package com.tranquiloos.dashboard.api;

import com.tranquiloos.scoring.domain.ScoreStatus;

public record DashboardScoreResponse(
		Integer score,
		ScoreStatus status,
		String summary) {
}
