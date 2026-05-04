package com.tranquiloos.settings.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingRepository extends JpaRepository<SystemSettingEntity, String> {

	List<SystemSettingEntity> findAllByOrderByKeyAsc();
}
