package com.tranquiloos.home.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseCatalogItemJpaRepository extends JpaRepository<PurchaseCatalogItemEntity, Long> {

	Optional<PurchaseCatalogItemEntity> findByCode(String code);

	Optional<PurchaseCatalogItemEntity> findByCodeIgnoreCase(String code);

	List<PurchaseCatalogItemEntity> findAllByOrderByTierAscSortOrderAscCategoryAscNameAsc();

	List<PurchaseCatalogItemEntity> findByIsActiveOrderByTierAscSortOrderAscCategoryAscNameAsc(Boolean isActive);

	List<PurchaseCatalogItemEntity> findByIsActiveAndTierOrderByTierAscSortOrderAscCategoryAscNameAsc(Boolean isActive,
			String tier);

	List<PurchaseCatalogItemEntity> findByIsActiveAndCategoryOrderByTierAscSortOrderAscCategoryAscNameAsc(Boolean isActive,
			String category);

	List<PurchaseCatalogItemEntity> findByIsActiveAndTierAndCategoryOrderByTierAscSortOrderAscCategoryAscNameAsc(
			Boolean isActive, String tier, String category);
}
