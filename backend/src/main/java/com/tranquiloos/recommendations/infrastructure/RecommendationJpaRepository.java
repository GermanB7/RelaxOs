package com.tranquiloos.recommendations.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tranquiloos.recommendations.domain.RecommendationStatus;

public interface RecommendationJpaRepository extends JpaRepository<RecommendationEntity, Long> {

	List<RecommendationEntity> findByUserIdAndStatusOrderByPriorityAscSeverityDescCreatedAtDesc(Long userId, RecommendationStatus status);

	List<RecommendationEntity> findByUserIdAndScenarioIdAndStatusOrderByPriorityAscSeverityDescCreatedAtDesc(Long userId, Long scenarioId, RecommendationStatus status);

	Optional<RecommendationEntity> findByUserIdAndScenarioIdAndSourceRuleKeyAndStatus(Long userId, Long scenarioId, String sourceRuleKey, RecommendationStatus status);

	Optional<RecommendationEntity> findByIdAndUserId(Long id, Long userId);

	List<RecommendationEntity> findTop5ByUserIdAndStatusOrderByPriorityAscSeverityDescCreatedAtDesc(Long userId, RecommendationStatus status);
}
