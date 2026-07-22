-- Add billing_address and shipping_address to challans
ALTER TABLE challans ADD COLUMN billing_address TEXT;
ALTER TABLE challans ADD COLUMN shipping_address TEXT;
