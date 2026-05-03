import type { MealSuggestion } from '../../../shared/types/domain'

export function MealCard({ meal }: { meal: MealSuggestion }) {
  const price =
    meal.estimatedCostMin !== null && meal.estimatedCostMax !== null
      ? `${meal.estimatedCostMin.toLocaleString()}-${meal.estimatedCostMax.toLocaleString()} COP`
      : 'Cost not estimated'

  return (
    <article className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex items-start justify-between gap-3">
        <div>
          <h3 className="text-base font-semibold text-slate-950">{meal.name}</h3>
          <p className="mt-1 text-sm text-slate-600">{price}</p>
        </div>
        <span className="rounded-full border border-teal-200 bg-teal-50 px-2.5 py-1 text-xs font-semibold text-teal-800">
          {meal.fitScore}
        </span>
      </div>
      <dl className="mt-3 grid grid-cols-2 gap-2 text-sm">
        <div>
          <dt className="text-slate-500">Time</dt>
          <dd className="font-semibold text-slate-900">{meal.prepTimeMinutes} min</dd>
        </div>
        <div>
          <dt className="text-slate-500">Effort</dt>
          <dd className="font-semibold text-slate-900">{meal.effortLevel}</dd>
        </div>
        <div>
          <dt className="text-slate-500">Craving</dt>
          <dd className="font-semibold text-slate-900">{meal.cravingLevel}</dd>
        </div>
        <div>
          <dt className="text-slate-500">Budget</dt>
          <dd className="font-semibold text-slate-900">{meal.budgetLevel}</dd>
        </div>
      </dl>
      {meal.requiredEquipment && (
        <p className="mt-3 text-sm text-slate-600">{meal.requiredEquipment}</p>
      )}
      <p className="mt-3 text-sm leading-6 text-slate-700">{meal.reason}</p>
    </article>
  )
}
