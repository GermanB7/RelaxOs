import { Link } from 'react-router-dom'
import type { DashboardSummary } from '../../../shared/types/domain'

type Props = {
  events: DashboardSummary['recentDecisionEvents']
}

export function DecisionTimelinePreview({ events }: Props) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex items-center justify-between gap-3">
        <h2 className="text-base font-semibold text-slate-950">
          Recent decisions
        </h2>
        <Link to="/decisions" className="text-sm font-semibold text-teal-700">
          Timeline
        </Link>
      </div>
      {events.length === 0 ? (
        <p className="mt-3 text-sm text-slate-600">
          Decisions will appear here when you calculate, compare, buy, postpone,
          or evaluate.
        </p>
      ) : (
        <div className="mt-3 grid gap-2">
          {events.map((event) => (
            <article key={event.id} className="rounded-md bg-slate-50 p-3">
              <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
                {event.decisionType.replaceAll('_', ' ')}
              </p>
              <p className="mt-1 text-sm font-medium text-slate-950">
                {event.question}
              </p>
            </article>
          ))}
        </div>
      )}
    </section>
  )
}
