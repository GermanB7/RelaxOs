import { Link } from 'react-router-dom'
import type { DashboardActiveMode } from '../../../shared/types/domain'

type DashboardActiveModeCardProps = {
  activeMode: DashboardActiveMode
}

export function DashboardActiveModeCard({
  activeMode,
}: DashboardActiveModeCardProps) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
            Active mode
          </p>
          <h2 className="mt-1 text-base font-semibold text-slate-950">
            {activeMode.hasActiveMode ? activeMode.modeName : 'No mode active'}
          </h2>
        </div>
        <Link
          to="/modes"
          className="text-sm font-semibold text-teal-700 hover:text-teal-900"
        >
          {activeMode.hasActiveMode ? 'Manage' : 'Activate'}
        </Link>
      </div>
      {activeMode.guidance.length > 0 ? (
        <ul className="mt-3 grid gap-2 text-sm text-slate-700">
          {activeMode.guidance.slice(0, 3).map((item) => (
            <li key={item}>{item}</li>
          ))}
        </ul>
      ) : (
        <p className="mt-3 text-sm leading-6 text-slate-600">
          Activate a temporary policy when you need stricter focus, recovery, or
          flexibility.
        </p>
      )}
    </section>
  )
}
