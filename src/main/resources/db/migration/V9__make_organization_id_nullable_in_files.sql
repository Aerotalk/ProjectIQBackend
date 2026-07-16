-- Make organization_id nullable to allow global/system files (e.g., uploaded by Super Admin)
ALTER TABLE files ALTER COLUMN organization_id DROP NOT NULL;
