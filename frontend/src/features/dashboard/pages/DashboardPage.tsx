import { PageHeader } from '../../../shared/components/PageHeader'
import { CriticalRisksCard } from '../components/CriticalRisksCard'
import { CurrentStateCard } from '../components/CurrentStateCard'
import { DecisionTimelinePreview } from '../components/DecisionTimelinePreview'
import { FinancialSnapshotCard } from '../components/FinancialSnapshotCard'
import { HomePriorityCard } from '../components/HomePriorityCard'
import { MealSuggestionCard } from '../components/MealSuggestionCard'
import { QuickActionsGrid } from '../components/QuickActionsGrid'
import { ScenarioComparisonSummaryCard } from '../components/ScenarioComparisonSummaryCard'
import { TopRecommendationsCard } from '../components/TopRecommendationsCard'
import { TransportSummaryCard } from '../components/TransportSummaryCard'
import { useDashboardSummary } from '../hooks/useDashboard'

export function DashboardPage() {
  const dashboardQuery = useDashboardSummary()

  if (dashboardQuery.isLoading) {
    return (
      <>
        <PageHeader
          title="Dashboard"
          description="Loading the command center..."
        />
        <div className="rounded-lg border border-slate-200 bg-white p-4 text-sm text-slate-600 shadow-sm">
          Loading dashboard...
        </div>
      </>
    )
  }

  if (dashboardQuery.isError || !dashboardQuery.data) {
    return (
      <>
        <PageHeader
          title="Dashboard"
          description="The dashboard could not load right now."
        />
        <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
          Check that the backend is running and refresh the page.
        </div>
      </>
    )
  }

  const dashboard = dashboardQuery.data

  return (
    <>
      <PageHeader
        title="Dashboard"
        description="A decision-first view of status, risk, next action, mode, transport, home setup and recent choices."
      />

      <div className="grid gap-4">
        <CurrentStateCard summary={dashboard} />
        <QuickActionsGrid actions={dashboard.quickActions} />
        <div className="grid gap-4 lg:grid-cols-[1.1fr_0.9fr]">
          <FinancialSnapshotCard snapshot={dashboard.financialSnapshot} />
          <div className="grid gap-4">
            <CriticalRisksCard mainRisk={dashboard.mainRisk} />
            <TransportSummaryCard
              scenarioId={dashboard.activeScenario?.id}
              transport={dashboard.transportSummary}
            />
          </div>
        </div>
        <div className="grid gap-4 lg:grid-cols-2">
          <TopRecommendationsCard
            recommendations={dashboard.topRecommendations}
          />
          <ScenarioComparisonSummaryCard
            comparison={dashboard.scenarioComparisonSummary}
          />
          <HomePriorityCard homePriority={dashboard.homePriority} />
          <MealSuggestionCard mealSuggestion={dashboard.mealSuggestion} />
        </div>
        <div>
          <DecisionTimelinePreview events={dashboard.recentDecisionEvents} />
        </div>
      </div>
    </>
  )
}
