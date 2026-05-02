package com.tranquiloos.users.api;

import java.math.BigDecimal;

public record ProfileResponse(String displayName, String city, String currency, BigDecimal monthlyIncome) {
}
