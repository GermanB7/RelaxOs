import { useState } from 'react'
import { PageHeader } from '../../../shared/components/PageHeader'
import { adminApi } from '../api/adminApi'
import { ImportPreviewModal } from '../components/ImportPreviewModal'
import { MetadataJsonEditor } from '../components/MetadataJsonEditor'

const catalogTypes = [
  'expense-categories',
  'purchase-items',
  'meal-items',
  'modes',
  'recommendation-copy',
  'settings',
]

export function ImportExportAdminPage() {
  const [catalogType, setCatalogType] = useState(catalogTypes[0])
  const [json, setJson] = useState('[\n]\n')
  const [errors, setErrors] = useState<string[]>([])
  const [importedCount, setImportedCount] = useState<number | null>(null)

  async function exportCatalog() {
    const data = await adminApi.exportCatalog<unknown>(catalogType)
    setJson(JSON.stringify(data, null, 2))
    setErrors([])
    setImportedCount(null)
  }

  async function importCatalog() {
    try {
      const parsed = JSON.parse(json)
      if (!Array.isArray(parsed)) {
        setErrors(['Import JSON must be an array.'])
        return
      }
      const result = await adminApi.importCatalog(catalogType, parsed)
      setErrors(result.errors)
      setImportedCount(result.errors.length ? null : result.importedCount)
    } catch {
      setErrors(['Import JSON is invalid.'])
    }
  }

  return (
    <div className="grid gap-4">
      <PageHeader
        title="Import / Export"
        description="Move catalog seeds as JSON with backend validation."
      />
      <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
        <div className="flex flex-col gap-3 sm:flex-row">
          <select
            value={catalogType}
            onChange={(event) => setCatalogType(event.target.value)}
            className="rounded-md border border-slate-300 px-3 py-2 text-sm"
          >
            {catalogTypes.map((type) => (
              <option key={type} value={type}>
                {type}
              </option>
            ))}
          </select>
          <button
            type="button"
            onClick={exportCatalog}
            className="rounded-md border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"
          >
            Export
          </button>
          <button
            type="button"
            onClick={importCatalog}
            className="rounded-md bg-slate-900 px-3 py-2 text-sm font-semibold text-white hover:bg-slate-800"
          >
            Import
          </button>
        </div>
        <div className="mt-4">
          <MetadataJsonEditor value={json} onChange={setJson} />
        </div>
      </section>
      <ImportPreviewModal errors={errors} importedCount={importedCount} />
    </div>
  )
}
