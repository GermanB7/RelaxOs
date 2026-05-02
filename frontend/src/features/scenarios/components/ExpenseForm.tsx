import { useEffect, useMemo, useState } from 'react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import type {
  ExpenseCategory,
  ScenarioExpense,
} from '../../../shared/types/domain'
import type { ExpensePayload } from '../api/scenariosApi'

type ExpenseFormValues = {
  categoryId: string
  name: string
  amount: string
  frequency: 'MONTHLY' | 'WEEKLY' | 'YEARLY' | 'ONE_TIME'
  isEssential: boolean
}

type ExpenseFormProps = {
  categories: ExpenseCategory[]
  initialValues?: ScenarioExpense
  submitLabel: string
  onSubmit: (payload: ExpensePayload) => Promise<void>
  onCancel?: () => void
}

const expenseSchema = z.object({
  categoryId: z.coerce.number().min(1, 'Category is required'),
  name: z.string().trim().min(1, 'Name is required').max(160),
  amount: z.coerce.number().min(0, 'Amount must be positive'),
  frequency: z.enum(['MONTHLY', 'WEEKLY', 'YEARLY', 'ONE_TIME']),
  isEssential: z.boolean(),
})

function toFormValues(
  categories: ExpenseCategory[],
  expense?: ScenarioExpense,
): ExpenseFormValues {
  return {
    categoryId: (expense?.categoryId ?? categories[0]?.id ?? '').toString(),
    name: expense?.name ?? '',
    amount: expense?.amount?.toString() ?? '',
    frequency: expense?.frequency ?? 'MONTHLY',
    isEssential: expense?.isEssential ?? true,
  }
}

export function ExpenseForm({
  categories,
  initialValues,
  submitLabel,
  onSubmit,
  onCancel,
}: ExpenseFormProps) {
  const [formMessage, setFormMessage] = useState<string | null>(null)
  const defaultValues = useMemo(
    () => toFormValues(categories, initialValues),
    [categories, initialValues],
  )
  const {
    register,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<ExpenseFormValues>({ defaultValues })

  useEffect(() => {
    reset(defaultValues)
  }, [defaultValues, reset])

  const submit = handleSubmit(async (values) => {
    setFormMessage(null)
    const parsed = expenseSchema.safeParse(values)

    if (!parsed.success) {
      parsed.error.issues.forEach((issue) => {
        const field = issue.path[0] as keyof ExpenseFormValues
        setError(field, { message: issue.message })
      })
      return
    }

    await onSubmit(parsed.data)

    if (!initialValues) {
      reset(toFormValues(categories))
    }
    setFormMessage('Saved')
  })

  return (
    <form
      onSubmit={submit}
      className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm"
    >
      <div className="grid gap-4 sm:grid-cols-2">
        <label className="grid gap-1 text-sm font-medium text-slate-700">
          Category
          <select
            className="rounded-md border border-slate-300 px-3 py-2 text-slate-950"
            disabled={categories.length === 0}
            {...register('categoryId')}
          >
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </select>
          {errors.categoryId && (
            <span className="text-xs text-red-700">
              {errors.categoryId.message}
            </span>
          )}
        </label>

        <label className="grid gap-1 text-sm font-medium text-slate-700">
          Expense name
          <input
            className="rounded-md border border-slate-300 px-3 py-2 text-slate-950"
            {...register('name')}
          />
          {errors.name && (
            <span className="text-xs text-red-700">{errors.name.message}</span>
          )}
        </label>

        <label className="grid gap-1 text-sm font-medium text-slate-700">
          Amount
          <input
            type="number"
            min="0"
            step="0.01"
            className="rounded-md border border-slate-300 px-3 py-2 text-slate-950"
            {...register('amount')}
          />
          {errors.amount && (
            <span className="text-xs text-red-700">
              {errors.amount.message}
            </span>
          )}
        </label>

        <label className="grid gap-1 text-sm font-medium text-slate-700">
          Frequency
          <select
            className="rounded-md border border-slate-300 px-3 py-2 text-slate-950"
            {...register('frequency')}
          >
            <option value="MONTHLY">Monthly</option>
            <option value="WEEKLY">Weekly</option>
            <option value="YEARLY">Yearly</option>
            <option value="ONE_TIME">One time</option>
          </select>
        </label>

        <label className="flex items-center gap-2 text-sm font-medium text-slate-700">
          <input type="checkbox" {...register('isEssential')} />
          Essential expense
        </label>
      </div>

      <div className="mt-4 flex flex-wrap items-center gap-3">
        <button
          type="submit"
          disabled={isSubmitting || categories.length === 0}
          className="rounded-md bg-teal-700 px-4 py-2 text-sm font-semibold text-white hover:bg-teal-800 disabled:opacity-60"
        >
          {isSubmitting ? 'Saving...' : submitLabel}
        </button>
        {onCancel && (
          <button
            type="button"
            onClick={onCancel}
            className="rounded-md border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50"
          >
            Cancel
          </button>
        )}
        {formMessage && (
          <span className="text-sm font-medium text-teal-700">
            {formMessage}
          </span>
        )}
      </div>
    </form>
  )
}
