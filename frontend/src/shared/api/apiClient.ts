import { clearAuthToken, getAuthToken } from '../../features/auth/api/authToken'

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1'

type ApiClientOptions = RequestInit & {
  path: string
}

export class ApiClientError extends Error {
  readonly status: number

  constructor(message: string, status: number) {
    super(message)
    this.name = 'ApiClientError'
    this.status = status
  }
}

export async function apiClient<TResponse>({
  path,
  headers,
  ...init
}: ApiClientOptions): Promise<TResponse> {
  const token = getAuthToken()
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...headers,
    },
  })

  if (!response.ok) {
    if (response.status === 401) {
      clearAuthToken()
      window.dispatchEvent(new CustomEvent('tranquiloos:unauthorized'))
      if (!window.location.pathname.startsWith('/login')) {
        window.location.assign('/login')
      }
    }
    throw new ApiClientError(
      `Request failed with status ${response.status}`,
      response.status,
    )
  }

  if (response.status === 204) {
    return undefined as TResponse
  }

  return response.json() as Promise<TResponse>
}
