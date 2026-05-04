import { adminApi } from '../api/adminApi'
import { AdminCatalogPage } from './AdminCatalogPage'

export function ExpenseCategoriesAdminPage() {
  return (
    <AdminCatalogPage
      title="Expense categories"
      description="Manage category names and active status used by scenario expenses."
      queryKey="expense-categories"
      list={adminApi.expenseCategories}
      create={adminApi.createExpenseCategory}
      update={adminApi.updateExpenseCategory}
      toggle={adminApi.toggleExpenseCategory}
      columns={[
        { key: 'code', label: 'Code' },
        { key: 'name', label: 'Name' },
        { key: 'active', label: 'Active' },
        { key: 'sortOrder', label: 'Sort' },
      ]}
      fields={[
        { name: 'code', label: 'Code' },
        { name: 'name', label: 'Name' },
        { name: 'parentId', label: 'Parent ID', type: 'number' },
        { name: 'sortOrder', label: 'Sort order', type: 'number' },
        { name: 'active', label: 'Active', type: 'checkbox' },
      ]}
    />
  )
}
