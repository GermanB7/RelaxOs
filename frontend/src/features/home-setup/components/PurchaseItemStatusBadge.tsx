interface PurchaseItemStatusBadgeProps {
  status: string;
}

export function PurchaseItemStatusBadge({ status }: PurchaseItemStatusBadgeProps) {
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'BOUGHT':
        return 'bg-green-100 text-green-800 border-green-300';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800 border-yellow-300';
      case 'POSTPONED':
        return 'bg-orange-100 text-orange-800 border-orange-300';
      case 'WISHLIST':
        return 'bg-blue-100 text-blue-800 border-blue-300';
      case 'SKIPPED':
        return 'bg-gray-100 text-gray-800 border-gray-300';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-300';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'BOUGHT':
        return 'Comprado';
      case 'PENDING':
        return 'Pendiente';
      case 'POSTPONED':
        return 'Pospuesto';
      case 'WISHLIST':
        return 'Deseo';
      case 'SKIPPED':
        return 'Omitido';
      default:
        return status;
    }
  };

  return (
    <span className={`inline-block px-3 py-1 rounded-full text-xs font-semibold border ${getStatusColor(status)}`}>
      {getStatusLabel(status)}
    </span>
  );
}
