import { RouterProvider, createBrowserRouter } from 'react-router-dom'
import { AppLayout } from '../layout/AppLayout'
import { ProtectedRoute } from '../features/auth/components/ProtectedRoute'
import { LoginPage } from '../features/auth/pages/LoginPage'
import { RegisterPage } from '../features/auth/pages/RegisterPage'
import { AdminLayout } from '../features/admin/components/AdminLayout'
import { AdminAuditLogPage } from '../features/admin/pages/AdminAuditLogPage'
import { AdminOverviewPage } from '../features/admin/pages/AdminOverviewPage'
import { ExpenseCategoriesAdminPage } from '../features/admin/pages/ExpenseCategoriesAdminPage'
import { ImportExportAdminPage } from '../features/admin/pages/ImportExportAdminPage'
import { MealCatalogAdminPage } from '../features/admin/pages/MealCatalogAdminPage'
import { ModesAdminPage } from '../features/admin/pages/ModesAdminPage'
import { PurchaseCatalogAdminPage } from '../features/admin/pages/PurchaseCatalogAdminPage'
import { RecommendationCopyAdminPage } from '../features/admin/pages/RecommendationCopyAdminPage'
import { SystemSettingsAdminPage } from '../features/admin/pages/SystemSettingsAdminPage'
import { DashboardPage } from '../features/dashboard/DashboardPage'
import { RecommendationsPage } from '../features/recommendations/RecommendationsPage'
import { HomeSetupPage } from '../features/home-setup/pages/HomeSetupPage'
import { ModesPage } from '../features/modes/pages/ModesPage'
import { MealsPage } from '../features/meals/pages/MealsPage'
import { DecisionTimelinePage } from '../features/decisions/pages/DecisionTimelinePage'
import { ScenarioComparisonPage } from '../features/scenarios/pages/ScenarioComparisonPage'
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
            path: 'scenarios/compare',
            element: <ScenarioComparisonPage />,
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
            path: 'decisions',
            element: <DecisionTimelinePage />,
          },
          {
            path: 'settings',
            element: <SettingsPage />,
          },
          {
            path: 'admin',
            element: <AdminLayout />,
            children: [
              { index: true, element: <AdminOverviewPage /> },
              {
                path: 'expense-categories',
                element: <ExpenseCategoriesAdminPage />,
              },
              {
                path: 'purchase-catalog',
                element: <PurchaseCatalogAdminPage />,
              },
              { path: 'meal-catalog', element: <MealCatalogAdminPage /> },
              { path: 'modes', element: <ModesAdminPage /> },
              {
                path: 'recommendation-copy',
                element: <RecommendationCopyAdminPage />,
              },
              { path: 'settings', element: <SystemSettingsAdminPage /> },
              { path: 'import-export', element: <ImportExportAdminPage /> },
              { path: 'audit-log', element: <AdminAuditLogPage /> },
            ],
          },
        ],
      },
    ],
  },
])

export function AppRouter() {
  return <RouterProvider router={router} />
}
