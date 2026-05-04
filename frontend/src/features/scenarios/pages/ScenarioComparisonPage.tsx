import { useState } from 'react'
import { Link } from 'react-router-dom'
import { PageHeader } from '../../../shared/components/PageHeader'
import { useScenarioComparison } from '../hooks/useScenarioComparison'
import { useScenarios } from '../hooks/useScenarios'

function percent(value: number) {
  return `${(value * 100).toFixed(1)}%`
}

export function ScenarioComparisonPage() {
  const { scenariosQuery } = useScenarios()
  const { compareScenarios, selectScenario } = useScenarioComparison()
  const [selectedIds, setSelectedIds] = useState<number[]>([])
  const scenarios = scenariosQuery.data ?? []
  const comparison = compareScenarios.data

  const toggle = (scenarioId: number) => {
    setSelectedIds((current) =>
      current.includes(scenarioId)
        ? current.filter((id) => id !== scenarioId)
        : current.length < 4
          ? [...current, scenarioId]
          : current,
    )
  }

  return (
    <>
      <Link className="text-sm font-semibold text-teal-700" to="/scenarios">
        Back to scenarios
      </Link>
      <PageHeader
        title="Scenario Comparison"
        description="Compare options as decisions, not just as budgets."
      />

      <section className="mt-6 rounded-lg border border-slate-200 bg-white p-4">
        <h3 className="text-base font-semibold text-slate-950">
          Select 2-4 scenarios
        </h3>
        <div className="mt-3 grid gap-2 sm:grid-cols-2">
          {scenarios.map((scenario) => (
            <label
              key={scenario.id}
              className="flex items-center gap-3 rounded-md border border-slate-200 p-3 text-sm"
            >
              <input
                type="checkbox"
                checked={selectedIds.includes(scenario.id)}
                onChange={() => toggle(scenario.id)}
              />
              <span className="font-medium text-slate-900">{scenario.name}</span>
            </label>
          ))}
        </div>
        <button
          type="button"
          disabled={selectedIds.length < 2 || compareScenarios.isPending}
          onClick={() => compareScenarios.mutate(selectedIds)}
          className="mt-4 rounded-md bg-teal-700 px-4 py-2 text-sm font-semibold text-white hover:bg-teal-800 disabled:opacity-60"
        >
          {compareScenarios.isPending ? 'Comparing...' : 'Compare scenarios'}
        </button>
      </section>

      {comparison && (
        <section className="mt-6 space-y-4">
          <article className="rounded-lg border border-teal-200 bg-teal-50 p-4">
            <p className="text-xs font-semibold uppercase text-teal-700">
              Recommended option
            </p>
            <h3 className="mt-1 text-lg font-semibold text-slate-950">
              {
                comparison.comparedScenarios.find(
                  (item) => item.scenarioId === comparison.recommendedScenarioId,
                )?.name
              }
            </h3>
            <p className="mt-2 text-sm leading-6 text-slate-700">
              {comparison.recommendationReason}
            </p>
            <button
              type="button"
              disabled={selectScenario.isPending}
              onClick={() =>
                selectScenario.mutate({
                  scenarioId: comparison.recommendedScenarioId,
                  reason: comparison.recommendationReason,
                })
              }
              className="mt-4 rounded-md bg-slate-950 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
            >
              Confirm selected scenario
            </button>
          </article>

          <div className="overflow-x-auto rounded-lg border border-slate-200 bg-white">
            <table className="min-w-full text-left text-sm">
              <thead className="bg-slate-50 text-xs uppercase text-slate-500">
                <tr>
                  <th className="px-3 py-2">Scenario</th>
                  <th className="px-3 py-2">Score</th>
                  <th className="px-3 py-2">Savings</th>
                  <th className="px-3 py-2">Savings rate</th>
                  <th className="px-3 py-2">Emergency</th>
                  <th className="px-3 py-2">Main risk</th>
                </tr>
              </thead>
              <tbody>
                {comparison.comparedScenarios.map((item) => (
                  <tr key={item.scenarioId} className="border-t border-slate-100">
                    <td className="px-3 py-2 font-medium text-slate-950">
                      {item.name}
                    </td>
                    <td className="px-3 py-2">
                      {item.latestScore ?? 'Missing'}
                      {item.scoreStale && ' stale'}
                    </td>
                    <td className="px-3 py-2">{item.estimatedSavings}</td>
                    <td className="px-3 py-2">{percent(item.savingsRate)}</td>
                    <td className="px-3 py-2">
                      {item.emergencyCoverage.toFixed(2)} mo
                    </td>
                    <td className="px-3 py-2">
                      {item.mainRiskSeverity ?? 'None'} {item.mainRisk ?? ''}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      )}
    </>
  )
}
