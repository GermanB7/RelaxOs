import { apiClient } from '../../../shared/api/apiClient'
import type {
  ScenarioScore,
  ScoreHistoryItem,
} from '../../../shared/types/domain'

export const scoringApi = {
  calculateScore: (scenarioId: number) =>
    apiClient<ScenarioScore>({
      path: `/scenarios/${scenarioId}/score/calculate`,
      method: 'POST',
    }),
  getLatestScore: (scenarioId: number) =>
    apiClient<ScenarioScore>({
      path: `/scenarios/${scenarioId}/score/latest`,
    }),
  getScoreHistory: (scenarioId: number) =>
    apiClient<ScoreHistoryItem[]>({
      path: `/scenarios/${scenarioId}/score/history`,
    }),
}
