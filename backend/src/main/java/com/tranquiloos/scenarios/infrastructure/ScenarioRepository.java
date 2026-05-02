package com.tranquiloos.scenarios.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Long> {

	List<ScenarioEntity> findByUserIdOrderByUpdatedAtDesc(Long userId);

	Optional<ScenarioEntity> findByIdAndUserId(Long id, Long userId);
}
