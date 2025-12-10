import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from './context';
import { Layout, ProtectedRoute } from './components';
import {
  LoginPage,
  RegisterPage,
  DashboardPage,
  AffiliatesPage,
  ApplicationsListPage,
  PendingApplicationsPage,
  CreateApplicationPage,
  MyApplicationsPage,
} from './pages';

// Crear cliente de React Query
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
      staleTime: 5 * 60 * 1000, // 5 minutos
    },
  },
});

const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <AuthProvider>
          <Routes>
            {/* Rutas públicas */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Rutas protegidas */}
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <Layout />
                </ProtectedRoute>
              }
            >
              {/* Dashboard - todos los usuarios autenticados */}
              <Route index element={<Navigate to="/dashboard" replace />} />
              <Route path="dashboard" element={<DashboardPage />} />

              {/* Afiliados - ADMIN y ANALYST */}
              <Route
                path="affiliates"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_ANALYST']}>
                    <AffiliatesPage />
                  </ProtectedRoute>
                }
              />

              {/* Todas las solicitudes - solo ADMIN */}
              <Route
                path="applications"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
                    <ApplicationsListPage />
                  </ProtectedRoute>
                }
              />

              {/* Solicitudes pendientes - ADMIN y ANALYST */}
              <Route
                path="applications/pending"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_ANALYST']}>
                    <PendingApplicationsPage />
                  </ProtectedRoute>
                }
              />

              {/* Crear solicitud - ADMIN y AFFILIATE */}
              <Route
                path="applications/new"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_AFFILIATE']}>
                    <CreateApplicationPage />
                  </ProtectedRoute>
                }
              />

              {/* Mis solicitudes - AFFILIATE */}
              <Route
                path="my-applications"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_AFFILIATE', 'ROLE_ADMIN']}>
                    <MyApplicationsPage />
                  </ProtectedRoute>
                }
              />
            </Route>

            {/* Ruta 404 */}
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </AuthProvider>
      </BrowserRouter>
    </QueryClientProvider>
  );
};

export default App;
