-- ═══════════════════════════════════════════════════════
-- HRMS PAYROLL TABLES  –  V17
-- ═══════════════════════════════════════════════════════

-- 1. Pay Components (master salary component catalog)
CREATE TABLE pay_components (
    id                UUID PRIMARY KEY,
    organization_id   UUID NOT NULL REFERENCES organizations(id),
    component_name    VARCHAR(100) NOT NULL,
    code              VARCHAR(30),
    type              VARCHAR(30),           -- Earning | Deduction | Reimbursement | Employer Contribution
    sub_type          VARCHAR(50),           -- Fixed | Variable | Statutory | Voluntary
    calculation_type  VARCHAR(30),           -- Flat Amount | Percentage | Formula | Slab
    percentage_of     VARCHAR(50),
    percentage_value  NUMERIC(5,2),
    max_limit         NUMERIC(12,2),
    taxable           BOOLEAN,
    pro_rata          BOOLEAN,
    part_of_ctc       BOOLEAN,
    part_of_gross     BOOLEAN,
    display_order     INTEGER,
    active            BOOLEAN DEFAULT TRUE,
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL
);

-- 2. Payslip Templates
CREATE TABLE pay_payslip_templates (
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    template_name   VARCHAR(100) NOT NULL,
    layout_html     TEXT,
    preview_image   VARCHAR(500),
    set_as_default  BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);

-- 3. Salary Inputs (per-employee per-period manual adjustments)
CREATE TABLE pay_salary_inputs (
    id               UUID PRIMARY KEY,
    organization_id  UUID NOT NULL REFERENCES organizations(id),
    employee_id      UUID NOT NULL REFERENCES employees(id),
    payroll_period   VARCHAR(20) NOT NULL,
    pay_component    VARCHAR(100) NOT NULL,
    amount           NUMERIC(12,2) NOT NULL,
    input_type       VARCHAR(20),           -- Addition | Override | Deduction
    reason           VARCHAR(500),
    recurring        BOOLEAN DEFAULT FALSE,
    recurring_until  VARCHAR(20),
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL
);

-- 4. Employee Loss of Pay (LOP)
CREATE TABLE pay_employee_lop (
    id               UUID PRIMARY KEY,
    organization_id  UUID NOT NULL REFERENCES organizations(id),
    employee_id      UUID NOT NULL REFERENCES employees(id),
    payroll_period   VARCHAR(20) NOT NULL,
    lop_days         NUMERIC(5,2) NOT NULL,
    source           VARCHAR(30),           -- Attendance | Manual | Leave System
    reason           VARCHAR(500),
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL
);

-- 5. Salary Holds
CREATE TABLE pay_salary_holds (
    id               UUID PRIMARY KEY,
    organization_id  UUID NOT NULL REFERENCES organizations(id),
    employee_id      UUID NOT NULL REFERENCES employees(id),
    payroll_period   VARCHAR(20) NOT NULL,
    hold_amount      NUMERIC(12,2) NOT NULL,
    reason           VARCHAR(500) NOT NULL,
    active           BOOLEAN DEFAULT TRUE,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL
);

-- 6. Salary Stops
CREATE TABLE pay_salary_stops (
    id               UUID PRIMARY KEY,
    organization_id  UUID NOT NULL REFERENCES organizations(id),
    employee_id      UUID NOT NULL REFERENCES employees(id),
    stop_from_date   DATE NOT NULL,
    stop_until_date  DATE,
    reason           VARCHAR(500) NOT NULL,
    active           BOOLEAN DEFAULT TRUE,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL
);

-- 7. IT Declarations (header – one per employee per financial year)
CREATE TABLE pay_it_declarations (
    id               UUID PRIMARY KEY,
    organization_id  UUID NOT NULL REFERENCES organizations(id),
    employee_id      UUID NOT NULL REFERENCES employees(id),
    financial_year   VARCHAR(20) NOT NULL,
    tax_regime       VARCHAR(20),           -- Old Regime | New Regime
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL,
    UNIQUE (organization_id, employee_id, financial_year)
);

