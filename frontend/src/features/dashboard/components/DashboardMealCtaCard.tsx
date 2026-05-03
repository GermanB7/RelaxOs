import { Link } from 'react-router-dom'

type DashboardMealCtaCardProps = {
  suggestedCta: string
}

export function DashboardMealCtaCard({ suggestedCta }: DashboardMealCtaCardProps) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h2 className="text-base font-semibold text-slate-950">{suggestedCta}</h2>
      <p className="mt-2 text-sm leading-6 text-slate-600">
        Pick a practical fallback before ordering delivery.
      </p>
      <Link
        to="/meals"
        className="mt-3 inline-flex text-sm font-semibold text-teal-700 hover:text-teal-900"
      >
        Open meal planner
      </Link>
    </section>
  )
}
