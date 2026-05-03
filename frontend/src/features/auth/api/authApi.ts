import { apiClient } from '../../../shared/api/apiClient'
import type { AuthResponse, AuthUser } from '../../../shared/types/domain'

export type LoginPayload = {
  email: string
  password: string
}

export type RegisterPayload = LoginPayload & {
  displayName?: string
  city?: string
  currency?: string
}

export const authApi = {
  register: (payload: RegisterPayload) =>
    apiClient<AuthResponse>({
      path: '/auth/register',
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  login: (payload: LoginPayload) =>
    apiClient<AuthResponse>({
      path: '/auth/login',
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  me: () => apiClient<AuthUser>({ path: '/auth/me' }),
}
