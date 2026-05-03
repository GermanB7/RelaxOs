import type { UserPurchaseItem } from '../api/homeSetupApi';
import { PurchaseItemCard } from './PurchaseItemCard';

interface PurchaseTierSectionProps {
  tier: string;
  items: UserPurchaseItem[];
  onEditItem?: (item: UserPurchaseItem) => void;
}

export function PurchaseTierSection({ tier, items, onEditItem }: PurchaseTierSectionProps) {
  if (items.length === 0) {
    return null;
  }

  const getTierLabel = (tier: string) => {
    switch (tier) {
      case 'TIER_1':
        return 'TIER 1 - Esencial';
      case 'TIER_2':
        return 'TIER 2 - Comodidad';
      case 'TIER_3':
        return 'TIER 3 - Mejora';
      case 'TIER_4':
        return 'TIER 4 - Lujo';
      default:
        return tier;
    }
  };

  const getTierColor = (tier: string) => {
    switch (tier) {
      case 'TIER_1':
        return 'border-l-4 border-red-500 bg-red-50';
      case 'TIER_2':
        return 'border-l-4 border-yellow-500 bg-yellow-50';
      case 'TIER_3':
        return 'border-l-4 border-blue-500 bg-blue-50';
      case 'TIER_4':
        return 'border-l-4 border-purple-500 bg-purple-50';
      default:
        return 'border-l-4 border-gray-500 bg-gray-50';
    }
  };

  const boughtCount = items.filter((i) => i.status === 'BOUGHT').length;
  const percentage = items.length > 0 ? Math.round((boughtCount / items.length) * 100) : 0;

  return (
    <div className={`rounded-lg overflow-hidden mb-6 ${getTierColor(tier)}`}>
      <div className="p-4 border-b border-gray-200">
        <div className="flex justify-between items-center">
          <h2 className="text-lg font-bold text-gray-900">{getTierLabel(tier)}</h2>
          <div className="flex items-center gap-4 text-sm">
            <span className="text-gray-600">
              ({boughtCount}/{items.length}) {percentage}% completado
            </span>
            <div className="w-24 bg-gray-200 rounded-full h-2">
              <div
                className="bg-green-500 h-2 rounded-full transition-all"
                style={{ width: `${percentage}%` }}
              ></div>
            </div>
          </div>
        </div>
      </div>

      <div className="p-4 grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {items.map((item) => (
          <PurchaseItemCard key={item.id} item={item} onEdit={onEditItem} />
        ))}
      </div>
    </div>
  );
}
