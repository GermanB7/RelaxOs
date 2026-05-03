import { useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { PageHeader } from '../../../shared/components/PageHeader'
import type { AdaptiveMode } from '../../../shared/types/domain'
import { ActiveModeBanner } from '../components/ActiveModeBanner'
import { ActivateModeDialog } from '../components/ActivateModeDialog'
import { ModeCard } from '../components/ModeCard'
import { ModeHistoryList } from '../components/ModeHistoryList'
import { useModeHistory } from '../hooks/useModeHistory'
import { useModes } from '../hooks/useModes'

export function ModesPage() {
  const [searchParams] = useSearchParams()
  const scenarioId = searchParams.get('scenarioId')
    ? Number(searchParams.get('scenarioId'))
    : undefined
  const [selectedMode, setSelectedMode] = useState<AdaptiveMode | null>(null)
  const { modesQuery, activateMode } = useModes()
  const historyQuery = useModeHistory(scenarioId)

  return (
    <>
      <PageHeader
        title="Adaptive Modes"
        description="Temporary backend policies for spending, purchases, alerts, routines, and recommendations."
      />

      <section className="mb-6">
        <ActiveModeBanner scenarioId={scenarioId} />
      </section>

      {selectedMode && (
        <section className="mb-6">
          <ActivateModeDialog
            mode={selectedMode}
            scenarioId={scenarioId}
            isPending={activateMode.isPending}
            onCancel={() => setSelectedMode(null)}
            onSubmit={async (payload) => {
              await activateMode.mutateAsync(payload)
              setSelectedMode(null)
            }}
          />
        </section>
      )}

      <section className="mb-8">
        <h3 className="mb-3 text-base font-semibold text-slate-950">
          Available modes
        </h3>
        {modesQuery.isLoading ? (
          <p className="text-sm text-slate-600">Loading modes...</p>
        ) : (
          <div className="grid gap-4">
            {(modesQuery.data ?? []).map((mode) => (
              <ModeCard
                key={mode.id}
                mode={mode}
                isPending={activateMode.isPending}
                onActivate={setSelectedMode}
              />
            ))}
          </div>
        )}
      </section>

      <section>
        <h3 className="mb-3 text-base font-semibold text-slate-950">
          Mode history
        </h3>
        {historyQuery.isLoading ? (
          <p className="text-sm text-slate-600">Loading history...</p>
        ) : (
          <ModeHistoryList history={historyQuery.data ?? []} />
        )}
      </section>
    </>
  )
}
