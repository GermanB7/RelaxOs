import { NavLink, Outlet } from 'react-router-dom'

const links = [
  ['/admin', 'Overview'],
  ['/admin/expense-categories', 'Expenses'],
  ['/admin/purchase-catalog', 'Purchases'],
  ['/admin/meal-catalog', 'Meals'],
  ['/admin/modes', 'Modes'],
  ['/admin/recommendation-copy', 'Copy'],
  ['/admin/settings', 'Settings'],
  ['/admin/import-export', 'Import/Export'],
  ['/admin/audit-log', 'Audit'],
]

export function AdminLayout() {
  return (
    <div className="grid gap-4">
      <div className="rounded-lg border border-slate-200 bg-white p-3 shadow-sm">
        <nav className="flex gap-2 overflow-x-auto" aria-label="Admin">
          {links.map(([to, label]) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/admin'}
              className={({ isActive }) =>
                [
                  'whitespace-nowrap rounded-md px-3 py-2 text-sm font-semibold',
                  isActive
                    ? 'bg-slate-900 text-white'
                    : 'text-slate-600 hover:bg-slate-100',
                ].join(' ')
              }
            >
              {label}
            </NavLink>
          ))}
        </nav>
      </div>
      <Outlet />
    </div>
  )
}
