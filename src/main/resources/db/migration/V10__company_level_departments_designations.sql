-- V10__company_level_departments_designations.sql
-- Adds company_id to designations, migrates existing dept/desig records to each org''s first company

-- 1. Add company_id column to designations (nullable first, for safe migration)
ALTER TABLE designations
    ADD COLUMN IF NOT EXISTS company_id UUID REFERENCES companies(company_id);

-- 2. Migrate existing designations: assign each to the first (oldest) company in their org
UPDATE designations d
SET company_id = (
    SELECT c.company_id
    FROM companies c
    WHERE c.organization_id = d.organization_id
      AND c.deleted_at IS NULL
    ORDER BY c.created_at ASC
    LIMIT 1
)
WHERE d.company_id IS NULL
  AND d.deleted_at IS NULL;

-- 3. Migrate existing departments that still have no company_id
UPDATE departments d
SET company_id = (
    SELECT c.company_id
    FROM companies c
    WHERE c.organization_id = d.organization_id
      AND c.deleted_at IS NULL
    ORDER BY c.created_at ASC
    LIMIT 1
)
WHERE d.company_id IS NULL
  AND d.deleted_at IS NULL;

-- NOTE: Records whose org has no companies yet will remain NULL until
-- a company is created and records are manually reassigned.
