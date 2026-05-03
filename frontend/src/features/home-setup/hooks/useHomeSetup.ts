import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  homeSetupApi,
  type CreateCustomPurchaseItemRequest,
  type UpdatePurchaseItemRequest,
  type UpdatePurchaseStatusRequest,
} from '../api/homeSetupApi'

export function usePurchaseCatalog(tier?: string, category?: string) {
  return useQuery({
    queryKey: ['purchase-catalog', tier, category],
    queryFn: () => homeSetupApi.getCatalog(tier, category),
  })
}

export function useHomeRoadmap(
  scenarioId?: number,
  status?: string,
  tier?: string,
  category?: string
) {
  return useQuery({
    queryKey: ['home-roadmap', scenarioId, status, tier, category],
    queryFn: () => homeSetupApi.getRoadmap(scenarioId, status, tier, category),
  })
}

export function useHomeSetupSummary(scenarioId?: number) {
  return useQuery({
    queryKey: ['home-setup-summary', scenarioId],
    queryFn: () => homeSetupApi.getSummary(scenarioId),
  })
}

export function useInitializeRoadmap() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (scenarioId?: number) => homeSetupApi.initializeRoadmap(scenarioId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['home-roadmap'] })
      queryClient.invalidateQueries({ queryKey: ['home-setup-summary'] })
    },
  })
}

export function useCreateCustomPurchaseItem() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: CreateCustomPurchaseItemRequest) =>
      homeSetupApi.createCustomItem(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['home-roadmap'] })
      queryClient.invalidateQueries({ queryKey: ['home-setup-summary'] })
    },
  })
}

export function useUpdatePurchaseItem() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: UpdatePurchaseItemRequest }) =>
      homeSetupApi.updateItem(id, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['home-roadmap'] })
      queryClient.invalidateQueries({ queryKey: ['home-setup-summary'] })
    },
  })
}

export function useUpdatePurchaseItemStatus() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: UpdatePurchaseStatusRequest }) =>
      homeSetupApi.updateItemStatus(id, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['home-roadmap'] })
      queryClient.invalidateQueries({ queryKey: ['home-setup-summary'] })
      queryClient.invalidateQueries({ queryKey: ['recommendations'] })
    },
  })
}

export function useDeleteCustomPurchaseItem() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => homeSetupApi.deleteCustomItem(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['home-roadmap'] })
      queryClient.invalidateQueries({ queryKey: ['home-setup-summary'] })
    },
  })
}
