import React from 'react';

interface AlertProps {
  type: 'success' | 'error' | 'warning' | 'info';
  title?: string;
  message: string;
  onClose?: () => void;
}

const alertStyles = {
  success: {
    container: 'bg-success-50 border-success-500 text-success-700',
    icon: '✓',
    iconBg: 'bg-success-100',
  },
  error: {
    container: 'bg-danger-50 border-danger-500 text-danger-700',
    icon: '✕',
    iconBg: 'bg-danger-100',
  },
  warning: {
    container: 'bg-warning-50 border-warning-500 text-warning-700',
    icon: '⚠',
    iconBg: 'bg-warning-100',
  },
  info: {
    container: 'bg-primary-50 border-primary-500 text-primary-700',
    icon: 'ℹ',
    iconBg: 'bg-primary-100',
  },
};

export const Alert: React.FC<AlertProps> = ({ type, title, message, onClose }) => {
  const styles = alertStyles[type];

  return (
    <div className={`rounded-lg border-l-4 p-4 ${styles.container}`}>
      <div className="flex items-start">
        <div className={`flex-shrink-0 w-8 h-8 rounded-full ${styles.iconBg} flex items-center justify-center mr-3`}>
          <span className="text-sm font-bold">{styles.icon}</span>
        </div>
        <div className="flex-1">
          {title && (
            <h3 className="text-sm font-medium mb-1">{title}</h3>
          )}
          <p className="text-sm">{message}</p>
        </div>
        {onClose && (
          <button
            onClick={onClose}
            className="flex-shrink-0 ml-4 text-gray-400 hover:text-gray-600 transition-colors"
          >
            <span className="sr-only">Cerrar</span>
            <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path
                fillRule="evenodd"
                d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                clipRule="evenodd"
              />
            </svg>
          </button>
        )}
      </div>
    </div>
  );
};
