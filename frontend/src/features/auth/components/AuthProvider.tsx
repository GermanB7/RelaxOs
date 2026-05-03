import { type ReactNode, useEffect, useMemo, useState } from 'react'
import type { AuthUser } from '../../../shared/types/domain'
import { authApi } from '../api/authApi'
import { clearAuthToken, getAuthToken, setAuthToken } from '../api/authToken'
import { AuthContext, type AuthContextValue } from '../hooks/authContext'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => getAuthToken())
  const [user, setUser] = useState<AuthUser | null>(null)
  const [isLoading, setIsLoading] = useState(Boolean(token))

  useEffect(() => {
    if (!token) {
      return
    }
    let cancelled = false
    authApi
      .me()
      .then((currentUser) => {
        if (!cancelled) setUser(currentUser)
      })
      .catch(() => {
        if (!cancelled) {
          clearAuthToken()
          setToken(null)
          setUser(null)
        }
      })
      .finally(() => {
        if (!cancelled) setIsLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [token])

  useEffect(() => {
    const onUnauthorized = () => {
      setToken(null)
      setUser(null)
    }
    window.addEventListener('tranquiloos:unauthorized', onUnauthorized)
    return () => window.removeEventListener('tranquiloos:unauthorized', onUnauthorized)
  }, [])

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      token,
      isAuthenticated: Boolean(token),
      isLoading,
      login: async (payload) => {
        const response = await authApi.login(payload)
        setAuthToken(response.token)
        setToken(response.token)
        setUser(response.user)
        window.location.assign('/')
      },
      register: async (payload) => {
        const response = await authApi.register(payload)
        setAuthToken(response.token)
        setToken(response.token)
        setUser(response.user)
        window.location.assign('/')
      },
      logout: () => {
        clearAuthToken()
        setToken(null)
        setUser(null)
        window.location.assign('/login')
      },
    }),
    [isLoading, token, user],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
