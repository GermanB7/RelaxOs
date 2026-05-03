import { useMutation, useQueryClient } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { recommendationsApi } from '../../recommendations/api/recommendationsApi'
import { scoringApi } from '../../scoring/api/scoringApi'

type DashboardQuickActionsProps = {
  scenarioId?: number
}

export function DashboardQuickActions({ scenarioId }: DashboardQuickActionsProps) {
  const queryClient = useQueryClient()

  const calculateScore = useMutation({
    mutationFn: () => scoringApi.calculateScore(scenarioId as number),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      queryClient.invalidateQueries({ queryKey: ['scenario-score-latest'] })
    },
  })

  const recalculateRecommendations = useMutation({
    mutationFn: () =>
      recommendationsApi.recalculateRecommendations({ scenarioId }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      queryClient.invalidateQueries({ queryKey: ['recommendations'] })
    },
  })

  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h2 className="text-base font-semibold text-slate-950">Quick actions</h2>
      <div className="mt-4 grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
        <Link className="dashboard-action" to="/scenarios">
          Create scenario
        </Link>
        <button
          type="button"
          className="dashboard-action disabled:cursor-not-allowed disabled:opacity-50"
          disabled={!scenarioId || calculateScore.isPending}
          onClick={() => calculateScore.mutate()}
        >
          {calculateScore.isPending ? 'Calculating...' : 'Calculate score'}
        </button>
        <button
          type="button"
          className="dashboard-action disabled:cursor-not-allowed disabled:opacity-50"
          disabled={!scenarioId || recalculateRecommendations.isPending}
          onClick={() => recalculateRecommendations.mutate()}
        >
          {recalculateRecommendations.isPending
            ? 'Recalculating...'
            : 'Recalculate recommendations'}
        </button>
        <Link className="dashboard-action" to="/home-setup">
          Open home setup
        </Link>
        <Link className="dashboard-action" to="/modes">
          Activate mode
        </Link>
        <Link className="dashboard-action" to="/meals">
          Open meal planner
        </Link>
      </div>
      {(calculateScore.isSuccess || recalculateRecommendations.isSuccess) && (
        <p className="mt-3 text-sm font-medium text-teal-700">
          Dashboard updated.
        </p>
      )}
      {(calculateScore.isError || recalculateRecommendations.isError) && (
        <p className="mt-3 text-sm font-medium text-red-700">
          Action could not be completed yet. Check the scenario data first.
        </p>
      )}
    </section>
  )
}
