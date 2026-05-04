package com.tranquiloos.transport.api;

import java.math.BigDecimal;

import com.tranquiloos.transport.domain.TransportOptionType;

public record EvaluatedTransportOptionResponse(
		Long optionId,
		TransportOptionType optionType,
		BigDecimal totalMonthlyCost,
		BigDecimal transportBurden,
		Integer fitScore,
		String riskLevel,
		String explanation) {
}
