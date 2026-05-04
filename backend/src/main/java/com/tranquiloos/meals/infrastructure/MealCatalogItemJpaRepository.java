package com.tranquiloos.meals.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MealCatalogItemJpaRepository extends JpaRepository<MealCatalogItemEntity, Long> {

	List<MealCatalogItemEntity> findByActiveTrueOrderBySortOrderAsc();

	List<MealCatalogItemEntity> findAllByOrderBySortOrderAscNameAsc();

	Optional<MealCatalogItemEntity> findByCodeIgnoreCase(String code);
}
