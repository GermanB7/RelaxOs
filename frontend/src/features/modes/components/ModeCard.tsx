import type { AdaptiveMode } from '../../../shared/types/domain'
import { ModePolicyList } from './ModePolicyList'

type ModeCardProps = {
  mode: AdaptiveMode
  onActivate: (mode: AdaptiveMode) => void
  isPending?: boolean
}

export function ModeCard({ mode, onActivate, isPending }: ModeCardProps) {
  return (
    <article className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <h3 className="text-base font-semibold text-slate-950">{mode.name}</h3>
          <p className="mt-1 text-sm leading-6 text-slate-600">
            {mode.objective}
          </p>
          <p className="mt-2 text-xs font-semibold text-slate-500">
            {mode.recommendedMinDays}-{mode.recommendedMaxDays} days · {mode.intensityLevel}
          </p>
        </div>
        <button
          type="button"
          disabled={isPending}
          onClick={() => onActivate(mode)}
          className="rounded-md bg-teal-700 px-3 py-2 text-sm font-semibold text-white hover:bg-teal-800 disabled:opacity-60"
        >
          Activate
        </button>
      </div>
      <div className="mt-4">
        <ModePolicyList mode={mode} />
      </div>
    </article>
  )
}
