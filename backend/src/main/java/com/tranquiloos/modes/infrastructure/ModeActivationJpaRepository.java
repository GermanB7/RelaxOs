package com.tranquiloos.modes.infrastructure;

import java.util.List;
import java.util.Optional;

import com.tranquiloos.modes.domain.ModeActivationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeActivationJpaRepository extends JpaRepository<ModeActivationEntity, Long> {

	Optional<ModeActivationEntity> findFirstByUserIdAndStatusOrderByActivatedAtDesc(Long userId, ModeActivationStatus status);

	List<ModeActivationEntity> findTop50ByUserIdOrderByActivatedAtDesc(Long userId);

	List<ModeActivationEntity> findTop50ByUserIdAndScenarioIdOrderByActivatedAtDesc(Long userId, Long scenarioId);
}
