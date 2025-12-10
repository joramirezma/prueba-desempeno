-- V4__allow_null_approved.sql
-- Allow NULL values in approved column for two-step evaluation process
-- Step 1: Risk evaluation (approved = NULL)
-- Step 2: Analyst decision (approved = TRUE/FALSE)

ALTER TABLE risk_evaluations ALTER COLUMN approved DROP NOT NULL;
