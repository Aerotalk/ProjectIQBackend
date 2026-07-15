-- V4__phase2_finance_schema.sql
-- Updates schema with Phase 2 structural requirements

-- 1. Update Clients Table (Add billing/shipping contact details)
ALTER TABLE sales_clients 
    ADD COLUMN billing_attention VARCHAR(255),
    ADD COLUMN billing_phone VARCHAR(50),
    ADD COLUMN shipping_attention VARCHAR(255),
    ADD COLUMN shipping_phone VARCHAR(50);

-- 2. Update Vendors Table (Add billing/shipping contact details)
ALTER TABLE sales_vendors 
    ADD COLUMN billing_attention VARCHAR(255),
    ADD COLUMN billing_phone VARCHAR(50),
    ADD COLUMN shipping_attention VARCHAR(255),
    ADD COLUMN shipping_phone VARCHAR(50);

-- 3. Update Purchase Order Line Items (Add product mapping & tax)
ALTER TABLE purchase_order_line_items RENAME COLUMN unit_price TO rate;

ALTER TABLE purchase_order_line_items 
    ADD COLUMN product_id UUID,
    ADD COLUMN item_name VARCHAR(255),
    ADD COLUMN discount NUMERIC(15,4),
    ADD COLUMN taxable_amount NUMERIC(15,4),
    ADD COLUMN gst_rate NUMERIC(5,2),
    ADD COLUMN gst_amount NUMERIC(15,4);

-- 4. Update Challans Table (Change linked_vendor_po_id to proper foreign key)
ALTER TABLE challans
    ADD CONSTRAINT fk_challan_purchase_order 
    FOREIGN KEY (linked_vendor_po_id) REFERENCES purchase_orders(id) ON DELETE SET NULL;
