package com.tranquiloos.settings.infrastructure;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "system_setting")
public class SystemSettingEntity {

	@Id
	@Column(name = "setting_key", length = 120)
	private String key;

	@Column(name = "setting_value", nullable = false, length = 500)
	private String value;

	@Column(name = "value_type", nullable = false, length = 30)
	private String valueType = "STRING";

	@Column(columnDefinition = "text")
	private String description;

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

	public String getKey() { return key; }
	public void setKey(String key) { this.key = key; }
	public String getValue() { return value; }
	public void setValue(String value) { this.value = value; }
	public String getValueType() { return valueType; }
	public void setValueType(String valueType) { this.valueType = valueType; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}