-- 8. IT Declaration Items
CREATE TABLE pay_it_declaration_items (
    id               UUID PRIMARY KEY,
    declaration_id   UUID NOT NULL REFERENCES pay_it_declarations(id),
    tax_section      VARCHAR(20) NOT NULL,
    description      VARCHAR(200) NOT NULL,
    declared_amount  NUMERIC(12,2) NOT NULL,
    proof_upload     VARCHAR(500),
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL
);

-- 9. FBP Declarations (header)
CREATE TABLE pay_fbp_declarations (
    id               UUID PRIMARY KEY,
    organization_id  UUID NOT NULL REFERENCES organizations(id),
    employee_id      UUID NOT NULL REFERENCES employees(id),
    financial_year   VARCHAR(20) NOT NULL,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL
);

-- 10. FBP Declaration Items
CREATE TABLE pay_fbp_declaration_items (
    id                  UUID PRIMARY KEY,
    declaration_id      UUID NOT NULL REFERENCES pay_fbp_declarations(id),
    reimbursement_type  VARCHAR(50) NOT NULL,
    annual_amount       NUMERIC(12,2) NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP NOT NULL
);

-- 11. Reimbursement Claims
CREATE TABLE pay_reimbursement_claims (
    id                  UUID PRIMARY KEY,
    organization_id     UUID NOT NULL REFERENCES organizations(id),
    employee_id         UUID NOT NULL REFERENCES employees(id),
    reimbursement_type  VARCHAR(50) NOT NULL,  -- Travel | Medical | Internet | Meals | Other
    claim_period        VARCHAR(20),
    claimed_amount      NUMERIC(12,2) NOT NULL,
    bill_date           DATE NOT NULL,
    bill_number         VARCHAR(50) NOT NULL,
    bill_upload         VARCHAR(500),
    remarks             VARCHAR(500),
    status              VARCHAR(20) DEFAULT 'Pending',  -- Pending | Approved | Rejected
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP NOT NULL
);

-- 12. Payroll Runs (batch header)
CREATE TABLE pay_payroll_runs (
    id               UUID PRIMARY KEY,
    organization_id  UUID NOT NULL REFERENCES organizations(id),
    payroll_period   VARCHAR(20) NOT NULL,
    run_type         VARCHAR(20),            -- Regular | Supplementary | Arrears
    employee_scope   VARCHAR(30),            -- All Employees | Department | Selected Employees
    department_id    UUID REFERENCES departments(id),
    status           VARCHAR(20) DEFAULT 'Draft',     -- Draft | Processing | Processed | Approved
    employee_count   INTEGER,
    total_gross      NUMERIC(14,2),
    total_deductions NUMERIC(14,2),
    total_net        NUMERIC(14,2),
    processed_by     UUID REFERENCES users(id),
    processed_on     TIMESTAMP,
    approved_by      UUID REFERENCES users(id),
    approved_on      TIMESTAMP,
    payout_status    VARCHAR(20) DEFAULT 'Unpaid',    -- Unpaid | Pending | Paid
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL
);

-- 12a. Selected Employees for a Payroll Run (for "Selected Employees" scope)
CREATE TABLE pay_payroll_run_employees (
    payroll_run_id  UUID NOT NULL REFERENCES pay_payroll_runs(id),
    employee_id     UUID NOT NULL,
    PRIMARY KEY (payroll_run_id, employee_id)
);

