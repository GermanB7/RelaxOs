import type { MealSuggestion } from '../../../shared/types/domain'
import { MealCard } from './MealCard'

export function MealSuggestionsList({ suggestions }: { suggestions: MealSuggestion[] }) {
  if (suggestions.length === 0) {
    return (
      <section className="rounded-lg border border-dashed border-slate-300 bg-white p-5">
        <h3 className="text-base font-semibold text-slate-950">No suggestions yet</h3>
        <p className="mt-2 text-sm text-slate-600">
          Choose your craving, time, effort, budget, and equipment.
        </p>
      </section>
    )
  }

  return (
    <div className="grid gap-4">
      {suggestions.map((meal) => (
        <MealCard key={meal.id} meal={meal} />
      ))}
    </div>
  )
}
