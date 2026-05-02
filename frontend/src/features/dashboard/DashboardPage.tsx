import { EmptyState } from '../../shared/components/EmptyState'
import { PageHeader } from '../../shared/components/PageHeader'
import { RecommendationStatusBadge } from '../recommendations/components/RecommendationStatusBadge'
import { useRecommendations } from '../recommendations/hooks/useRecommendations'

export function DashboardPage() {
  const { recommendationsQuery } = useRecommendations({ status: 'OPEN' })
  const topRecommendations = (recommendationsQuery.data ?? []).slice(0, 3)

  return (
    <>
      <PageHeader
        title="Dashboard"
        description="A quiet starting point with the most important open recommendations from the backend."
      />

      <section>
        <h3 className="mb-3 text-base font-semibold text-slate-950">
          Top open recommendations
        </h3>
        {recommendationsQuery.isLoading ? (
          <p className="text-sm text-slate-600">Loading recommendations...</p>
        ) : topRecommendations.length === 0 ? (
          <EmptyState
            title="No open recommendations"
            description="Create a scenario, calculate score, and recalculate recommendations to see the first guidance here."
            actionLabel="Open scenarios"
            to="/scenarios"
          />
        ) : (
          <div className="grid gap-3">
            {topRecommendations.map((recommendation) => (
              <article
                key={recommendation.id}
                className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm"
              >
                <div className="mb-2 flex flex-wrap items-center gap-2">
                  <RecommendationStatusBadge
                    label={recommendation.severity}
                    variant="severity"
                  />
                  <span className="text-xs font-medium text-slate-500">
                    Priority {recommendation.priority}
                  </span>
                </div>
                <h4 className="text-sm font-semibold text-slate-950">
                  {recommendation.title}
                </h4>
                <p className="mt-1 text-sm leading-6 text-slate-600">
                  {recommendation.message}
                </p>
              </article>
            ))}
          </div>
        )}
      </section>
    </>
  )
}
