import { Link } from 'react-router-dom'
import { ModePolicyList } from './ModePolicyList'
import { useActiveMode } from '../hooks/useActiveMode'

type ActiveModeBannerProps = {
  scenarioId?: number
  compact?: boolean
}

export function ActiveModeBanner({ scenarioId, compact = false }: ActiveModeBannerProps) {
  const { activeModeQuery, endActiveMode } = useActiveMode()
  const active = activeModeQuery.data

  if (activeModeQuery.isLoading) {
    return <p className="text-sm text-slate-600">Loading active mode...</p>
  }

  if (!active?.hasActiveMode) {
    return (
      <section className="rounded-lg border border-dashed border-slate-300 bg-white p-5">
        <h3 className="text-base font-semibold text-slate-950">No active mode</h3>
        <p className="mt-2 text-sm text-slate-600">
          Choose a temporary policy when you need focus, recovery, reset, or flexibility.
        </p>
        <Link
          to={scenarioId ? `/modes?scenarioId=${scenarioId}` : '/modes'}
          className="mt-3 inline-flex rounded-md bg-teal-700 px-3 py-2 text-sm font-semibold text-white"
        >
          Activate mode
        </Link>
      </section>
    )
  }

  const relatedToScenario =
    scenarioId === undefined ||
    active.scenarioId === null ||
    active.scenarioId === scenarioId

  return (
    <section className="rounded-lg border border-teal-200 bg-teal-50 p-5">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <p className="text-xs font-semibold uppercase text-teal-700">
            Active mode {relatedToScenario ? '' : 'for another scenario'}
          </p>
          <h3 className="mt-1 text-lg font-semibold text-slate-950">
            {active.modeName}
          </h3>
          {active.objective && (
            <p className="mt-1 text-sm leading-6 text-slate-700">
              {active.objective}
            </p>
          )}
          {active.daysRemaining !== null && (
            <p className="mt-1 text-sm font-medium text-teal-900">
              {active.daysRemaining} days remaining
            </p>
          )}
        </div>
        <button
          type="button"
          disabled={endActiveMode.isPending}
          onClick={() => endActiveMode.mutate({ reason: 'Ended from UI' })}
          className="rounded-md border border-teal-300 bg-white px-3 py-2 text-sm font-semibold text-teal-800 hover:bg-teal-100 disabled:opacity-60"
        >
          {endActiveMode.isPending ? 'Ending...' : 'End mode'}
        </button>
      </div>

      {!compact && (
        <div className="mt-4 grid gap-4 md:grid-cols-[1fr_1fr]">
          <ModePolicyList mode={active} />
          <ul className="grid gap-2 text-sm text-slate-700">
            {active.guidance.map((item) => (
              <li key={item}>{item}</li>
            ))}
          </ul>
        </div>
      )}
    </section>
  )
}
