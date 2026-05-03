import { apiClient } from '../../../shared/api/apiClient'
import type {
  ActiveModeSummary,
  AdaptiveMode,
  ModeActivation,
  ModeCode,
  ModeIntensityLevel,
} from '../../../shared/types/domain'

export type ActivateModePayload = {
  modeCode: ModeCode
  scenarioId?: number
  objective?: string
  durationDays?: number
  intensityLevel?: ModeIntensityLevel
  notes?: string
}

export type EndModePayload = {
  reason?: string
}

export const modesApi = {
  listModes: () => apiClient<AdaptiveMode[]>({ path: '/modes' }),
  getActiveMode: () => apiClient<ActiveModeSummary>({ path: '/modes/active' }),
  activateMode: (payload: ActivateModePayload) =>
    apiClient<ModeActivation>({
      path: '/modes/activate',
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  endActiveMode: (payload?: EndModePayload) =>
    apiClient<ActiveModeSummary>({
      path: '/modes/active/end',
      method: 'POST',
      body: JSON.stringify(payload ?? {}),
    }),
  listHistory: (scenarioId?: number) =>
    apiClient<ModeActivation[]>({
      path: `/modes/history${scenarioId ? `?scenarioId=${scenarioId}` : ''}`,
    }),
}
