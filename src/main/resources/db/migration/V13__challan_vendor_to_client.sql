-- V13__challan_vendor_to_client.sql
-- Change delivery challan linkage from vendor to client and remove linked_vendor_po_id

-- 1. Add client_id column to challans
ALTER TABLE challans ADD COLUMN client_id UUID;

-- 2. Add foreign key constraint for client_id
ALTER TABLE challans
    ADD CONSTRAINT fk_challan_client
    FOREIGN KEY (client_id) REFERENCES sales_clients(id) ON DELETE SET NULL;

-- 3. Create index for client_id
CREATE INDEX idx_challan_client ON challans(client_id);

-- 4. Drop foreign key constraint on linked_vendor_po_id if exists
ALTER TABLE challans DROP CONSTRAINT IF EXISTS fk_challan_purchase_order;

-- 5. Drop vendor_id and linked_vendor_po_id columns
ALTER TABLE challans DROP COLUMN IF EXISTS vendor_id;
ALTER TABLE challans DROP COLUMN IF EXISTS linked_vendor_po_id;
