import type { ActiveModeSummary, AdaptiveMode } from '../../../shared/types/domain'

type ModePolicyListProps = {
  mode: AdaptiveMode | ActiveModeSummary
}

export function ModePolicyList({ mode }: ModePolicyListProps) {
  return (
    <dl className="grid gap-2 text-sm sm:grid-cols-2">
      <div>
        <dt className="text-slate-500">Spending</dt>
        <dd className="font-semibold text-slate-900">{mode.spendingPolicy}</dd>
      </div>
      <div>
        <dt className="text-slate-500">Alerts</dt>
        <dd className="font-semibold text-slate-900">{mode.alertPolicy}</dd>
      </div>
      <div>
        <dt className="text-slate-500">Purchases</dt>
        <dd className="font-semibold text-slate-900">{mode.purchasePolicy}</dd>
      </div>
      <div>
        <dt className="text-slate-500">Routine</dt>
        <dd className="font-semibold text-slate-900">{mode.routinePolicy}</dd>
      </div>
    </dl>
  )
}
