package com.tranquiloos.expenses.api;

public record ExpenseCategoryResponse(Long id, String code, String name, boolean isActive) {
}
