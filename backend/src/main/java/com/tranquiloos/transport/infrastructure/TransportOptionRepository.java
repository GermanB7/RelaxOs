package com.tranquiloos.transport.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportOptionRepository extends JpaRepository<TransportOptionEntity, Long> {

	List<TransportOptionEntity> findByScenarioIdOrderByIdAsc(Long scenarioId);

	Optional<TransportOptionEntity> findByIdAndScenarioId(Long id, Long scenarioId);
}
