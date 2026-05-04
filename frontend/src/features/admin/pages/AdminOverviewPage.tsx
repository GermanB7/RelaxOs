import { PageHeader } from '../../../shared/components/PageHeader'
import { useAdminOverview } from '../hooks/useAdmin'

export function AdminOverviewPage() {
  const query = useAdminOverview()

  return (
    <div className="grid gap-4">
      <PageHeader
        title="Internal Admin"
        description="Maintain catalogs, settings, copy and seeds without touching the database."
      />
      {query.isLoading ? (
        <div className="rounded-lg border border-slate-200 bg-white p-4 text-sm text-slate-600">
          Loading admin overview...
        </div>
      ) : query.data ? (
        <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
          {Object.entries(query.data).map(([key, value]) => (
            <section
              key={key}
              className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm"
            >
              <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
                {key.replace(/([A-Z])/g, ' $1')}
              </p>
              <p className="mt-2 text-2xl font-semibold text-slate-950">
                {value}
              </p>
            </section>
          ))}
        </div>
      ) : (
        <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
          Admin overview could not load.
        </div>
      )}
    </div>
  )
}
