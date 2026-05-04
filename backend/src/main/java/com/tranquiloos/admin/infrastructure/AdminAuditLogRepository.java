package com.tranquiloos.admin.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLogEntity, Long> {

	List<AdminAuditLogEntity> findTop100ByOrderByCreatedAtDesc();
}
