import { apiClient } from '../../../shared/api/apiClient'
import type {
  AdminAuditLog,
  AdminExpenseCategory,
  AdminImportResult,
  AdminMealItem,
  AdminMode,
  AdminOverview,
  AdminPurchaseItem,
  AdminRecommendationCopy,
  AdminSetting,
} from '../../../shared/types/domain'

type JsonRecord = Record<string, unknown>

export const adminApi = {
  overview: () => apiClient<AdminOverview>({ path: '/admin/overview' }),
  expenseCategories: () =>
    apiClient<AdminExpenseCategory[]>({ path: '/admin/expense-categories' }),
  createExpenseCategory: (body: JsonRecord) =>
    apiClient<AdminExpenseCategory>({
      path: '/admin/expense-categories',
      method: 'POST',
      body: JSON.stringify(body),
    }),
  updateExpenseCategory: (id: number, body: JsonRecord) =>
    apiClient<AdminExpenseCategory>({
      path: `/admin/expense-categories/${id}`,
      method: 'PUT',
      body: JSON.stringify(body),
    }),
  toggleExpenseCategory: (id: number) =>
    apiClient<AdminExpenseCategory>({
      path: `/admin/expense-categories/${id}/toggle-active`,
      method: 'PATCH',
    }),
  purchaseItems: () =>
    apiClient<AdminPurchaseItem[]>({ path: '/admin/purchase-items' }),
  createPurchaseItem: (body: JsonRecord) =>
    apiClient<AdminPurchaseItem>({
      path: '/admin/purchase-items',
      method: 'POST',
      body: JSON.stringify(body),
    }),
  updatePurchaseItem: (id: number, body: JsonRecord) =>
    apiClient<AdminPurchaseItem>({
      path: `/admin/purchase-items/${id}`,
      method: 'PUT',
      body: JSON.stringify(body),
    }),
  togglePurchaseItem: (id: number) =>
    apiClient<AdminPurchaseItem>({
      path: `/admin/purchase-items/${id}/toggle-active`,
      method: 'PATCH',
    }),
  mealItems: () => apiClient<AdminMealItem[]>({ path: '/admin/meal-items' }),
  createMealItem: (body: JsonRecord) =>
    apiClient<AdminMealItem>({
      path: '/admin/meal-items',
      method: 'POST',
      body: JSON.stringify(body),
    }),
  updateMealItem: (id: number, body: JsonRecord) =>
    apiClient<AdminMealItem>({
      path: `/admin/meal-items/${id}`,
      method: 'PUT',
      body: JSON.stringify(body),
    }),
  toggleMealItem: (id: number) =>
    apiClient<AdminMealItem>({
      path: `/admin/meal-items/${id}/toggle-active`,
      method: 'PATCH',
    }),
  modes: () => apiClient<AdminMode[]>({ path: '/admin/modes' }),
  updateMode: (id: number, body: JsonRecord) =>
    apiClient<AdminMode>({
      path: `/admin/modes/${id}`,
      method: 'PUT',
      body: JSON.stringify(body),
    }),
  recommendationCopy: () =>
    apiClient<AdminRecommendationCopy[]>({
      path: '/admin/recommendation-copy',
    }),
  updateRecommendationCopy: (ruleKey: string, body: JsonRecord) =>
    apiClient<AdminRecommendationCopy>({
      path: `/admin/recommendation-copy/${ruleKey}`,
      method: 'PUT',
      body: JSON.stringify(body),
    }),
  settings: () => apiClient<AdminSetting[]>({ path: '/admin/settings' }),
  createSetting: (body: JsonRecord) =>
    apiClient<AdminSetting>({
      path: '/admin/settings',
      method: 'POST',
      body: JSON.stringify(body),
    }),
  updateSetting: (key: string, body: JsonRecord) =>
    apiClient<AdminSetting>({
      path: `/admin/settings/${key}`,
      method: 'PUT',
      body: JSON.stringify(body),
    }),
  importCatalog: (catalogType: string, items: JsonRecord[]) =>
    apiClient<AdminImportResult>({
      path: `/admin/import/${catalogType}`,
      method: 'POST',
      body: JSON.stringify({ items }),
    }),
  exportCatalog: <T>(catalogType: string) =>
    apiClient<T>({ path: `/admin/export/${catalogType}` }),
  auditLog: () => apiClient<AdminAuditLog[]>({ path: '/admin/audit-log' }),
}
