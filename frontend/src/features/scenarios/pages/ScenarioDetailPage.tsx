import { Link, useParams } from 'react-router-dom'
import { PageHeader } from '../../../shared/components/PageHeader'
import { RiskFactorsList } from '../../scoring/components/RiskFactorsList'
import { ScoreCard } from '../../scoring/components/ScoreCard'
import { ScoreFactorsList } from '../../scoring/components/ScoreFactorsList'
import { useScenarioScore } from '../../scoring/hooks/useScenarioScore'
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
  const { latestScoreQuery, calculateScore } =
    useScenarioScore(parsedScenarioId)

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
        <div className="mb-3 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h3 className="text-base font-semibold text-slate-950">
              Independence score
            </h3>
            <p className="mt-1 text-sm text-slate-600">
              Calculated and persisted by the backend.
            </p>
          </div>
          {!latestScoreQuery.data && (
            <button
              type="button"
              onClick={() => calculateScore.mutate()}
              disabled={calculateScore.isPending}
              className="rounded-md bg-teal-700 px-4 py-2 text-sm font-semibold text-white hover:bg-teal-800 disabled:opacity-60"
            >
              {calculateScore.isPending ? 'Calculating...' : 'Calculate Score'}
            </button>
          )}
        </div>

        {latestScoreQuery.isLoading && (
          <p className="text-sm text-slate-600">Loading latest score...</p>
        )}

        {latestScoreQuery.isError && !latestScoreQuery.data && (
          <section className="rounded-lg border border-dashed border-slate-300 bg-white p-5">
            <h3 className="text-base font-semibold text-slate-950">
              No score calculated yet
            </h3>
            <p className="mt-2 text-sm leading-6 text-slate-600">
              Add the core expenses for this scenario, then calculate the first
              backend score snapshot.
            </p>
          </section>
        )}

        {latestScoreQuery.data && (
          <div className="grid gap-4 lg:grid-cols-[320px_1fr]">
            <ScoreCard
              score={latestScoreQuery.data}
              onCalculate={() => calculateScore.mutate()}
              isCalculating={calculateScore.isPending}
            />
            <div className="grid gap-4">
              <ScoreFactorsList factors={latestScoreQuery.data.factors} />
              <RiskFactorsList risks={latestScoreQuery.data.risks} />
            </div>
          </div>
        )}
      </section>

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
