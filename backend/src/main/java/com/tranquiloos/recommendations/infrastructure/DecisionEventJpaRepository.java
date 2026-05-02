package com.tranquiloos.recommendations.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisionEventJpaRepository extends JpaRepository<DecisionEventEntity, Long> {

	List<DecisionEventEntity> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);

	List<DecisionEventEntity> findTop50ByUserIdAndScenarioIdOrderByCreatedAtDesc(Long userId, Long scenarioId);
}
