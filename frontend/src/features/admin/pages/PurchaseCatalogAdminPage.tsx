import { adminApi } from '../api/adminApi'
import { AdminCatalogPage } from './AdminCatalogPage'

export function PurchaseCatalogAdminPage() {
  return (
    <AdminCatalogPage
      title="Purchase catalog"
      description="Maintain home setup purchase seeds and safe buying guidance."
      queryKey="purchase-items"
      list={adminApi.purchaseItems}
      create={adminApi.createPurchaseItem}
      update={adminApi.updatePurchaseItem}
      toggle={adminApi.togglePurchaseItem}
      columns={[
        { key: 'code', label: 'Code' },
        { key: 'name', label: 'Name' },
        { key: 'tier', label: 'Tier' },
        { key: 'category', label: 'Category' },
        { key: 'active', label: 'Active' },
      ]}
      fields={[
        { name: 'code', label: 'Code' },
        { name: 'name', label: 'Name' },
        { name: 'category', label: 'Category' },
        { name: 'tier', label: 'Tier', type: 'select', options: ['TIER_1', 'TIER_2', 'TIER_3'] },
        { name: 'estimatedMinPrice', label: 'Min price', type: 'number' },
        { name: 'estimatedMaxPrice', label: 'Max price', type: 'number' },
        { name: 'impactLevel', label: 'Impact', type: 'select', options: ['LOW', 'MEDIUM', 'HIGH'] },
        { name: 'urgencyLevel', label: 'Urgency', type: 'select', options: ['LOW', 'MEDIUM', 'HIGH'] },
        { name: 'recommendedMoment', label: 'Recommended moment' },
        { name: 'earlyPurchaseRisk', label: 'Early purchase risk', type: 'textarea' },
        { name: 'dependencies', label: 'Dependencies', type: 'textarea' },
        { name: 'rationale', label: 'Rationale', type: 'textarea' },
        { name: 'sortOrder', label: 'Sort order', type: 'number' },
        { name: 'active', label: 'Active', type: 'checkbox' },
      ]}
    />
  )
}
