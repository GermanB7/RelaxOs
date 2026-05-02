import { apiClient } from '../../../shared/api/apiClient'
import type {
  DecisionEvent,
  RecalculateRecommendationsResponse,
  Recommendation,
  RecommendationStatus,
} from '../../../shared/types/domain'

export type RecommendationActionPayload = {
  reason?: string
}

export type RecalculateRecommendationsPayload = {
  scenarioId?: number
}

function recommendationsPath(params?: {
  status?: RecommendationStatus
  scenarioId?: number
}) {
  const searchParams = new URLSearchParams()
  if (params?.status) searchParams.set('status', params.status)
  if (params?.scenarioId) searchParams.set('scenarioId', String(params.scenarioId))
  const query = searchParams.toString()
  return `/recommendations${query ? `?${query}` : ''}`
}

export const recommendationsApi = {
  listRecommendations: (params?: {
    status?: RecommendationStatus
    scenarioId?: number
  }) =>
    apiClient<Recommendation[]>({
      path: recommendationsPath(params),
    }),
  recalculateRecommendations: (payload?: RecalculateRecommendationsPayload) =>
    apiClient<RecalculateRecommendationsResponse>({
      path: '/recommendations/recalculate',
      method: 'POST',
      body: JSON.stringify(payload ?? {}),
    }),
  acceptRecommendation: (id: number, payload?: RecommendationActionPayload) =>
    apiClient<Recommendation>({
      path: `/recommendations/${id}/accept`,
      method: 'POST',
      body: JSON.stringify(payload ?? {}),
    }),
  postponeRecommendation: (id: number, payload?: RecommendationActionPayload) =>
    apiClient<Recommendation>({
      path: `/recommendations/${id}/postpone`,
      method: 'POST',
      body: JSON.stringify(payload ?? {}),
    }),
  dismissRecommendation: (id: number, payload?: RecommendationActionPayload) =>
    apiClient<Recommendation>({
      path: `/recommendations/${id}/dismiss`,
      method: 'POST',
      body: JSON.stringify(payload ?? {}),
    }),
  listDecisions: (scenarioId?: number) =>
    apiClient<DecisionEvent[]>({
      path: `/decisions${scenarioId ? `?scenarioId=${scenarioId}` : ''}`,
    }),
}
