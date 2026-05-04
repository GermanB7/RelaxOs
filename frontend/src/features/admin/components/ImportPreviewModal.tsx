type Props = {
  errors: string[]
  importedCount: number | null
}

export function ImportPreviewModal({ errors, importedCount }: Props) {
  if (errors.length === 0 && importedCount === null) {
    return null
  }
  return (
    <div className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      {errors.length > 0 ? (
        <>
          <h2 className="text-sm font-semibold text-red-700">
            Import validation failed
          </h2>
          <ul className="mt-2 list-disc pl-5 text-sm text-red-700">
            {errors.map((error) => (
              <li key={error}>{error}</li>
            ))}
          </ul>
        </>
      ) : (
        <p className="text-sm font-semibold text-teal-700">
          Imported {importedCount} items.
        </p>
      )}
    </div>
  )
}
