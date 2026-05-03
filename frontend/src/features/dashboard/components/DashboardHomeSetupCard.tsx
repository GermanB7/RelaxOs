import { Link } from 'react-router-dom'
import type { DashboardHomeSetup } from '../../../shared/types/domain'

type DashboardHomeSetupCardProps = {
  homeSetup: DashboardHomeSetup
}

export function DashboardHomeSetupCard({ homeSetup }: DashboardHomeSetupCardProps) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
            Home setup
          </p>
          <h2 className="mt-1 text-base font-semibold text-slate-950">
            {homeSetup.hasRoadmap
              ? `${homeSetup.tier1CompletionPercentage}% Tier 1 complete`
              : 'Roadmap not initialized'}
          </h2>
        </div>
        <Link
          to="/home-setup"
          className="text-sm font-semibold text-teal-700 hover:text-teal-900"
        >
          Open
        </Link>
      </div>
      <p className="mt-3 text-sm leading-6 text-slate-600">
        {homeSetup.hasRoadmap
          ? `${homeSetup.pendingItems} pending items. Next: ${
              homeSetup.nextBestPurchaseName ?? 'review roadmap'
            }.`
          : 'Initialize the basic purchase roadmap when your scenario is ready.'}
      </p>
    </section>
  )
}
