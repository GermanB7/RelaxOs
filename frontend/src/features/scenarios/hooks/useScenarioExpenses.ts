import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { scenariosApi, type ExpensePayload } from '../api/scenariosApi'

export function useScenarioExpenses(scenarioId: number) {
  const queryClient = useQueryClient()

  const categoriesQuery = useQuery({
    queryKey: ['expense-categories'],
    queryFn: scenariosApi.listCategories,
  })

  const expensesQuery = useQuery({
    queryKey: ['scenario-expenses', scenarioId],
    queryFn: () => scenariosApi.listExpenses(scenarioId),
    enabled: Number.isFinite(scenarioId),
  })

  const invalidateScenarioState = () => {
    queryClient.invalidateQueries({
      queryKey: ['scenario-expenses', scenarioId],
    })
    queryClient.invalidateQueries({
      queryKey: ['scenario-summary', scenarioId],
    })
  }

  const createExpense = useMutation({
    mutationFn: (payload: ExpensePayload) =>
      scenariosApi.createExpense(scenarioId, payload),
    onSuccess: invalidateScenarioState,
  })

  const updateExpense = useMutation({
    mutationFn: ({
      expenseId,
      payload,
    }: {
      expenseId: number
      payload: ExpensePayload
    }) => scenariosApi.updateExpense(scenarioId, expenseId, payload),
    onSuccess: invalidateScenarioState,
  })

  const deleteExpense = useMutation({
    mutationFn: (expenseId: number) =>
      scenariosApi.deleteExpense(scenarioId, expenseId),
    onSuccess: invalidateScenarioState,
  })

  return {
    categoriesQuery,
    expensesQuery,
    createExpense,
    updateExpense,
    deleteExpense,
  }
}
