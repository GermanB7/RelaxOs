interface HomeSetupFiltersProps {
  selectedStatus?: string;
  selectedTier?: string;
  selectedCategory?: string;
  onStatusChange?: (status?: string) => void;
  onTierChange?: (tier?: string) => void;
  onCategoryChange?: (category?: string) => void;
}

export function HomeSetupFilters({
  selectedStatus,
  selectedTier,
  selectedCategory,
  onStatusChange,
  onTierChange,
  onCategoryChange,
}: HomeSetupFiltersProps) {
  return (
    <div className="bg-white rounded-lg p-4 border border-gray-200 mb-6">
      <h3 className="font-semibold text-gray-900 mb-4">Filtros</h3>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Estado</label>
          <select
            value={selectedStatus || ''}
            onChange={(e) => onStatusChange?.(e.target.value || undefined)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Todos los estados</option>
            <option value="PENDING">Pendiente</option>
            <option value="BOUGHT">Comprado</option>
            <option value="POSTPONED">Pospuesto</option>
            <option value="WISHLIST">Deseo</option>
            <option value="SKIPPED">Omitido</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Tier</label>
          <select
            value={selectedTier || ''}
            onChange={(e) => onTierChange?.(e.target.value || undefined)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Todos los tiers</option>
            <option value="TIER_1">TIER 1 - Esencial</option>
            <option value="TIER_2">TIER 2 - Comodidad</option>
            <option value="TIER_3">TIER 3 - Mejora</option>
            <option value="TIER_4">TIER 4 - Lujo</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Categoría</label>
          <select
            value={selectedCategory || ''}
            onChange={(e) => onCategoryChange?.(e.target.value || undefined)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Todas las categorías</option>
            <option value="Dormir">Dormir</option>
            <option value="Baño">Baño</option>
            <option value="Cocina">Cocina</option>
            <option value="Limpieza">Limpieza</option>
            <option value="Emergencias">Emergencias</option>
            <option value="Internet/Energía">Internet/Energía</option>
            <option value="Trabajo/estudio">Trabajo/estudio</option>
          </select>
        </div>
      </div>
    </div>
  );
}
