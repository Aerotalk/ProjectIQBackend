-- V14__add_employee_position_and_separation_tables.sql
-- Create employee_position_changes and employee_separations tables

CREATE TABLE IF NOT EXISTS employee_position_changes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employees(employee_id) ON DELETE CASCADE,
    change_type VARCHAR(50),
    effective_date DATE,
    department_id VARCHAR(100),
    designation_id VARCHAR(100),
    grade VARCHAR(50),
    location VARCHAR(100),
    reporting_manager_id UUID,
    remarks VARCHAR(1000),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_emp_pos_change_emp_id ON employee_position_changes(employee_id);

CREATE TABLE IF NOT EXISTS employee_separations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL UNIQUE REFERENCES employees(employee_id) ON DELETE CASCADE,
    separation_type VARCHAR(50),
    resignation_date DATE,
    last_working_date DATE,
    notice_period_days INTEGER,
    separation_reason VARCHAR(500),
    exit_interview BOOLEAN DEFAULT FALSE,
    separation_remarks VARCHAR(1000),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_emp_separation_emp_id ON employee_separations(employee_id);
