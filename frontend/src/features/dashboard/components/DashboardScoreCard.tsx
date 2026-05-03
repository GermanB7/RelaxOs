import { Link } from 'react-router-dom'
import type { DashboardRisk, DashboardScore } from '../../../shared/types/domain'
import { RecommendationStatusBadge } from '../../recommendations/components/RecommendationStatusBadge'

type DashboardScoreCardProps = {
  scenarioId?: number
  score: DashboardScore | null
  risks: DashboardRisk[]
}

export function DashboardScoreCard({
  scenarioId,
  score,
  risks,
}: DashboardScoreCardProps) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
            Independence Score
          </p>
          {score ? (
            <h2 className="mt-1 text-3xl font-semibold text-slate-950">
              {score.score}
            </h2>
          ) : (
            <h2 className="mt-1 text-lg font-semibold text-slate-950">
              Not calculated yet
            </h2>
          )}
        </div>
        {scenarioId && (
          <Link
            to={`/scenarios/${scenarioId}`}
            className="text-sm font-semibold text-teal-700 hover:text-teal-900"
          >
            Scenario
          </Link>
        )}
      </div>

      {score ? (
        <>
          <p className="mt-2 text-sm font-medium text-slate-700">
            {score.status}
          </p>
          {score.summary && (
            <p className="mt-2 text-sm leading-6 text-slate-600">
              {score.summary}
            </p>
          )}
        </>
      ) : (
        <p className="mt-2 text-sm leading-6 text-slate-600">
          Calculate the score from a scenario detail page or use quick actions
          after creating a scenario.
        </p>
      )}

      <div className="mt-4">
        <h3 className="text-sm font-semibold text-slate-950">Top risks</h3>
        {risks.length === 0 ? (
          <p className="mt-2 text-sm text-slate-600">
            No risks available yet.
          </p>
        ) : (
          <div className="mt-2 grid gap-2">
            {risks.map((risk) => (
              <div key={`${risk.severity}-${risk.title}`} className="flex gap-2">
                <RecommendationStatusBadge
                  label={risk.severity}
                  variant="severity"
                />
                <span className="text-sm text-slate-700">{risk.title}</span>
              </div>
            ))}
          </div>
        )}
      </div>
    </section>
  )
}
