import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { profileApi, type ProfilePayload } from '../api/profileApi'

export function useProfile() {
  const queryClient = useQueryClient()

  const profileQuery = useQuery({
    queryKey: ['profile'],
    queryFn: profileApi.getProfile,
  })

  const updateProfile = useMutation({
    mutationFn: (payload: ProfilePayload) => profileApi.updateProfile(payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['profile'] }),
  })

  return { profileQuery, updateProfile }
}
