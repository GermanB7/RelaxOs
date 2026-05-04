import { Link } from 'react-router-dom'
import type { DashboardSummary } from '../../../shared/types/domain'

type Props = {
  homePriority: DashboardSummary['homePriority']
}

export function HomePriorityCard({ homePriority }: Props) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h2 className="text-base font-semibold text-slate-950">Home priority</h2>
      {homePriority.hasRoadmap ? (
        <p className="mt-2 text-sm text-slate-700">
          Next purchase: {homePriority.nextBestPurchaseName ?? 'Review roadmap'}.
          Tier 1 is {homePriority.tier1CompletionPercentage}% complete.
        </p>
      ) : (
        <p className="mt-2 text-sm text-slate-600">
          Initialize your roadmap before buying random home items.
        </p>
      )}
      <Link
        to="/home-setup"
        className="mt-3 inline-flex rounded-md border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"
      >
        Open home setup
      </Link>
    </section>
  )
}