-- 13. Payroll Run Details (per-employee computed result)
CREATE TABLE pay_payroll_run_details (
    id                UUID PRIMARY KEY,
    payroll_run_id    UUID NOT NULL REFERENCES pay_payroll_runs(id),
    employee_id       UUID NOT NULL REFERENCES employees(id),
    gross             NUMERIC(12,2),
    total_deductions  NUMERIC(12,2),
    net               NUMERIC(12,2),
    lop_days          NUMERIC(5,2),
    payable_days      NUMERIC(5,2),
    basic_pay         NUMERIC(12,2),
    hra               NUMERIC(12,2),
    special_allowance NUMERIC(12,2),
    pf_deduction      NUMERIC(12,2),
    esi_deduction     NUMERIC(12,2),
    tds_deduction     NUMERIC(12,2),
    professional_tax  NUMERIC(12,2),
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL
);

-- 14. Final Settlements
CREATE TABLE pay_final_settlements (
    id                   UUID PRIMARY KEY,
    organization_id      UUID NOT NULL REFERENCES organizations(id),
    employee_id          UUID NOT NULL REFERENCES employees(id),
    settlement_date      DATE NOT NULL,
    last_working_date    DATE NOT NULL,
    net_settlement_amount NUMERIC(12,2),
    status               VARCHAR(20) DEFAULT 'Draft',   -- Draft | Processed | Paid
    created_at           TIMESTAMP NOT NULL,
    updated_at           TIMESTAMP NOT NULL
);

-- 15. Final Settlement Items
CREATE TABLE pay_final_settlement_items (
    id              UUID PRIMARY KEY,
    settlement_id   UUID NOT NULL REFERENCES pay_final_settlements(id),
    item_type       VARCHAR(100) NOT NULL,
    description     VARCHAR(200),
    amount          NUMERIC(12,2) NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);

-- ─── INDEXES FOR PERFORMANCE ────────────────────────────────────────────────
CREATE INDEX idx_pay_components_org        ON pay_components(organization_id);
CREATE INDEX idx_pay_salary_inputs_org     ON pay_salary_inputs(organization_id);
CREATE INDEX idx_pay_salary_inputs_emp     ON pay_salary_inputs(employee_id);
CREATE INDEX idx_pay_salary_inputs_period  ON pay_salary_inputs(payroll_period);
CREATE INDEX idx_pay_employee_lop_org      ON pay_employee_lop(organization_id);
CREATE INDEX idx_pay_employee_lop_emp      ON pay_employee_lop(employee_id);
CREATE INDEX idx_pay_employee_lop_period   ON pay_employee_lop(payroll_period);
CREATE INDEX idx_pay_salary_holds_org      ON pay_salary_holds(organization_id);
CREATE INDEX idx_pay_salary_holds_emp      ON pay_salary_holds(employee_id);
CREATE INDEX idx_pay_salary_stops_org      ON pay_salary_stops(organization_id);
CREATE INDEX idx_pay_salary_stops_emp      ON pay_salary_stops(employee_id);
CREATE INDEX idx_pay_it_decl_org           ON pay_it_declarations(organization_id);
CREATE INDEX idx_pay_it_decl_emp           ON pay_it_declarations(employee_id);
CREATE INDEX idx_pay_it_items_decl         ON pay_it_declaration_items(declaration_id);
CREATE INDEX idx_pay_fbp_decl_org          ON pay_fbp_declarations(organization_id);
CREATE INDEX idx_pay_fbp_decl_emp          ON pay_fbp_declarations(employee_id);
CREATE INDEX idx_pay_fbp_items_decl        ON pay_fbp_declaration_items(declaration_id);
CREATE INDEX idx_pay_reimb_org             ON pay_reimbursement_claims(organization_id);
CREATE INDEX idx_pay_reimb_emp             ON pay_reimbursement_claims(employee_id);
CREATE INDEX idx_pay_runs_org              ON pay_payroll_runs(organization_id);
CREATE INDEX idx_pay_run_details_run       ON pay_payroll_run_details(payroll_run_id);
CREATE INDEX idx_pay_run_details_emp       ON pay_payroll_run_details(employee_id);
CREATE INDEX idx_pay_settlements_org       ON pay_final_settlements(organization_id);
CREATE INDEX idx_pay_settlement_items_stl  ON pay_final_settlement_items(settlement_id);
