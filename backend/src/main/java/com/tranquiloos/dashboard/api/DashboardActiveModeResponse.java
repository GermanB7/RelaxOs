package com.tranquiloos.dashboard.api;

import java.util.List;

import com.tranquiloos.modes.domain.ModeCode;

public record DashboardActiveModeResponse(
		boolean hasActiveMode,
		ModeCode modeCode,
		String modeName,
		Long scenarioId,
		List<String> guidance) {
}
