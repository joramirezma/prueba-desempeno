import api from './api';
import {
  AffiliateResponse,
  CreateAffiliateRequest,
  UpdateAffiliateRequest,
} from '../types';

const AFFILIATES_BASE = '/affiliates';

export const affiliateService = {
  /**
   * Crear un nuevo afiliado
   */
  create: async (data: CreateAffiliateRequest): Promise<AffiliateResponse> => {
    const response = await api.post<AffiliateResponse>(AFFILIATES_BASE, data);
    return response.data;
  },

  /**
   * Obtener todos los afiliados
   */
  getAll: async (): Promise<AffiliateResponse[]> => {
    const response = await api.get<AffiliateResponse[]>(AFFILIATES_BASE);
    return response.data;
  },

  /**
   * Obtener un afiliado por su documento
   */
  getByDocument: async (documentNumber: string): Promise<AffiliateResponse> => {
    const response = await api.get<AffiliateResponse>(`${AFFILIATES_BASE}/${documentNumber}`);
    return response.data;
  },

  /**
   * Actualizar un afiliado
   */
  update: async (documentNumber: string, data: UpdateAffiliateRequest): Promise<AffiliateResponse> => {
    const response = await api.put<AffiliateResponse>(`${AFFILIATES_BASE}/${documentNumber}`, data);
    return response.data;
  },

  /**
   * Activar un afiliado
   */
  activate: async (documentNumber: string): Promise<AffiliateResponse> => {
    const response = await api.post<AffiliateResponse>(`${AFFILIATES_BASE}/${documentNumber}/activate`);
    return response.data;
  },

  /**
   * Desactivar un afiliado
   */
  deactivate: async (documentNumber: string): Promise<AffiliateResponse> => {
    const response = await api.post<AffiliateResponse>(`${AFFILIATES_BASE}/${documentNumber}/deactivate`);
    return response.data;
  },
};
