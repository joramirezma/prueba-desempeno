import React from 'react';
import { ApplicationStatus, AffiliateStatus, RiskLevel } from '../../types';

interface BadgeProps {
  children: React.ReactNode;
  variant?: 'default' | 'success' | 'warning' | 'danger' | 'info';
}

const variantClasses = {
  default: 'bg-gray-100 text-gray-800',
  success: 'bg-success-100 text-success-700',
  warning: 'bg-warning-100 text-warning-700',
  danger: 'bg-danger-100 text-danger-700',
  info: 'bg-primary-100 text-primary-700',
};

export const Badge: React.FC<BadgeProps> = ({ children, variant = 'default' }) => {
  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${variantClasses[variant]}`}
    >
      {children}
    </span>
  );
};

// Badge específico para estados de solicitud
export const ApplicationStatusBadge: React.FC<{ status: ApplicationStatus }> = ({ status }) => {
  const config: Record<ApplicationStatus, { variant: BadgeProps['variant']; label: string }> = {
    PENDING: { variant: 'warning', label: 'Pendiente' },
    APPROVED: { variant: 'success', label: 'Aprobada' },
    REJECTED: { variant: 'danger', label: 'Rechazada' },
  };

  const { variant, label } = config[status];
  return <Badge variant={variant}>{label}</Badge>;
};

// Badge específico para estados de afiliado
export const AffiliateStatusBadge: React.FC<{ status: AffiliateStatus }> = ({ status }) => {
  const config: Record<AffiliateStatus, { variant: BadgeProps['variant']; label: string }> = {
    ACTIVE: { variant: 'success', label: 'Activo' },
    INACTIVE: { variant: 'danger', label: 'Inactivo' },
  };

  const { variant, label } = config[status];
  return <Badge variant={variant}>{label}</Badge>;
};

// Badge específico para nivel de riesgo
export const RiskLevelBadge: React.FC<{ level: RiskLevel }> = ({ level }) => {
  const config: Record<RiskLevel, { variant: BadgeProps['variant']; label: string }> = {
    LOW: { variant: 'success', label: 'Bajo' },
    MEDIUM: { variant: 'warning', label: 'Medio' },
    HIGH: { variant: 'danger', label: 'Alto' },
  };

  const { variant, label } = config[level];
  return <Badge variant={variant}>{label}</Badge>;
};
