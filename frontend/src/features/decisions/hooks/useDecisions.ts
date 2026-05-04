import { useQuery } from '@tanstack/react-query'
import { decisionsApi } from '../api/decisionsApi'

export function useDecisions() {
  return useQuery({
    queryKey: ['decisions'],
    queryFn: decisionsApi.list,
  })
}

export function useScenarioDecisions(scenarioId: number) {
  return useQuery({
    queryKey: ['decisions', scenarioId],
    queryFn: () => decisionsApi.listByScenario(scenarioId),
    enabled: Number.isFinite(scenarioId),
  })
}
