import type { Recommendation } from '../../../shared/types/domain'

type RecommendationActionsProps = {
  recommendation: Recommendation
  isPending?: boolean
  onAccept: (recommendationId: number) => void
  onPostpone: (recommendationId: number) => void
  onDismiss: (recommendationId: number) => void
}

export function RecommendationActions({
  recommendation,
  isPending,
  onAccept,
  onPostpone,
  onDismiss,
}: RecommendationActionsProps) {
  if (recommendation.status !== 'OPEN') {
    return null
  }

  return (
    <div className="mt-4 flex flex-wrap gap-2">
      <button
        type="button"
        disabled={isPending}
        onClick={() => onAccept(recommendation.id)}
        className="rounded-md bg-teal-700 px-3 py-2 text-sm font-semibold text-white hover:bg-teal-800 disabled:opacity-60"
      >
        Accept
      </button>
      <button
        type="button"
        disabled={isPending}
        onClick={() => onPostpone(recommendation.id)}
        className="rounded-md border border-amber-300 bg-amber-50 px-3 py-2 text-sm font-semibold text-amber-900 hover:bg-amber-100 disabled:opacity-60"
      >
        Postpone
      </button>
      <button
        type="button"
        disabled={isPending}
        onClick={() => onDismiss(recommendation.id)}
        className="rounded-md border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-60"
      >
        Dismiss
      </button>
    </div>
  )
}
