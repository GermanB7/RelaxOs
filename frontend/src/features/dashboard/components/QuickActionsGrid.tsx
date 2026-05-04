import { Link } from 'react-router-dom'
import type { DashboardSummary } from '../../../shared/types/domain'

type Props = {
  actions: DashboardSummary['quickActions']
}

export function QuickActionsGrid({ actions }: Props) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h2 className="text-base font-semibold text-slate-950">Quick actions</h2>
      {actions.length === 0 ? (
        <p className="mt-3 text-sm text-slate-600">
          Nothing urgent. Keep the system updated.
        </p>
      ) : (
        <div className="mt-3 grid gap-2 sm:grid-cols-2">
          {actions.map((action) => (
            <Link
              key={action.key}
              to={action.path}
              className="rounded-md border border-slate-200 p-3 text-sm font-semibold text-slate-800 hover:border-teal-300 hover:bg-teal-50"
            >
              {action.label}
            </Link>
          ))}
        </div>
      )}
    </section>
  )
}
