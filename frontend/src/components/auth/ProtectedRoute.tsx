import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context';
import { Role } from '../../types';
import { Loading } from '../ui';

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles?: Role[];
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  allowedRoles,
}) => {
  const { isAuthenticated, user, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Loading text="Verificando autenticación..." />
      </div>
    );
  }

  if (!isAuthenticated) {
    // Redirigir al login guardando la ubicación actual
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // Verificar roles si se especifican
  if (allowedRoles && allowedRoles.length > 0) {
    const hasAllowedRole = user?.roles.some((role) => allowedRoles.includes(role));
    if (!hasAllowedRole) {
      // No tiene permisos, redirigir al dashboard
      return <Navigate to="/dashboard" replace />;
    }
  }

  return <>{children}</>;
};
