package com.tranquiloos.home.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPurchaseItemJpaRepository extends JpaRepository<UserPurchaseItemEntity, Long> {

	List<UserPurchaseItemEntity> findByUserIdOrderByPriorityAscTierAscStatusAscNameAsc(Long userId);

	List<UserPurchaseItemEntity> findByUserIdAndScenarioIdOrderByPriorityAscTierAscStatusAscNameAsc(Long userId,
			Long scenarioId);

	List<UserPurchaseItemEntity> findByUserIdAndStatusOrderByPriorityAscTierAscNameAsc(Long userId, String status);

	List<UserPurchaseItemEntity> findByUserIdAndScenarioIdAndStatusOrderByPriorityAscTierAscNameAsc(Long userId,
			Long scenarioId, String status);

	Optional<UserPurchaseItemEntity> findByUserIdAndScenarioIdAndCatalogItemId(Long userId, Long scenarioId,
			Long catalogItemId);

	Optional<UserPurchaseItemEntity> findByUserIdAndCatalogItemIdAndScenarioIdIsNull(Long userId, Long catalogItemId);

	List<UserPurchaseItemEntity> findByScenarioIdOrderByPriorityAscTierAscStatusAscNameAsc(Long scenarioId);

	List<UserPurchaseItemEntity> findByScenarioIdAndStatusOrderByPriorityAscTierAscNameAsc(Long scenarioId, String status);

	Long countByUserIdAndTierAndStatusNot(Long userId, String tier, String excludedStatus);

	Long countByUserIdAndTierAndStatus(Long userId, String tier, String status);
}
