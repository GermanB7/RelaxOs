import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { adminApi } from '../api/adminApi'

export function useAdminOverview() {
  return useQuery({ queryKey: ['admin', 'overview'], queryFn: adminApi.overview })
}

export function useAdminList<T>(key: string, queryFn: () => Promise<T[]>) {
  return useQuery({ queryKey: ['admin', key], queryFn })
}

export function useAdminMutation<TVariables>(
  key: string,
  mutationFn: (variables: TVariables) => Promise<unknown>,
) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['admin', key] })
      void queryClient.invalidateQueries({ queryKey: ['admin', 'overview'] })
      void queryClient.invalidateQueries({ queryKey: ['admin', 'audit-log'] })
    },
  })
}
