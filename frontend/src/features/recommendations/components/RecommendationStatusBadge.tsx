import type {
  RecommendationSeverity,
  RecommendationStatus,
} from '../../../shared/types/domain'

const statusStyles: Record<RecommendationStatus, string> = {
  OPEN: 'border-teal-200 bg-teal-50 text-teal-800',
  ACCEPTED: 'border-emerald-200 bg-emerald-50 text-emerald-800',
  POSTPONED: 'border-amber-200 bg-amber-50 text-amber-800',
  DISMISSED: 'border-slate-200 bg-slate-50 text-slate-700',
  EXPIRED: 'border-slate-200 bg-slate-100 text-slate-600',
}

const severityStyles: Record<RecommendationSeverity, string> = {
  LOW: 'border-slate-200 bg-slate-50 text-slate-700',
  MEDIUM: 'border-amber-200 bg-amber-50 text-amber-800',
  HIGH: 'border-orange-200 bg-orange-50 text-orange-800',
  CRITICAL: 'border-red-200 bg-red-50 text-red-800',
}

export function RecommendationStatusBadge({
  label,
  variant,
}: {
  label: RecommendationStatus | RecommendationSeverity
  variant: 'status' | 'severity'
}) {
  const styles =
    variant === 'status'
      ? statusStyles[label as RecommendationStatus]
      : severityStyles[label as RecommendationSeverity]

  return (
    <span
      className={`inline-flex rounded-full border px-2.5 py-1 text-xs font-semibold ${styles}`}
    >
      {label.replaceAll('_', ' ')}
    </span>
  )
}
