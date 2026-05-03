import { useQuery } from '@tanstack/react-query'
import { mealsApi } from '../api/mealsApi'

export function useMealCatalog() {
  return useQuery({
    queryKey: ['meal-catalog'],
    queryFn: () => mealsApi.getCatalog(),
  })
}
