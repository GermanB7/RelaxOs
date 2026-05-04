import { apiClient } from '../../../shared/api/apiClient'
import type { DecisionEvent } from '../../../shared/types/domain'

export type CreateDecisionEventPayload = {
  scenarioId?: number
  decisionType: string
  question: string
  chosenOption?: string
  scoreBefore?: number
  scoreAfter?: number
  reason?: string
  contextJson?: string
}

export const decisionsApi = {
  list: () => apiClient<DecisionEvent[]>({ path: '/decisions' }),
  listByScenario: (scenarioId: number) =>
    apiClient<DecisionEvent[]>({ path: `/scenarios/${scenarioId}/decisions` }),
  get: (id: number) => apiClient<DecisionEvent>({ path: `/decisions/${id}` }),
  create: (payload: CreateDecisionEventPayload) =>
    apiClient<DecisionEvent>({
      path: '/decisions',
      method: 'POST',
      body: JSON.stringify(payload),
    }),
}
