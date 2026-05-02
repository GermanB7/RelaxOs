import { Link } from 'react-router-dom'

type EmptyStateProps = {
  title: string
  description: string
  actionLabel: string
  to: string
}

export function EmptyState({
  title,
  description,
  actionLabel,
  to,
}: EmptyStateProps) {
  return (
    <section className="mt-6 rounded-lg border border-dashed border-slate-300 bg-white p-5 sm:p-6">
      <div className="max-w-xl">
        <h3 className="text-base font-semibold text-slate-950">{title}</h3>
        <p className="mt-2 text-sm leading-6 text-slate-600">{description}</p>
        <Link
          to={to}
          className="mt-4 inline-flex rounded-md bg-teal-700 px-4 py-2 text-sm font-semibold text-white transition hover:bg-teal-800"
        >
          {actionLabel}
        </Link>
      </div>
    </section>
  )
}
