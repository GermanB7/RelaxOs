import { adminApi } from '../api/adminApi'
import { AdminCatalogPage } from './AdminCatalogPage'

function keyId(value: unknown) {
  return String(value ?? '')
    .split('')
    .reduce((sum, char) => sum + char.charCodeAt(0), 0)
}

export function SystemSettingsAdminPage() {
  return (
    <AdminCatalogPage
      title="System settings"
      description="Edit thresholds used by decision rules and operating guidance."
      queryKey="settings"
      list={adminApi.settings}
      create={adminApi.createSetting}
      update={(_id, body) => adminApi.updateSetting(String(body.key), body)}
      getId={(row) => keyId(row.key)}
      columns={[
        { key: 'key', label: 'Key' },
        { key: 'value', label: 'Value' },
        { key: 'valueType', label: 'Type' },
        { key: 'active', label: 'Active' },
      ]}
      fields={[
        { name: 'key', label: 'Key' },
        { name: 'value', label: 'Value' },
        { name: 'valueType', label: 'Type', type: 'select', options: ['STRING', 'DECIMAL', 'INTEGER', 'BOOLEAN'] },
        { name: 'description', label: 'Description', type: 'textarea' },
        { name: 'active', label: 'Active', type: 'checkbox' },
      ]}
    />
  )
}
