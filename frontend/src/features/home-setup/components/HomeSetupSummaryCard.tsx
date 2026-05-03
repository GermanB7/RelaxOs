import { useHomeSetupSummary } from '../hooks/useHomeSetup';

interface HomeSetupSummaryCardProps {
  scenarioId?: number;
  compact?: boolean;
}

export function HomeSetupSummaryCard({ scenarioId, compact = false }: HomeSetupSummaryCardProps) {
  const { data: summary, isLoading, error } = useHomeSetupSummary(scenarioId);

  if (isLoading) {
    return (
      <div className="bg-white rounded-lg p-4 border border-gray-200 animate-pulse">
        <div className="h-6 bg-gray-200 rounded w-1/3 mb-4"></div>
        <div className="space-y-2">
          <div className="h-4 bg-gray-200 rounded"></div>
          <div className="h-4 bg-gray-200 rounded w-2/3"></div>
        </div>
      </div>
    );
  }

  if (error || !summary) {
    return (
      <div className="bg-white rounded-lg p-4 border border-gray-200">
        <p className="text-gray-600 text-sm">Sin datos de compras</p>
      </div>
    );
  }

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      maximumFractionDigits: 0,
    }).format(price);
  };

  if (compact) {
    return (
      <div className="bg-white rounded-lg p-4 border border-gray-200">
        <h3 className="font-semibold text-gray-900 mb-3">Setup del Hogar</h3>
        <div className="space-y-3">
          <div className="flex justify-between items-center text-sm">
            <span className="text-gray-600">Tier 1 - Completado:</span>
            <span className="font-semibold text-gray-900">
              {summary.tier1Bought}/{summary.tier1Total} ({summary.tier1CompletionPercentage}%)
            </span>
          </div>
          {summary.nextBestPurchase && (
            <div className="bg-blue-50 rounded p-2 border border-blue-200">
              <p className="text-xs text-gray-600 mb-1">Próxima compra prioritaria:</p>
              <p className="text-sm font-semibold text-blue-900">{summary.nextBestPurchase.name}</p>
            </div>
          )}
          <div className="text-xs text-gray-500">
            Total: {summary.totalItems} items | Pendientes: {summary.pendingItems}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
      <div className="bg-white rounded-lg p-4 border border-gray-200">
        <p className="text-gray-600 text-sm font-medium">Total</p>
        <p className="text-2xl font-bold text-gray-900">{summary.totalItems}</p>
      </div>

      <div className="bg-white rounded-lg p-4 border border-gray-200">
        <p className="text-gray-600 text-sm font-medium">Pendientes</p>
        <p className="text-2xl font-bold text-yellow-600">{summary.pendingItems}</p>
      </div>

      <div className="bg-white rounded-lg p-4 border border-gray-200">
        <p className="text-gray-600 text-sm font-medium">Comprados</p>
        <p className="text-2xl font-bold text-green-600">{summary.boughtItems}</p>
      </div>

      <div className="bg-white rounded-lg p-4 border border-gray-200">
        <p className="text-gray-600 text-sm font-medium">Pospuestos</p>
        <p className="text-2xl font-bold text-orange-600">{summary.postponedItems}</p>
      </div>

      <div className="bg-white rounded-lg p-4 border border-gray-200">
        <p className="text-gray-600 text-sm font-medium">Deseos</p>
        <p className="text-2xl font-bold text-blue-600">{summary.wishlistItems}</p>
      </div>

      <div className="bg-white rounded-lg p-4 border border-gray-200">
        <p className="text-gray-600 text-sm font-medium">Tier 1 Completado</p>
        <p className="text-2xl font-bold text-purple-600">{summary.tier1CompletionPercentage}%</p>
        <p className="text-xs text-gray-600 mt-1">
          {summary.tier1Bought}/{summary.tier1Total}
        </p>
      </div>

      <div className="bg-white rounded-lg p-4 border border-gray-200">
        <p className="text-gray-600 text-sm font-medium">Costo Pendiente</p>
        <p className="text-lg font-bold text-gray-900">{formatPrice(summary.estimatedPendingCost)}</p>
      </div>

      {summary.nextBestPurchase && (
        <div className="bg-blue-50 rounded-lg p-4 border border-blue-200 md:col-span-2">
          <p className="text-blue-600 text-sm font-medium mb-1">Próxima Mejor Compra</p>
          <p className="text-lg font-bold text-blue-900">{summary.nextBestPurchase.name}</p>
          <p className="text-xs text-blue-600 mt-1">
            {summary.nextBestPurchase.category} • {summary.nextBestPurchase.tier}
          </p>
        </div>
      )}
    </div>
  );
}
