CREATE TABLE att_approval_history (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    reference_id UUID,
    module VARCHAR(50),
    action VARCHAR(50),
    performed_by VARCHAR(100),
    performed_on TIMESTAMP,
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_approval_history_org FOREIGN KEY (organization_id) REFERENCES organizations(id)
);
