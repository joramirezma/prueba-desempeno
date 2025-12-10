import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { applicationService } from '../../services';
import { CreditApplicationResponse } from '../../types';
import { Card, Button, Loading, Alert, ApplicationStatusBadge, RiskLevelBadge, Modal } from '../../components/ui';
import { useAuth } from '../../context/AuthContext';

export const MyApplicationsPage: React.FC = () => {
  const { user } = useAuth();
  const [selectedApplication, setSelectedApplication] = useState<CreditApplicationResponse | null>(null);

  // Query para obtener las solicitudes del usuario autenticado automáticamente
  const {
    data: applications,
    isLoading,
    error: queryError,
  } = useQuery({
    queryKey: ['myApplications', user?.documentNumber],
    queryFn: () => applicationService.getByAffiliate(user!.documentNumber!),
    enabled: !!user?.documentNumber,
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
      month: 'long',
      day: 'numeric',
    });
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Mis Solicitudes de Crédito</h1>
          <p className="mt-1 text-sm text-gray-500">
            Consulta el estado de tus solicitudes de crédito
          </p>
        </div>
        <Link to="/applications/new">
          <Button className="mt-4 sm:mt-0">+ Nueva Solicitud</Button>
        </Link>
      </div>

      {/* Mensaje si no tiene documento configurado */}
      {!user?.documentNumber && (
        <Alert
          type="warning"
          title="Documento no configurado"
          message="No se puede cargar tus solicitudes porque tu perfil no tiene un número de documento asociado. Por favor contacta al administrador."
        />
      )}

      {/* Loading */}
      {isLoading && <Loading text="Buscando solicitudes..." />}

      {/* Error */}
      {queryError && (
        <Alert
          type="error"
          title="Error"
          message="No se encontraron solicitudes para este documento o no tienes permisos para verlas."
        />
      )}

      {/* Sin solicitudes */}
      {applications && applications.length === 0 && (
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
                d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
              />
            </svg>
            <h3 className="text-lg font-medium text-gray-900">No tienes solicitudes</h3>
            <p className="text-gray-500 mt-1">Crea tu primera solicitud de crédito.</p>
            <Link to="/applications/new" className="mt-4">
              <Button>Crear Solicitud</Button>
            </Link>
          </div>
        </Card>
      )}

      {/* Lista de solicitudes */}
      {applications && applications.length > 0 && (
        <div className="space-y-4">
          <div className="text-sm text-gray-500">
            {applications.length} solicitud(es) encontrada(s)
          </div>

          {applications.map((app) => (
            <Card key={app.id} className="hover:shadow-lg transition-shadow">
              <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between">
                <div className="flex-1">
                  <div className="flex items-center justify-between lg:justify-start gap-4">
                    <span className="text-lg font-semibold text-primary-600">
                      Solicitud #{app.id}
                    </span>
                    <ApplicationStatusBadge status={app.status} />
                  </div>

                  <div className="mt-4 grid grid-cols-2 md:grid-cols-4 gap-4">
                    <div>
                      <p className="text-xs text-gray-500 uppercase">Monto</p>
                      <p className="font-semibold text-gray-900">
                        {formatCurrency(app.requestedAmount)}
                      </p>
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
                      <p className="text-xs text-gray-500 uppercase">Cuota Mensual</p>
                      <p className="font-semibold text-gray-900">
                        {formatCurrency(app.estimatedMonthlyPayment)}
                      </p>
                    </div>
                  </div>

                  <p className="mt-2 text-xs text-gray-400">
                    Solicitado el {formatDate(app.applicationDate)}
                  </p>

                  {/* Resultado de evaluación si existe */}
                  {app.riskEvaluation && (
                    <div className="mt-4 p-3 bg-gray-50 rounded-lg">
                      <div className="flex items-center gap-4 flex-wrap">
                        <div className="flex items-center gap-2">
                          <span className="text-sm text-gray-500">Score:</span>
                          <span className="font-bold">{app.riskEvaluation.score}</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <span className="text-sm text-gray-500">Riesgo:</span>
                          <RiskLevelBadge level={app.riskEvaluation.riskLevel} />
                        </div>
                        <div className="flex items-center gap-2">
                          <span className="text-sm text-gray-500">Resultado:</span>
                          <span
                            className={`font-semibold ${app.riskEvaluation.approved ? 'text-success-600' : 'text-danger-600'
                              }`}
                          >
                            {app.riskEvaluation.approved ? 'Aprobado' : 'Rechazado'}
                          </span>
                        </div>
                      </div>
                      <p className="mt-2 text-sm text-gray-600">{app.riskEvaluation.reason}</p>
                    </div>
                  )}
                </div>

                <div className="mt-4 lg:mt-0 lg:ml-6">
                  <Button variant="outline" onClick={() => setSelectedApplication(app)}>
                    Ver Detalles
                  </Button>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Modal de detalles */}
      {selectedApplication && (
        <Modal
          isOpen={true}
          onClose={() => setSelectedApplication(null)}
          title={`Solicitud #${selectedApplication.id}`}
          size="lg"
        >
          <div className="space-y-6">
            {/* Detalles del crédito */}
            <div>
              <h4 className="font-medium text-gray-900 mb-2">Detalles del Crédito</h4>
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="text-gray-500">Monto Solicitado:</span>
                  <span className="ml-2 font-medium">
                    {formatCurrency(selectedApplication.requestedAmount)}
                  </span>
                </div>
                <div>
                  <span className="text-gray-500">Plazo:</span>
                  <span className="ml-2 font-medium">{selectedApplication.termMonths} meses</span>
                </div>
                <div>
                  <span className="text-gray-500">Tasa:</span>
                  <span className="ml-2 font-medium">{selectedApplication.proposedRate}%</span>
                </div>
                <div>
                  <span className="text-gray-500">Cuota Estimada:</span>
                  <span className="ml-2 font-medium">
                    {formatCurrency(selectedApplication.estimatedMonthlyPayment)}
                  </span>
                </div>
                <div>
                  <span className="text-gray-500">Fecha:</span>
                  <span className="ml-2 font-medium">
                    {formatDate(selectedApplication.applicationDate)}
                  </span>
                </div>
                <div>
                  <span className="text-gray-500">Estado:</span>
                  <span className="ml-2">
                    <ApplicationStatusBadge status={selectedApplication.status} />
                  </span>
                </div>
              </div>
            </div>

            {/* Evaluación de riesgo */}
            {selectedApplication.riskEvaluation && (
              <div className="border-t pt-4">
                <h4 className="font-medium text-gray-900 mb-2">Evaluación de Riesgo</h4>
                <div className="bg-gray-50 rounded-lg p-4 space-y-3">
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-gray-500">Score de Riesgo:</span>
                    <span className="font-bold text-lg">
                      {selectedApplication.riskEvaluation.score}
                    </span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-gray-500">Nivel de Riesgo:</span>
                    <RiskLevelBadge level={selectedApplication.riskEvaluation.riskLevel} />
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-gray-500">Ratio Deuda/Ingreso:</span>
                    <span className="font-medium">
                      {selectedApplication.riskEvaluation.debtToIncomeRatio.toFixed(2)}%
                    </span>
                  </div>
                  <div className="pt-2 border-t">
                    <p className="text-sm text-gray-500">Razón:</p>
                    <p className="text-sm font-medium">
                      {selectedApplication.riskEvaluation.reason}
                    </p>
                  </div>
                  {selectedApplication.riskEvaluation.details && selectedApplication.riskEvaluation.approved && (
                    <div>
                      <p className="text-sm text-gray-500">Detalles:</p>
                      <p className="text-sm">{selectedApplication.riskEvaluation.details}</p>
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* Mensaje si está pendiente */}
            {selectedApplication.status === 'PENDING' && (
              <div className="bg-warning-50 border border-warning-200 rounded-lg p-4">
                <div className="flex items-center">
                  <svg
                    className="h-5 w-5 text-warning-600 mr-2"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                    />
                  </svg>
                  <span className="text-warning-700 font-medium">
                    Tu solicitud está siendo evaluada
                  </span>
                </div>
                <p className="mt-1 text-sm text-warning-600">
                  Un analista revisará tu solicitud pronto. Te notificaremos cuando haya una
                  decisión.
                </p>
              </div>
            )}

            <div className="flex justify-end pt-4 border-t">
              <Button variant="outline" onClick={() => setSelectedApplication(null)}>
                Cerrar
              </Button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
