import type { DashboardSummary } from '../../../shared/types/domain'

type Props = {
  mainRisk: DashboardSummary['mainRisk']
}

export function CriticalRisksCard({ mainRisk }: Props) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h2 className="text-base font-semibold text-slate-950">Critical risk</h2>
      {mainRisk ? (
        <div className="mt-3 rounded-md bg-red-50 p-3">
          <p className="text-xs font-semibold uppercase tracking-wide text-red-700">
            {mainRisk.severity}
          </p>
          <h3 className="mt-1 text-sm font-semibold text-red-950">
            {mainRisk.title}
          </h3>
          {mainRisk.explanation ? (
            <p className="mt-1 text-sm text-red-800">{mainRisk.explanation}</p>
          ) : null}
        </div>
      ) : (
        <p className="mt-3 text-sm text-slate-600">
          Calculate or refresh the score to surface the main risk.
        </p>
      )}
    </section>
  )
}
