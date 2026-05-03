import { useMutation } from '@tanstack/react-query'
import { mealsApi, type MealSuggestionPayload } from '../api/mealsApi'

export function useMealSuggestions() {
  return useMutation({
    mutationFn: (payload: MealSuggestionPayload) => mealsApi.suggest(payload),
  })
}
