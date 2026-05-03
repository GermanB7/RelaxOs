import { PageHeader } from '../../../shared/components/PageHeader'
import { DashboardActiveModeCard } from '../components/DashboardActiveModeCard'
import { DashboardHomeSetupCard } from '../components/DashboardHomeSetupCard'
import { DashboardMealCtaCard } from '../components/DashboardMealCtaCard'
import { DashboardQuickActions } from '../components/DashboardQuickActions'
import { DashboardRecommendationsCard } from '../components/DashboardRecommendationsCard'
import { DashboardScenarioCard } from '../components/DashboardScenarioCard'
import { DashboardScoreCard } from '../components/DashboardScoreCard'
import { useDashboard } from '../hooks/useDashboard'

export function DashboardPage() {
  const dashboardQuery = useDashboard()

  if (dashboardQuery.isLoading) {
    return (
      <>
        <PageHeader
          title="Dashboard"
          description="Loading your command center..."
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
        description={`Hi ${dashboard.profile.displayName ?? 'there'}${
          dashboard.profile.city ? ` from ${dashboard.profile.city}` : ''
        }. Use this as the MVP command center.`}
      />

      <div className="grid gap-4">
        <DashboardScenarioCard scenario={dashboard.primaryScenario} />

        <div className="grid gap-4 lg:grid-cols-[1.2fr_0.8fr]">
          <DashboardScoreCard
            scenarioId={dashboard.primaryScenario?.id}
            score={dashboard.latestScore}
            risks={dashboard.topRisks}
          />
          <DashboardActiveModeCard activeMode={dashboard.activeMode} />
        </div>

        <DashboardQuickActions scenarioId={dashboard.primaryScenario?.id} />

        <div className="grid gap-4 lg:grid-cols-3">
          <div className="lg:col-span-2">
            <DashboardRecommendationsCard
              recommendations={dashboard.topRecommendations}
            />
          </div>
          <div className="grid gap-4">
            <DashboardHomeSetupCard homeSetup={dashboard.homeSetup} />
            <DashboardMealCtaCard
              suggestedCta={dashboard.mealPlanner.suggestedCta}
            />
          </div>
        </div>
      </div>
    </>
  )
}
