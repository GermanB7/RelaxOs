import type { RiskFactor } from '../../../shared/types/domain'

type RiskFactorsListProps = {
  risks: RiskFactor[]
}

export function RiskFactorsList({ risks }: RiskFactorsListProps) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h3 className="text-base font-semibold text-slate-950">Risk factors</h3>
      {risks.length === 0 ? (
        <p className="mt-3 text-sm leading-6 text-slate-600">
          No major risk factors were detected by the current engine.
        </p>
      ) : (
        <div className="mt-4 grid gap-3">
          {risks.map((risk) => (
            <article key={risk.key} className="border-t border-slate-100 pt-3">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <h4 className="text-sm font-semibold text-slate-950">
                    {risk.title}
                  </h4>
                  <p className="mt-1 text-sm text-slate-600">
                    {risk.explanation}
                  </p>
                </div>
                <span className="rounded-md bg-slate-100 px-2 py-1 text-xs font-semibold text-slate-700">
                  {risk.severity}
                </span>
              </div>
            </article>
          ))}
        </div>
      )}
    </section>
  )
}
