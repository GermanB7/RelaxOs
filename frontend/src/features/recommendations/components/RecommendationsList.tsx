import type { Recommendation } from '../../../shared/types/domain'
import { EmptyState } from '../../../shared/components/EmptyState'
import { RecommendationCard } from './RecommendationCard'

type RecommendationsListProps = {
  recommendations: Recommendation[]
  isActionPending?: boolean
  emptyTitle?: string
  emptyDescription?: string
  onAccept: (recommendationId: number) => void
  onPostpone: (recommendationId: number) => void
  onDismiss: (recommendationId: number) => void
}

export function RecommendationsList({
  recommendations,
  isActionPending,
  emptyTitle = 'No recommendations found',
  emptyDescription = 'Calculate a score and recalculate recommendations to see backend-generated guidance here.',
  onAccept,
  onPostpone,
  onDismiss,
}: RecommendationsListProps) {
  if (recommendations.length === 0) {
    return (
      <EmptyState
        title={emptyTitle}
        description={emptyDescription}
        actionLabel="Open scenarios"
        to="/scenarios"
      />
    )
  }

  return (
    <div className="grid gap-4">
      {recommendations.map((recommendation) => (
        <RecommendationCard
          key={recommendation.id}
          recommendation={recommendation}
          isActionPending={isActionPending}
          onAccept={onAccept}
          onPostpone={onPostpone}
          onDismiss={onDismiss}
        />
      ))}
    </div>
  )
}
