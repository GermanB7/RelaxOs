import { Link } from 'react-router-dom'
import type { Recommendation } from '../../../shared/types/domain'
import { RecommendationActions } from './RecommendationActions'
import { RecommendationStatusBadge } from './RecommendationStatusBadge'

type RecommendationCardProps = {
  recommendation: Recommendation
  isActionPending?: boolean
  onAccept: (recommendationId: number) => void
  onPostpone: (recommendationId: number) => void
  onDismiss: (recommendationId: number) => void
}

const emphasisStyles = {
  CRITICAL: 'border-red-200 bg-red-50/70',
  HIGH: 'border-orange-200 bg-orange-50/70',
  MEDIUM: 'border-slate-200 bg-white',
  LOW: 'border-slate-200 bg-white',
}

export function RecommendationCard({
  recommendation,
  isActionPending,
  onAccept,
  onPostpone,
  onDismiss,
}: RecommendationCardProps) {
  const actionTo = recommendationActionTo(recommendation)

  return (
    <article
      className={`rounded-lg border p-4 shadow-sm ${emphasisStyles[recommendation.severity]}`}
    >
      <div className="flex flex-wrap items-center gap-2">
        <RecommendationStatusBadge
          label={recommendation.severity}
          variant="severity"
        />
        <RecommendationStatusBadge
          label={recommendation.status}
          variant="status"
        />
        {recommendation.sourceRuleKey && (
          <span className="text-xs font-medium text-slate-500">
            {recommendation.sourceRuleKey}
          </span>
        )}
      </div>

      <h3 className="mt-3 text-base font-semibold text-slate-950">
        {recommendation.title}
      </h3>
      <p className="mt-2 text-sm leading-6 text-slate-700">
        {recommendation.message}
      </p>
      {recommendation.actionLabel && !actionTo && (
        <p className="mt-3 text-sm font-semibold text-slate-900">
          {recommendation.actionLabel}
        </p>
      )}
      {recommendation.actionLabel && actionTo && (
        <Link
          to={actionTo}
          className="mt-3 inline-flex text-sm font-semibold text-teal-700 hover:text-teal-900"
        >
          {recommendation.actionLabel}
        </Link>
      )}

      <RecommendationActions
        recommendation={recommendation}
        isPending={isActionPending}
        onAccept={onAccept}
        onPostpone={onPostpone}
        onDismiss={onDismiss}
      />
    </article>
  )
}

function recommendationActionTo(recommendation: Recommendation) {
  switch (recommendation.actionType) {
    case 'OPEN_HOME_ROADMAP':
      return '/home-setup'
    case 'OPEN_EXPENSES':
      return recommendation.scenarioId
        ? `/scenarios/${recommendation.scenarioId}`
        : '/scenarios'
    case 'OPEN_RECOMMENDATIONS':
      return '/recommendations'
    case 'OPEN_DASHBOARD':
      return '/'
    case 'OPEN_MEAL_PLANNER':
      return '/meals'
    case 'OPEN_SCENARIO':
    case 'EDIT_SCENARIO':
      return recommendation.scenarioId
        ? `/scenarios/${recommendation.scenarioId}`
        : '/scenarios'
    default:
      return null
  }
}
