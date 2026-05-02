package com.tranquiloos.expenses.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tranquiloos.expenses.api.CreateScenarioExpenseRequest;
import com.tranquiloos.expenses.api.ExpenseCategoryResponse;
import com.tranquiloos.expenses.api.ScenarioExpenseResponse;
import com.tranquiloos.expenses.api.UpdateScenarioExpenseRequest;
import com.tranquiloos.expenses.domain.ExpenseMonthlyCalculator;
import com.tranquiloos.expenses.infrastructure.ExpenseCategoryEntity;
import com.tranquiloos.expenses.infrastructure.ExpenseCategoryRepository;
import com.tranquiloos.expenses.infrastructure.ScenarioExpenseEntity;
import com.tranquiloos.expenses.infrastructure.ScenarioExpenseRepository;
import com.tranquiloos.shared.error.ResourceNotFoundException;

@Service
public class ExpenseService {

	private final ExpenseCategoryRepository categoryRepository;
	private final ScenarioExpenseRepository expenseRepository;
	private final ExpenseMonthlyCalculator monthlyCalculator = new ExpenseMonthlyCalculator();

	public ExpenseService(ExpenseCategoryRepository categoryRepository, ScenarioExpenseRepository expenseRepository) {
		this.categoryRepository = categoryRepository;
		this.expenseRepository = expenseRepository;
	}

	@Transactional(readOnly = true)
	public List<ExpenseCategoryResponse> listCategories() {
		return categoryRepository.findByActiveTrueOrderByNameAsc()
				.stream()
				.map(category -> new ExpenseCategoryResponse(category.getId(), category.getCode(), category.getName(), category.isActive()))
				.toList();
	}

	@Transactional(readOnly = true)
	public List<ScenarioExpenseResponse> listScenarioExpenses(Long scenarioId) {
		Map<Long, ExpenseCategoryEntity> categories = categoriesById();
		return expenseRepository.findByScenarioIdOrderByIdAsc(scenarioId)
				.stream()
				.map(expense -> toResponse(expense, categories.get(expense.getCategoryId())))
				.toList();
	}

	@Transactional
	public ScenarioExpenseResponse createExpense(Long scenarioId, CreateScenarioExpenseRequest request) {
		ExpenseCategoryEntity category = findCategory(request.categoryId());
		ScenarioExpenseEntity expense = new ScenarioExpenseEntity();
		expense.setScenarioId(scenarioId);
		applyRequest(expense, request.categoryId(), request.name(), request.amount(), request.frequency(), request.isEssential());
		return toResponse(expenseRepository.save(expense), category);
	}

	@Transactional
	public ScenarioExpenseResponse updateExpense(Long scenarioId, Long expenseId, UpdateScenarioExpenseRequest request) {
		ScenarioExpenseEntity expense = findExpense(scenarioId, expenseId);
		ExpenseCategoryEntity category = findCategory(request.categoryId());
		applyRequest(expense, request.categoryId(), request.name(), request.amount(), request.frequency(), request.isEssential());
		return toResponse(expenseRepository.save(expense), category);
	}

	@Transactional
	public void deleteExpense(Long scenarioId, Long expenseId) {
		ScenarioExpenseEntity expense = findExpense(scenarioId, expenseId);
		expenseRepository.delete(expense);
	}

	@Transactional(readOnly = true)
	public BigDecimal totalMonthlyExpenses(Long scenarioId) {
		return expenseRepository.findByScenarioIdOrderByIdAsc(scenarioId)
				.stream()
				.map(expense -> monthlyCalculator.monthlyEquivalent(expense.getAmount(), expense.getFrequency()))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Transactional(readOnly = true)
	public long countScenarioExpenses(Long scenarioId) {
		return expenseRepository.countByScenarioId(scenarioId);
	}

	@Transactional
	public void copyExpenses(Long sourceScenarioId, Long targetScenarioId) {
		expenseRepository.findByScenarioIdOrderByIdAsc(sourceScenarioId).forEach(source -> {
			ScenarioExpenseEntity copy = new ScenarioExpenseEntity();
			copy.setScenarioId(targetScenarioId);
			copy.setCategoryId(source.getCategoryId());
			copy.setName(source.getName());
			copy.setAmount(source.getAmount());
			copy.setFrequency(source.getFrequency());
			copy.setEssential(source.isEssential());
			expenseRepository.save(copy);
		});
	}

	private void applyRequest(
			ScenarioExpenseEntity expense,
			Long categoryId,
			String name,
			BigDecimal amount,
			com.tranquiloos.expenses.domain.ExpenseFrequency frequency,
			Boolean essential) {
		expense.setCategoryId(categoryId);
		expense.setName(name);
		expense.setAmount(amount);
		expense.setFrequency(frequency);
		expense.setEssential(Boolean.TRUE.equals(essential));
	}

	private ScenarioExpenseEntity findExpense(Long scenarioId, Long expenseId) {
		return expenseRepository.findByIdAndScenarioId(expenseId, scenarioId)
				.orElseThrow(() -> new ResourceNotFoundException("Scenario expense was not found"));
	}

	private ExpenseCategoryEntity findCategory(Long categoryId) {
		return categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Expense category was not found"));
	}

	private Map<Long, ExpenseCategoryEntity> categoriesById() {
		return categoryRepository.findAll()
				.stream()
				.collect(Collectors.toMap(ExpenseCategoryEntity::getId, Function.identity()));
	}

	private ScenarioExpenseResponse toResponse(ScenarioExpenseEntity expense, ExpenseCategoryEntity category) {
		return new ScenarioExpenseResponse(
				expense.getId(),
				expense.getScenarioId(),
				expense.getCategoryId(),
				category == null ? null : category.getCode(),
				category == null ? null : category.getName(),
				expense.getName(),
				expense.getAmount(),
				expense.getFrequency(),
				expense.isEssential(),
				monthlyCalculator.monthlyEquivalent(expense.getAmount(), expense.getFrequency()));
	}
}
