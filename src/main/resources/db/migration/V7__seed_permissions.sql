DO $$
DECLARE
    v_org_group_id UUID;
    v_company_group_id UUID;
    v_emp_group_id UUID;
    v_setting_group_id UUID;
    v_erp_group_id UUID;
BEGIN
    SELECT permission_group_id INTO v_org_group_id FROM permission_groups WHERE group_name = 'Organization Management';
    SELECT permission_group_id INTO v_company_group_id FROM permission_groups WHERE group_name = 'Company Management';
    SELECT permission_group_id INTO v_emp_group_id FROM permission_groups WHERE group_name = 'Employee Management';
    SELECT permission_group_id INTO v_setting_group_id FROM permission_groups WHERE group_name = 'Settings Management';
    SELECT permission_group_id INTO v_erp_group_id FROM permission_groups WHERE group_name = 'ERP Operations';

    -- Insert Permissions
    INSERT INTO permissions (permission_id, permission_key, permission_name, module, description)
    SELECT gen_random_uuid(), v.permission_key, v.permission_name, v.module, v.description
    FROM (
        VALUES 
            ('org.create', 'Create Organization', 'Organization', 'Allows creating an organization'),
            ('org.view', 'View Organization', 'Organization', 'Allows viewing organization details'),
            ('org.edit', 'Edit Organization', 'Organization', 'Allows editing organization details'),
            ('org.delete', 'Delete Organization', 'Organization', 'Allows deleting an organization'),
            
            ('company.create', 'Create Company', 'Company', 'Allows creating a company'),
            ('company.view', 'View Company', 'Company', 'Allows viewing company details'),
            ('company.edit', 'Edit Company', 'Company', 'Allows editing company details'),
            ('company.delete', 'Delete Company', 'Company', 'Allows deleting a company'),
            
            ('employee.create', 'Create Employee', 'Employee', 'Allows adding users/employees'),
            ('employee.view', 'View Employee', 'Employee', 'Allows viewing users/employees'),
            ('employee.edit', 'Edit Employee', 'Employee', 'Allows editing users/employees'),
            ('employee.delete', 'Delete Employee', 'Employee', 'Allows deleting users/employees'),
            
            ('setting.view', 'View Settings', 'Settings', 'Allows viewing settings'),
            ('setting.edit', 'Edit Settings', 'Settings', 'Allows editing settings'),
            
            ('vendor.create', 'Create Vendor', 'Vendor', 'Allows creating a vendor'),
            ('vendor.view', 'View Vendor', 'Vendor', 'Allows viewing vendor details'),
            ('vendor.edit', 'Edit Vendor', 'Vendor', 'Allows editing vendor details'),
            ('vendor.delete', 'Delete Vendor', 'Vendor', 'Allows deleting a vendor'),

            ('client.create', 'Create Client', 'Client', 'Allows creating a client'),
            ('client.view', 'View Client', 'Client', 'Allows viewing client details'),
            ('client.edit', 'Edit Client', 'Client', 'Allows editing client details'),
            ('client.delete', 'Delete Client', 'Client', 'Allows deleting a client')
    ) AS v(permission_key, permission_name, module, description)
    WHERE NOT EXISTS (
        SELECT 1 FROM permissions p WHERE p.permission_key = v.permission_key
    );

    -- Map Permissions to Permission Groups
    -- Map Organization Permissions
    INSERT INTO permission_group_mappings (mapping_id, permission_group_id, permission_id)
    SELECT gen_random_uuid(), v_org_group_id, p.permission_id
    FROM permissions p
    WHERE p.module = 'Organization'
    AND NOT EXISTS (
        SELECT 1 FROM permission_group_mappings pgm 
        WHERE pgm.permission_group_id = v_org_group_id AND pgm.permission_id = p.permission_id
    );

    -- Map Company Permissions
    INSERT INTO permission_group_mappings (mapping_id, permission_group_id, permission_id)
    SELECT gen_random_uuid(), v_company_group_id, p.permission_id
    FROM permissions p
    WHERE p.module = 'Company'
    AND NOT EXISTS (
        SELECT 1 FROM permission_group_mappings pgm 
        WHERE pgm.permission_group_id = v_company_group_id AND pgm.permission_id = p.permission_id
    );

    -- Map Employee Permissions
    INSERT INTO permission_group_mappings (mapping_id, permission_group_id, permission_id)
    SELECT gen_random_uuid(), v_emp_group_id, p.permission_id
    FROM permissions p
    WHERE p.module = 'Employee'
    AND NOT EXISTS (
        SELECT 1 FROM permission_group_mappings pgm 
        WHERE pgm.permission_group_id = v_emp_group_id AND pgm.permission_id = p.permission_id
    );

    -- Map Setting Permissions
    INSERT INTO permission_group_mappings (mapping_id, permission_group_id, permission_id)
    SELECT gen_random_uuid(), v_setting_group_id, p.permission_id
    FROM permissions p
    WHERE p.module = 'Settings'
    AND NOT EXISTS (
        SELECT 1 FROM permission_group_mappings pgm 
        WHERE pgm.permission_group_id = v_setting_group_id AND pgm.permission_id = p.permission_id
    );

    -- Map Vendor/Client Permissions
    INSERT INTO permission_group_mappings (mapping_id, permission_group_id, permission_id)
    SELECT gen_random_uuid(), v_erp_group_id, p.permission_id
    FROM permissions p
    WHERE p.module IN ('Vendor', 'Client')
    AND NOT EXISTS (
        SELECT 1 FROM permission_group_mappings pgm 
        WHERE pgm.permission_group_id = v_erp_group_id AND pgm.permission_id = p.permission_id
    );

END $$;
