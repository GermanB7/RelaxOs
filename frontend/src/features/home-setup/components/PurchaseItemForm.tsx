import React, { useState } from 'react';
import type { UserPurchaseItem, CreateCustomPurchaseItemRequest } from '../api/homeSetupApi';
import { useCreateCustomPurchaseItem, useUpdatePurchaseItem } from '../hooks/useHomeSetup';

interface PurchaseItemFormProps {
  item?: UserPurchaseItem;
  scenarioId?: number;
  onSuccess?: () => void;
  onCancel?: () => void;
}

export function PurchaseItemForm({
  item,
  scenarioId,
  onSuccess,
  onCancel,
}: PurchaseItemFormProps) {
  const [formData, setFormData] = useState({
    name: item?.name || '',
    category: item?.category || 'Otros',
    tier: item?.tier || 'TIER_2',
    estimatedPrice: item?.estimatedPrice || undefined,
    priority: item?.priority || undefined,
    link: item?.link || '',
    notes: item?.notes || '',
  });

  const createMutation = useCreateCustomPurchaseItem();
  const updateMutation = useUpdatePurchaseItem();
  const isLoading = createMutation.isPending || updateMutation.isPending;

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'estimatedPrice' || name === 'priority' ? (value ? Number(value) : undefined) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      if (item) {
        await updateMutation.mutateAsync({
          id: item.id,
          request: {
            name: formData.name,
            category: formData.category,
            tier: formData.tier,
            estimatedPrice: formData.estimatedPrice ? Number(formData.estimatedPrice) : undefined,
            priority: formData.priority ? Number(formData.priority) : undefined,
            link: formData.link || undefined,
            notes: formData.notes || undefined,
          },
        });
      } else {
        const request: CreateCustomPurchaseItemRequest = {
          scenarioId,
          name: formData.name,
          category: formData.category,
          tier: formData.tier,
          estimatedPrice: formData.estimatedPrice ? Number(formData.estimatedPrice) : undefined,
          priority: formData.priority ? Number(formData.priority) : undefined,
          link: formData.link || undefined,
          notes: formData.notes || undefined,
        };
        await createMutation.mutateAsync(request);
      }
      onSuccess?.();
    } catch (error) {
      console.error('Error saving item:', error);
    }
  };

  return (
    <div className="bg-white rounded-lg p-6 border border-gray-200">
      <h2 className="text-xl font-bold mb-4">
        {item ? 'Editar item de compra' : 'Agregar nuevo item'}
      </h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Nombre *</label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Nombre del item"
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Categoría *</label>
            <select
              name="category"
              value={formData.category}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="Dormir">Dormir</option>
              <option value="Baño">Baño</option>
              <option value="Cocina">Cocina</option>
              <option value="Limpieza">Limpieza</option>
              <option value="Emergencias">Emergencias</option>
              <option value="Internet/Energía">Internet/Energía</option>
              <option value="Trabajo/estudio">Trabajo/estudio</option>
              <option value="Otros">Otros</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Tier *</label>
            <select
              name="tier"
              value={formData.tier}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="TIER_1">TIER_1 - Esencial</option>
              <option value="TIER_2">TIER_2 - Comodidad</option>
              <option value="TIER_3">TIER_3 - Mejora</option>
              <option value="TIER_4">TIER_4 - Lujo</option>
            </select>
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Precio estimado (COP)</label>
            <input
              type="number"
              name="estimatedPrice"
              value={formData.estimatedPrice || ''}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Ej: 100000"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Prioridad (1-100)</label>
            <input
              type="number"
              name="priority"
              value={formData.priority || ''}
              onChange={handleChange}
              min="1"
              max="100"
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Ej: 20"
            />
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Link (opcional)</label>
          <input
            type="url"
            name="link"
            value={formData.link}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="https://ejemplo.com"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Notas (opcional)</label>
          <textarea
            name="notes"
            value={formData.notes}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Notas personales sobre este item"
            rows={3}
          />
        </div>

        <div className="flex gap-2 pt-4">
          <button
            type="submit"
            disabled={isLoading}
            className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-gray-400 font-medium"
          >
            {isLoading ? 'Guardando...' : item ? 'Actualizar' : 'Crear item'}
          </button>
          {onCancel && (
            <button
              type="button"
              onClick={onCancel}
              className="flex-1 px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 font-medium"
            >
              Cancelar
            </button>
          )}
        </div>
      </form>
    </div>
  );
}
