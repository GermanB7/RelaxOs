package com.tranquiloos.modes.infrastructure;

import java.time.Instant;

import com.tranquiloos.modes.domain.IntensityLevel;
import com.tranquiloos.modes.domain.ModeActivationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "mode_activation")
public class ModeActivationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "mode_id", nullable = false)
	private Long modeId;

	@Column(name = "scenario_id")
	private Long scenarioId;

	@Column(length = 180)
	private String objective;

	@Enumerated(EnumType.STRING)
	@Column(name = "intensity_level", length = 20)
	private IntensityLevel intensityLevel;

	@Column(name = "activated_at", nullable = false)
	private Instant activatedAt;

	@Column(name = "expires_at")
	private Instant expiresAt;

	@Column(name = "ended_at")
	private Instant endedAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ModeActivationStatus status = ModeActivationStatus.ACTIVE;

	@Column(columnDefinition = "text")
	private String notes;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@PrePersist
	void prePersist() {
		Instant now = Instant.now();
		if (activatedAt == null) {
			activatedAt = now;
		}
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = Instant.now();
	}

	public Long getId() { return id; }
	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }
	public Long getModeId() { return modeId; }
	public void setModeId(Long modeId) { this.modeId = modeId; }
	public Long getScenarioId() { return scenarioId; }
	public void setScenarioId(Long scenarioId) { this.scenarioId = scenarioId; }
	public String getObjective() { return objective; }
	public void setObjective(String objective) { this.objective = objective; }
	public IntensityLevel getIntensityLevel() { return intensityLevel; }
	public void setIntensityLevel(IntensityLevel intensityLevel) { this.intensityLevel = intensityLevel; }
	public Instant getActivatedAt() { return activatedAt; }
	public void setActivatedAt(Instant activatedAt) { this.activatedAt = activatedAt; }
	public Instant getExpiresAt() { return expiresAt; }
	public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
	public Instant getEndedAt() { return endedAt; }
	public void setEndedAt(Instant endedAt) { this.endedAt = endedAt; }
	public ModeActivationStatus getStatus() { return status; }
	public void setStatus(ModeActivationStatus status) { this.status = status; }
	public String getNotes() { return notes; }
	public void setNotes(String notes) { this.notes = notes; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}
