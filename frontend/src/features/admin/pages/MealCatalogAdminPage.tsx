import { adminApi } from '../api/adminApi'
import { AdminCatalogPage } from './AdminCatalogPage'

export function MealCatalogAdminPage() {
  return (
    <AdminCatalogPage
      title="Meal catalog"
      description="Maintain manual meal options used by suggestions."
      queryKey="meal-items"
      list={adminApi.mealItems}
      create={adminApi.createMealItem}
      update={adminApi.updateMealItem}
      toggle={adminApi.toggleMealItem}
      columns={[
        { key: 'code', label: 'Code' },
        { key: 'name', label: 'Name' },
        { key: 'category', label: 'Category' },
        { key: 'effortLevel', label: 'Effort' },
        { key: 'active', label: 'Active' },
      ]}
      fields={[
        { name: 'code', label: 'Code' },
        { name: 'name', label: 'Name' },
        { name: 'category', label: 'Category' },
        { name: 'estimatedCostMin', label: 'Min cost', type: 'number' },
        { name: 'estimatedCostMax', label: 'Max cost', type: 'number' },
        { name: 'prepTimeMinutes', label: 'Prep minutes', type: 'number' },
        { name: 'effortLevel', label: 'Effort', type: 'select', options: ['LOW', 'MEDIUM', 'HIGH'] },
        { name: 'cravingLevel', label: 'Craving', type: 'select', options: ['SIMPLE', 'COMFORT', 'RICH', 'HEAVY'] },
        { name: 'budgetLevel', label: 'Budget', type: 'select', options: ['LOW', 'MEDIUM', 'HIGH'] },
        { name: 'requiredEquipment', label: 'Required equipment', type: 'textarea' },
        { name: 'suggestedMode', label: 'Suggested mode' },
        { name: 'description', label: 'Description', type: 'textarea' },
        { name: 'sortOrder', label: 'Sort order', type: 'number' },
        { name: 'active', label: 'Active', type: 'checkbox' },
      ]}
    />
  )
}
