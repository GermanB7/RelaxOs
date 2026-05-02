import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import type { Scenario } from '../../../shared/types/domain'
import type {
  ScenarioPayload,
  UpdateScenarioPayload,
} from '../api/scenariosApi'

type ScenarioFormValues = {
  name: string
  monthlyIncome: string
  emergencyFundCurrent: string
  emergencyFundTarget: string
  status: 'DRAFT' | 'ACTIVE' | 'ARCHIVED'
}

type ScenarioFormProps = {
  initialValues?: Scenario
  submitLabel: string
  onSubmit: (payload: ScenarioPayload | UpdateScenarioPayload) => Promise<void>
  includeStatus?: boolean
}

const scenarioSchema = z.object({
  name: z.string().trim().min(1, 'Name is required').max(160),
  monthlyIncome: z.coerce.number().min(0, 'Monthly income must be positive'),
  emergencyFundCurrent: z.coerce
    .number()
    .min(0, 'Current emergency fund must be positive'),
  emergencyFundTarget: z.preprocess(
    (value) => (value === '' ? null : value),
    z.coerce.number().min(0).nullable(),
  ),
  status: z.enum(['DRAFT', 'ACTIVE', 'ARCHIVED']).optional(),
})

function toFormValues(scenario?: Scenario): ScenarioFormValues {
  return {
    name: scenario?.name ?? '',
    monthlyIncome: scenario?.monthlyIncome?.toString() ?? '',
    emergencyFundCurrent: scenario?.emergencyFundCurrent?.toString() ?? '0',
    emergencyFundTarget: scenario?.emergencyFundTarget?.toString() ?? '',
    status: scenario?.status ?? 'DRAFT',
  }
}

export function ScenarioForm({
  initialValues,
  submitLabel,
  onSubmit,
  includeStatus = false,
}: ScenarioFormProps) {
  const [formMessage, setFormMessage] = useState<string | null>(null)
  const {
    register,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<ScenarioFormValues>({
    defaultValues: toFormValues(initialValues),
  })

  useEffect(() => {
    reset(toFormValues(initialValues))
  }, [initialValues, reset])

  const submit = handleSubmit(async (values) => {
    setFormMessage(null)
    const parsed = scenarioSchema.safeParse(values)

    if (!parsed.success) {
      parsed.error.issues.forEach((issue) => {
        const field = issue.path[0] as keyof ScenarioFormValues
        setError(field, { message: issue.message })
      })
      return
    }

    await onSubmit({
      name: parsed.data.name,
      monthlyIncome: parsed.data.monthlyIncome,
      emergencyFundCurrent: parsed.data.emergencyFundCurrent,
      emergencyFundTarget: parsed.data.emergencyFundTarget,
      ...(includeStatus ? { status: values.status } : {}),
    })

    if (!initialValues) {
      reset(toFormValues())
    }
    setFormMessage('Saved')
  })

  return (
    <form
      onSubmit={submit}
      className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm"
    >
      <div className="grid gap-4 sm:grid-cols-2">
        <label className="grid gap-1 text-sm font-medium text-slate-700 sm:col-span-2">
          Scenario name
          <input
            className="rounded-md border border-slate-300 px-3 py-2 text-slate-950"
            {...register('name')}
          />
          {errors.name && (
            <span className="text-xs text-red-700">{errors.name.message}</span>
          )}
        </label>

        <label className="grid gap-1 text-sm font-medium text-slate-700">
          Monthly income
          <input
            type="number"
            min="0"
            step="0.01"
            className="rounded-md border border-slate-300 px-3 py-2 text-slate-950"
            {...register('monthlyIncome')}
          />
          {errors.monthlyIncome && (
            <span className="text-xs text-red-700">
              {errors.monthlyIncome.message}
            </span>
          )}
        </label>

        <label className="grid gap-1 text-sm font-medium text-slate-700">
          Current emergency fund
          <input
            type="number"
            min="0"
            step="0.01"
            className="rounded-md border border-slate-300 px-3 py-2 text-slate-950"
            {...register('emergencyFundCurrent')}
          />
          {errors.emergencyFundCurrent && (
            <span className="text-xs text-red-700">
              {errors.emergencyFundCurrent.message}
            </span>
          )}
        </label>

        <label className="grid gap-1 text-sm font-medium text-slate-700">
          Target emergency fund
          <input
            type="number"
            min="0"
            step="0.01"
            className="rounded-md border border-slate-300 px-3 py-2 text-slate-950"
            {...register('emergencyFundTarget')}
          />
        </label>

        {includeStatus && (
          <label className="grid gap-1 text-sm font-medium text-slate-700">
            Status
            <select
              className="rounded-md border border-slate-300 px-3 py-2 text-slate-950"
              {...register('status')}
            >
              <option value="DRAFT">Draft</option>
              <option value="ACTIVE">Active</option>
              <option value="ARCHIVED">Archived</option>
            </select>
          </label>
        )}
      </div>

      <div className="mt-4 flex items-center gap-3">
        <button
          type="submit"
          disabled={isSubmitting}
          className="rounded-md bg-teal-700 px-4 py-2 text-sm font-semibold text-white hover:bg-teal-800 disabled:opacity-60"
        >
          {isSubmitting ? 'Saving...' : submitLabel}
        </button>
        {formMessage && (
          <span className="text-sm font-medium text-teal-700">
            {formMessage}
          </span>
        )}
      </div>
    </form>
  )
}
