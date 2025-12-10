import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context';

export const Navbar: React.FC = () => {
  const { user, logout, isAdmin, isAnalyst, isAffiliate } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path: string) => location.pathname === path;

  const navLinkClass = (path: string) =>
    `px-3 py-2 rounded-md text-sm font-medium transition-colors ${
      isActive(path)
        ? 'bg-primary-700 text-white'
        : 'text-primary-100 hover:bg-primary-500 hover:text-white'
    }`;

  return (
    <nav className="bg-primary-600 shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo y nombre */}
          <div className="flex items-center">
            <Link to="/dashboard" className="flex items-center">
              <svg className="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
              </svg>
              <span className="ml-2 text-white text-xl font-bold">CoopCredit</span>
            </Link>
          </div>

          {/* Navegación */}
          <div className="hidden md:block">
            <div className="ml-10 flex items-baseline space-x-4">
              <Link to="/dashboard" className={navLinkClass('/dashboard')}>
                Dashboard
              </Link>

              {/* Enlaces para ADMIN */}
              {isAdmin && (
                <>
                  <Link to="/affiliates" className={navLinkClass('/affiliates')}>
                    Afiliados
                  </Link>
                  <Link to="/applications" className={navLinkClass('/applications')}>
                    Todas las Solicitudes
                  </Link>
                </>
              )}

              {/* Enlaces para ANALYST */}
              {isAnalyst && !isAdmin && (
                <>
                  <Link to="/affiliates" className={navLinkClass('/affiliates')}>
                    Afiliados
                  </Link>
                  <Link to="/applications/pending" className={navLinkClass('/applications/pending')}>
                    Solicitudes Pendientes
                  </Link>
                </>
              )}

              {/* Enlaces para AFFILIATE */}
              {isAffiliate && !isAdmin && !isAnalyst && (
                <>
                  <Link to="/my-applications" className={navLinkClass('/my-applications')}>
                    Mis Solicitudes
                  </Link>
                  <Link to="/applications/new" className={navLinkClass('/applications/new')}>
                    Nueva Solicitud
                  </Link>
                </>
              )}
            </div>
          </div>

          {/* Usuario y logout */}
          <div className="flex items-center">
            <div className="text-primary-100 text-sm mr-4">
              <span className="hidden sm:inline">Hola, </span>
              <span className="font-medium text-white">{user?.username}</span>
              <span className="hidden sm:inline ml-2 px-2 py-1 bg-primary-500 rounded text-xs">
                {user?.roles[0]?.replace('ROLE_', '')}
              </span>
            </div>
            <button
              onClick={handleLogout}
              className="bg-primary-700 hover:bg-primary-800 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
            >
              Cerrar Sesión
            </button>
          </div>
        </div>
      </div>

      {/* Menú móvil */}
      <div className="md:hidden border-t border-primary-500">
        <div className="px-2 pt-2 pb-3 space-y-1">
          <Link to="/dashboard" className={`block ${navLinkClass('/dashboard')}`}>
            Dashboard
          </Link>
          {isAdmin && (
            <>
              <Link to="/affiliates" className={`block ${navLinkClass('/affiliates')}`}>
                Afiliados
              </Link>
              <Link to="/applications" className={`block ${navLinkClass('/applications')}`}>
                Todas las Solicitudes
              </Link>
            </>
          )}
          {isAnalyst && !isAdmin && (
            <>
              <Link to="/affiliates" className={`block ${navLinkClass('/affiliates')}`}>
                Afiliados
              </Link>
              <Link to="/applications/pending" className={`block ${navLinkClass('/applications/pending')}`}>
                Solicitudes Pendientes
              </Link>
            </>
          )}
          {isAffiliate && !isAdmin && !isAnalyst && (
            <>
              <Link to="/my-applications" className={`block ${navLinkClass('/my-applications')}`}>
                Mis Solicitudes
              </Link>
              <Link to="/applications/new" className={`block ${navLinkClass('/applications/new')}`}>
                Nueva Solicitud
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};
