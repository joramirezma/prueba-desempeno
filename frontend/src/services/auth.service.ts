import api from './api';
import { LoginRequest, RegisterRequest, AuthResponse } from '../types';

const AUTH_BASE = '/auth';

/**
 * Helper para parsear JWT y extraer claims
 */
const parseJwt = (token: string): any => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      window.atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
};

export const authService = {
  /**
   * Iniciar sesión
   */
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>(`${AUTH_BASE}/login`, credentials);
    return response.data;
  },

  /**
   * Registrar nuevo usuario
   */
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>(`${AUTH_BASE}/register`, data);
    return response.data;
  },

  /**
   * Guardar token y datos de usuario en localStorage
   */
  saveAuth: (authResponse: AuthResponse, documentNumber?: string): void => {
    // Intentar extraer documentNumber del token JWT si no se proporciona
    let docNumber = documentNumber;
    if (!docNumber) {
      const decoded = parseJwt(authResponse.token);
      docNumber = decoded?.document;
    }

    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('user', JSON.stringify({
      username: authResponse.username,
      roles: authResponse.roles,
      token: authResponse.token,
      documentNumber: docNumber,
    }));
  },

  /**
   * Limpiar datos de autenticación
   */
  clearAuth: (): void => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  /**
   * Obtener usuario del localStorage
   */
  getStoredUser: () => {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        return JSON.parse(userStr);
      } catch {
        return null;
      }
    }
    return null;
  },

  /**
   * Verificar si hay un token válido almacenado
   */
  isAuthenticated: (): boolean => {
    return !!localStorage.getItem('token');
  },
};
