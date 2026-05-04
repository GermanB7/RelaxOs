type Props = {
  value: string
  onChange: (value: string) => void
}

export function MetadataJsonEditor({ value, onChange }: Props) {
  return (
    <textarea
      value={value}
      onChange={(event) => onChange(event.target.value)}
      className="min-h-40 w-full rounded-md border border-slate-300 px-3 py-2 font-mono text-sm"
      spellCheck={false}
    />
  )
}
