import type { ScoreFactor } from '../../../shared/types/domain'

type ScoreFactorsListProps = {
  factors: ScoreFactor[]
}

export function ScoreFactorsList({ factors }: ScoreFactorsListProps) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h3 className="text-base font-semibold text-slate-950">Score factors</h3>
      <div className="mt-4 grid gap-3">
        {factors.map((factor) => (
          <article key={factor.key} className="border-t border-slate-100 pt-3">
            <div className="flex items-start justify-between gap-3">
              <div>
                <h4 className="text-sm font-semibold text-slate-950">
                  {factor.label}
                </h4>
                <p className="mt-1 text-sm text-slate-600">
                  {factor.explanation}
                </p>
              </div>
              <div className="text-right">
                <p className="text-sm font-semibold text-slate-950">
                  {factor.valueText}
                </p>
                <p
                  className={
                    factor.impact < 0
                      ? 'text-sm font-semibold text-red-700'
                      : 'text-sm font-semibold text-teal-700'
                  }
                >
                  {factor.impact > 0 ? '+' : ''}
                  {factor.impact}
                </p>
              </div>
            </div>
          </article>
        ))}
      </div>
    </section>
  )
}
