import { Link } from 'react-router-dom'
import type { DashboardSummary } from '../../../shared/types/domain'

type Props = {
  summary: DashboardSummary
}

export function CurrentStateCard({ summary }: Props) {
  const action = summary.quickActions[0]
  const status = summary.scoreStatus ?? 'No score yet'

  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-teal-700">
            Current state
          </p>
          <h2 className="mt-1 text-2xl font-semibold text-slate-950">
            {summary.activeScenario?.name ?? 'No active scenario'}
          </h2>
          <p className="mt-2 text-sm text-slate-600">
            Score {summary.latestScore ?? '--'} · {status}
          </p>
          {summary.mainRisk ? (
            <p className="mt-2 text-sm font-medium text-red-700">
              Main risk: {summary.mainRisk.title}
            </p>
          ) : (
            <p className="mt-2 text-sm text-slate-600">
              No dominant risk detected yet.
            </p>
          )}
        </div>
        {action ? (
          <Link
            to={action.path}
            className="rounded-md bg-teal-700 px-4 py-2 text-center text-sm font-semibold text-white hover:bg-teal-800"
          >
            {action.label}
          </Link>
        ) : null}
      </div>
    </section>
  )
}
