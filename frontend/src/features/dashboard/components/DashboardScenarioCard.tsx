import { Link } from 'react-router-dom'
import type { DashboardScenarioSummary } from '../../../shared/types/domain'
import { EmptyState } from '../../../shared/components/EmptyState'
import { formatCop } from './formatters'

type DashboardScenarioCardProps = {
  scenario: DashboardScenarioSummary | null
}

export function DashboardScenarioCard({ scenario }: DashboardScenarioCardProps) {
  if (!scenario) {
    return (
      <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
        <EmptyState
          title="No primary scenario yet"
          description="Create your first independence scenario so the dashboard has something real to summarize."
          actionLabel="Create scenario"
          to="/scenarios"
        />
      </section>
    )
  }

  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
            Primary scenario
          </p>
          <h2 className="mt-1 text-lg font-semibold text-slate-950">
            {scenario.name}
          </h2>
        </div>
        <Link
          to={`/scenarios/${scenario.id}`}
          className="text-sm font-semibold text-teal-700 hover:text-teal-900"
        >
          Open
        </Link>
      </div>
      <dl className="mt-4 grid gap-3 text-sm sm:grid-cols-3">
        <div>
          <dt className="text-slate-500">Income</dt>
          <dd className="font-semibold text-slate-950">
            {formatCop(scenario.monthlyIncome)}
          </dd>
        </div>
        <div>
          <dt className="text-slate-500">Expenses</dt>
          <dd className="font-semibold text-slate-950">
            {formatCop(scenario.totalMonthlyExpenses)}
          </dd>
        </div>
        <div>
          <dt className="text-slate-500">Available</dt>
          <dd className="font-semibold text-slate-950">
            {formatCop(scenario.estimatedMonthlyAvailable)}
          </dd>
        </div>
      </dl>
    </section>
  )
}
