package com.tranquiloos.scenarios.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import com.tranquiloos.expenses.application.ExpenseService;
import com.tranquiloos.scenarios.api.CreateScenarioRequest;
import com.tranquiloos.scenarios.api.ScenarioResponse;
import com.tranquiloos.scenarios.api.ScenarioSummaryResponse;
import com.tranquiloos.scenarios.domain.ScenarioStatus;
import com.tranquiloos.scenarios.infrastructure.ScenarioEntity;
import com.tranquiloos.scenarios.infrastructure.ScenarioRepository;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ScenarioServiceTest {

	@Mock
	private CurrentUserProvider currentUserProvider;

	@Mock
	private ScenarioRepository scenarioRepository;

	@Mock
	private ExpenseService expenseService;

	@InjectMocks
	private ScenarioService scenarioService;

	@Test
	void createsScenarioForCurrentUser() {
		when(currentUserProvider.currentUserId()).thenReturn(1L);
		when(scenarioRepository.save(any(ScenarioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ScenarioResponse response = scenarioService.createScenario(new CreateScenarioRequest(
				"First apartment",
				new BigDecimal("3000000.00"),
				new BigDecimal("500000.00"),
				new BigDecimal("6000000.00")));

		ArgumentCaptor<ScenarioEntity> captor = ArgumentCaptor.forClass(ScenarioEntity.class);
		verify(scenarioRepository).save(captor.capture());
		ScenarioEntity saved = captor.getValue();

		assertThat(saved.getUserId()).isEqualTo(1L);
		assertThat(saved.getName()).isEqualTo("First apartment");
		assertThat(saved.getStatus()).isEqualTo(ScenarioStatus.DRAFT);
		assertThat(response.name()).isEqualTo("First apartment");
	}

	@Test
	void returnsSummaryUsingBackendMonthlyTotals() {
		when(currentUserProvider.currentUserId()).thenReturn(1L);
		ScenarioEntity scenario = new ScenarioEntity();
		ReflectionTestUtils.setField(scenario, "id", 10L);
		scenario.setUserId(1L);
		scenario.setName("Bogota solo");
		scenario.setMonthlyIncome(new BigDecimal("3000000.00"));
		scenario.setEmergencyFundCurrent(BigDecimal.ZERO);
		when(scenarioRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(scenario));
		when(expenseService.totalMonthlyExpenses(10L)).thenReturn(new BigDecimal("1800000.00"));
		when(expenseService.countScenarioExpenses(10L)).thenReturn(5L);

		ScenarioSummaryResponse summary = scenarioService.getSummary(10L);

		assertThat(summary.scenarioId()).isEqualTo(10L);
		assertThat(summary.totalMonthlyExpenses()).isEqualByComparingTo(new BigDecimal("1800000.00"));
		assertThat(summary.estimatedMonthlyAvailable()).isEqualByComparingTo(new BigDecimal("1200000.00"));
		assertThat(summary.expenseCount()).isEqualTo(5L);
	}
}
