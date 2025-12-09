-- V3__initial_data.sql
-- Initial data for testing and development

-- All test users have password: password
-- BCrypt hash for 'password' with strength 10

-- Insert admin user (password: password)
INSERT INTO users (username, password, email, document_number, enabled)
VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'admin@coopcredit.com', NULL, true);

-- Insert analyst user (password: password)
INSERT INTO users (username, password, email, document_number, enabled)
VALUES ('analyst', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'analyst@coopcredit.com', NULL, true);

-- Insert affiliate user (password: password)
INSERT INTO users (username, password, email, document_number, enabled)
VALUES ('affiliate1', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'affiliate1@email.com', '1017654321', true);

-- Assign roles
INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_ADMIN');
INSERT INTO user_roles (user_id, role) VALUES (2, 'ROLE_ANALYST');
INSERT INTO user_roles (user_id, role) VALUES (3, 'ROLE_AFFILIATE');

-- Insert sample affiliates
INSERT INTO affiliates (document_number, name, salary, affiliation_date, status)
VALUES ('1017654321', 'Juan Carlos Pérez', 5000000.00, '2023-01-15', 'ACTIVE');

INSERT INTO affiliates (document_number, name, salary, affiliation_date, status)
VALUES ('1017654322', 'María García López', 7500000.00, '2022-06-20', 'ACTIVE');

INSERT INTO affiliates (document_number, name, salary, affiliation_date, status)
VALUES ('1017654323', 'Carlos Rodríguez', 3500000.00, '2024-03-10', 'ACTIVE');

INSERT INTO affiliates (document_number, name, salary, affiliation_date, status)
VALUES ('1017654324', 'Ana Martínez', 6000000.00, '2021-11-05', 'INACTIVE');

