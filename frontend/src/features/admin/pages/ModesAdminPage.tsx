import { adminApi } from '../api/adminApi'
import { AdminCatalogPage } from './AdminCatalogPage'

export function ModesAdminPage() {
  return (
    <AdminCatalogPage
      title="Adaptive modes"
      description="Edit mode metadata and policies. Activations remain user-owned."
      queryKey="modes"
      list={adminApi.modes}
      update={adminApi.updateMode}
      columns={[
        { key: 'code', label: 'Code' },
        { key: 'name', label: 'Name' },
        { key: 'intensityLevel', label: 'Intensity' },
        { key: 'active', label: 'Active' },
      ]}
      fields={[
        { name: 'name', label: 'Name' },
        { name: 'description', label: 'Description', type: 'textarea' },
        { name: 'objective', label: 'Objective', type: 'textarea' },
        { name: 'recommendedMinDays', label: 'Min days', type: 'number' },
        { name: 'recommendedMaxDays', label: 'Max days', type: 'number' },
        { name: 'intensityLevel', label: 'Intensity', type: 'select', options: ['LOW', 'MEDIUM', 'HIGH'] },
        { name: 'spendingPolicy', label: 'Spending', type: 'select', options: ['STRICT', 'NORMAL', 'FLEXIBLE', 'MINIMAL'] },
        { name: 'alertPolicy', label: 'Alerts', type: 'select', options: ['STRICT', 'NORMAL', 'SOFT', 'MINIMAL'] },
        { name: 'purchasePolicy', label: 'Purchases', type: 'select', options: ['FREEZE_NON_ESSENTIAL', 'PLAN_ONLY', 'NORMAL', 'FLEXIBLE'] },
        { name: 'routinePolicy', label: 'Routine', type: 'select', options: ['STRICT', 'NORMAL', 'MINIMUM_VIABLE', 'RESET'] },
        { name: 'sortOrder', label: 'Sort order', type: 'number' },
        { name: 'active', label: 'Active', type: 'checkbox' },
      ]}
    />
  )
}
