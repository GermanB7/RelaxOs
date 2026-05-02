import { RouterProvider, createBrowserRouter } from 'react-router-dom'
import { AppLayout } from '../layout/AppLayout'
import { DashboardPage } from '../features/dashboard/DashboardPage'
import { RecommendationsPage } from '../features/recommendations/RecommendationsPage'
import { ScenarioDetailPage } from '../features/scenarios/pages/ScenarioDetailPage'
import { ScenariosPage } from '../features/scenarios/pages/ScenariosPage'
import { SettingsPage } from '../features/settings/SettingsPage'

const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
    children: [
      {
        index: true,
        element: <DashboardPage />,
      },
      {
        path: 'scenarios',
        element: <ScenariosPage />,
      },
      {
        path: 'scenarios/:scenarioId',
        element: <ScenarioDetailPage />,
      },
      {
        path: 'recommendations',
        element: <RecommendationsPage />,
      },
      {
        path: 'settings',
        element: <SettingsPage />,
      },
    ],
  },
])

export function AppRouter() {
  return <RouterProvider router={router} />
}
