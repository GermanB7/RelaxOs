import { Link, useParams } from 'react-router-dom'
import { PageHeader } from '../../../shared/components/PageHeader'
import { ExpenseForm } from '../components/ExpenseForm'
import { ExpenseList } from '../components/ExpenseList'
import { ScenarioForm } from '../components/ScenarioForm'
import { ScenarioSummaryCard } from '../components/ScenarioSummaryCard'
import { useScenarioExpenses } from '../hooks/useScenarioExpenses'
import { useScenario } from '../hooks/useScenarios'

export function ScenarioDetailPage() {
  const { scenarioId } = useParams()
  const parsedScenarioId = Number(scenarioId)
  const { scenarioQuery, summaryQuery, updateScenario } =
    useScenario(parsedScenarioId)
  const {
    categoriesQuery,
    expensesQuery,
    createExpense,
    updateExpense,
    deleteExpense,
  } = useScenarioExpenses(parsedScenarioId)

  const categories = categoriesQuery.data ?? []
  const expenses = expensesQuery.data ?? []

  if (!Number.isFinite(parsedScenarioId)) {
    return <p className="text-sm text-red-700">Invalid scenario id.</p>
  }

  if (scenarioQuery.isLoading) {
    return <p className="text-sm text-slate-600">Loading scenario...</p>
  }

  if (!scenarioQuery.data) {
    return (
      <div>
        <p className="text-sm text-red-700">Scenario was not found.</p>
        <Link className="mt-4 inline-flex text-sm text-teal-700" to="/scenarios">
          Back to scenarios
        </Link>
      </div>
    )
  }

  return (
    <>
      <Link className="text-sm font-semibold text-teal-700" to="/scenarios">
        Back to scenarios
      </Link>
      <PageHeader
        title={scenarioQuery.data.name}
        description="Edit the scenario foundation, add expenses, and read the monthly summary returned by the backend."
      />

      <div className="mt-6 grid gap-6 lg:grid-cols-[1fr_320px]">
        <section>
          <h3 className="mb-3 text-base font-semibold text-slate-950">
            Scenario details
          </h3>
          <ScenarioForm
            initialValues={scenarioQuery.data}
            includeStatus
            submitLabel="Save scenario"
            onSubmit={async (payload) => {
              await updateScenario.mutateAsync({
                ...payload,
                status: 'status' in payload ? payload.status : 'DRAFT',
              })
            }}
          />
        </section>

        {summaryQuery.data && (
          <ScenarioSummaryCard summary={summaryQuery.data} />
        )}
      </div>

      <section className="mt-8">
        <h3 className="mb-3 text-base font-semibold text-slate-950">
          Add expense
        </h3>
        {categoriesQuery.isLoading ? (
          <p className="text-sm text-slate-600">Loading categories...</p>
        ) : (
          <ExpenseForm
            categories={categories}
            submitLabel="Add expense"
            onSubmit={async (payload) => {
              await createExpense.mutateAsync(payload)
            }}
          />
        )}
      </section>

      <section className="mt-8">
        <h3 className="mb-3 text-base font-semibold text-slate-950">
          Expenses
        </h3>
        {expensesQuery.isLoading ? (
          <p className="text-sm text-slate-600">Loading expenses...</p>
        ) : (
          <ExpenseList
            expenses={expenses}
            categories={categories}
            onUpdate={async (expenseId, payload) => {
              await updateExpense.mutateAsync({ expenseId, payload })
            }}
            onDelete={(expenseId) => deleteExpense.mutate(expenseId)}
          />
        )}
      </section>
    </>
  )
}
