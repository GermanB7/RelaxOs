import { useQuery } from '@tanstack/react-query'
import { modesApi } from '../api/modesApi'

export function useModeHistory(scenarioId?: number) {
  return useQuery({
    queryKey: ['mode-history', scenarioId],
    queryFn: () => modesApi.listHistory(scenarioId),
  })
}
