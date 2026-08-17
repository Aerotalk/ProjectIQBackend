-- Force seed a fallback Super Admin account
DO $$ 
DECLARE 
    v_role UUID; 
    v_user UUID := gen_random_uuid(); 
BEGIN 
    -- Get the Role ID for Super Admin
    SELECT role_id INTO v_role FROM roles WHERE role_name = 'ROLE_SUPER_ADMIN'; 
    
    -- Insert user if it doesn't already exist and role was found
    IF v_role IS NOT NULL AND NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@aerotalk.in') THEN 
        INSERT INTO users (user_id, username, email, password, status, email_verified, mobile_verified, account_locked, mfa_enabled, failed_login_attempts) 
        VALUES (v_user, 'Master Admin', 'admin@aerotalk.in', crypt('Admin@123', gen_salt('bf')), 'ACTIVE', true, true, false, false, 0); 
        
        -- Link user to super admin role
        INSERT INTO user_roles (user_role_id, user_id, role_id) 
        VALUES (gen_random_uuid(), v_user, v_role); 
    END IF; 
END $$;
