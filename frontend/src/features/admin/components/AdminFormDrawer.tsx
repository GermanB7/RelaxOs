import { useState } from 'react'

type Field = {
  name: string
  label: string
  type?: 'text' | 'number' | 'textarea' | 'checkbox' | 'select'
  options?: string[]
}

type Props = {
  title: string
  fields: Field[]
  initialValue?: Record<string, unknown> | null
  error?: string | null
  onSubmit: (value: Record<string, unknown>) => void
  onCancel: () => void
}

export function AdminFormDrawer({
  title,
  fields,
  initialValue,
  error,
  onSubmit,
  onCancel,
}: Props) {
  const [value, setValue] = useState<Record<string, unknown>>(
    initialValue ?? {},
  )

  return (
    <form
      onSubmit={(event) => {
        event.preventDefault()
        onSubmit(value)
      }}
      className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm"
    >
      <div className="flex items-center justify-between gap-3">
        <h2 className="text-base font-semibold text-slate-950">{title}</h2>
        <button
          type="button"
          onClick={onCancel}
          className="text-sm font-semibold text-slate-600"
        >
          Clear
        </button>
      </div>
      {error ? (
        <p className="mt-3 rounded-md bg-red-50 p-3 text-sm text-red-700">
          {error}
        </p>
      ) : null}
      <div className="mt-4 grid gap-3 sm:grid-cols-2">
        {fields.map((field) => {
          const fieldValue = value[field.name]
          if (field.type === 'textarea') {
            return (
              <label key={field.name} className="sm:col-span-2">
                <span className="text-sm font-medium text-slate-700">
                  {field.label}
                </span>
                <textarea
                  value={String(fieldValue ?? '')}
                  onChange={(event) =>
                    setValue({ ...value, [field.name]: event.target.value })
                  }
                  className="mt-1 min-h-24 w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                />
              </label>
            )
          }
          if (field.type === 'checkbox') {
            return (
              <label key={field.name} className="flex items-center gap-2 pt-6">
                <input
                  type="checkbox"
                  checked={Boolean(fieldValue ?? true)}
                  onChange={(event) =>
                    setValue({ ...value, [field.name]: event.target.checked })
                  }
                />
                <span className="text-sm font-medium text-slate-700">
                  {field.label}
                </span>
              </label>
            )
          }
          if (field.type === 'select') {
            return (
              <label key={field.name}>
                <span className="text-sm font-medium text-slate-700">
                  {field.label}
                </span>
                <select
                  value={String(fieldValue ?? field.options?.[0] ?? '')}
                  onChange={(event) =>
                    setValue({ ...value, [field.name]: event.target.value })
                  }
                  className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                >
                  {field.options?.map((option) => (
                    <option key={option} value={option}>
                      {option}
                    </option>
                  ))}
                </select>
              </label>
            )
          }
          return (
            <label key={field.name}>
              <span className="text-sm font-medium text-slate-700">
                {field.label}
              </span>
              <input
                type={field.type ?? 'text'}
                value={String(fieldValue ?? '')}
                onChange={(event) =>
                  setValue({
                    ...value,
                    [field.name]:
                      field.type === 'number'
                        ? Number(event.target.value)
                        : event.target.value,
                  })
                }
                className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
              />
            </label>
          )
        })}
      </div>
      <button
        type="submit"
        className="mt-4 rounded-md bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800"
      >
        Save
      </button>
    </form>
  )
}
