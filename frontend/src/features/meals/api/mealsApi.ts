import { apiClient } from '../../../shared/api/apiClient'
import type {
  MealBudgetLevel,
  MealCatalogItem,
  MealCravingLevel,
  MealEffortLevel,
  MealSuggestionResponse,
} from '../../../shared/types/domain'

export type MealSuggestionPayload = {
  cravingLevel: MealCravingLevel
  maxPrepTimeMinutes: number
  effortLevel: MealEffortLevel
  budgetLevel: MealBudgetLevel
  availableEquipment: string[]
  scenarioId?: number
}

export const mealsApi = {
  getCatalog: (params?: {
    effortLevel?: MealEffortLevel
    budgetLevel?: MealBudgetLevel
    cravingLevel?: MealCravingLevel
    maxPrepTimeMinutes?: number
    equipment?: string
    modeCode?: string
  }) => {
    const searchParams = new URLSearchParams()
    if (params?.effortLevel) searchParams.set('effortLevel', params.effortLevel)
    if (params?.budgetLevel) searchParams.set('budgetLevel', params.budgetLevel)
    if (params?.cravingLevel) searchParams.set('cravingLevel', params.cravingLevel)
    if (params?.maxPrepTimeMinutes) searchParams.set('maxPrepTimeMinutes', String(params.maxPrepTimeMinutes))
    if (params?.equipment) searchParams.set('equipment', params.equipment)
    if (params?.modeCode) searchParams.set('modeCode', params.modeCode)
    const query = searchParams.toString()
    return apiClient<MealCatalogItem[]>({
      path: `/meals/catalog${query ? `?${query}` : ''}`,
    })
  },
  suggest: (payload: MealSuggestionPayload) =>
    apiClient<MealSuggestionResponse>({
      path: '/meals/suggest',
      method: 'POST',
      body: JSON.stringify(payload),
    }),
}
