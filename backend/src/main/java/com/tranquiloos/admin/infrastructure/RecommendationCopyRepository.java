package com.tranquiloos.admin.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationCopyRepository extends JpaRepository<RecommendationCopyEntity, String> {

	List<RecommendationCopyEntity> findAllByOrderByRuleKeyAsc();
}
