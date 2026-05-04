import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { transportApi, type TransportOptionPayload } from '../api/transportApi'

export function useTransport(scenarioId: number) {
  const queryClient = useQueryClient()
  const optionsQuery = useQuery({
    queryKey: ['transport-options', scenarioId],
    queryFn: () => transportApi.listOptions(scenarioId),
    enabled: Number.isFinite(scenarioId),
  })
  const latestEvaluationQuery = useQuery({
    queryKey: ['transport-evaluation', scenarioId],
    queryFn: () => transportApi.latest(scenarioId),
    enabled: Number.isFinite(scenarioId),
    retry: false,
  })
  const createOption = useMutation({
    mutationFn: (payload: TransportOptionPayload) =>
      transportApi.createOption(scenarioId, payload),
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: ['transport-options', scenarioId] }),
  })
  const deleteOption = useMutation({
    mutationFn: (id: number) => transportApi.deleteOption(id),
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: ['transport-options', scenarioId] }),
  })
  const evaluate = useMutation({
    mutationFn: () => transportApi.evaluate(scenarioId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transport-evaluation', scenarioId] })
      queryClient.invalidateQueries({ queryKey: ['recommendations'] })
      queryClient.invalidateQueries({ queryKey: ['decisions'] })
    },
  })
  return { optionsQuery, latestEvaluationQuery, createOption, deleteOption, evaluate }
}
