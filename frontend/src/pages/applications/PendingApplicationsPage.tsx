import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { applicationService } from '../../services';
import { CreditApplicationResponse } from '../../types';
import { useAuth } from '../../context';
import { Card, Button, Loading, Alert, ApplicationStatusBadge } from '../../components/ui';
import { ApplicationDetailModal } from './ApplicationsListPage';

export const PendingApplicationsPage: React.FC = () => {
  const { isAdmin, isAnalyst } = useAuth();
  const [selectedApplication, setSelectedApplication] = useState<CreditApplicationResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Query para obtener solicitudes pendientes
  const { data: applications, isLoading, error: queryError, refetch } = useQuery({
    queryKey: ['applications', 'pending'],
    queryFn: applicationService.getPending,
    enabled: isAdmin || isAnalyst,
  });

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(value);
  };

  const formatDate = (date: string) => {
    return new Date(date).toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  if (!isAdmin && !isAnalyst) {
    return (
      <Alert
        type="warning"
        title="Acceso Denegado"
        message="No tienes permisos para evaluar solicitudes."
      />
    );
  }

  if (isLoading) {
    return <Loading text="Cargando solicitudes pendientes..." />;
  }

  if (queryError) {
    return (
      <Alert
        type="error"
        title="Error"
        message="No se pudieron cargar las solicitudes. Por favor intenta de nuevo."
      />
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Solicitudes Pendientes</h1>
        <p className="mt-1 text-sm text-gray-500">
          {applications?.length || 0} solicitudes esperando evaluación
        </p>
      </div>

      {/* Alertas */}
      {error && (
        <Alert type="error" message={error} onClose={() => setError(null)} />
      )}
      {success && (
        <Alert type="success" message={success} onClose={() => setSuccess(null)} />
      )}

      {/* Sin solicitudes pendientes */}
      {applications?.length === 0 && (
        <Card className="text-center py-12">
          <div className="flex flex-col items-center">
            <svg
              className="h-16 w-16 text-gray-400 mb-4"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
            <h3 className="text-lg font-medium text-gray-900">¡Todo al día!</h3>
            <p className="text-gray-500 mt-1">No hay solicitudes pendientes de evaluación.</p>
          </div>
        </Card>
      )}

      {/* Lista de solicitudes pendientes */}
      <div className="grid gap-4">
        {applications?.map((app) => (
          <Card key={app.id} className="hover:shadow-lg transition-shadow">
            <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between">
              <div className="flex-1">
                <div className="flex items-center justify-between lg:justify-start">
                  <span className="text-sm font-medium text-primary-600">Solicitud #{app.id}</span>
                  <ApplicationStatusBadge status={app.status} />
                </div>
                <h3 className="mt-2 text-lg font-semibold text-gray-900">{app.affiliateName}</h3>
                <p className="text-sm text-gray-500">Documento: {app.affiliateDocumentNumber}</p>

                <div className="mt-4 grid grid-cols-2 md:grid-cols-4 gap-4">
                  <div>
                    <p className="text-xs text-gray-500 uppercase">Monto</p>
                    <p className="font-semibold text-gray-900">{formatCurrency(app.requestedAmount)}</p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-500 uppercase">Plazo</p>
                    <p className="font-semibold text-gray-900">{app.termMonths} meses</p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-500 uppercase">Tasa</p>
                    <p className="font-semibold text-gray-900">{app.proposedRate}%</p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-500 uppercase">Cuota Est.</p>
                    <p className="font-semibold text-gray-900">{formatCurrency(app.estimatedMonthlyPayment)}</p>
                  </div>
                </div>

                <p className="mt-2 text-xs text-gray-400">
                  Solicitado el {formatDate(app.applicationDate)}
                </p>
              </div>

              <div className="mt-4 lg:mt-0 lg:ml-6 flex flex-col sm:flex-row lg:flex-col gap-2">
                <Button
                  variant="outline"
                  onClick={() => setSelectedApplication(app)}
                >
                  Ver Detalles
                </Button>
                <Button
                  variant="success"
                  onClick={() => setSelectedApplication(app)}
                >
                  Evaluar Solicitud
                </Button>
              </div>
            </div>
          </Card>
        ))}
      </div>

      {/* Modal de detalles */}
      {selectedApplication && (
        <ApplicationDetailModal
          application={selectedApplication}
          onClose={() => setSelectedApplication(null)}
          onRefresh={() => refetch()}
        />
      )}
    </div>
  );
};
