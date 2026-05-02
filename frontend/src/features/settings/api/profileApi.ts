import { apiClient } from '../../../shared/api/apiClient'
import type { Profile } from '../../../shared/types/domain'

export type ProfilePayload = {
  displayName: string
  city: string
  currency: string
  monthlyIncome?: number | null
}

export const profileApi = {
  getProfile: () => apiClient<Profile>({ path: '/me/profile' }),
  updateProfile: (payload: ProfilePayload) =>
    apiClient<Profile>({
      path: '/me/profile',
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
}
