import type { ScenarioScore } from '../../../shared/types/domain'

type ScoreCardProps = {
  score: ScenarioScore
  onCalculate: () => void
  isCalculating: boolean
}

export function ScoreCard({
  score,
  onCalculate,
  isCalculating,
}: ScoreCardProps) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
            Independence Score
          </p>
          <div className="mt-2 flex items-baseline gap-2">
            <span className="text-4xl font-semibold text-slate-950">
              {score.score}
            </span>
            <span className="text-sm font-semibold text-slate-500">/100</span>
          </div>
          <p className="mt-2 text-sm font-semibold text-teal-700">
            {score.status.replaceAll('_', ' ')}
          </p>
          <p className="mt-2 text-sm leading-6 text-slate-600">
            {score.summary}
          </p>
          <p className="mt-2 text-xs text-slate-500">
            Confidence: {score.confidenceLevel}
          </p>
        </div>
        <button
          type="button"
          onClick={onCalculate}
          disabled={isCalculating}
          className="rounded-md bg-teal-700 px-4 py-2 text-sm font-semibold text-white hover:bg-teal-800 disabled:opacity-60"
        >
          {isCalculating ? 'Calculating...' : 'Recalculate'}
        </button>
      </div>
    </section>
  )
}
