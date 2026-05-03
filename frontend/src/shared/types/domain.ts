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

export type RecommendationStatus =
  | 'OPEN'
  | 'ACCEPTED'
  | 'POSTPONED'
  | 'DISMISSED'
  | 'EXPIRED'

export type RecommendationSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

export type RecommendationType =
  | 'FINANCIAL_RISK'
  | 'EMERGENCY_FUND'
  | 'RENT'
  | 'MONTHLY_MARGIN'
  | 'DEBT'
  | 'FOOD_DELIVERY'
  | 'DATA_QUALITY'
  | 'HOUSEHOLD_SETUP'
  | 'GENERAL'

export type Recommendation = {
  id: number
  userId: number
  scenarioId: number | null
  scoreSnapshotId: number | null
  type: RecommendationType
  severity: RecommendationSeverity
  priority: number
  title: string
  message: string
  actionLabel: string | null
  actionType: string | null
  sourceRuleKey: string | null
  status: RecommendationStatus
  createdAt: string
  updatedAt: string
}

export type RecalculateRecommendationsResponse = {
  scenarioId: number
  generatedCount: number
  recommendations: Recommendation[]
}

export type DecisionEvent = {
  id: number
  userId: number
  scenarioId: number | null
  recommendationId: number | null
  decisionType: string
  question: string
  chosenOption: string | null
  scoreBefore: number | null
  scoreAfter: number | null
  reason: string | null
  createdAt: string
}

export type ModeCode =
  | 'WAR_MODE'
  | 'STABLE_MODE'
  | 'LIVE_LIFE_MODE'
  | 'RECOVERY_MODE'
  | 'AGGRESSIVE_SAVING_MODE'
  | 'RESET_MODE'

export type ModeActivationStatus = 'ACTIVE' | 'ENDED' | 'CANCELLED' | 'EXPIRED'
export type ModeIntensityLevel = 'LOW' | 'MEDIUM' | 'HIGH'
export type SpendingPolicy = 'STRICT' | 'NORMAL' | 'FLEXIBLE' | 'MINIMAL'
export type AlertPolicy = 'STRICT' | 'NORMAL' | 'SOFT' | 'MINIMAL'
export type PurchasePolicy =
  | 'FREEZE_NON_ESSENTIAL'
  | 'PLAN_ONLY'
  | 'NORMAL'
  | 'FLEXIBLE'
export type RoutinePolicy = 'STRICT' | 'NORMAL' | 'MINIMUM_VIABLE' | 'RESET'

export type AdaptiveMode = {
  id: number
  code: ModeCode
  name: string
  description: string | null
  objective: string | null
  recommendedMinDays: number | null
  recommendedMaxDays: number | null
  intensityLevel: ModeIntensityLevel
  spendingPolicy: SpendingPolicy
  alertPolicy: AlertPolicy
  purchasePolicy: PurchasePolicy
  routinePolicy: RoutinePolicy
  sortOrder: number
}

export type ModeActivation = {
  activationId: number
  modeCode: ModeCode
  modeName: string
  scenarioId: number | null
  objective: string | null
  intensityLevel: ModeIntensityLevel
  spendingPolicy: SpendingPolicy
  alertPolicy: AlertPolicy
  purchasePolicy: PurchasePolicy
  routinePolicy: RoutinePolicy
  status: ModeActivationStatus
  activatedAt: string
  expiresAt: string | null
  endedAt: string | null
  notes: string | null
}

export type ActiveModeSummary = {
  hasActiveMode: boolean
  activationId: number | null
  modeCode: ModeCode | null
  modeName: string | null
  objective: string | null
  intensityLevel: ModeIntensityLevel | null
  spendingPolicy: SpendingPolicy | null
  alertPolicy: AlertPolicy | null
  purchasePolicy: PurchasePolicy | null
  routinePolicy: RoutinePolicy | null
  scenarioId: number | null
  activatedAt: string | null
  expiresAt: string | null
  daysRemaining: number | null
  guidance: string[]
}

export type MealEffortLevel = 'LOW' | 'MEDIUM' | 'HIGH'
export type MealCravingLevel = 'SIMPLE' | 'COMFORT' | 'RICH' | 'HEAVY'
export type MealBudgetLevel = 'LOW' | 'MEDIUM' | 'HIGH'

export type MealCatalogItem = {
  id: number
  code: string
  name: string
  category: string
  estimatedCostMin: number | null
  estimatedCostMax: number | null
  prepTimeMinutes: number
  effortLevel: MealEffortLevel
  cravingLevel: MealCravingLevel
  budgetLevel: MealBudgetLevel
  requiredEquipment: string | null
  suggestedMode: ModeCode | null
  description: string | null
}

export type MealSuggestion = {
  id: number
  name: string
  estimatedCostMin: number | null
  estimatedCostMax: number | null
  prepTimeMinutes: number
  effortLevel: MealEffortLevel
  cravingLevel: MealCravingLevel
  budgetLevel: MealBudgetLevel
  requiredEquipment: string | null
  fitScore: number
  reason: string
}

export type MealSuggestionResponse = {
  activeModeCode: ModeCode | null
  suggestions: MealSuggestion[]
}

export type DashboardProfile = {
  displayName: string | null
  city: string | null
  currency: string
}

export type DashboardScenarioSummary = {
  id: number
  name: string
  monthlyIncome: number
  totalMonthlyExpenses: number
  estimatedMonthlyAvailable: number
}

export type DashboardScore = {
  score: number
  status: ScoreStatus
  summary: string | null
}

export type DashboardRisk = {
  severity: RiskSeverity
  title: string
}

export type DashboardRecommendation = {
  id: number
  scenarioId: number | null
  severity: RecommendationSeverity
  title: string
  actionLabel: string | null
  actionType: string | null
}

export type DashboardActiveMode = {
  hasActiveMode: boolean
  modeCode: ModeCode | null
  modeName: string | null
  scenarioId: number | null
  guidance: string[]
}

export type DashboardHomeSetup = {
  hasRoadmap: boolean
  tier1CompletionPercentage: number
  nextBestPurchaseName: string | null
  pendingItems: number
}

export type DashboardMealPlanner = {
  suggestedCta: string
}

export type Dashboard = {
  profile: DashboardProfile
  primaryScenario: DashboardScenarioSummary | null
  latestScore: DashboardScore | null
  topRisks: DashboardRisk[]
  topRecommendations: DashboardRecommendation[]
  activeMode: DashboardActiveMode
  homeSetup: DashboardHomeSetup
  mealPlanner: DashboardMealPlanner
}

export type AuthUser = {
  id: number
  email: string
  displayName: string | null
  city: string | null
  currency: string
}

export type AuthResponse = {
  token: string
  tokenType: 'Bearer'
  user: AuthUser
}
