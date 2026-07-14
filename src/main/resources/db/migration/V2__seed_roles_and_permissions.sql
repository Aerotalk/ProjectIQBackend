-- Seed Roles
INSERT INTO roles (role_id, role_name, system_role, description, status)
VALUES 
  (gen_random_uuid(), 'ROLE_SUPER_ADMIN', true, 'System Super Administrator', 'ACTIVE'),
  (gen_random_uuid(), 'ROLE_ORG_ADMIN', true, 'Organization Administrator', 'ACTIVE'),
  (gen_random_uuid(), 'ROLE_COMPANY_ADMIN', true, 'Company Administrator', 'ACTIVE')
ON CONFLICT (role_name) DO NOTHING;

-- Seed Permission Groups
INSERT INTO permission_groups (permission_group_id, group_name, description)
VALUES 
  (gen_random_uuid(), 'Organization Management', 'Manage organizations and teams'),
  (gen_random_uuid(), 'Company Management', 'Manage companies'),
  (gen_random_uuid(), 'Employee Management', 'HR and Employee lifecycle'),
  (gen_random_uuid(), 'Department Management', 'Manage departments'),
  (gen_random_uuid(), 'Role Management', 'Manage roles and permissions'),
  (gen_random_uuid(), 'Settings Management', 'Manage system settings'),
  (gen_random_uuid(), 'ERP Operations', 'Customers, Vendors, Products, Quotations'),
  (gen_random_uuid(), 'Finance Operations', 'Invoices, Payments, Expenses, POs'),
  (gen_random_uuid(), 'HR Operations', 'Attendance, Leaves, Payroll, Recruitment'),
  (gen_random_uuid(), 'Incident Management', 'Tickets, SLAs, Knowledge Base'),
  (gen_random_uuid(), 'Project Management', 'Projects, Tasks, Milestones'),
  (gen_random_uuid(), 'Reports Management', 'Analytics & BI')
ON CONFLICT (group_name) DO NOTHING;

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
    
    -- Map Groups to Super Admin Role (GLOBAL)
    INSERT INTO role_permission_groups (role_group_id, role_id, permission_group_id, data_scope)
    VALUES 
        (gen_random_uuid(), v_super_admin_role_id, v_org_group_id, 'GLOBAL'),
        (gen_random_uuid(), v_super_admin_role_id, v_company_group_id, 'GLOBAL'),
        (gen_random_uuid(), v_super_admin_role_id, v_emp_group_id, 'GLOBAL'),
        (gen_random_uuid(), v_super_admin_role_id, v_dept_group_id, 'GLOBAL'),
        (gen_random_uuid(), v_super_admin_role_id, v_role_group_id, 'GLOBAL'),
        (gen_random_uuid(), v_super_admin_role_id, v_setting_group_id, 'GLOBAL'),
        (gen_random_uuid(), v_super_admin_role_id, v_erp_group_id, 'GLOBAL'),
        (gen_random_uuid(), v_super_admin_role_id, v_finance_group_id, 'GLOBAL'),
        (gen_random_uuid(), v_super_admin_role_id, v_hr_group_id, 'GLOBAL'),
        (gen_random_uuid(), v_super_admin_role_id, v_incident_group_id, 'GLOBAL'),
        (gen_random_uuid(), v_super_admin_role_id, v_project_group_id, 'GLOBAL'),
        (gen_random_uuid(), v_super_admin_role_id, v_report_group_id, 'GLOBAL')
    ON CONFLICT DO NOTHING;

    -- Map Groups to Org Admin Role (ORGANIZATION)
    INSERT INTO role_permission_groups (role_group_id, role_id, permission_group_id, data_scope)
    VALUES 
        (gen_random_uuid(), v_org_admin_role_id, v_org_group_id, 'ORGANIZATION'),
        (gen_random_uuid(), v_org_admin_role_id, v_company_group_id, 'ORGANIZATION'),
        (gen_random_uuid(), v_org_admin_role_id, v_setting_group_id, 'ORGANIZATION'),
        (gen_random_uuid(), v_org_admin_role_id, v_emp_group_id, 'ORGANIZATION'),
        (gen_random_uuid(), v_org_admin_role_id, v_dept_group_id, 'ORGANIZATION'),
        (gen_random_uuid(), v_org_admin_role_id, v_role_group_id, 'ORGANIZATION'),
        (gen_random_uuid(), v_org_admin_role_id, v_erp_group_id, 'ORGANIZATION'),
        (gen_random_uuid(), v_org_admin_role_id, v_finance_group_id, 'ORGANIZATION'),
        (gen_random_uuid(), v_org_admin_role_id, v_hr_group_id, 'ORGANIZATION'),
        (gen_random_uuid(), v_org_admin_role_id, v_incident_group_id, 'ORGANIZATION'),
        (gen_random_uuid(), v_org_admin_role_id, v_project_group_id, 'ORGANIZATION'),
        (gen_random_uuid(), v_org_admin_role_id, v_report_group_id, 'ORGANIZATION')
    ON CONFLICT DO NOTHING;

END $$;
