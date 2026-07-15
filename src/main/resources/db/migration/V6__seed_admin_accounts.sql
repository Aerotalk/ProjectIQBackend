-- Enable pgcrypto for BCrypt hashing
CREATE EXTENSION IF NOT EXISTS pgcrypto;

DO $$
DECLARE
    v_super_admin_id UUID := gen_random_uuid();
    v_org_admin_id UUID := gen_random_uuid();
    v_company_admin_id UUID := gen_random_uuid();
    
    v_super_admin_role UUID;
    v_org_admin_role UUID;
    v_company_admin_role UUID;
BEGIN
    -- Get Role IDs
    SELECT role_id INTO v_super_admin_role FROM roles WHERE role_name = 'ROLE_SUPER_ADMIN';
    SELECT role_id INTO v_org_admin_role FROM roles WHERE role_name = 'ROLE_ORG_ADMIN';
    SELECT role_id INTO v_company_admin_role FROM roles WHERE role_name = 'ROLE_COMPANY_ADMIN';

    -- Insert Super Admin if not exists
    IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'business@aerotalk.in') THEN
        INSERT INTO users (user_id, username, email, password, status, email_verified, mobile_verified, account_locked, mfa_enabled, failed_login_attempts)
        VALUES (v_super_admin_id, 'business_super', 'business@aerotalk.in', crypt('Aerotalk@2611', gen_salt('bf')), 'ACTIVE', true, true, false, false, 0);

        INSERT INTO user_roles (user_role_id, user_id, role_id)
        VALUES (gen_random_uuid(), v_super_admin_id, v_super_admin_role);
    END IF;

    -- Insert Org Admin if not exists
    IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'priyanshu@aerotalk.in') THEN
        INSERT INTO users (user_id, username, email, password, status, email_verified, mobile_verified, account_locked, mfa_enabled, failed_login_attempts)
        VALUES (v_org_admin_id, 'priyanshu_org', 'priyanshu@aerotalk.in', crypt('Paromits@2611', gen_salt('bf')), 'ACTIVE', true, true, false, false, 0);

        INSERT INTO user_roles (user_role_id, user_id, role_id)
        VALUES (gen_random_uuid(), v_org_admin_id, v_org_admin_role);
    END IF;

    -- Insert Company Admin if not exists
    IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'ratul@aerotalk.in') THEN
        INSERT INTO users (user_id, username, email, password, status, email_verified, mobile_verified, account_locked, mfa_enabled, failed_login_attempts)
        VALUES (v_company_admin_id, 'ratul_company', 'ratul@aerotalk.in', crypt('Messi@10', gen_salt('bf')), 'ACTIVE', true, true, false, false, 0);

        INSERT INTO user_roles (user_role_id, user_id, role_id)
        VALUES (gen_random_uuid(), v_company_admin_id, v_company_admin_role);
    END IF;

END $$;
