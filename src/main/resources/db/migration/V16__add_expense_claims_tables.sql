-- Expense Claims Module Tables

-- 1. Expense Categories Config
CREATE TABLE ecl_categories (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    category VARCHAR(100) NOT NULL,
    gl_code VARCHAR(30),
    receipt_required BOOLEAN,
    min_receipt_amount NUMERIC(12,2),
    active BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 2. Expense Claim Templates
CREATE TABLE ecl_templates (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    template_name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 3. Template Categories Mapping (Many-to-Many)
CREATE TABLE ecl_template_categories (
    id UUID PRIMARY KEY,
    template_id UUID NOT NULL REFERENCES ecl_templates(id),
    category_id UUID NOT NULL REFERENCES ecl_categories(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 4. Expense Policies
CREATE TABLE ecl_policies (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    category_id UUID NOT NULL REFERENCES ecl_categories(id),
    grade VARCHAR(50),
    max_claim NUMERIC(12,2),
    limit_type VARCHAR(20),
    period_limit NUMERIC(12,2),
    backdated_days INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 5. Reviewer Assignments
CREATE TABLE ecl_reviewer_assignments (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    template_id UUID REFERENCES ecl_templates(id),
    reviewer1_id UUID,
    reviewer2_id UUID,
    reviewer3_id UUID,
    auto_escalation_days INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 6. Expense Claims
CREATE TABLE ecl_claims (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    claim_no VARCHAR(30) NOT NULL,
    employee_id UUID NOT NULL,
    department_id UUID,
    project_id UUID,
    template_id UUID REFERENCES ecl_templates(id),
    title VARCHAR(200) NOT NULL,
    total_claimed NUMERIC(12,2),
    approved_amount NUMERIC(12,2),
    status VARCHAR(30),
    current_reviewer_id UUID,
    submitted_on TIMESTAMP,
    outstanding_advance NUMERIC(12,2),
    currency VARCHAR(10),
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 7. Expense Items
CREATE TABLE ecl_expense_items (
    id UUID PRIMARY KEY,
    claim_id UUID NOT NULL REFERENCES ecl_claims(id),
    expense_date DATE NOT NULL,
    category_id UUID NOT NULL REFERENCES ecl_categories(id),
    merchant_name VARCHAR(200),
    claim_amount NUMERIC(12,2) NOT NULL,
    description VARCHAR(500),
    project_id UUID,
    cost_center VARCHAR(50),
    bill_number VARCHAR(50),
    tax_amount NUMERIC(12,2),
    currency VARCHAR(10),
    receipt_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 8. Expense Advances
CREATE TABLE ecl_advances (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    advance_no VARCHAR(30) NOT NULL,
    employee_id UUID NOT NULL,
    trip_or_project VARCHAR(200),
    amount NUMERIC(12,2) NOT NULL,
    currency VARCHAR(10),
    requested_date DATE,
    required_date DATE,
    status VARCHAR(20),
    disbursed BOOLEAN,
    outstanding_balance NUMERIC(12,2),
    purpose VARCHAR(500),
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 9. Claim Batches
CREATE TABLE ecl_claim_batches (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    batch_no VARCHAR(30) NOT NULL,
    payroll_period VARCHAR(20),
    claims_count INTEGER,
    total_amount NUMERIC(14,2),
    status VARCHAR(20),
    created_by UUID,
    paid_on TIMESTAMP,
    payment_method VARCHAR(50),
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 10. Audit Logs
CREATE TABLE ecl_audit_logs (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    reviewer_id UUID,
    action VARCHAR(50) NOT NULL,
    comment VARCHAR(500),
    date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
