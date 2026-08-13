-- Checkout metadata for catalog cart orders
ALTER TABLE orders ADD COLUMN IF NOT EXISTS checkout_json TEXT;
ALTER TABLE orders ALTER COLUMN notes TYPE TEXT;
