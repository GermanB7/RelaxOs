import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ApiClientError } from '../../../shared/api/apiClient'
import { scoringApi } from '../api/scoringApi'

export function useScenarioScore(scenarioId: number) {
  const queryClient = useQueryClient()

  const latestScoreQuery = useQuery({
    queryKey: ['scenario-score-latest', scenarioId],
    queryFn: () => scoringApi.getLatestScore(scenarioId),
    enabled: Number.isFinite(scenarioId),
    retry: (failureCount, error) => {
      if (error instanceof ApiClientError && error.status === 404) {
        return false
      }
      return failureCount < 1
    },
  })

  const calculateScore = useMutation({
    mutationFn: () => scoringApi.calculateScore(scenarioId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['scenario-score-latest', scenarioId],
      })
      queryClient.invalidateQueries({
        queryKey: ['scenario-summary', scenarioId],
      })
    },
  })

  return { latestScoreQuery, calculateScore }
}
