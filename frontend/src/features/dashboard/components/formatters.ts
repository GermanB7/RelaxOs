export function formatCop(value: number | null | undefined) {
  if (value === null || value === undefined) {
    return 'Not set'
  }
  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0,
  }).format(value)
}
