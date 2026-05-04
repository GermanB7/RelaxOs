import { Link } from 'react-router-dom'
import { EmptyState } from '../../../shared/components/EmptyState'
import { PageHeader } from '../../../shared/components/PageHeader'
import { ScenarioForm } from '../components/ScenarioForm'
import { ScenarioList } from '../components/ScenarioList'
import { useScenarios } from '../hooks/useScenarios'

export function ScenariosPage() {
  const { scenariosQuery, createScenario, duplicateScenario } = useScenarios()
  const scenarios = scenariosQuery.data ?? []

  return (
    <>
      <PageHeader
        title="Scenarios"
        description="Create and compare the basic monthly shape of an independence scenario. Totals are calculated by the backend."
      />

      <section className="mt-6">
        <h3 className="mb-3 text-base font-semibold text-slate-950">
          New scenario
        </h3>
        <ScenarioForm
          submitLabel="Create scenario"
          onSubmit={async (payload) => {
            await createScenario.mutateAsync(payload)
          }}
        />
      </section>

      <section className="mt-8">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <h3 className="text-base font-semibold text-slate-950">
            Your scenarios
          </h3>
          {scenarios.length >= 2 && (
            <Link
              to="/scenarios/compare"
              className="text-sm font-semibold text-teal-700"
            >
              Compare scenarios
            </Link>
          )}
        </div>
        {scenariosQuery.isLoading && (
          <p className="mt-4 text-sm text-slate-600">Loading scenarios...</p>
        )}
        {scenariosQuery.isError && (
          <p className="mt-4 text-sm text-red-700">
            Could not load scenarios.
          </p>
        )}
        {!scenariosQuery.isLoading && scenarios.length === 0 && (
          <EmptyState
            title="No scenarios created"
            description="Create a simple scenario above to start adding expenses and see the backend monthly summary."
            actionLabel="Go to settings"
            to="/settings"
          />
        )}
        {scenarios.length > 0 && (
          <ScenarioList
            scenarios={scenarios}
            onDuplicate={(scenarioId) => duplicateScenario.mutate(scenarioId)}
            duplicatingId={duplicateScenario.variables}
          />
        )}
      </section>
    </>
  )
}
