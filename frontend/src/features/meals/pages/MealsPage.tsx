import { PageHeader } from '../../../shared/components/PageHeader'
import { ActiveModeBanner } from '../../modes/components/ActiveModeBanner'
import { MealSuggestionForm } from '../components/MealSuggestionForm'
import { MealSuggestionsList } from '../components/MealSuggestionsList'
import { useMealSuggestions } from '../hooks/useMealSuggestions'

export function MealsPage() {
  const mealSuggestions = useMealSuggestions()

  return (
    <>
      <PageHeader
        title="Meal Planner"
        description="Simple practical meal suggestions based on craving, time, effort, budget, equipment, and active mode."
      />

      <section className="mb-6">
        <ActiveModeBanner compact />
      </section>

      <section className="mb-8">
        <MealSuggestionForm
          isPending={mealSuggestions.isPending}
          onSubmit={(payload) => mealSuggestions.mutate(payload)}
        />
      </section>

      {mealSuggestions.data?.activeModeCode && (
        <p className="mb-3 text-sm font-semibold text-teal-800">
          Active mode considered: {mealSuggestions.data.activeModeCode}
        </p>
      )}

      <MealSuggestionsList suggestions={mealSuggestions.data?.suggestions ?? []} />
    </>
  )
}
