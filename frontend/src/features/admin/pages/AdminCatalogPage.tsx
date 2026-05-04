import { useState } from 'react'
import { ApiClientError } from '../../../shared/api/apiClient'
import { PageHeader } from '../../../shared/components/PageHeader'
import { AdminFormDrawer } from '../components/AdminFormDrawer'
import { AdminTable } from '../components/AdminTable'
import { useAdminList, useAdminMutation } from '../hooks/useAdmin'

type Field = Parameters<typeof AdminFormDrawer>[0]['fields'][number]

type Props<T extends Record<string, unknown>> = {
  title: string
  description: string
  queryKey: string
  list: () => Promise<T[]>
  create?: (body: Record<string, unknown>) => Promise<unknown>
  update?: (id: number, body: Record<string, unknown>) => Promise<unknown>
  toggle?: (id: number) => Promise<unknown>
  columns: Array<{ key: keyof T; label: string }>
  fields: Field[]
  getId?: (row: T) => number
}

export function AdminCatalogPage<T extends Record<string, unknown>>({
  title,
  description,
  queryKey,
  list,
  create,
  update,
  toggle,
  columns,
  fields,
  getId = (row) => Number(row.id),
}: Props<T>) {
  const query = useAdminList(queryKey, list)
  const [editing, setEditing] = useState<T | null>(null)
  const [error, setError] = useState<string | null>(null)
  const saveMutation = useAdminMutation<Record<string, unknown>>(
    queryKey,
    async (body) => {
      if (editing && update) {
        return update(getId(editing), body)
      }
      if (!create) {
        throw new Error('Create is not available for this catalog.')
      }
      return create(body)
    },
  )
  const toggleMutation = useAdminMutation<T>(queryKey, async (row) => {
    if (!toggle) return
    return toggle(getId(row))
  })

  return (
    <div className="grid gap-4">
      <PageHeader title={title} description={description} />
      {(create || editing) && (
        <AdminFormDrawer
          key={editing ? `edit-${getId(editing)}` : `create-${queryKey}`}
          title={editing ? `Edit ${title}` : `Create ${title}`}
          fields={fields}
          initialValue={editing}
          error={error}
          onCancel={() => {
            setEditing(null)
            setError(null)
          }}
          onSubmit={(value) => {
            setError(null)
            saveMutation.mutate(value, {
              onSuccess: () => setEditing(null),
              onError: (mutationError) => {
                setError(
                  mutationError instanceof ApiClientError
                    ? mutationError.message
                    : 'The admin change could not be saved.',
                )
              },
            })
          }}
        />
      )}
      {query.isLoading ? (
        <div className="rounded-lg border border-slate-200 bg-white p-4 text-sm text-slate-600">
          Loading...
        </div>
      ) : query.isError || !query.data ? (
        <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
          Admin data could not load.
        </div>
      ) : (
        <AdminTable
          rows={query.data}
          columns={columns}
          getRowId={(row) => getId(row)}
          onEdit={update ? setEditing : undefined}
          onToggle={toggle ? (row) => toggleMutation.mutate(row) : undefined}
        />
      )}
    </div>
  )
}
