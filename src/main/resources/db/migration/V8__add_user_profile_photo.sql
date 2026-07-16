-- Add profile photo reference to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS profile_photo_id UUID;

-- Add foreign key constraint to files table
ALTER TABLE users ADD CONSTRAINT fk_users_profile_photo
    FOREIGN KEY (profile_photo_id) REFERENCES files(file_id)
    ON DELETE SET NULL;
