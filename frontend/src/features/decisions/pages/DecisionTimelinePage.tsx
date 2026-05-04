import { PageHeader } from '../../../shared/components/PageHeader'
import { useDecisions } from '../hooks/useDecisions'

export function DecisionTimelinePage() {
  const decisionsQuery = useDecisions()
  const decisions = decisionsQuery.data ?? []

  return (
    <>
      <PageHeader
        title="Decision Timeline"
        description="A product memory of meaningful decisions, not technical logs."
      />

      <section className="mt-6 space-y-3">
        {decisionsQuery.isLoading && (
          <p className="text-sm text-slate-600">Loading decisions...</p>
        )}
        {decisionsQuery.isError && (
          <p className="text-sm text-red-700">Could not load decisions.</p>
        )}
        {!decisionsQuery.isLoading && decisions.length === 0 && (
          <div className="rounded-lg border border-dashed border-slate-300 bg-white p-5">
            <h3 className="text-base font-semibold text-slate-950">
              No decisions yet
            </h3>
            <p className="mt-2 text-sm text-slate-600">
              Accept recommendations, compare scenarios, activate modes, or
              evaluate transport to build the timeline.
            </p>
          </div>
        )}
        {decisions.map((decision) => (
          <article
            key={decision.id}
            className="rounded-lg border border-slate-200 bg-white p-4"
          >
            <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
              <div>
                <p className="text-xs font-semibold uppercase text-teal-700">
                  {decision.decisionType.replaceAll('_', ' ')}
                </p>
                <h3 className="mt-1 text-base font-semibold text-slate-950">
                  {decision.question}
                </h3>
              </div>
              <time className="text-xs text-slate-500">
                {new Date(decision.createdAt).toLocaleString()}
              </time>
            </div>
            <dl className="mt-3 grid gap-2 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-slate-500">Chosen option</dt>
                <dd className="font-medium text-slate-900">
                  {decision.chosenOption ?? 'Not specified'}
                </dd>
              </div>
              <div>
                <dt className="text-slate-500">Scenario</dt>
                <dd className="font-medium text-slate-900">
                  {decision.scenarioId ?? 'Global'}
                </dd>
              </div>
              <div>
                <dt className="text-slate-500">Score before / after</dt>
                <dd className="font-medium text-slate-900">
                  {decision.scoreBefore ?? '-'} / {decision.scoreAfter ?? '-'}
                </dd>
              </div>
              <div>
                <dt className="text-slate-500">Reason</dt>
                <dd className="font-medium text-slate-900">
                  {decision.reason ?? 'No reason captured'}
                </dd>
              </div>
            </dl>
          </article>
        ))}
      </section>
    </>
  )
}
