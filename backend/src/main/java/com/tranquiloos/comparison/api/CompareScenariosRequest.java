package com.tranquiloos.comparison.api;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CompareScenariosRequest(
		@NotNull @Size(min = 2, max = 4) List<Long> scenarioIds) {
}
