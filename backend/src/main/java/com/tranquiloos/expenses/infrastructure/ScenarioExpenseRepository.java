package com.tranquiloos.expenses.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioExpenseRepository extends JpaRepository<ScenarioExpenseEntity, Long> {

	List<ScenarioExpenseEntity> findByScenarioIdOrderByIdAsc(Long scenarioId);

	Optional<ScenarioExpenseEntity> findByIdAndScenarioId(Long id, Long scenarioId);

	long countByScenarioId(Long scenarioId);
}
