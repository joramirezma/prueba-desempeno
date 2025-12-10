import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context';
import { Card } from '../../components/ui';

export const DashboardPage: React.FC = () => {
  const { user, isAdmin, isAnalyst, isAffiliate } = useAuth();

  const roleName = user?.roles[0]?.replace('ROLE_', '') || 'Usuario';

  return (
    <div className="space-y-6">
      {/* Bienvenida */}
      <div className="bg-gradient-to-r from-primary-600 to-primary-700 rounded-xl shadow-lg p-6 text-white">
        <h1 className="text-3xl font-bold">¡Bienvenido, {user?.username}!</h1>
        <p className="mt-2 text-primary-100">
          Has iniciado sesión como <span className="font-semibold">{roleName}</span>
        </p>
      </div>

      {/* Tarjetas de acceso rápido según el rol */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* ADMIN: Acceso completo */}
        {isAdmin && (
          <>
            <Card className="hover:shadow-lg transition-shadow">
              <div className="flex items-center">
                <div className="flex-shrink-0 bg-primary-100 rounded-lg p-3">
                  <svg className="h-8 w-8 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                </div>
                <div className="ml-4">
                  <h3 className="text-lg font-semibold text-gray-900">Gestión de Afiliados</h3>
                  <p className="text-sm text-gray-500">Crear, editar y gestionar afiliados</p>
                </div>
              </div>
              <Link
                to="/affiliates"
                className="mt-4 block w-full text-center bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg transition-colors"
              >
                Ir a Afiliados
              </Link>
            </Card>

            <Card className="hover:shadow-lg transition-shadow">
              <div className="flex items-center">
                <div className="flex-shrink-0 bg-success-100 rounded-lg p-3">
                  <svg className="h-8 w-8 text-success-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                </div>
                <div className="ml-4">
                  <h3 className="text-lg font-semibold text-gray-900">Todas las Solicitudes</h3>
                  <p className="text-sm text-gray-500">Ver y gestionar todas las solicitudes</p>
                </div>
              </div>
              <Link
                to="/applications"
                className="mt-4 block w-full text-center bg-success-600 hover:bg-success-700 text-white px-4 py-2 rounded-lg transition-colors"
              >
                Ver Solicitudes
              </Link>
            </Card>

            <Card className="hover:shadow-lg transition-shadow">
              <div className="flex items-center">
                <div className="flex-shrink-0 bg-warning-100 rounded-lg p-3">
                  <svg className="h-8 w-8 text-warning-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <div className="ml-4">
                  <h3 className="text-lg font-semibold text-gray-900">Pendientes</h3>
                  <p className="text-sm text-gray-500">Solicitudes que requieren evaluación</p>
                </div>
              </div>
              <Link
                to="/applications/pending"
                className="mt-4 block w-full text-center bg-warning-600 hover:bg-warning-700 text-white px-4 py-2 rounded-lg transition-colors"
              >
                Ver Pendientes
              </Link>
            </Card>
          </>
        )}

        {/* ANALYST: Ver afiliados y evaluar solicitudes */}
        {isAnalyst && !isAdmin && (
          <>
            <Card className="hover:shadow-lg transition-shadow">
              <div className="flex items-center">
                <div className="flex-shrink-0 bg-primary-100 rounded-lg p-3">
                  <svg className="h-8 w-8 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                </div>
                <div className="ml-4">
                  <h3 className="text-lg font-semibold text-gray-900">Ver Afiliados</h3>
                  <p className="text-sm text-gray-500">Consultar información de afiliados</p>
                </div>
              </div>
              <Link
                to="/affiliates"
                className="mt-4 block w-full text-center bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg transition-colors"
              >
                Ver Afiliados
              </Link>
            </Card>

            <Card className="hover:shadow-lg transition-shadow">
              <div className="flex items-center">
                <div className="flex-shrink-0 bg-warning-100 rounded-lg p-3">
                  <svg className="h-8 w-8 text-warning-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
                  </svg>
                </div>
                <div className="ml-4">
                  <h3 className="text-lg font-semibold text-gray-900">Evaluar Solicitudes</h3>
                  <p className="text-sm text-gray-500">Revisar y evaluar solicitudes pendientes</p>
                </div>
              </div>
              <Link
                to="/applications/pending"
                className="mt-4 block w-full text-center bg-warning-600 hover:bg-warning-700 text-white px-4 py-2 rounded-lg transition-colors"
              >
                Evaluar Pendientes
              </Link>
            </Card>
          </>
        )}

        {/* AFFILIATE: Ver sus solicitudes y crear nuevas */}
        {isAffiliate && !isAdmin && !isAnalyst && (
          <>
            <Card className="hover:shadow-lg transition-shadow">
              <div className="flex items-center">
                <div className="flex-shrink-0 bg-primary-100 rounded-lg p-3">
                  <svg className="h-8 w-8 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                  </svg>
                </div>
                <div className="ml-4">
                  <h3 className="text-lg font-semibold text-gray-900">Nueva Solicitud</h3>
                  <p className="text-sm text-gray-500">Solicitar un nuevo crédito</p>
                </div>
              </div>
              <Link
                to="/applications/new"
                className="mt-4 block w-full text-center bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg transition-colors"
              >
                Crear Solicitud
              </Link>
            </Card>

            <Card className="hover:shadow-lg transition-shadow">
              <div className="flex items-center">
                <div className="flex-shrink-0 bg-success-100 rounded-lg p-3">
                  <svg className="h-8 w-8 text-success-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                </div>
                <div className="ml-4">
                  <h3 className="text-lg font-semibold text-gray-900">Mis Solicitudes</h3>
                  <p className="text-sm text-gray-500">Ver el estado de mis créditos</p>
                </div>
              </div>
              <Link
                to="/my-applications"
                className="mt-4 block w-full text-center bg-success-600 hover:bg-success-700 text-white px-4 py-2 rounded-lg transition-colors"
              >
                Ver Mis Solicitudes
              </Link>
            </Card>
          </>
        )}
      </div>

      {/* Información del sistema */}
      <Card title="Acerca del Sistema" subtitle="CoopCredit - Sistema de Solicitudes de Crédito">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="text-center">
            <div className="bg-primary-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-3">
              <svg className="h-8 w-8 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
            </div>
            <h4 className="font-semibold text-gray-900">Seguro</h4>
            <p className="text-sm text-gray-500">Autenticación JWT y roles de acceso</p>
          </div>
          <div className="text-center">
            <div className="bg-success-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-3">
              <svg className="h-8 w-8 text-success-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
            <h4 className="font-semibold text-gray-900">Rápido</h4>
            <p className="text-sm text-gray-500">Evaluación automática de riesgo</p>
          </div>
          <div className="text-center">
            <div className="bg-warning-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-3">
              <svg className="h-8 w-8 text-warning-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
            </div>
            <h4 className="font-semibold text-gray-900">Trazable</h4>
            <p className="text-sm text-gray-500">Seguimiento completo de solicitudes</p>
          </div>
        </div>
      </Card>
    </div>
  );
};
