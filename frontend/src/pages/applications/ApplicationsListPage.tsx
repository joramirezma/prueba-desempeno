import React, { useState } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { applicationService } from '../../services';
import { CreditApplicationResponse } from '../../types';
import { useAuth } from '../../context';
import { Card, Button, Loading, Alert, Modal, ApplicationStatusBadge, RiskLevelBadge } from '../../components/ui';

export const ApplicationsListPage: React.FC = () => {
  const { isAdmin, isAnalyst } = useAuth();
  const queryClient = useQueryClient();
  const [selectedApplication, setSelectedApplication] = useState<CreditApplicationResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Query para obtener todas las solicitudes (solo ADMIN)
  const { data: applications, isLoading, error: queryError, refetch } = useQuery({
    queryKey: ['applications'],
    queryFn: applicationService.getAll,
    enabled: isAdmin,
  });

  const handleRefresh = () => {
    refetch();
    queryClient.invalidateQueries({ queryKey: ['applications'] });
  };

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

  if (!isAdmin) {
    return (
      <Alert
        type="warning"
        title="Acceso Denegado"
        message="No tienes permisos para ver todas las solicitudes."
      />
    );
  }

  if (isLoading) {
    return <Loading text="Cargando solicitudes..." />;
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
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Todas las Solicitudes</h1>
          <p className="mt-1 text-sm text-gray-500">
            {applications?.length || 0} solicitudes en el sistema
          </p>
        </div>
        <Link to="/applications/new">
          <Button className="mt-4 sm:mt-0">+ Nueva Solicitud</Button>
        </Link>
      </div>

      {/* Alertas */}
      {error && (
        <Alert type="error" message={error} onClose={() => setError(null)} />
      )}
      {success && (
        <Alert type="success" message={success} onClose={() => setSuccess(null)} />
      )}

      {/* Estadísticas */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="bg-gray-50">
          <div className="text-center">
            <p className="text-2xl font-bold text-gray-900">{applications?.length || 0}</p>
            <p className="text-sm text-gray-500">Total</p>
          </div>
        </Card>
        <Card className="bg-warning-50">
          <div className="text-center">
            <p className="text-2xl font-bold text-warning-600">
              {applications?.filter((a) => a.status === 'PENDING').length || 0}
            </p>
            <p className="text-sm text-gray-500">Pendientes</p>
          </div>
        </Card>
        <Card className="bg-success-50">
          <div className="text-center">
            <p className="text-2xl font-bold text-success-600">
              {applications?.filter((a) => a.status === 'APPROVED').length || 0}
            </p>
            <p className="text-sm text-gray-500">Aprobadas</p>
          </div>
        </Card>
        <Card className="bg-danger-50">
          <div className="text-center">
            <p className="text-2xl font-bold text-danger-600">
              {applications?.filter((a) => a.status === 'REJECTED').length || 0}
            </p>
            <p className="text-sm text-gray-500">Rechazadas</p>
          </div>
        </Card>
      </div>

      {/* Tabla de solicitudes */}
      <Card>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Afiliado
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Monto
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Plazo
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Cuota Est.
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Fecha
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Estado
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Acciones
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {applications?.map((app) => (
                <tr key={app.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    #{app.id}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">{app.affiliateName}</div>
                    <div className="text-sm text-gray-500">{app.affiliateDocumentNumber}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {formatCurrency(app.requestedAmount)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {app.termMonths} meses
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {formatCurrency(app.estimatedMonthlyPayment)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {formatDate(app.applicationDate)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <ApplicationStatusBadge status={app.status} />
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setSelectedApplication(app)}
                    >
                      Ver
                    </Button>
                    {app.status === 'PENDING' && (isAdmin || isAnalyst) && (
                      <Button
                        variant="success"
                        size="sm"
                        onClick={() => setSelectedApplication(app)}
                      >
                        Evaluar
                      </Button>
                    )}
                  </td>
                </tr>
              ))}
              {(!applications || applications.length === 0) && (
                <tr>
                  <td colSpan={8} className="px-6 py-12 text-center text-gray-500">
                    No hay solicitudes registradas
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </Card>

      {/* Modal de detalles */}
      {selectedApplication && (
        <ApplicationDetailModal
          application={selectedApplication}
          onClose={() => setSelectedApplication(null)}
          onRefresh={handleRefresh}
        />
      )}
    </div>
  );
};

// Modal de detalles de solicitud
const ApplicationDetailModal: React.FC<{
  application: CreditApplicationResponse;
  onClose: () => void;
  onRefresh?: () => void;
}> = ({ application, onClose, onRefresh }) => {
  const [evaluatingRisk, setEvaluatingRisk] = useState(false);
  const [makingDecision, setMakingDecision] = useState(false);
  const [showDecisionForm, setShowDecisionForm] = useState(false);
  const [decision, setDecision] = useState<'approve' | 'reject' | null>(null);
  const [comments, setComments] = useState('');
  const [localApplication, setLocalApplication] = useState(application);

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(value);
  };

  const handleEvaluateRisk = async () => {
    try {
      setEvaluatingRisk(true);
      const updated = await applicationService.evaluateRisk(localApplication.id);
      setLocalApplication(updated);
      if (onRefresh) onRefresh();
    } catch (error: any) {
      console.error('Error evaluating risk:', error);
      alert(error.response?.data?.detail || 'Error al evaluar el riesgo. La solicitud puede haber sido ya evaluada.');
    } finally {
      setEvaluatingRisk(false);
    }
  };

  const handleMakeDecision = async () => {
    if (!decision) return;

    try {
      setMakingDecision(true);
      const approved = decision === 'approve';
      const updated = await applicationService.makeDecision(localApplication.id, approved, comments);
      setLocalApplication(updated);
      if (onRefresh) onRefresh();
      setShowDecisionForm(false);
      setDecision(null);
      setComments('');
      // Cerrar el modal después de tomar la decisión exitosamente
      onClose();
    } catch (error: any) {
      console.error('Error making decision:', error);
      alert(error.response?.data?.detail || 'Error al procesar la decisión. La solicitud puede haber sido ya procesada.');
    } finally {
      setMakingDecision(false);
    }
  };

  const hasRiskEvaluation = localApplication.riskEvaluation !== null;
  const isPending = localApplication.status === 'PENDING';

  return (
    <Modal isOpen={true} onClose={onClose} title={`Solicitud #${localApplication.id}`} size="lg">
      <div className="space-y-6">
        {/* Información del afiliado */}
        <div className="bg-gray-50 rounded-lg p-4">
          <h4 className="font-medium text-gray-900 mb-2">Información del Afiliado</h4>
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span className="text-gray-500">Nombre:</span>
              <span className="ml-2 font-medium">{localApplication.affiliateName}</span>
            </div>
            <div>
              <span className="text-gray-500">Documento:</span>
              <span className="ml-2 font-medium">{localApplication.affiliateDocumentNumber}</span>
            </div>
          </div>
        </div>

        {/* Detalles de la solicitud */}
        <div>
          <h4 className="font-medium text-gray-900 mb-2">Detalles del Crédito</h4>
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span className="text-gray-500">Monto Solicitado:</span>
              <span className="ml-2 font-medium">{formatCurrency(localApplication.requestedAmount)}</span>
            </div>
            <div>
              <span className="text-gray-500">Plazo:</span>
              <span className="ml-2 font-medium">{localApplication.termMonths} meses</span>
            </div>
            <div>
              <span className="text-gray-500">Tasa Propuesta:</span>
              <span className="ml-2 font-medium">{localApplication.proposedRate}%</span>
            </div>
            <div>
              <span className="text-gray-500">Cuota Estimada:</span>
              <span className="ml-2 font-medium">{formatCurrency(localApplication.estimatedMonthlyPayment)}</span>
            </div>
            <div>
              <span className="text-gray-500">Fecha de Solicitud:</span>
              <span className="ml-2 font-medium">
                {new Date(localApplication.applicationDate).toLocaleDateString('es-CO')}
              </span>
            </div>
            <div>
              <span className="text-gray-500">Estado:</span>
              <span className="ml-2">
                <ApplicationStatusBadge status={localApplication.status} />
              </span>
            </div>
          </div>
        </div>

        {/* Evaluación de riesgo (si existe) */}
        {hasRiskEvaluation && localApplication.riskEvaluation && (
          <div className="border-t pt-4">
            <h4 className="font-medium text-gray-900 mb-2">Evaluación de Riesgo</h4>
            <div className="bg-gray-50 rounded-lg p-4 space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-500">Score de Riesgo:</span>
                <span className="font-bold text-lg">{localApplication.riskEvaluation.score}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-500">Nivel de Riesgo:</span>
                <RiskLevelBadge level={localApplication.riskEvaluation.riskLevel} />
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-500">Ratio Deuda/Ingreso:</span>
                <span className="font-medium">{localApplication.riskEvaluation.debtToIncomeRatio.toFixed(2)}%</span>
              </div>
              <div className="pt-2 border-t">
                <p className="text-sm text-gray-500">Razón:</p>
                <p className="text-sm font-medium">{localApplication.riskEvaluation.reason}</p>
              </div>
              {localApplication.riskEvaluation.details && localApplication.riskEvaluation.approved && (
                <div>
                  <p className="text-sm text-gray-500">Detalles:</p>
                  <p className="text-sm">{localApplication.riskEvaluation.details}</p>
                </div>
              )}
            </div>
          </div>
        )}

        {/* Formulario de decisión */}
        {showDecisionForm && isPending && hasRiskEvaluation && (
          <div className="border-t pt-4">
            <h4 className="font-medium text-gray-900 mb-3">Decisión del Analista</h4>
            <div className="space-y-4">
              <div className="flex gap-3">
                <Button
                  variant={decision === 'approve' ? 'success' : 'outline'}
                  onClick={() => setDecision('approve')}
                  className="flex-1"
                >
                  Aprobar
                </Button>
                <Button
                  variant={decision === 'reject' ? 'danger' : 'outline'}
                  onClick={() => setDecision('reject')}
                  className="flex-1"
                >
                  Rechazar
                </Button>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Comentarios (opcional)
                </label>
                <textarea
                  value={comments}
                  onChange={(e) => setComments(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
                  rows={3}
                  placeholder="Agregar comentarios sobre la decisión..."
                />
              </div>
            </div>
          </div>
        )}

        {/* Acciones */}
        <div className="flex justify-end space-x-3 pt-4 border-t">
          <Button variant="outline" onClick={onClose}>
            Cerrar
          </Button>

          {isPending && !hasRiskEvaluation && (
            <Button variant="primary" onClick={handleEvaluateRisk} loading={evaluatingRisk}>
              Evaluar Riesgo
            </Button>
          )}

          {isPending && hasRiskEvaluation && !showDecisionForm && (
            <Button variant="success" onClick={() => setShowDecisionForm(true)}>
              Tomar Decisión
            </Button>
          )}

          {showDecisionForm && (
            <>
              <Button variant="outline" onClick={() => {
                setShowDecisionForm(false);
                setDecision(null);
                setComments('');
              }}>
                Cancelar
              </Button>
              <Button
                variant={decision === 'approve' ? 'success' : 'danger'}
                onClick={handleMakeDecision}
                loading={makingDecision}
                disabled={!decision}
              >
                Confirmar {decision === 'approve' ? 'Aprobación' : 'Rechazo'}
              </Button>
            </>
          )}
        </div>
      </div>
    </Modal>
  );
};

export { ApplicationDetailModal };
