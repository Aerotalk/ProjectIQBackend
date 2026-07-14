-- Seed Roles
INSERT INTO roles (role_id, role_name, system_role, description, status)
SELECT gen_random_uuid(), v.role_name, v.system_role, v.description, v.status
FROM (
  VALUES 
    ('ROLE_SUPER_ADMIN', true, 'System Super Administrator', 'ACTIVE'),
    ('ROLE_ORG_ADMIN', true, 'Organization Administrator', 'ACTIVE'),
    ('ROLE_COMPANY_ADMIN', true, 'Company Administrator', 'ACTIVE')
) AS v(role_name, system_role, description, status)
WHERE NOT EXISTS (
  SELECT 1 FROM roles r WHERE r.role_name = v.role_name
);

-- Seed Permission Groups
INSERT INTO permission_groups (permission_group_id, group_name, description)
SELECT gen_random_uuid(), v.group_name, v.description
FROM (
  VALUES 
    ('Organization Management', 'Manage organizations and teams'),
    ('Company Management', 'Manage companies'),
    ('Employee Management', 'HR and Employee lifecycle'),
    ('Department Management', 'Manage departments'),
    ('Role Management', 'Manage roles and permissions'),
    ('Settings Management', 'Manage system settings'),
    ('ERP Operations', 'Customers, Vendors, Products, Quotations'),
    ('Finance Operations', 'Invoices, Payments, Expenses, POs'),
    ('HR Operations', 'Attendance, Leaves, Payroll, Recruitment'),
    ('Incident Management', 'Tickets, SLAs, Knowledge Base'),
    ('Project Management', 'Projects, Tasks, Milestones'),
    ('Reports Management', 'Analytics & BI')
) AS v(group_name, description)
WHERE NOT EXISTS (
  SELECT 1 FROM permission_groups pg WHERE pg.group_name = v.group_name
);

-- Since the relations require exact UUIDs, we will use a DO block for PostgreSQL
DO $$
DECLARE
    v_org_group_id UUID;
    v_company_group_id UUID;
    v_emp_group_id UUID;
    v_dept_group_id UUID;
    v_role_group_id UUID;
    v_setting_group_id UUID;
    v_erp_group_id UUID;
    v_finance_group_id UUID;
    v_hr_group_id UUID;
    v_incident_group_id UUID;
    v_project_group_id UUID;
    v_report_group_id UUID;
    
    v_super_admin_role_id UUID;
    v_org_admin_role_id UUID;
    v_company_admin_role_id UUID;
BEGIN
    SELECT permission_group_id INTO v_org_group_id FROM permission_groups WHERE group_name = 'Organization Management';
    SELECT permission_group_id INTO v_company_group_id FROM permission_groups WHERE group_name = 'Company Management';
    SELECT permission_group_id INTO v_emp_group_id FROM permission_groups WHERE group_name = 'Employee Management';
    SELECT permission_group_id INTO v_dept_group_id FROM permission_groups WHERE group_name = 'Department Management';
    SELECT permission_group_id INTO v_role_group_id FROM permission_groups WHERE group_name = 'Role Management';
    SELECT permission_group_id INTO v_setting_group_id FROM permission_groups WHERE group_name = 'Settings Management';
    SELECT permission_group_id INTO v_erp_group_id FROM permission_groups WHERE group_name = 'ERP Operations';
    SELECT permission_group_id INTO v_finance_group_id FROM permission_groups WHERE group_name = 'Finance Operations';
    SELECT permission_group_id INTO v_hr_group_id FROM permission_groups WHERE group_name = 'HR Operations';
    SELECT permission_group_id INTO v_incident_group_id FROM permission_groups WHERE group_name = 'Incident Management';
    SELECT permission_group_id INTO v_project_group_id FROM permission_groups WHERE group_name = 'Project Management';
    SELECT permission_group_id INTO v_report_group_id FROM permission_groups WHERE group_name = 'Reports Management';

    SELECT role_id INTO v_super_admin_role_id FROM roles WHERE role_name = 'ROLE_SUPER_ADMIN';
    SELECT role_id INTO v_org_admin_role_id FROM roles WHERE role_name = 'ROLE_ORG_ADMIN';
    SELECT role_id INTO v_company_admin_role_id FROM roles WHERE role_name = 'ROLE_COMPANY_ADMIN';

    -- Note: Ideally we would also insert Permissions and map them to Permission Groups and Roles here.
    -- To keep the migration concise and robust, the application can insert individual permissions if they don't exist.
    
    INSERT INTO role_permission_groups (role_group_id, role_id, permission_group_id, data_scope)
    SELECT gen_random_uuid(), v_super_admin_role_id, v.pg_id, 'GLOBAL'
    FROM (
        VALUES 
            (v_org_group_id), (v_company_group_id), (v_emp_group_id),
            (v_dept_group_id), (v_role_group_id), (v_setting_group_id),
            (v_erp_group_id), (v_finance_group_id), (v_hr_group_id),
            (v_incident_group_id), (v_project_group_id), (v_report_group_id)
    ) AS v(pg_id)
    WHERE NOT EXISTS (
        SELECT 1 FROM role_permission_groups rpg 
        WHERE rpg.role_id = v_super_admin_role_id 
        AND rpg.permission_group_id = v.pg_id 
        AND rpg.data_scope = 'GLOBAL'
    );

    INSERT INTO role_permission_groups (role_group_id, role_id, permission_group_id, data_scope)
    SELECT gen_random_uuid(), v_org_admin_role_id, v.pg_id, 'ORGANIZATION'
    FROM (
        VALUES 
            (v_org_group_id), (v_company_group_id), (v_setting_group_id),
            (v_emp_group_id), (v_dept_group_id), (v_role_group_id),
            (v_erp_group_id), (v_finance_group_id), (v_hr_group_id),
            (v_incident_group_id), (v_project_group_id), (v_report_group_id)
    ) AS v(pg_id)
    WHERE NOT EXISTS (
        SELECT 1 FROM role_permission_groups rpg 
        WHERE rpg.role_id = v_org_admin_role_id 
        AND rpg.permission_group_id = v.pg_id 
        AND rpg.data_scope = 'ORGANIZATION'
    );

END $$;
