import { useState } from 'react';
import type { UserPurchaseItem } from '../api/homeSetupApi';
import { PurchaseItemStatusBadge } from './PurchaseItemStatusBadge';
import { useUpdatePurchaseItemStatus } from '../hooks/useHomeSetup';

interface PurchaseItemCardProps {
  item: UserPurchaseItem;
  onEdit?: (item: UserPurchaseItem) => void;
}

export function PurchaseItemCard({ item, onEdit }: PurchaseItemCardProps) {
  const [showMenu, setShowMenu] = useState(false);
  const updateStatus = useUpdatePurchaseItemStatus();

  const handleStatusChange = (newStatus: string) => {
    updateStatus.mutate({
      id: item.id,
      request: {
        status: newStatus,
        reason: `Cambio de estado a ${newStatus}`,
      },
    });
    setShowMenu(false);
  };

  const formatPrice = (price?: number) => {
    if (!price) return 'No especificado';
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      maximumFractionDigits: 0,
    }).format(price);
  };

  return (
    <div className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
      <div className="flex justify-between items-start mb-3">
        <div className="flex-1">
          <h3 className="text-lg font-semibold text-gray-900">{item.name}</h3>
          <p className="text-sm text-gray-600">{item.category}</p>
        </div>
        <PurchaseItemStatusBadge status={item.status} />
      </div>

      <div className="grid grid-cols-2 gap-4 mb-4 text-sm">
        <div>
          <span className="text-gray-600">Tier:</span>
          <p className="font-medium text-gray-900">{item.tier}</p>
        </div>
        <div>
          <span className="text-gray-600">Prioridad:</span>
          <p className="font-medium text-gray-900">{item.priority}</p>
        </div>
        <div>
          <span className="text-gray-600">Precio estimado:</span>
          <p className="font-medium text-gray-900">{formatPrice(item.estimatedPrice)}</p>
        </div>
        {item.actualPrice && (
          <div>
            <span className="text-gray-600">Precio real:</span>
            <p className="font-medium text-gray-900">{formatPrice(item.actualPrice)}</p>
          </div>
        )}
      </div>

      {item.notes && <p className="text-sm text-gray-700 mb-3 italic">Notas: {item.notes}</p>}

      <div className="flex gap-2">
        <button
          onClick={() => setShowMenu(!showMenu)}
          className="flex-1 px-3 py-2 bg-blue-50 text-blue-700 rounded hover:bg-blue-100 text-sm font-medium"
        >
          Cambiar estado
        </button>
        {onEdit && (
          <button
            onClick={() => onEdit(item)}
            className="flex-1 px-3 py-2 bg-gray-50 text-gray-700 rounded hover:bg-gray-100 text-sm font-medium"
          >
            Editar
          </button>
        )}
      </div>

      {showMenu && (
        <div className="mt-2 p-2 bg-gray-50 rounded border border-gray-200">
          <div className="text-xs font-semibold text-gray-600 mb-2">Cambiar estado a:</div>
          <div className="grid grid-cols-2 gap-2">
            <button
              onClick={() => handleStatusChange('PENDING')}
              className="px-2 py-1 text-xs bg-yellow-100 text-yellow-800 rounded hover:bg-yellow-200"
            >
              Pendiente
            </button>
            <button
              onClick={() => handleStatusChange('BOUGHT')}
              className="px-2 py-1 text-xs bg-green-100 text-green-800 rounded hover:bg-green-200"
            >
              Comprado
            </button>
            <button
              onClick={() => handleStatusChange('POSTPONED')}
              className="px-2 py-1 text-xs bg-orange-100 text-orange-800 rounded hover:bg-orange-200"
            >
              Pospuesto
            </button>
            <button
              onClick={() => handleStatusChange('WISHLIST')}
              className="px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded hover:bg-blue-200"
            >
              Deseo
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
