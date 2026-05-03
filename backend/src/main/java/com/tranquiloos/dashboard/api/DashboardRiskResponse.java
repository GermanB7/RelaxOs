package com.tranquiloos.dashboard.api;

import com.tranquiloos.scoring.domain.RiskSeverity;

public record DashboardRiskResponse(
		RiskSeverity severity,
		String title) {
}
