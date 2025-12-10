// ============================================
// TIPOS GENERADOS DESDE LA API DE COOPCREDIT
// Basados en los DTOs del backend
// ============================================

// ============================================
// ENUMS
// ============================================

export type Role = 'ROLE_AFFILIATE' | 'ROLE_ANALYST' | 'ROLE_ADMIN';

export type AffiliateStatus = 'ACTIVE' | 'INACTIVE';

export type ApplicationStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH';

// ============================================
// AUTH DTOs
// ============================================

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  documentNumber?: string;
  name?: string;
  salary?: number;
  roles?: Role[];
}

export interface AuthResponse {
  token: string;
  type: string;
  username: string;
  roles: Role[];
}

// ============================================
// AFFILIATE DTOs
// ============================================

export interface CreateAffiliateRequest {
  documentNumber: string;
  name: string;
  salary: number;
  affiliationDate: string; // ISO date format: yyyy-MM-dd
}

export interface UpdateAffiliateRequest {
  name?: string;
  salary?: number;
}

export interface AffiliateResponse {
  id: number;
  documentNumber: string;
  name: string;
  salary: number;
  affiliationDate: string;
  status: AffiliateStatus;
  monthsOfAffiliation: number;
}

// ============================================
// CREDIT APPLICATION DTOs
// ============================================

export interface CreateCreditApplicationRequest {
  affiliateDocumentNumber: string;
  requestedAmount: number;
  termMonths: number;
  proposedRate: number;
}

export interface CreditApplicationResponse {
  id: number;
  affiliateDocumentNumber: string;
  affiliateName: string;
  requestedAmount: number;
  termMonths: number;
  proposedRate: number;
  estimatedMonthlyPayment: number;
  applicationDate: string;
  status: ApplicationStatus;
  riskEvaluation?: RiskEvaluationResponse;
}

export interface RiskEvaluationResponse {
  id: number;
  score: number;
  riskLevel: RiskLevel;
  debtToIncomeRatio: number;
  reason: string;
  details: string;
  evaluationDate: string;
  approved: boolean;
}

// ============================================
// ERROR RESPONSE (RFC 7807)
// ============================================

export interface ProblemDetail {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  timestamp?: string;
  traceId?: string;
  errors?: Record<string, string>;
}

// ============================================
// USER/AUTH CONTEXT TYPES
// ============================================

export interface User {
  username: string;
  roles: Role[];
  token: string;
  documentNumber?: string;
}

export interface AuthContextType {
  user: User | null;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  isAdmin: boolean;
  isAnalyst: boolean;
  isAffiliate: boolean;
  hasRole: (role: Role) => boolean;
  loading: boolean;
}

// ============================================
// API RESPONSE WRAPPER
// ============================================

export interface ApiError {
  message: string;
  status: number;
  details?: ProblemDetail;
}
