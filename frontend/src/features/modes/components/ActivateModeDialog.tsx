import { useState } from 'react'
import type { AdaptiveMode, ModeIntensityLevel } from '../../../shared/types/domain'
import type { ActivateModePayload } from '../api/modesApi'

type ActivateModeDialogProps = {
  mode: AdaptiveMode
  scenarioId?: number
  isPending?: boolean
  onCancel: () => void
  onSubmit: (payload: ActivateModePayload) => void
}

export function ActivateModeDialog({
  mode,
  scenarioId,
  isPending,
  onCancel,
  onSubmit,
}: ActivateModeDialogProps) {
  const [objective, setObjective] = useState(mode.objective ?? '')
  const [durationDays, setDurationDays] = useState(mode.recommendedMaxDays ?? 14)
  const [intensityLevel, setIntensityLevel] = useState<ModeIntensityLevel>(mode.intensityLevel)
  const [notes, setNotes] = useState('')

  return (
    <section className="rounded-lg border border-teal-200 bg-white p-5 shadow-sm">
      <h3 className="text-base font-semibold text-slate-950">
        Activate {mode.name}
      </h3>
      <div className="mt-4 grid gap-4">
        <label className="grid gap-1 text-sm">
          <span className="font-medium text-slate-700">Objective</span>
          <input
            value={objective}
            onChange={(event) => setObjective(event.target.value)}
            className="rounded-md border border-slate-300 px-3 py-2"
          />
        </label>
        <div className="grid gap-4 sm:grid-cols-2">
          <label className="grid gap-1 text-sm">
            <span className="font-medium text-slate-700">Duration days</span>
            <input
              type="number"
              min={1}
              max={90}
              value={durationDays}
              onChange={(event) => setDurationDays(Number(event.target.value))}
              className="rounded-md border border-slate-300 px-3 py-2"
            />
          </label>
          <label className="grid gap-1 text-sm">
            <span className="font-medium text-slate-700">Intensity</span>
            <select
              value={intensityLevel}
              onChange={(event) => setIntensityLevel(event.target.value as ModeIntensityLevel)}
              className="rounded-md border border-slate-300 px-3 py-2"
            >
              <option value="LOW">LOW</option>
              <option value="MEDIUM">MEDIUM</option>
              <option value="HIGH">HIGH</option>
            </select>
          </label>
        </div>
        <label className="grid gap-1 text-sm">
          <span className="font-medium text-slate-700">Notes</span>
          <textarea
            value={notes}
            onChange={(event) => setNotes(event.target.value)}
            className="min-h-20 rounded-md border border-slate-300 px-3 py-2"
          />
        </label>
      </div>
      <div className="mt-4 flex flex-wrap gap-2">
        <button
          type="button"
          disabled={isPending}
          onClick={() =>
            onSubmit({
              modeCode: mode.code,
              scenarioId,
              objective,
              durationDays,
              intensityLevel,
              notes,
            })
          }
          className="rounded-md bg-teal-700 px-3 py-2 text-sm font-semibold text-white hover:bg-teal-800 disabled:opacity-60"
        >
          {isPending ? 'Activating...' : 'Activate mode'}
        </button>
        <button
          type="button"
          onClick={onCancel}
          className="rounded-md border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-slate-700"
        >
          Cancel
        </button>
      </div>
    </section>
  )
}
