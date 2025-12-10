import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context';
import { RegisterRequest, ProblemDetail } from '../../types';
import { Button, Input, Alert, Card } from '../../components/ui';
import { AxiosError } from 'axios';

interface RegisterFormData extends RegisterRequest {
  confirmPassword: string;
}

export const RegisterPage: React.FC = () => {
  const { register: registerUser } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<RegisterFormData>();

  const password = watch('password');

  const onSubmit = async (data: RegisterFormData) => {
    setError(null);
    setLoading(true);

    try {
      const { confirmPassword, ...registerData } = data;
      // Por defecto, los nuevos usuarios se registran como AFFILIATE
      await registerUser({
        ...registerData,
        roles: ['ROLE_AFFILIATE'],
      });
      navigate('/dashboard', { replace: true });
    } catch (err) {
      const axiosError = err as AxiosError<ProblemDetail>;
      if (axiosError.response?.data?.detail) {
        setError(axiosError.response.data.detail);
      } else if (axiosError.response?.data?.errors) {
        const errorMessages = Object.values(axiosError.response.data.errors).join(', ');
        setError(errorMessages);
      } else {
        setError('Error al registrarse. Intenta nuevamente.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-500 to-primary-700 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="w-full max-w-md">
        {/* Logo y título */}
        <div className="text-center mb-8">
          <div className="flex justify-center">
            <div className="bg-white p-4 rounded-full shadow-lg">
              <svg className="h-12 w-12 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
              </svg>
            </div>
          </div>
          <h1 className="mt-4 text-3xl font-bold text-white">CoopCredit</h1>
          <p className="mt-2 text-primary-100">Sistema de Solicitudes de Crédito</p>
        </div>

        <Card>
          <h2 className="text-2xl font-bold text-gray-900 text-center mb-6">
            Crear Cuenta
          </h2>

          {error && (
            <div className="mb-4">
              <Alert type="error" message={error} onClose={() => setError(null)} />
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input
              label="Usuario"
              type="text"
              placeholder="Elige un nombre de usuario"
              error={errors.username?.message}
              {...register('username', {
                required: 'El usuario es requerido',
                minLength: { value: 3, message: 'Mínimo 3 caracteres' },
                maxLength: { value: 50, message: 'Máximo 50 caracteres' },
              })}
            />

            <Input
              label="Correo Electrónico"
              type="email"
              placeholder="correo@ejemplo.com"
              error={errors.email?.message}
              {...register('email', {
                required: 'El correo es requerido',
                pattern: {
                  value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                  message: 'Correo electrónico inválido',
                },
              })}
            />

            <Input
              label="Nombre Completo"
              type="text"
              placeholder="Tu nombre completo"
              error={errors.name?.message}
              {...register('name', {
                required: 'El nombre es requerido',
                maxLength: { value: 100, message: 'Máximo 100 caracteres' },
              })}
            />

            <Input
              label="Número de Documento"
              type="text"
              placeholder="Tu número de documento"
              error={errors.documentNumber?.message}
              {...register('documentNumber', {
                required: 'El número de documento es requerido',
                maxLength: { value: 20, message: 'Máximo 20 caracteres' },
              })}
            />

            <Input
              label="Salario Mensual"
              type="number"
              placeholder="Tu salario mensual"
              error={errors.salary?.message}
              {...register('salary', {
                required: 'El salario es requerido',
                min: { value: 1, message: 'El salario debe ser mayor a 0' },
                valueAsNumber: true,
              })}
            />

            <Input
              label="Contraseña"
              type="password"
              placeholder="Crea una contraseña segura"
              error={errors.password?.message}
              {...register('password', {
                required: 'La contraseña es requerida',
                minLength: { value: 6, message: 'Mínimo 6 caracteres' },
                maxLength: { value: 100, message: 'Máximo 100 caracteres' },
              })}
            />

            <Input
              label="Confirmar Contraseña"
              type="password"
              placeholder="Repite tu contraseña"
              error={errors.confirmPassword?.message}
              {...register('confirmPassword', {
                required: 'Debes confirmar la contraseña',
                validate: (value) =>
                  value === password || 'Las contraseñas no coinciden',
              })}
            />

            <Button type="submit" fullWidth loading={loading}>
              Crear Cuenta
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              ¿Ya tienes cuenta?{' '}
              <Link to="/login" className="text-primary-600 hover:text-primary-700 font-medium">
                Inicia sesión
              </Link>
            </p>
          </div>
        </Card>
      </div>
    </div>
  );
};
