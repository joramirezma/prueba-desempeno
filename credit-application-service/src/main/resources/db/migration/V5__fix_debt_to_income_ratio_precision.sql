-- V5: Fix debt_to_income_ratio precision to handle higher percentages
-- Change from DECIMAL(5,2) to DECIMAL(10,2) to support values up to 99,999,999.99%

ALTER TABLE risk_evaluations
ALTER COLUMN debt_to_income_ratio TYPE DECIMAL(10, 2);
