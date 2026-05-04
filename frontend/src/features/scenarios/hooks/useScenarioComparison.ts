import { useMutation, useQueryClient } from '@tanstack/react-query'
import { scenarioComparisonApi } from '../api/scenarioComparisonApi'

export function useScenarioComparison() {
  const queryClient = useQueryClient()
  const compareScenarios = useMutation({
    mutationFn: (scenarioIds: number[]) => scenarioComparisonApi.compare(scenarioIds),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['decisions'] }),
  })
  const selectScenario = useMutation({
    mutationFn: ({ scenarioId, reason }: { scenarioId: number; reason?: string }) =>
      scenarioComparisonApi.select(scenarioId, reason),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['decisions'] }),
  })
  return { compareScenarios, selectScenario }
}
