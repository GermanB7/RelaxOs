package com.tranquiloos.scoring.domain;

public record RiskFactor(String key, RiskSeverity severity, String title, String explanation) {
}
