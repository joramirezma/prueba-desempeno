import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { applicationService, affiliateService } from '../../services';
import { CreateCreditApplicationRequest, ProblemDetail } from '../../types';
import { useAuth } from '../../context';
import { Card, Button, Input, Select, Alert, Loading } from '../../components/ui';
import { AxiosError } from 'axios';

export const CreateApplicationPage: React.FC = () => {
  const { isAdmin, user } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [error, setError] = useState<string | null>(null);

  // Obtener afiliados para el select (solo para admin)
  const { data: affiliates, isLoading: loadingAffiliates } = useQuery({
    queryKey: ['affiliates'],
    queryFn: affiliateService.getAll,
    enabled: isAdmin,
  });

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<CreateCreditApplicationRequest>({
    defaultValues: {
      // Si es afiliado, usar su documento automáticamente
      affiliateDocumentNumber: !isAdmin ? (user?.documentNumber || '') : '',
      requestedAmount: undefined,
      termMonths: 12,
      proposedRate: 1.5,
    },
  });

  const requestedAmount = watch('requestedAmount');
  const termMonths = watch('termMonths');
  const proposedRate = watch('proposedRate');

  // Calcular cuota estimada
  const calculateMonthlyPayment = () => {
    // Validar que todos los valores existan y sean números válidos
    if (!requestedAmount || !termMonths || !proposedRate ||
      isNaN(requestedAmount) || isNaN(termMonths) || isNaN(proposedRate) ||
      requestedAmount <= 0 || termMonths <= 0 || proposedRate <= 0) {
      return 0;
    }

    const monthlyRate = proposedRate / 100;
    const payment =
      (requestedAmount * monthlyRate * Math.pow(1 + monthlyRate, termMonths)) /
      (Math.pow(1 + monthlyRate, termMonths) - 1);

    return isFinite(payment) && !isNaN(payment) ? payment : 0;
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(value);
  };

  const createMutation = useMutation({
    mutationFn: applicationService.create,
    onSuccess: () => {
      // Invalidar cache de solicitudes
      queryClient.invalidateQueries({ queryKey: ['applications'] });
      queryClient.invalidateQueries({ queryKey: ['pending-applications'] });
      queryClient.invalidateQueries({ queryKey: ['myApplications'] });
      // Redirigir según el rol
      if (isAdmin) {
        navigate('/applications');
      } else {
        navigate('/my-applications');
      }
    },
    onError: (err: AxiosError<ProblemDetail>) => {
      if (err.response?.data?.errors) {
        const errorMessages = Object.entries(err.response.data.errors)
          .map(([field, message]) => `${field}: ${message}`)
          .join('\n');
        setError(errorMessages);
      } else {
        setError(err.response?.data?.detail || 'Error al crear la solicitud');
      }
    },
  });

  const onSubmit = (data: CreateCreditApplicationRequest) => {
    setError(null);
    createMutation.mutate(data);
  };

  if (isAdmin && loadingAffiliates) {
    return <Loading text="Cargando datos..." />;
  }

  const affiliateOptions =
    affiliates
      ?.filter((a) => a.status === 'ACTIVE')
      .map((a) => ({
        value: a.documentNumber,
        label: `${a.name} (${a.documentNumber})`,
      })) || [];

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Nueva Solicitud de Crédito</h1>
        <p className="mt-1 text-sm text-gray-500">
          Complete el formulario para solicitar un nuevo crédito
        </p>
      </div>

      {/* Alertas */}
      {error && (
        <Alert type="error" message={error} onClose={() => setError(null)} />
      )}

      <Card>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          {/* Afiliado */}
          {isAdmin ? (
            <Select
              label="Afiliado"
              placeholder="Seleccione un afiliado"
              options={affiliateOptions}
              error={errors.affiliateDocumentNumber?.message}
              {...register('affiliateDocumentNumber', {
                required: 'Debe seleccionar un afiliado',
              })}
            />
          ) : (
            // Para afiliados: mostrar su documento pero no editable
            <div className="space-y-1">
              <label className="block text-sm font-medium text-gray-700">
                Número de Documento
              </label>
              <div className="px-3 py-2 bg-gray-100 border border-gray-300 rounded-md text-gray-700">
                {user?.documentNumber || 'No disponible'}
              </div>
              <input type="hidden" {...register('affiliateDocumentNumber')} />
              {!user?.documentNumber && (
                <p className="text-sm text-red-600">
                  Su cuenta no tiene un número de documento asociado. Por favor, contacte al administrador.
                </p>
              )}
            </div>
          )}

          {/* Monto solicitado */}
          <Input
            label="Monto Solicitado"
            type="number"
            placeholder="Ej: 10000000"
            helperText="Mínimo: $100,000 - Máximo: $500,000,000"
            error={errors.requestedAmount?.message}
            {...register('requestedAmount', {
              required: 'El monto es requerido',
              min: { value: 100000, message: 'Mínimo $100,000' },
              max: { value: 500000000, message: 'Máximo $500,000,000' },
              valueAsNumber: true,
            })}
          />

          {/* Plazo */}
          <Input
            label="Plazo (meses)"
            type="number"
            placeholder="Ej: 24"
            helperText="Entre 6 y 120 meses"
            error={errors.termMonths?.message}
            {...register('termMonths', {
              required: 'El plazo es requerido',
              min: { value: 6, message: 'Mínimo 6 meses' },
              max: { value: 120, message: 'Máximo 120 meses' },
              valueAsNumber: true,
            })}
          />

          {/* Tasa propuesta */}
          <Input
            label="Tasa de Interés Mensual (%)"
            type="number"
            step="0.1"
            placeholder="Ej: 1.5"
            helperText="Entre 0.1% y 50%"
            error={errors.proposedRate?.message}
            {...register('proposedRate', {
              required: 'La tasa es requerida',
              min: { value: 0.1, message: 'Mínimo 0.1%' },
              max: { value: 50, message: 'Máximo 50%' },
              valueAsNumber: true,
            })}
          />

          {/* Resumen del crédito */}
          {requestedAmount && termMonths && proposedRate && (
            <div className="bg-primary-50 rounded-lg p-4 space-y-3">
              <h4 className="font-medium text-primary-900">Resumen del Crédito</h4>
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="text-primary-600">Monto:</span>
                  <span className="ml-2 font-semibold">{formatCurrency(requestedAmount)}</span>
                </div>
                <div>
                  <span className="text-primary-600">Plazo:</span>
                  <span className="ml-2 font-semibold">{termMonths} meses</span>
                </div>
                <div>
                  <span className="text-primary-600">Tasa Mensual:</span>
                  <span className="ml-2 font-semibold">{proposedRate}%</span>
                </div>
                <div>
                  <span className="text-primary-600">Cuota Estimada:</span>
                  <span className="ml-2 font-bold text-primary-700">
                    {formatCurrency(calculateMonthlyPayment())}
                  </span>
                </div>
              </div>
              <div className="pt-2 border-t border-primary-200">
                <span className="text-primary-600 text-sm">Total a Pagar (aprox.):</span>
                <span className="ml-2 font-bold text-lg text-primary-700">
                  {formatCurrency(calculateMonthlyPayment() * termMonths)}
                </span>
              </div>
            </div>
          )}

          {/* Información adicional */}
          <div className="bg-gray-50 rounded-lg p-4 text-sm text-gray-600">
            <h4 className="font-medium text-gray-900 mb-2">Requisitos para Aprobación</h4>
            <ul className="list-disc list-inside space-y-1">
              <li>El afiliado debe estar en estado ACTIVO</li>
              <li>Mínimo 6 meses de antigüedad como afiliado</li>
              <li>El monto no puede exceder 12 veces el salario</li>
              <li>La cuota no puede superar el 40% del salario</li>
              <li>Score de riesgo crediticio favorable</li>
            </ul>
          </div>

          {/* Botones */}
          <div className="flex justify-end space-x-3 pt-4">
            <Button
              variant="outline"
              type="button"
              onClick={() => navigate(-1)}
            >
              Cancelar
            </Button>
            <Button type="submit" loading={createMutation.isPending}>
              Enviar Solicitud
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};
