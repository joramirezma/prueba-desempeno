import api from './api';
import {
  CreditApplicationResponse,
  CreateCreditApplicationRequest,
} from '../types';

const APPLICATIONS_BASE = '/applications';

export const applicationService = {
  /**
   * Crear una nueva solicitud de crédito
   */
  create: async (data: CreateCreditApplicationRequest): Promise<CreditApplicationResponse> => {
    const response = await api.post<CreditApplicationResponse>(APPLICATIONS_BASE, data);
    return response.data;
  },

  /**
   * Obtener todas las solicitudes (solo ADMIN)
   */
  getAll: async (): Promise<CreditApplicationResponse[]> => {
    const response = await api.get<CreditApplicationResponse[]>(APPLICATIONS_BASE);
    return response.data;
  },

  /**
   * Obtener una solicitud por ID
   */
  getById: async (id: number): Promise<CreditApplicationResponse> => {
    const response = await api.get<CreditApplicationResponse>(`${APPLICATIONS_BASE}/${id}`);
    return response.data;
  },

  /**
   * Obtener solicitudes pendientes (ANALYST, ADMIN)
   */
  getPending: async (): Promise<CreditApplicationResponse[]> => {
    const response = await api.get<CreditApplicationResponse[]>(`${APPLICATIONS_BASE}/pending`);
    return response.data;
  },

  /**
   * Obtener solicitudes por documento de afiliado
   */
  getByAffiliate: async (documentNumber: string): Promise<CreditApplicationResponse[]> => {
    const response = await api.get<CreditApplicationResponse[]>(
      `${APPLICATIONS_BASE}/affiliate/${documentNumber}`
    );
    return response.data;
  },

  /**
   * Evaluar el riesgo de una solicitud automáticamente (ANALYST, ADMIN)
   */
  evaluateRisk: async (id: number): Promise<CreditApplicationResponse> => {
    const response = await api.post<CreditApplicationResponse>(`${APPLICATIONS_BASE}/${id}/evaluate-risk`);
    return response.data;
  },

  /**
   * Tomar decisión manual sobre una solicitud (ANALYST, ADMIN)
   */
  makeDecision: async (id: number, approved: boolean, comments?: string): Promise<CreditApplicationResponse> => {
    const response = await api.post<CreditApplicationResponse>(
      `${APPLICATIONS_BASE}/${id}/decide`,
      { approved, comments }
    );
    return response.data;
  },
};
