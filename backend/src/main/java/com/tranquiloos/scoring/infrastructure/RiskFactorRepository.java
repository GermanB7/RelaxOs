package com.tranquiloos.scoring.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskFactorRepository extends JpaRepository<RiskFactorEntity, Long> {

	List<RiskFactorEntity> findByScoreSnapshotIdOrderByIdAsc(Long scoreSnapshotId);
}
