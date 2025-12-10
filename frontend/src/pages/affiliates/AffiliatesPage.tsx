import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { affiliateService } from '../../services';
import { CreateAffiliateRequest, UpdateAffiliateRequest, AffiliateResponse, ProblemDetail } from '../../types';
import { useAuth } from '../../context';
import { Card, Button, Input, Loading, Alert, Modal, AffiliateStatusBadge } from '../../components/ui';
import { AxiosError } from 'axios';

export const AffiliatesPage: React.FC = () => {
  const { isAdmin } = useAuth();
  const queryClient = useQueryClient();
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [editingAffiliate, setEditingAffiliate] = useState<AffiliateResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Query para obtener todos los afiliados
  const { data: affiliates, isLoading, error: queryError } = useQuery({
    queryKey: ['affiliates'],
    queryFn: affiliateService.getAll,
  });

  // Mutation para crear afiliado
  const createMutation = useMutation({
    mutationFn: affiliateService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['affiliates'] });
      setShowCreateModal(false);
      setSuccess('Afiliado creado exitosamente');
    },
    onError: (err: AxiosError<ProblemDetail>) => {
      setError(err.response?.data?.detail || 'Error al crear afiliado');
    },
  });

  // Mutation para actualizar afiliado
  const updateMutation = useMutation({
    mutationFn: ({ documentNumber, data }: { documentNumber: string; data: UpdateAffiliateRequest }) =>
      affiliateService.update(documentNumber, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['affiliates'] });
      setEditingAffiliate(null);
      setSuccess('Afiliado actualizado exitosamente');
    },
    onError: (err: AxiosError<ProblemDetail>) => {
      setError(err.response?.data?.detail || 'Error al actualizar afiliado');
    },
  });

  // Mutation para activar/desactivar
  const toggleStatusMutation = useMutation({
    mutationFn: ({ documentNumber, activate }: { documentNumber: string; activate: boolean }) =>
      activate ? affiliateService.activate(documentNumber) : affiliateService.deactivate(documentNumber),
    onSuccess: (_, { activate }) => {
      queryClient.invalidateQueries({ queryKey: ['affiliates'] });
      setSuccess(`Afiliado ${activate ? 'activado' : 'desactivado'} exitosamente`);
    },
    onError: (err: AxiosError<ProblemDetail>) => {
      setError(err.response?.data?.detail || 'Error al cambiar estado del afiliado');
    },
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

  if (isLoading) {
    return <Loading text="Cargando afiliados..." />;
  }

  if (queryError) {
    return (
      <Alert
        type="error"
        title="Error"
        message="No se pudieron cargar los afiliados. Por favor intenta de nuevo."
      />
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Gestión de Afiliados</h1>
          <p className="mt-1 text-sm text-gray-500">
            {affiliates?.length || 0} afiliados registrados
          </p>
        </div>
        {isAdmin && (
          <Button onClick={() => setShowCreateModal(true)} className="mt-4 sm:mt-0">
            + Nuevo Afiliado
          </Button>
        )}
      </div>

      {/* Alertas */}
      {error && (
        <Alert type="error" message={error} onClose={() => setError(null)} />
      )}
      {success && (
        <Alert type="success" message={success} onClose={() => setSuccess(null)} />
      )}

      {/* Tabla de afiliados */}
      <Card>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Documento
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Nombre
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Salario
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Fecha Afiliación
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Antigüedad
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Estado
                </th>
                {isAdmin && (
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Acciones
                  </th>
                )}
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {affiliates?.map((affiliate) => (
                <tr key={affiliate.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {affiliate.documentNumber}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {affiliate.name}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {formatCurrency(affiliate.salary)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {formatDate(affiliate.affiliationDate)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {affiliate.monthsOfAffiliation} meses
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <AffiliateStatusBadge status={affiliate.status} />
                  </td>
                  {isAdmin && (
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setEditingAffiliate(affiliate)}
                      >
                        Editar
                      </Button>
                      <Button
                        variant={affiliate.status === 'ACTIVE' ? 'warning' : 'success'}
                        size="sm"
                        loading={toggleStatusMutation.isPending}
                        onClick={() =>
                          toggleStatusMutation.mutate({
                            documentNumber: affiliate.documentNumber,
                            activate: affiliate.status !== 'ACTIVE',
                          })
                        }
                      >
                        {affiliate.status === 'ACTIVE' ? 'Desactivar' : 'Activar'}
                      </Button>
                    </td>
                  )}
                </tr>
              ))}
              {(!affiliates || affiliates.length === 0) && (
                <tr>
                  <td colSpan={isAdmin ? 7 : 6} className="px-6 py-12 text-center text-gray-500">
                    No hay afiliados registrados
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </Card>

      {/* Modal crear afiliado */}
      <CreateAffiliateModal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        onSubmit={(data) => createMutation.mutate(data)}
        loading={createMutation.isPending}
      />

      {/* Modal editar afiliado */}
      {editingAffiliate && (
        <EditAffiliateModal
          isOpen={true}
          onClose={() => setEditingAffiliate(null)}
          affiliate={editingAffiliate}
          onSubmit={(data) =>
            updateMutation.mutate({
              documentNumber: editingAffiliate.documentNumber,
              data,
            })
          }
          loading={updateMutation.isPending}
        />
      )}
    </div>
  );
};

// Modal para crear afiliado
const CreateAffiliateModal: React.FC<{
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: CreateAffiliateRequest) => void;
  loading: boolean;
}> = ({ isOpen, onClose, onSubmit, loading }) => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CreateAffiliateRequest>();

  const handleClose = () => {
    reset();
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Nuevo Afiliado" size="md">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          label="Número de Documento"
          error={errors.documentNumber?.message}
          {...register('documentNumber', {
            required: 'El documento es requerido',
            minLength: { value: 5, message: 'Mínimo 5 caracteres' },
            maxLength: { value: 20, message: 'Máximo 20 caracteres' },
          })}
        />

        <Input
          label="Nombre Completo"
          error={errors.name?.message}
          {...register('name', {
            required: 'El nombre es requerido',
            minLength: { value: 2, message: 'Mínimo 2 caracteres' },
            maxLength: { value: 100, message: 'Máximo 100 caracteres' },
          })}
        />

        <Input
          label="Salario"
          type="number"
          error={errors.salary?.message}
          {...register('salary', {
            required: 'El salario es requerido',
            min: { value: 1, message: 'El salario debe ser mayor a 0' },
            valueAsNumber: true,
          })}
        />

        <Input
          label="Fecha de Afiliación"
          type="date"
          error={errors.affiliationDate?.message}
          {...register('affiliationDate', {
            required: 'La fecha de afiliación es requerida',
          })}
        />

        <div className="flex justify-end space-x-3 pt-4">
          <Button variant="outline" type="button" onClick={handleClose}>
            Cancelar
          </Button>
          <Button type="submit" loading={loading}>
            Crear Afiliado
          </Button>
        </div>
      </form>
    </Modal>
  );
};

// Modal para editar afiliado
const EditAffiliateModal: React.FC<{
  isOpen: boolean;
  onClose: () => void;
  affiliate: AffiliateResponse;
  onSubmit: (data: UpdateAffiliateRequest) => void;
  loading: boolean;
}> = ({ isOpen, onClose, affiliate, onSubmit, loading }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<UpdateAffiliateRequest>({
    defaultValues: {
      name: affiliate.name,
      salary: affiliate.salary,
    },
  });

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Editar Afiliado" size="md">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div className="bg-gray-50 p-3 rounded-lg mb-4">
          <p className="text-sm text-gray-500">Documento</p>
          <p className="font-medium">{affiliate.documentNumber}</p>
        </div>

        <Input
          label="Nombre Completo"
          error={errors.name?.message}
          {...register('name', {
            minLength: { value: 2, message: 'Mínimo 2 caracteres' },
            maxLength: { value: 100, message: 'Máximo 100 caracteres' },
          })}
        />

        <Input
          label="Salario"
          type="number"
          error={errors.salary?.message}
          {...register('salary', {
            min: { value: 1, message: 'El salario debe ser mayor a 0' },
            valueAsNumber: true,
          })}
        />

        <div className="flex justify-end space-x-3 pt-4">
          <Button variant="outline" type="button" onClick={onClose}>
            Cancelar
          </Button>
          <Button type="submit" loading={loading}>
            Guardar Cambios
          </Button>
        </div>
      </form>
    </Modal>
  );
};
