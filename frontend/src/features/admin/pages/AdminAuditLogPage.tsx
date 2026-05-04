import { PageHeader } from '../../../shared/components/PageHeader'
import { adminApi } from '../api/adminApi'
import { AdminTable } from '../components/AdminTable'
import { useAdminList } from '../hooks/useAdmin'

export function AdminAuditLogPage() {
  const query = useAdminList('audit-log', adminApi.auditLog)

  return (
    <div className="grid gap-4">
      <PageHeader
        title="Admin audit log"
        description="Read-only trail of catalog, settings and import changes."
      />
      {query.data ? (
        <AdminTable
          rows={query.data}
          columns={[
            { key: 'createdAt', label: 'Created' },
            { key: 'actionType', label: 'Action' },
            { key: 'entityType', label: 'Entity' },
            { key: 'entityId', label: 'ID' },
            { key: 'summary', label: 'Summary' },
          ]}
          getRowId={(row) => row.id}
        />
      ) : (
        <div className="rounded-lg border border-slate-200 bg-white p-4 text-sm text-slate-600">
          Loading audit log...
        </div>
      )}
    </div>
  )
}
