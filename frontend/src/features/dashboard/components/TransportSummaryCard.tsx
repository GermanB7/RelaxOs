import { Link } from 'react-router-dom'
import type { DashboardSummary } from '../../../shared/types/domain'

type Props = {
  scenarioId: number | null | undefined
  transport: DashboardSummary['transportSummary']
}

export function TransportSummaryCard({ scenarioId, transport }: Props) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h2 className="text-base font-semibold text-slate-950">Transport</h2>
      {transport.available ? (
        <div className="mt-3 text-sm text-slate-700">
          <p className="font-semibold text-slate-950">
            {transport.recommendedCurrentOption ?? 'No option'} ·{' '}
            {transport.riskLevel ?? 'UNKNOWN'}
          </p>
          {transport.explanation ? <p className="mt-1">{transport.explanation}</p> : null}
          {transport.conditionsToSwitch ? (
            <p className="mt-2 text-slate-600">{transport.conditionsToSwitch}</p>
          ) : null}
        </div>
      ) : (
        <p className="mt-2 text-sm text-slate-600">
          Add transport options and evaluate them for the active scenario.
        </p>
      )}
      {scenarioId ? (
        <Link
          to={`/scenarios/${scenarioId}`}
          className="mt-3 inline-flex rounded-md border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"
        >
          Open transport
        </Link>
      ) : null}
    </section>
  )
}
