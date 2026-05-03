import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { modesApi, type EndModePayload } from '../api/modesApi'

export function useActiveMode() {
  const queryClient = useQueryClient()
  const activeModeQuery = useQuery({
    queryKey: ['active-mode'],
    queryFn: modesApi.getActiveMode,
  })

  const endActiveMode = useMutation({
    mutationFn: (payload?: EndModePayload) => modesApi.endActiveMode(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['active-mode'] })
      queryClient.invalidateQueries({ queryKey: ['mode-history'] })
      queryClient.invalidateQueries({ queryKey: ['recommendations'] })
    },
  })

  return { activeModeQuery, endActiveMode }
}
