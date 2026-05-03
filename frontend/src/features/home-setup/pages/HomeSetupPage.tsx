import { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { HomeSetupSummaryCard } from '../components/HomeSetupSummaryCard';
import { PurchaseTierSection } from '../components/PurchaseTierSection';
import { HomeSetupFilters } from '../components/HomeSetupFilters';
import { PurchaseItemForm } from '../components/PurchaseItemForm';
import type { UserPurchaseItem } from '../api/homeSetupApi';
import {
  useHomeRoadmap,
  useInitializeRoadmap,
} from '../hooks/useHomeSetup';

export function HomeSetupPage() {
  const [searchParams] = useSearchParams();
  const scenarioId = searchParams.get('scenarioId') ? Number(searchParams.get('scenarioId')) : undefined;

  const [selectedStatus, setSelectedStatus] = useState<string | undefined>();
  const [selectedTier, setSelectedTier] = useState<string | undefined>();
  const [selectedCategory, setSelectedCategory] = useState<string | undefined>();
  const [showForm, setShowForm] = useState(false);
  const [editingItem, setEditingItem] = useState<UserPurchaseItem | undefined>();

  const roadmapQuery = useHomeRoadmap(scenarioId, selectedStatus, selectedTier, selectedCategory);
  const roadmapItems = roadmapQuery?.data || [];
  const loadingRoadmap = roadmapQuery?.isLoading || false;

  const initializeRoadmap = useInitializeRoadmap();

  const hasItems = roadmapItems.length > 0;

  const handleInitialize = async () => {
    await initializeRoadmap.mutateAsync(scenarioId);
  };

  const handleEditItem = (item: UserPurchaseItem) => {
    setEditingItem(item);
    setShowForm(true);
  };

  const handleFormSuccess = () => {
    setShowForm(false);
    setEditingItem(undefined);
  };

  const itemsByTier: Record<string, UserPurchaseItem[]> = {
    TIER_1: [],
    TIER_2: [],
    TIER_3: [],
    TIER_4: [],
  };

  roadmapItems.forEach((item) => {
    if (itemsByTier[item.tier]) {
      itemsByTier[item.tier].push(item);
    }
  });

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-900">Setup del Hogar</h1>
        <div className="flex gap-2">
          {!hasItems && (
            <button
              onClick={handleInitialize}
              disabled={initializeRoadmap.isPending}
              className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 disabled:bg-gray-400 font-medium"
            >
              {initializeRoadmap.isPending ? 'Inicializando...' : 'Inicializar desde catálogo'}
            </button>
          )}
          <button
            onClick={() => {
              setEditingItem(undefined);
              setShowForm(!showForm);
            }}
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 font-medium"
          >
            {showForm ? 'Cancelar' : 'Agregar item personalizado'}
          </button>
        </div>
      </div>

      {showForm && (
        <PurchaseItemForm
          scenarioId={scenarioId}
          item={editingItem}
          onSuccess={handleFormSuccess}
          onCancel={() => {
            setShowForm(false);
            setEditingItem(undefined);
          }}
        />
      )}

      {hasItems && (
        <>
          <HomeSetupSummaryCard scenarioId={scenarioId} compact={false} />

          <HomeSetupFilters
            selectedStatus={selectedStatus}
            selectedTier={selectedTier}
            selectedCategory={selectedCategory}
            onStatusChange={setSelectedStatus}
            onTierChange={setSelectedTier}
            onCategoryChange={setSelectedCategory}
          />
        </>
      )}

      {loadingRoadmap ? (
        <div className="flex justify-center py-12">
          <div className="animate-spin">
            <div className="w-8 h-8 border-4 border-gray-300 border-t-blue-600 rounded-full"></div>
          </div>
        </div>
      ) : hasItems ? (
        <div>
          {Object.entries(itemsByTier)
            .filter(([, items]) => items.length > 0)
            .map(([tier, items]) => (
              <PurchaseTierSection key={tier} tier={tier} items={items} onEditItem={handleEditItem} />
            ))}
        </div>
      ) : (
        <div className="bg-blue-50 rounded-lg p-8 border border-blue-200 text-center">
          <h3 className="text-lg font-semibold text-blue-900 mb-2">Comienza tu Setup del Hogar</h3>
          <p className="text-blue-700 mb-4">
            Inicializa tu roadmap desde nuestro catálogo base de compras esenciales.
          </p>
          <button
            onClick={handleInitialize}
            disabled={initializeRoadmap.isPending}
            className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-gray-400 font-medium"
          >
            {initializeRoadmap.isPending ? 'Inicializando...' : 'Inicializar catálogo'}
          </button>
        </div>
      )}
    </div>
  );
}
