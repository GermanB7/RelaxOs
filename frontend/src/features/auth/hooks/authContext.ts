import { createContext } from 'react'
import type { AuthUser } from '../../../shared/types/domain'
import type { LoginPayload, RegisterPayload } from '../api/authApi'

export type AuthContextValue = {
  user: AuthUser | null
  token: string | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (payload: LoginPayload) => Promise<void>
  register: (payload: RegisterPayload) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthContextValue | null>(null)
