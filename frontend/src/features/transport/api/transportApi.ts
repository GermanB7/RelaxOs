import { apiClient } from '../../../shared/api/apiClient'
import type {
  TransportEvaluation,
  TransportOption,
  TransportOptionType,
} from '../../../shared/types/domain'

export type TransportOptionPayload = {
  optionType: TransportOptionType
  monthlyCost: number
  tripsPerWeek: number
  averageTimeMinutes: number
  comfortScore: number
  safetyScore: number
  flexibilityScore: number
  parkingCost?: number
  maintenanceCost?: number
  insuranceCost?: number
  fuelCost?: number
  upfrontCost?: number
  hasParking?: boolean
  hasLicense?: boolean
  notes?: string
}

export const transportApi = {
  listOptions: (scenarioId: number) =>
    apiClient<TransportOption[]>({
      path: `/scenarios/${scenarioId}/transport-options`,
    }),
  createOption: (scenarioId: number, payload: TransportOptionPayload) =>
    apiClient<TransportOption>({
      path: `/scenarios/${scenarioId}/transport-options`,
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  deleteOption: (id: number) =>
    apiClient<void>({
      path: `/transport-options/${id}`,
      method: 'DELETE',
    }),
  evaluate: (scenarioId: number) =>
    apiClient<TransportEvaluation>({
      path: `/scenarios/${scenarioId}/transport/evaluate`,
      method: 'POST',
    }),
  latest: (scenarioId: number) =>
    apiClient<TransportEvaluation>({
      path: `/scenarios/${scenarioId}/transport/latest`,
    }),
}
