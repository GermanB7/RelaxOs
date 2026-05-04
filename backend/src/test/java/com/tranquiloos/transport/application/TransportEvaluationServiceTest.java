package com.tranquiloos.transport.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.decisions.application.DecisionEventService;
import com.tranquiloos.expenses.application.ExpenseService;
import com.tranquiloos.recommendations.infrastructure.RecommendationEntity;
import com.tranquiloos.recommendations.infrastructure.RecommendationJpaRepository;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.scenarios.infrastructure.ScenarioEntity;
import com.tranquiloos.transport.api.TransportEvaluationResponse;
import com.tranquiloos.transport.domain.TransportOptionType;
import com.tranquiloos.transport.infrastructure.TransportEvaluationEntity;
import com.tranquiloos.transport.infrastructure.TransportEvaluationRepository;
import com.tranquiloos.transport.infrastructure.TransportOptionEntity;
import com.tranquiloos.transport.infrastructure.TransportOptionRepository;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TransportEvaluationServiceTest {

	@Mock private CurrentUserProvider currentUserProvider;
	@Mock private ScenarioService scenarioService;
	@Mock private ExpenseService expenseService;
	@Mock private TransportOptionRepository optionRepository;
	@Mock private TransportEvaluationRepository evaluationRepository;
	@Mock private RecommendationJpaRepository recommendationRepository;
	@Mock private DecisionEventService decisionEventService;

	@Test
	void motorcycleIsNotRecommendedWhenEmergencyCoverageIsBelowTwoMonths() {
		when(currentUserProvider.currentUserId()).thenReturn(1L);
		when(scenarioService.findCurrentUserScenario(10L)).thenReturn(scenario());
		when(expenseService.listScenarioExpenses(10L)).thenReturn(List.of());
		when(optionRepository.findByScenarioIdOrderByIdAsc(10L)).thenReturn(List.of(
				option(1L, TransportOptionType.MOTORCYCLE, "350000", 10, 25, 4, 4, 5),
				option(2L, TransportOptionType.PUBLIC_TRANSPORT, "180000", 10, 60, 3, 3, 2)));
		when(evaluationRepository.save(any(TransportEvaluationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(recommendationRepository.findByUserIdAndScenarioIdAndSourceRuleKeyAndStatus(any(), any(), any(), any()))
				.thenReturn(Optional.empty());
		TransportEvaluationService service = service();

		TransportEvaluationResponse response = service.evaluate(10L);

		assertThat(response.evaluatedOptions()).anyMatch(option ->
				option.optionType() == TransportOptionType.MOTORCYCLE
						&& option.explanation().contains("below 2 months"));
		ArgumentCaptor<RecommendationEntity> captor = ArgumentCaptor.forClass(RecommendationEntity.class);
		verify(recommendationRepository, org.mockito.Mockito.atLeastOnce()).save(captor.capture());
		assertThat(captor.getAllValues()).anyMatch(recommendation ->
				"MOTORCYCLE_NOT_READY".equals(recommendation.getSourceRuleKey()));
	}

	private TransportEvaluationService service() {
		return new TransportEvaluationService(
				currentUserProvider,
				scenarioService,
				expenseService,
				optionRepository,
				evaluationRepository,
				recommendationRepository,
				decisionEventService,
				new ObjectMapper());
	}

	private ScenarioEntity scenario() {
		ScenarioEntity scenario = new ScenarioEntity();
		ReflectionTestUtils.setField(scenario, "id", 10L);
		scenario.setUserId(1L);
		scenario.setName("Transport scenario");
		scenario.setMonthlyIncome(new BigDecimal("4000000"));
		scenario.setEmergencyFundCurrent(new BigDecimal("500000"));
		return scenario;
	}

	private TransportOptionEntity option(
			Long id,
			TransportOptionType type,
			String cost,
			int trips,
			int time,
			int comfort,
			int safety,
			int flexibility) {
		TransportOptionEntity option = new TransportOptionEntity();
		ReflectionTestUtils.setField(option, "id", id);
		option.setScenarioId(10L);
		option.setOptionType(type);
		option.setMonthlyCost(new BigDecimal(cost));
		option.setTripsPerWeek(trips);
		option.setAverageTimeMinutes(time);
		option.setComfortScore(comfort);
		option.setSafetyScore(safety);
		option.setFlexibilityScore(flexibility);
		return option;
	}
}
