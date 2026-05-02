import { Link } from 'react-router-dom'
import type { Scenario } from '../../../shared/types/domain'

type ScenarioListProps = {
  scenarios: Scenario[]
  onDuplicate: (scenarioId: number) => void
  duplicatingId?: number
}

export function ScenarioList({
  scenarios,
  onDuplicate,
  duplicatingId,
}: ScenarioListProps) {
  return (
    <div className="mt-6 grid gap-3">
      {scenarios.map((scenario) => (
        <article
          key={scenario.id}
          className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm"
        >
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h3 className="text-base font-semibold text-slate-950">
                {scenario.name}
              </h3>
              <p className="mt-1 text-sm text-slate-600">
                Income {scenario.monthlyIncome.toLocaleString()} ·{' '}
                {scenario.status}
              </p>
            </div>
            <div className="flex gap-2">
              <Link
                to={`/scenarios/${scenario.id}`}
                className="rounded-md border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50"
              >
                Open
              </Link>
              <button
                type="button"
                onClick={() => onDuplicate(scenario.id)}
                disabled={duplicatingId === scenario.id}
                className="rounded-md bg-slate-900 px-3 py-2 text-sm font-semibold text-white hover:bg-slate-700 disabled:opacity-60"
              >
                {duplicatingId === scenario.id ? 'Duplicating...' : 'Duplicate'}
              </button>
            </div>
          </div>
        </article>
      ))}
    </div>
  )
}
