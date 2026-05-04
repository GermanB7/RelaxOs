package com.tranquiloos.decisions.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tranquiloos.decisions.api.CreateDecisionEventRequest;
import com.tranquiloos.decisions.api.DecisionEventResponse;
import com.tranquiloos.decisions.domain.DecisionType;
import com.tranquiloos.recommendations.infrastructure.DecisionEventEntity;
import com.tranquiloos.recommendations.infrastructure.DecisionEventJpaRepository;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DecisionEventServiceTest {

	@Mock
	private CurrentUserProvider currentUserProvider;

	@Mock
	private ScenarioService scenarioService;

	@Mock
	private DecisionEventJpaRepository decisionEventRepository;

	@Test
	void createsManualDecisionEventForCurrentUser() {
		when(currentUserProvider.currentUserId()).thenReturn(7L);
		when(decisionEventRepository.save(any(DecisionEventEntity.class))).thenAnswer(invocation -> {
			DecisionEventEntity event = invocation.getArgument(0);
			ReflectionTestUtils.setField(event, "id", 99L);
			return event;
		});
		DecisionEventService service = new DecisionEventService(currentUserProvider, scenarioService, decisionEventRepository);

		DecisionEventResponse response = service.createManualEvent(new CreateDecisionEventRequest(
				10L,
				DecisionType.SCENARIO_SELECTED,
				"Pick scenario",
				"Bogota solo",
				40,
				55,
				"Best stability",
				"{\"source\":\"test\"}"));

		ArgumentCaptor<DecisionEventEntity> captor = ArgumentCaptor.forClass(DecisionEventEntity.class);
		verify(scenarioService).findCurrentUserScenario(10L);
		verify(decisionEventRepository).save(captor.capture());
		assertThat(captor.getValue().getUserId()).isEqualTo(7L);
		assertThat(captor.getValue().getDecisionType()).isEqualTo("SCENARIO_SELECTED");
		assertThat(response.id()).isEqualTo(99L);
	}
}
