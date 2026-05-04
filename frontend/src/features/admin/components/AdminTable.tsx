import { useMemo, useState } from 'react'

type Props<T> = {
  rows: T[]
  columns: Array<{ key: keyof T; label: string }>
  getRowId: (row: T) => string | number
  onEdit?: (row: T) => void
  onToggle?: (row: T) => void
}

export function AdminTable<T extends Record<string, unknown>>({
  rows,
  columns,
  getRowId,
  onEdit,
  onToggle,
}: Props<T>) {
  const [search, setSearch] = useState('')
  const filtered = useMemo(() => {
    const term = search.trim().toLowerCase()
    if (!term) return rows
    return rows.filter((row) =>
      JSON.stringify(row).toLowerCase().includes(term),
    )
  }, [rows, search])

  return (
    <div className="rounded-lg border border-slate-200 bg-white shadow-sm">
      <div className="border-b border-slate-200 p-3">
        <input
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Search"
          className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
        />
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-slate-200 text-sm">
          <thead className="bg-slate-50 text-left text-xs uppercase tracking-wide text-slate-500">
            <tr>
              {columns.map((column) => (
                <th key={String(column.key)} className="px-3 py-2">
                  {column.label}
                </th>
              ))}
              {(onEdit || onToggle) && <th className="px-3 py-2">Actions</th>}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {filtered.map((row) => (
              <tr key={getRowId(row)}>
                {columns.map((column) => (
                  <td key={String(column.key)} className="max-w-xs px-3 py-2">
                    <span className="line-clamp-2">
                      {String(row[column.key] ?? '')}
                    </span>
                  </td>
                ))}
                {(onEdit || onToggle) && (
                  <td className="whitespace-nowrap px-3 py-2">
                    {onEdit ? (
                      <button
                        type="button"
                        onClick={() => onEdit(row)}
                        className="mr-2 font-semibold text-teal-700"
                      >
                        Edit
                      </button>
                    ) : null}
                    {onToggle ? (
                      <button
                        type="button"
                        onClick={() => onToggle(row)}
                        className="font-semibold text-slate-700"
                      >
                        Toggle
                      </button>
                    ) : null}
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
