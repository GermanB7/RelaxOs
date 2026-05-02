export type ScenarioStatus = 'DRAFT' | 'ACTIVE' | 'ARCHIVED'

export type ExpenseFrequency = 'MONTHLY' | 'WEEKLY' | 'YEARLY' | 'ONE_TIME'

export type Profile = {
  displayName: string | null
  city: string | null
  currency: string
  monthlyIncome: number | null
}

export type Scenario = {
  id: number
  name: string
  monthlyIncome: number
  emergencyFundCurrent: number
  emergencyFundTarget: number | null
  status: ScenarioStatus
  createdAt: string
  updatedAt: string
}

export type ScenarioSummary = {
  scenarioId: number
  monthlyIncome: number
  totalMonthlyExpenses: number
  estimatedMonthlyAvailable: number
  expenseCount: number
}

export type ExpenseCategory = {
  id: number
  code: string
  name: string
  isActive: boolean
}

export type ScenarioExpense = {
  id: number
  scenarioId: number
  categoryId: number
  categoryCode: string
  categoryName: string
  name: string
  amount: number
  frequency: ExpenseFrequency
  isEssential: boolean
  monthlyEquivalent: number
}
