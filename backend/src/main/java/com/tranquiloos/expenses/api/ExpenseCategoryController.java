package com.tranquiloos.expenses.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tranquiloos.expenses.application.ExpenseService;

@RestController
@RequestMapping("/api/v1/expense-categories")
public class ExpenseCategoryController {

	private final ExpenseService expenseService;

	public ExpenseCategoryController(ExpenseService expenseService) {
		this.expenseService = expenseService;
	}

	@GetMapping
	List<ExpenseCategoryResponse> listCategories() {
		return expenseService.listCategories();
	}
}
