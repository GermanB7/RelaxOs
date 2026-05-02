import { EmptyState } from '../../shared/components/EmptyState'
import { PageHeader } from '../../shared/components/PageHeader'

export function DashboardPage() {
  return (
    <>
      <PageHeader
        title="Dashboard"
        description="A quiet starting point for the MVP foundation. Product metrics and decision support arrive after the domain model exists."
      />
      <EmptyState
        title="No dashboard data yet"
        description="Sprint 0 keeps this page intentionally light so the backend remains the source of truth for future scoring and recommendations."
        actionLabel="Review scenarios area"
        to="/scenarios"
      />
    </>
  )
}
