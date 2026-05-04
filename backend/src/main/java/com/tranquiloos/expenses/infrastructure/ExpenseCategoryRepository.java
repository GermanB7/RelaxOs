package com.tranquiloos.expenses.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategoryEntity, Long> {

	List<ExpenseCategoryEntity> findByActiveTrueOrderByNameAsc();

	List<ExpenseCategoryEntity> findAllByOrderBySortOrderAscNameAsc();

	Optional<ExpenseCategoryEntity> findByCodeIgnoreCase(String code);
}
