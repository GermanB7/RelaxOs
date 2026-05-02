import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  scenariosApi,
  type ScenarioPayload,
  type UpdateScenarioPayload,
} from '../api/scenariosApi'

export function useScenarios() {
  const queryClient = useQueryClient()
  const scenariosQuery = useQuery({
    queryKey: ['scenarios'],
    queryFn: scenariosApi.listScenarios,
  })

  const createScenario = useMutation({
    mutationFn: (payload: ScenarioPayload) =>
      scenariosApi.createScenario(payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['scenarios'] }),
  })

  const duplicateScenario = useMutation({
    mutationFn: (scenarioId: number) =>
      scenariosApi.duplicateScenario(scenarioId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['scenarios'] }),
  })

  return { scenariosQuery, createScenario, duplicateScenario }
}

export function useScenario(scenarioId: number) {
  const queryClient = useQueryClient()

  const scenarioQuery = useQuery({
    queryKey: ['scenario', scenarioId],
    queryFn: () => scenariosApi.getScenario(scenarioId),
    enabled: Number.isFinite(scenarioId),
  })

  const summaryQuery = useQuery({
    queryKey: ['scenario-summary', scenarioId],
    queryFn: () => scenariosApi.getSummary(scenarioId),
    enabled: Number.isFinite(scenarioId),
  })

  const updateScenario = useMutation({
    mutationFn: (payload: UpdateScenarioPayload) =>
      scenariosApi.updateScenario(scenarioId, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['scenarios'] })
      queryClient.invalidateQueries({ queryKey: ['scenario', scenarioId] })
      queryClient.invalidateQueries({
        queryKey: ['scenario-summary', scenarioId],
      })
    },
  })

  return { scenarioQuery, summaryQuery, updateScenario }
}
