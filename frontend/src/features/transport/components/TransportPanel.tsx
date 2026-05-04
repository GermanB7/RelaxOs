import { useState } from 'react'
import type { TransportOptionType } from '../../../shared/types/domain'
import { useTransport } from '../hooks/useTransport'

const optionTypes: TransportOptionType[] = [
  'PUBLIC_TRANSPORT',
  'UBER_DIDI',
  'MOTORCYCLE',
  'CAR',
  'BICYCLE',
  'WALKING',
  'MIXED',
]

export function TransportPanel({ scenarioId }: { scenarioId: number }) {
  const { optionsQuery, latestEvaluationQuery, createOption, deleteOption, evaluate } =
    useTransport(scenarioId)
  const [form, setForm] = useState({
    optionType: 'PUBLIC_TRANSPORT' as TransportOptionType,
    monthlyCost: 180000,
    tripsPerWeek: 10,
    averageTimeMinutes: 45,
    comfortScore: 3,
    safetyScore: 3,
    flexibilityScore: 3,
  })

  const options = optionsQuery.data ?? []
  const evaluation = evaluate.data ?? latestEvaluationQuery.data

  return (
    <section className="mt-8">
      <div className="mb-3 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h3 className="text-base font-semibold text-slate-950">
            Transport decision
          </h3>
          <p className="mt-1 text-sm text-slate-600">
            Manual options evaluated by backend rules.
          </p>
        </div>
        <button
          type="button"
          disabled={options.length === 0 || evaluate.isPending}
          onClick={() => evaluate.mutate()}
          className="rounded-md bg-teal-700 px-4 py-2 text-sm font-semibold text-white hover:bg-teal-800 disabled:opacity-60"
        >
          {evaluate.isPending ? 'Evaluating...' : 'Evaluate transport'}
        </button>
      </div>

      <form
        className="rounded-lg border border-slate-200 bg-white p-4"
        onSubmit={async (event) => {
          event.preventDefault()
          await createOption.mutateAsync(form)
        }}
      >
        <div className="grid gap-3 sm:grid-cols-3">
          <label className="text-sm">
            <span className="font-medium text-slate-700">Option</span>
            <select
              value={form.optionType}
              onChange={(event) =>
                setForm((current) => ({
                  ...current,
                  optionType: event.target.value as TransportOptionType,
                }))
              }
              className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2"
            >
              {optionTypes.map((type) => (
                <option key={type} value={type}>
                  {type.replaceAll('_', ' ')}
                </option>
              ))}
            </select>
          </label>
          {[
            ['monthlyCost', 'Monthly cost'],
            ['tripsPerWeek', 'Trips/week'],
            ['averageTimeMinutes', 'Avg minutes'],
            ['comfortScore', 'Comfort 1-5'],
            ['safetyScore', 'Safety 1-5'],
            ['flexibilityScore', 'Flexibility 1-5'],
          ].map(([name, label]) => (
            <label key={name} className="text-sm">
              <span className="font-medium text-slate-700">{label}</span>
              <input
                type="number"
                value={form[name as keyof typeof form] as number}
                onChange={(event) =>
                  setForm((current) => ({
                    ...current,
                    [name]: Number(event.target.value),
                  }))
                }
                className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2"
              />
            </label>
          ))}
        </div>
        <button
          type="submit"
          disabled={createOption.isPending}
          className="mt-4 rounded-md border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-60"
        >
          Add option
        </button>
      </form>

      <div className="mt-4 grid gap-3">
        {options.map((option) => (
          <article
            key={option.id}
            className="flex flex-col gap-2 rounded-lg border border-slate-200 bg-white p-3 sm:flex-row sm:items-center sm:justify-between"
          >
            <div>
              <p className="font-semibold text-slate-950">
                {option.optionType.replaceAll('_', ' ')}
              </p>
              <p className="text-sm text-slate-600">
                Cost {option.totalMonthlyCost} · {option.tripsPerWeek} trips/week · {option.averageTimeMinutes} min
              </p>
            </div>
            <button
              type="button"
              onClick={() => deleteOption.mutate(option.id)}
              className="text-sm font-semibold text-red-700"
            >
              Delete
            </button>
          </article>
        ))}
      </div>

      {evaluation && (
        <article className="mt-4 rounded-lg border border-teal-200 bg-teal-50 p-4">
          <p className="text-xs font-semibold uppercase text-teal-700">
            Backend recommendation
          </p>
          <h4 className="mt-1 text-lg font-semibold text-slate-950">
            {evaluation.recommendedCurrentOption?.replaceAll('_', ' ')}
          </h4>
          <p className="mt-2 text-sm leading-6 text-slate-700">
            {evaluation.explanation}
          </p>
          <p className="mt-2 text-sm text-slate-700">
            Fit {evaluation.fitScore} · Risk {evaluation.riskLevel} · Burden{' '}
            {(evaluation.transportBurden * 100).toFixed(1)}%
          </p>
          {evaluation.conditionsToSwitch && (
            <p className="mt-2 text-sm text-slate-700">
              {evaluation.conditionsToSwitch}
            </p>
          )}
        </article>
      )}
    </section>
  )
}
