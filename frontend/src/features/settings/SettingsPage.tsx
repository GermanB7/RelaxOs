import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { PageHeader } from '../../shared/components/PageHeader'
import { useProfile } from './hooks/useProfile'

type ProfileFormValues = {
  displayName: string
  city: string
  currency: string
  monthlyIncome: string
}

const profileSchema = z.object({
  displayName: z.string().max(120).default(''),
  city: z.string().max(120).default(''),
  currency: z.string().min(1, 'Currency is required').max(10),
  monthlyIncome: z.preprocess(
    (value) => (value === '' ? null : value),
    z.coerce.number().min(0).nullable(),
  ),
})

export function SettingsPage() {
  const { profileQuery, updateProfile } = useProfile()
  const [formMessage, setFormMessage] = useState<string | null>(null)
  const {
    register,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<ProfileFormValues>({
    defaultValues: {
      displayName: '',
      city: '',
      currency: 'COP',
      monthlyIncome: '',
    },
  })

  useEffect(() => {
    if (profileQuery.data) {
      reset({
        displayName: profileQuery.data.displayName ?? '',
        city: profileQuery.data.city ?? '',
        currency: profileQuery.data.currency ?? 'COP',
        monthlyIncome: profileQuery.data.monthlyIncome?.toString() ?? '',
      })
    }
  }, [profileQuery.data, reset])

  const submit = handleSubmit(async (values) => {
    setFormMessage(null)
    const parsed = profileSchema.safeParse(values)

    if (!parsed.success) {
      parsed.error.issues.forEach((issue) => {
        const field = issue.path[0] as keyof ProfileFormValues
        setError(field, { message: issue.message })
      })
      return
    }

    await updateProfile.mutateAsync({
      displayName: parsed.data.displayName,
      city: parsed.data.city,
      currency: parsed.data.currency,
      monthlyIncome: parsed.data.monthlyIncome,
    })
    setFormMessage('Profile saved')
  })

  return (
    <>
      <PageHeader
        title="Settings"
        description="Edit the local profile seed used by Sprint 1 until full authentication exists."
      />

      {profileQuery.isLoading && (
        <p className="mt-6 text-sm text-slate-600">Loading profile...</p>
      )}

      {profileQuery.isError && (
        <p className="mt-6 text-sm text-red-700">Could not load profile.</p>
      )}

      {profileQuery.data && (
        <form
          onSubmit={submit}
          className="mt-6 rounded-lg border border-slate-200 bg-white p-4 shadow-sm"
        >
          <div className="grid gap-4 sm:grid-cols-2">
            <label className="grid gap-1 text-sm font-medium text-slate-700">
              Display name
              <input
                className="rounded-md border border-slate-300 px-3 py-2 text-slate-950"
                {...register('displayName')}
              />
              {errors.displayName && (
                <span className="text-xs text-red-700">
                  {errors.displayName.message}
                </span>
              )}
            </label>

            <label className="grid gap-1 text-sm font-medium text-slate-700">
              City
              <input
                className="rounded-md border border-slate-300 px-3 py-2 text-slate-950"
                {...register('city')}
              />
            </label>

            <label className="grid gap-1 text-sm font-medium text-slate-700">
              Currency
              <input
                className="rounded-md border border-slate-300 px-3 py-2 text-slate-950"
                {...register('currency')}
              />
              {errors.currency && (
                <span className="text-xs text-red-700">
                  {errors.currency.message}
                </span>
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
          </div>

          <div className="mt-4 flex items-center gap-3">
            <button
              type="submit"
              disabled={isSubmitting}
              className="rounded-md bg-teal-700 px-4 py-2 text-sm font-semibold text-white hover:bg-teal-800 disabled:opacity-60"
            >
              {isSubmitting ? 'Saving...' : 'Save profile'}
            </button>
            {formMessage && (
              <span className="text-sm font-medium text-teal-700">
                {formMessage}
              </span>
            )}
          </div>
        </form>
      )}
    </>
  )
}
