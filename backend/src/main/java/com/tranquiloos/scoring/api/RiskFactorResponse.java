package com.tranquiloos.scoring.api;

import com.tranquiloos.scoring.domain.RiskSeverity;

public record RiskFactorResponse(String key, RiskSeverity severity, String title, String explanation) {
}
