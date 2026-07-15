-- V3__update_finance_schema.sql
-- Updates the database schema to support the Header + Items ERP pattern

-- 1. Update Purchase Orders Table
ALTER TABLE purchase_orders RENAME COLUMN amount TO grand_total;

ALTER TABLE purchase_orders 
    ADD COLUMN description TEXT,
    ADD COLUMN internal_notes TEXT,
    ADD COLUMN expected_delivery DATE,
    ADD COLUMN attachment_name VARCHAR(255);

-- 2. Create Purchase Order Line Items Table
CREATE TABLE purchase_order_line_items (
    id UUID PRIMARY KEY,
    purchase_order_id UUID NOT NULL REFERENCES purchase_orders(id) ON DELETE CASCADE,
    description TEXT,
    quantity NUMERIC(10,2),
    unit VARCHAR(50),
    unit_price NUMERIC(15,4),
    total_amount NUMERIC(15,4),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);

-- 3. Update Challans Table
ALTER TABLE challans 
    ADD COLUMN description TEXT,
    ADD COLUMN linked_vendor_po_id UUID,
    ADD COLUMN attachment_name VARCHAR(255);

-- 4. Create Challan Line Items Table
CREATE TABLE challan_line_items (
    id UUID PRIMARY KEY,
    challan_id UUID NOT NULL REFERENCES challans(id) ON DELETE CASCADE,
    description TEXT,
    dispatched_quantity NUMERIC(10,2),
    unit VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);

-- 5. Update Expenses Table
ALTER TABLE expenses RENAME COLUMN expense_type TO category;

ALTER TABLE expenses
    ADD COLUMN description TEXT,
    ADD COLUMN paid_by VARCHAR(255),
    ADD COLUMN is_gst_applicable BOOLEAN,
    ADD COLUMN gst_amount NUMERIC(15,2),
    ADD COLUMN is_input_credit_claimable BOOLEAN,
    ADD COLUMN receipt_name VARCHAR(255);
