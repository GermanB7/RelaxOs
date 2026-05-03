package com.tranquiloos.meals.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MealCatalogItemJpaRepository extends JpaRepository<MealCatalogItemEntity, Long> {

	List<MealCatalogItemEntity> findByActiveTrueOrderBySortOrderAsc();
}
