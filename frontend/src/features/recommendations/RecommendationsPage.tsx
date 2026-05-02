import { EmptyState } from '../../shared/components/EmptyState'
import { PageHeader } from '../../shared/components/PageHeader'

export function RecommendationsPage() {
  return (
    <>
      <PageHeader
        title="Recommendations"
        description="Recommendations will be calculated by backend services later. React only renders server state when that contract exists."
      />
      <EmptyState
        title="No recommendations available"
        description="This page is intentionally empty until Sprint 1 defines the minimum recommendation inputs and API shape."
        actionLabel="Open settings"
        to="/settings"
      />
    </>
  )
}
