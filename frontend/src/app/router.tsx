import { RouterProvider, createBrowserRouter } from 'react-router-dom'
import { AppLayout } from '../layout/AppLayout'
import { ProtectedRoute } from '../features/auth/components/ProtectedRoute'
import { LoginPage } from '../features/auth/pages/LoginPage'
import { RegisterPage } from '../features/auth/pages/RegisterPage'
import { DashboardPage } from '../features/dashboard/DashboardPage'
import { RecommendationsPage } from '../features/recommendations/RecommendationsPage'
import { HomeSetupPage } from '../features/home-setup/pages/HomeSetupPage'
import { ModesPage } from '../features/modes/pages/ModesPage'
import { MealsPage } from '../features/meals/pages/MealsPage'
import { ScenarioDetailPage } from '../features/scenarios/pages/ScenarioDetailPage'
import { ScenariosPage } from '../features/scenarios/pages/ScenariosPage'
import { SettingsPage } from '../features/settings/SettingsPage'

const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/register',
    element: <RegisterPage />,
  },
  {
    path: '/',
    element: <ProtectedRoute />,
    children: [
      {
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
            path: 'home-setup',
            element: <HomeSetupPage />,
          },
          {
            path: 'modes',
            element: <ModesPage />,
          },
          {
            path: 'meals',
            element: <MealsPage />,
          },
          {
            path: 'settings',
            element: <SettingsPage />,
          },
        ],
      },
    ],
  },
])

export function AppRouter() {
  return <RouterProvider router={router} />
}
