-- V5__minor_tweaks.sql
-- Minor backend tweaks as requested

-- 1. Make item_code optional in Product
ALTER TABLE sales_products ALTER COLUMN item_code DROP NOT NULL;

-- 2. Add delivery_cost to Quotation
ALTER TABLE sales_quotations ADD COLUMN delivery_cost NUMERIC(19,4);

-- 3. Add eway_bill_no to Challan
ALTER TABLE challans ADD COLUMN eway_bill_no VARCHAR(100);
