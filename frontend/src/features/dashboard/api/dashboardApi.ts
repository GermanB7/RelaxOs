import { apiClient } from '../../../shared/api/apiClient'
import type { Dashboard } from '../../../shared/types/domain'

export const dashboardApi = {
  getDashboard: () => apiClient<Dashboard>({ path: '/dashboard' }),
}
