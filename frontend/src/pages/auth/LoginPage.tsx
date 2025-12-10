import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context';
import { LoginRequest, ProblemDetail } from '../../types';
import { Button, Input, Alert, Card } from '../../components/ui';
import { AxiosError } from 'axios';

export const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const from = (location.state as { from?: { pathname: string } })?.from?.pathname || '/dashboard';

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginRequest>();

  const onSubmit = async (data: LoginRequest) => {
    setError(null);
    setLoading(true);

    try {
      await login(data);
      navigate(from, { replace: true });
    } catch (err) {
      const axiosError = err as AxiosError<ProblemDetail>;
      if (axiosError.response?.data?.detail) {
        setError(axiosError.response.data.detail);
      } else if (axiosError.response?.status === 401) {
        setError('Credenciales inválidas. Por favor verifica tu usuario y contraseña.');
      } else {
        setError('Error al iniciar sesión. Intenta nuevamente.');
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
            Iniciar Sesión
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
              placeholder="Ingresa tu usuario"
              error={errors.username?.message}
              {...register('username', {
                required: 'El usuario es requerido',
              })}
            />

            <Input
              label="Contraseña"
              type="password"
              placeholder="Ingresa tu contraseña"
              error={errors.password?.message}
              {...register('password', {
                required: 'La contraseña es requerida',
              })}
            />

            <Button type="submit" fullWidth loading={loading}>
              Iniciar Sesión
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              ¿No tienes cuenta?{' '}
              <Link to="/register" className="text-primary-600 hover:text-primary-700 font-medium">
                Regístrate aquí
              </Link>
            </p>
          </div>

          {/* Usuarios de prueba */}
          <div className="mt-6 pt-6 border-t border-gray-200">
            <p className="text-xs text-gray-500 text-center mb-3">Usuarios de prueba:</p>
            <div className="grid grid-cols-3 gap-2 text-xs">
              <div className="bg-gray-50 p-2 rounded text-center">
                <p className="font-medium text-gray-700">Admin</p>
                <p className="text-gray-500">admin / password</p>
              </div>
              <div className="bg-gray-50 p-2 rounded text-center">
                <p className="font-medium text-gray-700">Analista</p>
                <p className="text-gray-500">analyst / password</p>
              </div>
              <div className="bg-gray-50 p-2 rounded text-center">
                <p className="font-medium text-gray-700">Afiliado</p>
                <p className="text-gray-500">affiliate1 / password</p>
              </div>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};
