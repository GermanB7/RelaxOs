import { Link } from 'react-router-dom'
import type { DashboardSummary } from '../../../shared/types/domain'

type Props = {
  recommendations: DashboardSummary['topRecommendations']
}

export function TopRecommendationsCard({ recommendations }: Props) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex items-center justify-between gap-3">
        <h2 className="text-base font-semibold text-slate-950">
          Top recommendations
        </h2>
        <Link to="/recommendations" className="text-sm font-semibold text-teal-700">
          Open
        </Link>
      </div>
      {recommendations.length === 0 ? (
        <p className="mt-3 text-sm text-slate-600">
          No open recommendations. Recalculate after updating a scenario.
        </p>
      ) : (
        <div className="mt-3 grid gap-3">
          {recommendations.slice(0, 3).map((recommendation) => (
            <article key={recommendation.id} className="rounded-md bg-slate-50 p-3">
              <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
                {recommendation.severity}
              </p>
              <h3 className="mt-1 text-sm font-semibold text-slate-950">
                {recommendation.title}
              </h3>
              <p className="mt-1 line-clamp-2 text-sm text-slate-600">
                {recommendation.message}
              </p>
            </article>
          ))}
        </div>
      )}
    </section>
  )
}
