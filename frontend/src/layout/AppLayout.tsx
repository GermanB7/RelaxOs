import { NavLink, Outlet } from 'react-router-dom'

const navItems = [
  { to: '/', label: 'Dashboard', end: true },
  { to: '/scenarios', label: 'Scenarios' },
  { to: '/recommendations', label: 'Recommendations' },
  { to: '/settings', label: 'Settings' },
]

export function AppLayout() {
  return (
    <div className="min-h-screen bg-[#f8faf9] text-slate-900">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex w-full max-w-5xl flex-col gap-4 px-4 py-4 sm:px-6">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.16em] text-teal-700">
              TranquiloOS
            </p>
            <h1 className="mt-1 text-xl font-semibold text-slate-950">
              IndependenceOS MVP
            </h1>
          </div>

          <nav className="flex gap-2 overflow-x-auto pb-1" aria-label="Main">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) =>
                  [
                    'whitespace-nowrap rounded-md px-3 py-2 text-sm font-medium transition',
                    isActive
                      ? 'bg-teal-700 text-white'
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-950',
                  ].join(' ')
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
        </div>
      </header>

      <main className="mx-auto w-full max-w-5xl px-4 py-6 sm:px-6 sm:py-8">
        <Outlet />
      </main>
    </div>
  )
}
