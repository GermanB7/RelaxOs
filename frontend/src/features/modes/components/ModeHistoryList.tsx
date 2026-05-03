import type { ModeActivation } from '../../../shared/types/domain'

export function ModeHistoryList({ history }: { history: ModeActivation[] }) {
  if (history.length === 0) {
    return <p className="text-sm text-slate-600">No mode history yet.</p>
  }

  return (
    <div className="grid gap-3">
      {history.map((activation) => (
        <article
          key={activation.activationId}
          className="rounded-lg border border-slate-200 bg-white p-4 text-sm shadow-sm"
        >
          <div className="flex flex-wrap items-center justify-between gap-2">
            <h3 className="font-semibold text-slate-950">{activation.modeName}</h3>
            <span className="rounded-full border border-slate-200 px-2.5 py-1 text-xs font-semibold text-slate-700">
              {activation.status}
            </span>
          </div>
          {activation.objective && (
            <p className="mt-2 leading-6 text-slate-600">{activation.objective}</p>
          )}
        </article>
      ))}
    </div>
  )
}
