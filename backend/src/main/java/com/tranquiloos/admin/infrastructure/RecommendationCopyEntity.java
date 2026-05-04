package com.tranquiloos.admin.infrastructure;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "recommendation_copy")
public class RecommendationCopyEntity {

	@Id
	@Column(name = "rule_key", length = 80)
	private String ruleKey;

	@Column(nullable = false, length = 160)
	private String title;

	@Column(nullable = false, columnDefinition = "text")
	private String message;

	@Column(name = "action_label", length = 120)
	private String actionLabel;

	@Column(length = 20)
	private String severity;

	@Column(name = "is_active", nullable = false)
	private boolean active = true;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@PrePersist
	void prePersist() {
		Instant now = Instant.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		this.updatedAt = Instant.now();
	}

	public String getRuleKey() { return ruleKey; }
	public void setRuleKey(String ruleKey) { this.ruleKey = ruleKey; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	public String getActionLabel() { return actionLabel; }
	public void setActionLabel(String actionLabel) { this.actionLabel = actionLabel; }
	public String getSeverity() { return severity; }
	public void setSeverity(String severity) { this.severity = severity; }
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}
