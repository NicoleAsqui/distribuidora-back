-- Cotizaciones simples (precio × cantidad, con/sin IVA).
ALTER TABLE pricing_quotes
  ADD COLUMN IF NOT EXISTS kind VARCHAR(16) NOT NULL DEFAULT 'motor';

ALTER TABLE pricing_quotes
  ADD COLUMN IF NOT EXISTS requires_invoice BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE pricing_quotes
  ADD COLUMN IF NOT EXISTS subtotal NUMERIC(14, 2);

ALTER TABLE pricing_quotes
  ADD COLUMN IF NOT EXISTS iva NUMERIC(14, 2);

UPDATE pricing_quotes
SET subtotal = total, iva = 0
WHERE subtotal IS NULL;
