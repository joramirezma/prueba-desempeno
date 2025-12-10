-- V7: Add sample credit applications and risk evaluations
-- This migration adds test data for credit applications with various states

-- Insert credit applications with different statuses
INSERT INTO credit_applications (affiliate_id, requested_amount, term_months, proposed_rate, application_date, status) VALUES
-- PENDING applications (not yet evaluated)
(1, 15000000, 36, 1.2, '2025-12-01'::timestamp, 'PENDING'),
(2, 8000000, 24, 1.2, '2025-12-02'::timestamp, 'PENDING'),
(3, 20000000, 48, 1.2, '2025-12-03'::timestamp, 'PENDING'),
(6, 5000000, 12, 1.2, '2025-12-05'::timestamp, 'PENDING'),
(7, 4500000, 18, 1.2, '2025-12-06'::timestamp, 'PENDING'),

-- APPROVED applications (with positive risk evaluation)
(4, 12000000, 36, 1.2, '2025-11-15'::timestamp, 'APPROVED'),
(5, 18000000, 48, 1.2, '2025-11-20'::timestamp, 'APPROVED'),
(8, 6000000, 24, 1.2, '2025-11-25'::timestamp, 'APPROVED'),

-- REJECTED applications (with negative risk evaluation)
(9, 10000000, 36, 1.2, '2025-11-10'::timestamp, 'REJECTED'),
(10, 7500000, 24, 1.2, '2025-11-12'::timestamp, 'REJECTED'),
(11, 3000000, 12, 1.2, '2025-11-18'::timestamp, 'REJECTED'),
(12, 3500000, 18, 1.2, '2025-11-22'::timestamp, 'REJECTED');

-- Insert risk evaluations for APPROVED applications
INSERT INTO risk_evaluations (credit_application_id, score, risk_level, debt_to_income_ratio, reason, details, evaluation_date, approved) VALUES
(6, 750, 'LOW', 25.50, 'Aprobado - Excelente historial crediticio', 'Cliente con ingresos estables y buen puntaje crediticio. Capacidad de pago verificada.', '2025-11-15'::timestamp, true),
(7, 720, 'LOW', 28.75, 'Aprobado - Buen perfil crediticio', 'Cliente demuestra capacidad financiera adecuada. Relación ingreso-deuda favorable.', '2025-11-20'::timestamp, true),
(8, 680, 'MEDIUM', 32.10, 'Aprobado - Perfil aceptable', 'Aprobado con monitoreo. Cliente cumple requisitos mínimos de aprobación.', '2025-11-25'::timestamp, true);

-- Insert risk evaluations for REJECTED applications
INSERT INTO risk_evaluations (credit_application_id, score, risk_level, debt_to_income_ratio, reason, details, evaluation_date, approved) VALUES
(9, 520, 'HIGH', 48.90, 'Rechazado - Alto riesgo crediticio', 'Relación deuda-ingreso excede límites permitidos. Score crediticio bajo el mínimo requerido.', '2025-11-10'::timestamp, false),
(10, 580, 'HIGH', 45.30, 'Rechazado - Capacidad de pago insuficiente', 'Ingresos no son suficientes para cubrir las obligaciones solicitadas.', '2025-11-12'::timestamp, false),
(11, 490, 'HIGH', 52.75, 'Rechazado - Alto nivel de endeudamiento', 'Cliente presenta alto nivel de endeudamiento actual. No cumple políticas de crédito.', '2025-11-18'::timestamp, false),
(12, 550, 'HIGH', 46.20, 'Rechazado - Historial crediticio negativo', 'Historial de pagos muestra morosidad reciente. Riesgo elevado de impago.', '2025-11-22'::timestamp, false);
