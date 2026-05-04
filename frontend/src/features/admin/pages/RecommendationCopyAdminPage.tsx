import { adminApi } from '../api/adminApi'
import { AdminCatalogPage } from './AdminCatalogPage'

function keyId(value: unknown) {
  return String(value ?? '')
    .split('')
    .reduce((sum, char) => sum + char.charCodeAt(0), 0)
}

export function RecommendationCopyAdminPage() {
  return (
    <AdminCatalogPage
      title="Recommendation copy"
      description="Maintain rule copy metadata without editing generated recommendation history."
      queryKey="recommendation-copy"
      list={adminApi.recommendationCopy}
      update={(id, body) =>
        adminApi.updateRecommendationCopy(String(body.ruleKey ?? id), body)
      }
      getId={(row) => keyId(row.ruleKey)}
      columns={[
        { key: 'ruleKey', label: 'Rule' },
        { key: 'title', label: 'Title' },
        { key: 'severity', label: 'Severity' },
        { key: 'active', label: 'Active' },
      ]}
      fields={[
        { name: 'ruleKey', label: 'Rule key' },
        { name: 'title', label: 'Title' },
        { name: 'message', label: 'Message', type: 'textarea' },
        { name: 'actionLabel', label: 'Action label' },
        { name: 'severity', label: 'Severity', type: 'select', options: ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'] },
        { name: 'active', label: 'Active', type: 'checkbox' },
      ]}
    />
  )
}
