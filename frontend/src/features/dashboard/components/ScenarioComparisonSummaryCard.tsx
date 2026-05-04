import { Link } from 'react-router-dom'
import type { DashboardSummary } from '../../../shared/types/domain'

type Props = {
  comparison: DashboardSummary['scenarioComparisonSummary']
}

export function ScenarioComparisonSummaryCard({ comparison }: Props) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h2 className="text-base font-semibold text-slate-950">
        Scenario comparison
      </h2>
      {comparison.available ? (
        <>
          <p className="mt-2 text-sm text-slate-600">
            {comparison.scenarioCount} scenarios can be compared.
          </p>
          {comparison.lastReason ? (
            <p className="mt-2 text-sm text-slate-700">{comparison.lastReason}</p>
          ) : null}
          <Link
            to="/scenarios/compare"
            className="mt-3 inline-flex rounded-md border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"
          >
            Compare scenarios
          </Link>
        </>
      ) : (
        <p className="mt-2 text-sm text-slate-600">
          Create at least two scenarios to compare options.
        </p>
      )}
    </section>
  )
}
