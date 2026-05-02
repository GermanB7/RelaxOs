import type { ScenarioSummary } from '../../../shared/types/domain'

type ScenarioSummaryCardProps = {
  summary: ScenarioSummary
}

export function ScenarioSummaryCard({ summary }: ScenarioSummaryCardProps) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h3 className="text-base font-semibold text-slate-950">
        Monthly summary
      </h3>
      <dl className="mt-4 grid gap-3 sm:grid-cols-2">
        <div>
          <dt className="text-xs font-semibold uppercase tracking-wide text-slate-500">
            Income
          </dt>
          <dd className="text-lg font-semibold text-slate-950">
            {summary.monthlyIncome.toLocaleString()}
          </dd>
        </div>
        <div>
          <dt className="text-xs font-semibold uppercase tracking-wide text-slate-500">
            Expenses
          </dt>
          <dd className="text-lg font-semibold text-slate-950">
            {summary.totalMonthlyExpenses.toLocaleString()}
          </dd>
        </div>
        <div>
          <dt className="text-xs font-semibold uppercase tracking-wide text-slate-500">
            Available
          </dt>
          <dd className="text-lg font-semibold text-teal-700">
            {summary.estimatedMonthlyAvailable.toLocaleString()}
          </dd>
        </div>
        <div>
          <dt className="text-xs font-semibold uppercase tracking-wide text-slate-500">
            Expense count
          </dt>
          <dd className="text-lg font-semibold text-slate-950">
            {summary.expenseCount}
          </dd>
        </div>
      </dl>
    </section>
  )
}
