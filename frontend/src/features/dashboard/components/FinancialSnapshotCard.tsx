import type { DashboardSummary } from '../../../shared/types/domain'
import { formatCop } from './formatters'

type Props = {
  snapshot: DashboardSummary['financialSnapshot']
}

function percent(value: number) {
  return `${(value * 100).toFixed(1)}%`
}

export function FinancialSnapshotCard({ snapshot }: Props) {
  const rows = [
    ['Income', formatCop(snapshot.monthlyIncome)],
    ['Expenses', formatCop(snapshot.monthlyExpenses)],
    ['Savings', formatCop(snapshot.estimatedSavings)],
    ['Savings rate', percent(snapshot.savingsRate)],
    ['Fixed burden', percent(snapshot.fixedBurden)],
    ['Transport burden', percent(snapshot.transportBurden)],
    ['Emergency coverage', `${snapshot.emergencyCoverage.toFixed(1)} months`],
  ]

  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h2 className="text-base font-semibold text-slate-950">
        Financial snapshot
      </h2>
      <dl className="mt-4 grid gap-3 sm:grid-cols-2">
        {rows.map(([label, value]) => (
          <div key={label} className="rounded-md bg-slate-50 p-3">
            <dt className="text-xs font-medium uppercase tracking-wide text-slate-500">
              {label}
            </dt>
            <dd className="mt-1 text-sm font-semibold text-slate-950">
              {value}
            </dd>
          </div>
        ))}
      </dl>
    </section>
  )
}
