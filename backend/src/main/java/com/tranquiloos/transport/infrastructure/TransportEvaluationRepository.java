package com.tranquiloos.transport.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportEvaluationRepository extends JpaRepository<TransportEvaluationEntity, Long> {

	Optional<TransportEvaluationEntity> findFirstByScenarioIdOrderByCreatedAtDesc(Long scenarioId);

	Optional<TransportEvaluationEntity> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
