package com.tranquiloos.expenses.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategoryEntity, Long> {

	List<ExpenseCategoryEntity> findByActiveTrueOrderByNameAsc();
}
