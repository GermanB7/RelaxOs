import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import type { RecommendationStatus } from '../../../shared/types/domain'
import {
  recommendationsApi,
  type RecommendationActionPayload,
  type RecalculateRecommendationsPayload,
} from '../api/recommendationsApi'

export function useRecommendations(params?: {
  status?: RecommendationStatus
  scenarioId?: number
}) {
  const queryClient = useQueryClient()
  const status = params?.status ?? 'OPEN'
  const scenarioId = params?.scenarioId
  const isScenarioIdValid =
    scenarioId === undefined || Number.isFinite(scenarioId)

  const recommendationsQuery = useQuery({
    queryKey:
      scenarioId === undefined
        ? ['recommendations', status]
        : ['recommendations', scenarioId, status],
    queryFn: () => recommendationsApi.listRecommendations({ status, scenarioId }),
    enabled: isScenarioIdValid,
  })

  const decisionsQuery = useQuery({
    queryKey: ['decisions', scenarioId],
    queryFn: () => recommendationsApi.listDecisions(scenarioId),
    enabled: scenarioId !== undefined && Number.isFinite(scenarioId),
  })

  const invalidateRecommendations = () => {
    queryClient.invalidateQueries({ queryKey: ['recommendations'] })
    queryClient.invalidateQueries({ queryKey: ['decisions'] })
  }

  const recalculateRecommendations = useMutation({
    mutationFn: (payload?: RecalculateRecommendationsPayload) =>
      recommendationsApi.recalculateRecommendations(payload),
    onSuccess: () => invalidateRecommendations(),
  })

  const acceptRecommendation = useMutation({
    mutationFn: ({
      id,
      payload,
    }: {
      id: number
      payload?: RecommendationActionPayload
    }) => recommendationsApi.acceptRecommendation(id, payload),
    onSuccess: () => invalidateRecommendations(),
  })

  const postponeRecommendation = useMutation({
    mutationFn: ({
      id,
      payload,
    }: {
      id: number
      payload?: RecommendationActionPayload
    }) => recommendationsApi.postponeRecommendation(id, payload),
    onSuccess: () => invalidateRecommendations(),
  })

  const dismissRecommendation = useMutation({
    mutationFn: ({
      id,
      payload,
    }: {
      id: number
      payload?: RecommendationActionPayload
    }) => recommendationsApi.dismissRecommendation(id, payload),
    onSuccess: () => invalidateRecommendations(),
  })

  return {
    recommendationsQuery,
    decisionsQuery,
    recalculateRecommendations,
    acceptRecommendation,
    postponeRecommendation,
    dismissRecommendation,
  }
}
