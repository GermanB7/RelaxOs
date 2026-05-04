import { apiClient } from '../../../shared/api/apiClient'
import type { ScenarioComparison } from '../../../shared/types/domain'

export const scenarioComparisonApi = {
  compare: (scenarioIds: number[]) =>
    apiClient<ScenarioComparison>({
      path: '/scenarios/compare',
      method: 'POST',
      body: JSON.stringify({ scenarioIds }),
    }),
  select: (scenarioId: number, reason?: string) =>
    apiClient<void>({
      path: `/scenarios/${scenarioId}/select`,
      method: 'POST',
      body: JSON.stringify({ reason }),
    }),
}
