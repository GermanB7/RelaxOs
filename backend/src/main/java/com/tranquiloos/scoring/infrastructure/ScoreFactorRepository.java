package com.tranquiloos.scoring.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreFactorRepository extends JpaRepository<ScoreFactorEntity, Long> {

	List<ScoreFactorEntity> findByScoreSnapshotIdOrderByIdAsc(Long scoreSnapshotId);
}
