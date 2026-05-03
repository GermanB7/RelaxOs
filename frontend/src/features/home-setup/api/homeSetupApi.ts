import { apiClient } from '../../../shared/api/apiClient'

export interface PurchaseCatalogItem {
  id: number
  code: string
  name: string
  category: string
  tier: string
  estimatedMinPrice?: number
  estimatedMaxPrice?: number
  impactLevel: string
  urgencyLevel: string
  recommendedMoment?: string
  earlyPurchaseRisk?: string
  dependencies?: string
  rationale?: string
  isActive: boolean
  sortOrder: number
  createdAt: string
  updatedAt: string
}

export interface UserPurchaseItem {
  id: number
  name: string
  category: string
  tier: string
  estimatedPrice?: number
  actualPrice?: number
  status: string
  priority: number
  link?: string
  notes?: string
  purchasedAt?: string
  postponedUntil?: string
  createdAt: string
  updatedAt: string
}

export interface HomeSetupSummary {
  totalItems: number
  pendingItems: number
  boughtItems: number
  postponedItems: number
  wishlistItems: number
  tier1Total: number
  tier1Bought: number
  tier1CompletionPercentage: number
  estimatedPendingCost: number
  nextBestPurchase?: {
    id: number
    name: string
    tier: string
    category: string
    priority: number
  }
}

export interface CreateCustomPurchaseItemRequest {
  scenarioId?: number
  name: string
  category: string
  tier: string
  estimatedPrice?: number
  priority?: number
  link?: string
  notes?: string
}

export interface UpdatePurchaseItemRequest {
  name?: string
  category?: string
  tier?: string
  estimatedPrice?: number
  actualPrice?: number
  priority?: number
  link?: string
  notes?: string
}

export interface UpdatePurchaseStatusRequest {
  status: string
  actualPrice?: number
  reason?: string
}

export const homeSetupApi = {
  getCatalog: (tier?: string, category?: string) => {
    const params = new URLSearchParams()
    if (tier) params.append('tier', tier)
    if (category) params.append('category', category)
    const query = params.toString() ? `?${params.toString()}` : ''
    return apiClient<PurchaseCatalogItem[]>({ path: `/home/catalog${query}` })
  },

  initializeRoadmap: (scenarioId?: number) =>
    apiClient<void>({
      path: '/home/roadmap/initialize',
      method: 'POST',
      body: JSON.stringify({ scenarioId }),
    }),

  getRoadmap: (
    scenarioId?: number,
    status?: string,
    tier?: string,
    category?: string
  ) => {
    const params = new URLSearchParams()
    if (scenarioId) params.append('scenarioId', scenarioId.toString())
    if (status) params.append('status', status)
    if (tier) params.append('tier', tier)
    if (category) params.append('category', category)
    const query = params.toString() ? `?${params.toString()}` : ''
    return apiClient<UserPurchaseItem[]>({ path: `/home/roadmap${query}` })
  },

  getSummary: (scenarioId?: number) => {
    const params = new URLSearchParams()
    if (scenarioId) params.append('scenarioId', scenarioId.toString())
    const query = params.toString() ? `?${params.toString()}` : ''
    return apiClient<HomeSetupSummary>({ path: `/home/roadmap/summary${query}` })
  },

  createCustomItem: (request: CreateCustomPurchaseItemRequest) =>
    apiClient<UserPurchaseItem>({
      path: '/home/roadmap/items',
      method: 'POST',
      body: JSON.stringify(request),
    }),

  updateItem: (id: number, request: UpdatePurchaseItemRequest) =>
    apiClient<UserPurchaseItem>({
      path: `/home/roadmap/items/${id}`,
      method: 'PUT',
      body: JSON.stringify(request),
    }),

  updateItemStatus: (id: number, request: UpdatePurchaseStatusRequest) =>
    apiClient<UserPurchaseItem>({
      path: `/home/roadmap/items/${id}/status`,
      method: 'PATCH',
      body: JSON.stringify(request),
    }),

  deleteCustomItem: (id: number) =>
    apiClient<void>({
      path: `/home/roadmap/items/${id}`,
      method: 'DELETE',
    }),
}
