package com.tranquiloos.admin.api;

import java.util.List;

import com.tranquiloos.admin.application.AdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@GetMapping("/overview")
	public AdminDtos.OverviewResponse overview() {
		return adminService.overview();
	}

	@GetMapping("/expense-categories")
	public List<AdminDtos.ExpenseCategoryResponse> listExpenseCategories() {
		return adminService.listExpenseCategories();
	}

	@PostMapping("/expense-categories")
	public AdminDtos.ExpenseCategoryResponse createExpenseCategory(@Valid @RequestBody AdminDtos.ExpenseCategoryRequest request) {
		return adminService.createExpenseCategory(request);
	}

	@PutMapping("/expense-categories/{id}")
	public AdminDtos.ExpenseCategoryResponse updateExpenseCategory(@PathVariable Long id, @Valid @RequestBody AdminDtos.ExpenseCategoryRequest request) {
		return adminService.updateExpenseCategory(id, request);
	}

	@PatchMapping("/expense-categories/{id}/toggle-active")
	public AdminDtos.ExpenseCategoryResponse toggleExpenseCategory(@PathVariable Long id) {
		return adminService.toggleExpenseCategory(id);
	}

	@GetMapping("/purchase-items")
	public List<AdminDtos.PurchaseItemResponse> listPurchaseItems() {
		return adminService.listPurchaseItems();
	}

	@PostMapping("/purchase-items")
	public AdminDtos.PurchaseItemResponse createPurchaseItem(@Valid @RequestBody AdminDtos.PurchaseItemRequest request) {
		return adminService.createPurchaseItem(request);
	}

	@PutMapping("/purchase-items/{id}")
	public AdminDtos.PurchaseItemResponse updatePurchaseItem(@PathVariable Long id, @Valid @RequestBody AdminDtos.PurchaseItemRequest request) {
		return adminService.updatePurchaseItem(id, request);
	}

	@PatchMapping("/purchase-items/{id}/toggle-active")
	public AdminDtos.PurchaseItemResponse togglePurchaseItem(@PathVariable Long id) {
		return adminService.togglePurchaseItem(id);
	}

	@GetMapping("/meal-items")
	public List<AdminDtos.MealItemResponse> listMealItems() {
		return adminService.listMealItems();
	}

	@PostMapping("/meal-items")
	public AdminDtos.MealItemResponse createMealItem(@Valid @RequestBody AdminDtos.MealItemRequest request) {
		return adminService.createMealItem(request);
	}

	@PutMapping("/meal-items/{id}")
	public AdminDtos.MealItemResponse updateMealItem(@PathVariable Long id, @Valid @RequestBody AdminDtos.MealItemRequest request) {
		return adminService.updateMealItem(id, request);
	}

	@PatchMapping("/meal-items/{id}/toggle-active")
	public AdminDtos.MealItemResponse toggleMealItem(@PathVariable Long id) {
		return adminService.toggleMealItem(id);
	}

	@GetMapping("/modes")
	public List<AdminDtos.ModeResponse> listModes() {
		return adminService.listModes();
	}

	@PutMapping("/modes/{id}")
	public AdminDtos.ModeResponse updateMode(@PathVariable Long id, @Valid @RequestBody AdminDtos.ModeRequest request) {
		return adminService.updateMode(id, request);
	}

	@GetMapping("/recommendation-copy")
	public List<AdminDtos.RecommendationCopyResponse> listRecommendationCopy() {
		return adminService.listRecommendationCopy();
	}

	@PutMapping("/recommendation-copy/{ruleKey}")
	public AdminDtos.RecommendationCopyResponse updateRecommendationCopy(
			@PathVariable String ruleKey,
			@Valid @RequestBody AdminDtos.RecommendationCopyRequest request) {
		return adminService.updateRecommendationCopy(ruleKey, request);
	}

	@GetMapping("/settings")
	public List<AdminDtos.SettingResponse> listSettings() {
		return adminService.listSettings();
	}

	@PostMapping("/settings")
	public AdminDtos.SettingResponse createSetting(@Valid @RequestBody AdminDtos.SettingRequest request) {
		return adminService.createSetting(request);
	}

	@PutMapping("/settings/{key}")
	public AdminDtos.SettingResponse updateSetting(@PathVariable String key, @Valid @RequestBody AdminDtos.SettingRequest request) {
		return adminService.updateSetting(key, request);
	}

	@PostMapping("/import/{catalogType}")
	public AdminDtos.ImportResultResponse importCatalog(@PathVariable String catalogType, @Valid @RequestBody AdminDtos.ImportRequest request) {
		return adminService.importCatalog(catalogType, request);
	}

	@GetMapping("/export/{catalogType}")
	public Object exportCatalog(@PathVariable String catalogType) {
		return adminService.exportCatalog(catalogType);
	}

	@GetMapping("/audit-log")
	public List<AdminDtos.AuditLogResponse> auditLog() {
		return adminService.auditLog();
	}
}
