CREATE TABLE IF NOT EXISTS pay_payroll_run_employees (
    payroll_run_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    PRIMARY KEY (payroll_run_id, employee_id),
    CONSTRAINT fk_pay_payroll_run_emp_run FOREIGN KEY (payroll_run_id) REFERENCES pay_payroll_runs(id) ON DELETE CASCADE,
    CONSTRAINT fk_pay_payroll_run_emp_emp FOREIGN KEY (employee_id) REFERENCES directory_employees(id) ON DELETE CASCADE
);
