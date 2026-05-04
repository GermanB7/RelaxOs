import { apiClient } from '../../../shared/api/apiClient'
import type { Dashboard, DashboardSummary } from '../../../shared/types/domain'

export const dashboardApi = {
  getDashboard: () => apiClient<Dashboard>({ path: '/dashboard' }),
  getDashboardSummary: () =>
    apiClient<DashboardSummary>({ path: '/dashboard/summary' }),
}
