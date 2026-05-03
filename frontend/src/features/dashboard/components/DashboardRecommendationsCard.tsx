import { Link } from 'react-router-dom'
import type { DashboardRecommendation } from '../../../shared/types/domain'
import { EmptyState } from '../../../shared/components/EmptyState'
import { RecommendationStatusBadge } from '../../recommendations/components/RecommendationStatusBadge'

type DashboardRecommendationsCardProps = {
  recommendations: DashboardRecommendation[]
}

export function DashboardRecommendationsCard({
  recommendations,
}: DashboardRecommendationsCardProps) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex items-center justify-between gap-3">
        <h2 className="text-base font-semibold text-slate-950">
          Top open recommendations
        </h2>
        <Link
          to="/recommendations"
          className="text-sm font-semibold text-teal-700 hover:text-teal-900"
        >
          View all
        </Link>
      </div>

      {recommendations.length === 0 ? (
        <div className="mt-4">
          <EmptyState
            title="No open recommendations"
            description="Calculate score and recalculate recommendations to fill this section."
            actionLabel="Open scenarios"
            to="/scenarios"
          />
        </div>
      ) : (
        <div className="mt-4 grid gap-3">
          {recommendations.map((recommendation) => (
            <article
              key={recommendation.id}
              className="rounded-md border border-slate-200 p-3"
            >
              <div className="flex flex-wrap items-center gap-2">
                <RecommendationStatusBadge
                  label={recommendation.severity}
                  variant="severity"
                />
                {recommendation.actionLabel && (
                  <Link
                    to={actionTo(recommendation)}
                    className="text-xs font-semibold text-teal-700 hover:text-teal-900"
                  >
                    {recommendation.actionLabel}
                  </Link>
                )}
              </div>
              <h3 className="mt-2 text-sm font-semibold text-slate-950">
                {recommendation.title}
              </h3>
            </article>
          ))}
        </div>
      )}
    </section>
  )
}

function actionTo(recommendation: DashboardRecommendation) {
  switch (recommendation.actionType) {
    case 'OPEN_SCENARIO':
    case 'EDIT_SCENARIO':
    case 'OPEN_EXPENSES':
      return recommendation.scenarioId
        ? `/scenarios/${recommendation.scenarioId}`
        : '/scenarios'
    case 'OPEN_HOME_ROADMAP':
      return '/home-setup'
    case 'OPEN_MEAL_PLANNER':
      return '/meals'
    case 'OPEN_RECOMMENDATIONS':
      return '/recommendations'
    case 'OPEN_DASHBOARD':
      return '/'
    default:
      return '/recommendations'
  }
}
