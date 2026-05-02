package com.tranquiloos.scoring.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreSnapshotRepository extends JpaRepository<ScoreSnapshotEntity, Long> {

	Optional<ScoreSnapshotEntity> findFirstByScenarioIdOrderByCreatedAtDesc(Long scenarioId);

	List<ScoreSnapshotEntity> findTop10ByScenarioIdOrderByCreatedAtDesc(Long scenarioId);
}
