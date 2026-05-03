import { useState } from 'react'
import type {
  MealBudgetLevel,
  MealCravingLevel,
  MealEffortLevel,
} from '../../../shared/types/domain'
import type { MealSuggestionPayload } from '../api/mealsApi'

const equipmentOptions = ['AIR_FRYER', 'RICE_COOKER', 'SARTEN', 'OLLA']

type MealSuggestionFormProps = {
  isPending?: boolean
  onSubmit: (payload: MealSuggestionPayload) => void
}

export function MealSuggestionForm({ isPending, onSubmit }: MealSuggestionFormProps) {
  const [cravingLevel, setCravingLevel] = useState<MealCravingLevel>('RICH')
  const [maxPrepTimeMinutes, setMaxPrepTimeMinutes] = useState(25)
  const [effortLevel, setEffortLevel] = useState<MealEffortLevel>('LOW')
  const [budgetLevel, setBudgetLevel] = useState<MealBudgetLevel>('MEDIUM')
  const [availableEquipment, setAvailableEquipment] = useState<string[]>(['AIR_FRYER'])

  const toggleEquipment = (equipment: string) => {
    setAvailableEquipment((current) =>
      current.includes(equipment)
        ? current.filter((item) => item !== equipment)
        : [...current, equipment],
    )
  }

  return (
    <form
      className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm"
      onSubmit={(event) => {
        event.preventDefault()
        onSubmit({
          cravingLevel,
          maxPrepTimeMinutes,
          effortLevel,
          budgetLevel,
          availableEquipment,
        })
      }}
    >
      <div className="grid gap-4 sm:grid-cols-2">
        <label className="grid gap-1 text-sm">
          <span className="font-medium text-slate-700">Craving</span>
          <select
            value={cravingLevel}
            onChange={(event) => setCravingLevel(event.target.value as MealCravingLevel)}
            className="rounded-md border border-slate-300 px-3 py-2"
          >
            <option value="SIMPLE">SIMPLE</option>
            <option value="COMFORT">COMFORT</option>
            <option value="RICH">RICH</option>
            <option value="HEAVY">HEAVY</option>
          </select>
        </label>
        <label className="grid gap-1 text-sm">
          <span className="font-medium text-slate-700">Max prep time</span>
          <input
            type="number"
            min={1}
            max={180}
            value={maxPrepTimeMinutes}
            onChange={(event) => setMaxPrepTimeMinutes(Number(event.target.value))}
            className="rounded-md border border-slate-300 px-3 py-2"
          />
        </label>
        <label className="grid gap-1 text-sm">
          <span className="font-medium text-slate-700">Effort</span>
          <select
            value={effortLevel}
            onChange={(event) => setEffortLevel(event.target.value as MealEffortLevel)}
            className="rounded-md border border-slate-300 px-3 py-2"
          >
            <option value="LOW">LOW</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
          </select>
        </label>
        <label className="grid gap-1 text-sm">
          <span className="font-medium text-slate-700">Budget</span>
          <select
            value={budgetLevel}
            onChange={(event) => setBudgetLevel(event.target.value as MealBudgetLevel)}
            className="rounded-md border border-slate-300 px-3 py-2"
          >
            <option value="LOW">LOW</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
          </select>
        </label>
      </div>

      <fieldset className="mt-4">
        <legend className="text-sm font-medium text-slate-700">Equipment</legend>
        <div className="mt-2 flex flex-wrap gap-2">
          {equipmentOptions.map((equipment) => (
            <label
              key={equipment}
              className="inline-flex items-center gap-2 rounded-md border border-slate-300 px-3 py-2 text-sm"
            >
              <input
                type="checkbox"
                checked={availableEquipment.includes(equipment)}
                onChange={() => toggleEquipment(equipment)}
              />
              {equipment}
            </label>
          ))}
        </div>
      </fieldset>

      <button
        type="submit"
        disabled={isPending}
        className="mt-5 rounded-md bg-teal-700 px-4 py-2 text-sm font-semibold text-white hover:bg-teal-800 disabled:opacity-60"
      >
        {isPending ? 'Suggesting...' : 'Suggest meals'}
      </button>
    </form>
  )
}
