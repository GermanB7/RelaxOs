package com.tranquiloos.admin.infrastructure;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_audit_log")
public class AdminAuditLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "admin_user_id")
	private Long adminUserId;

	@Column(name = "action_type", nullable = false, length = 60)
	private String actionType;

	@Column(name = "entity_type", nullable = false, length = 80)
	private String entityType;

	@Column(name = "entity_id", length = 120)
	private String entityId;

	@Column(nullable = false, columnDefinition = "text")
	private String summary;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "before_json", columnDefinition = "jsonb")
	private String beforeJson;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "after_json", columnDefinition = "jsonb")
	private String afterJson;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@PrePersist
	void prePersist() {
		this.createdAt = Instant.now();
	}

	public Long getId() { return id; }
	public Long getAdminUserId() { return adminUserId; }
	public void setAdminUserId(Long adminUserId) { this.adminUserId = adminUserId; }
	public String getActionType() { return actionType; }
	public void setActionType(String actionType) { this.actionType = actionType; }
	public String getEntityType() { return entityType; }
	public void setEntityType(String entityType) { this.entityType = entityType; }
	public String getEntityId() { return entityId; }
	public void setEntityId(String entityId) { this.entityId = entityId; }
	public String getSummary() { return summary; }
	public void setSummary(String summary) { this.summary = summary; }
	public String getBeforeJson() { return beforeJson; }
	public void setBeforeJson(String beforeJson) { this.beforeJson = beforeJson; }
	public String getAfterJson() { return afterJson; }
	public void setAfterJson(String afterJson) { this.afterJson = afterJson; }
	public Instant getCreatedAt() { return createdAt; }
}
