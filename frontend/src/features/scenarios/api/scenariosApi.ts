import { apiClient } from '../../../shared/api/apiClient'
import type {
  ExpenseCategory,
  ExpenseFrequency,
  Scenario,
  ScenarioExpense,
  ScenarioStatus,
  ScenarioSummary,
} from '../../../shared/types/domain'

export type ScenarioPayload = {
  name: string
  monthlyIncome: number
  emergencyFundCurrent: number
  emergencyFundTarget?: number | null
}

export type UpdateScenarioPayload = ScenarioPayload & {
  status: ScenarioStatus
}

export type ExpensePayload = {
  categoryId: number
  name: string
  amount: number
  frequency: ExpenseFrequency
  isEssential: boolean
}

export const scenariosApi = {
  listScenarios: () => apiClient<Scenario[]>({ path: '/scenarios' }),
  getScenario: (scenarioId: number) =>
    apiClient<Scenario>({ path: `/scenarios/${scenarioId}` }),
  createScenario: (payload: ScenarioPayload) =>
    apiClient<Scenario>({
      path: '/scenarios',
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  updateScenario: (scenarioId: number, payload: UpdateScenarioPayload) =>
    apiClient<Scenario>({
      path: `/scenarios/${scenarioId}`,
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  duplicateScenario: (scenarioId: number) =>
    apiClient<Scenario>({
      path: `/scenarios/${scenarioId}/duplicate`,
      method: 'POST',
    }),
  getSummary: (scenarioId: number) =>
    apiClient<ScenarioSummary>({ path: `/scenarios/${scenarioId}/summary` }),
  listCategories: () =>
    apiClient<ExpenseCategory[]>({ path: '/expense-categories' }),
  listExpenses: (scenarioId: number) =>
    apiClient<ScenarioExpense[]>({
      path: `/scenarios/${scenarioId}/expenses`,
    }),
  createExpense: (scenarioId: number, payload: ExpensePayload) =>
    apiClient<ScenarioExpense>({
      path: `/scenarios/${scenarioId}/expenses`,
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  updateExpense: (
    scenarioId: number,
    expenseId: number,
    payload: ExpensePayload,
  ) =>
    apiClient<ScenarioExpense>({
      path: `/scenarios/${scenarioId}/expenses/${expenseId}`,
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  deleteExpense: (scenarioId: number, expenseId: number) =>
    apiClient<void>({
      path: `/scenarios/${scenarioId}/expenses/${expenseId}`,
      method: 'DELETE',
    }),
}
