package com.tranquiloos.modes.infrastructure;

import java.util.List;
import java.util.Optional;

import com.tranquiloos.modes.domain.ModeCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdaptiveModeJpaRepository extends JpaRepository<AdaptiveModeEntity, Long> {

	List<AdaptiveModeEntity> findByActiveTrueOrderBySortOrderAsc();

	Optional<AdaptiveModeEntity> findByCodeAndActiveTrue(ModeCode code);
}
