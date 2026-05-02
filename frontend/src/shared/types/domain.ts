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

export type ScoreStatus =
  | 'NOT_RECOMMENDED'
  | 'VIABLE_BUT_FRAGILE'
  | 'STABLE_BUT_SENSITIVE'
  | 'TRANQUILO'

export type ConfidenceLevel = 'LOW' | 'MEDIUM' | 'HIGH'

export type RiskSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

export type ScoreFactor = {
  key: string
  label: string
  valueText: string
  impact: number
  weight: number
  explanation: string
}

export type RiskFactor = {
  key: string
  severity: RiskSeverity
  title: string
  explanation: string
}

export type ScenarioScore = {
  snapshotId: number
  scenarioId: number
  score: number
  status: ScoreStatus
  confidenceLevel: ConfidenceLevel
  summary: string
  factors: ScoreFactor[]
  risks: RiskFactor[]
  createdAt: string
}

export type ScoreHistoryItem = {
  snapshotId: number
  scenarioId: number
  score: number
  status: ScoreStatus
  confidenceLevel: ConfidenceLevel
  summary: string
  createdAt: string
}
