import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { AuthContextType, User, LoginRequest, RegisterRequest, Role } from '../types';
import { authService } from '../services';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  // Cargar usuario del localStorage al iniciar
  useEffect(() => {
    const storedUser = authService.getStoredUser();
    if (storedUser) {
      setUser(storedUser);
    }
    setLoading(false);
  }, []);

  const login = useCallback(async (credentials: LoginRequest) => {
    const response = await authService.login(credentials);
    authService.saveAuth(response);

    // Obtener el usuario guardado que ahora incluye el documentNumber del token
    const savedUser = authService.getStoredUser();
    if (savedUser) {
      setUser(savedUser);
    }
  }, []);

  const register = useCallback(async (data: RegisterRequest) => {
    const response = await authService.register(data);
    authService.saveAuth(response, data.documentNumber);
    setUser({
      username: response.username,
      roles: response.roles,
      token: response.token,
      documentNumber: data.documentNumber,
    });
  }, []);

  const logout = useCallback(() => {
    authService.clearAuth();
    setUser(null);
  }, []);

  const hasRole = useCallback((role: Role): boolean => {
    return user?.roles?.includes(role) ?? false;
  }, [user]);

  const value: AuthContextType = {
    user,
    login,
    register,
    logout,
    isAuthenticated: !!user,
    isAdmin: hasRole('ROLE_ADMIN'),
    isAnalyst: hasRole('ROLE_ANALYST'),
    isAffiliate: hasRole('ROLE_AFFILIATE'),
    hasRole,
    loading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
