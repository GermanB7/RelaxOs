package com.tranquiloos.transport.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.tranquiloos.transport.domain.TransportOptionType;
import com.tranquiloos.transport.domain.TransportRiskLevel;

public record TransportEvaluationResponse(
		Long id,
		Long scenarioId,
		TransportOptionType recommendedCurrentOption,
		TransportOptionType futureViableOption,
		BigDecimal transportBurden,
		Integer fitScore,
		TransportRiskLevel riskLevel,
		String explanation,
		String conditionsToSwitch,
		List<EvaluatedTransportOptionResponse> evaluatedOptions,
		Instant createdAt) {
}
