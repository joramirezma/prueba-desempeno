-- V2__relations.sql
-- Credit applications and risk evaluations tables

-- Credit applications table
CREATE TABLE credit_applications (
    id BIGSERIAL PRIMARY KEY,
    affiliate_id BIGINT NOT NULL,
    requested_amount DECIMAL(15, 2) NOT NULL,
    term_months INTEGER NOT NULL CHECK (term_months >= 6 AND term_months <= 120),
    proposed_rate DECIMAL(5, 2) NOT NULL,
    application_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (affiliate_id) REFERENCES affiliates(id) ON DELETE CASCADE
);

-- Risk evaluations table
CREATE TABLE risk_evaluations (
    id BIGSERIAL PRIMARY KEY,
    credit_application_id BIGINT NOT NULL UNIQUE,
    score INTEGER NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    debt_to_income_ratio DECIMAL(5, 2),
    reason VARCHAR(500),
    details VARCHAR(1000),
    evaluation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (credit_application_id) REFERENCES credit_applications(id) ON DELETE CASCADE
);

-- Create indexes for credit applications
CREATE INDEX idx_applications_affiliate ON credit_applications(affiliate_id);
CREATE INDEX idx_applications_status ON credit_applications(status);
CREATE INDEX idx_applications_date ON credit_applications(application_date);

-- Create index for risk evaluations
CREATE INDEX idx_evaluations_application ON risk_evaluations(credit_application_id);
CREATE INDEX idx_evaluations_risk_level ON risk_evaluations(risk_level);
