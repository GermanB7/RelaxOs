import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { modesApi, type ActivateModePayload } from '../api/modesApi'

export function useModes() {
  const queryClient = useQueryClient()
  const modesQuery = useQuery({
    queryKey: ['modes'],
    queryFn: modesApi.listModes,
  })

  const activateMode = useMutation({
    mutationFn: (payload: ActivateModePayload) => modesApi.activateMode(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['active-mode'] })
      queryClient.invalidateQueries({ queryKey: ['mode-history'] })
      queryClient.invalidateQueries({ queryKey: ['recommendations'] })
    },
  })

  return { modesQuery, activateMode }
}
