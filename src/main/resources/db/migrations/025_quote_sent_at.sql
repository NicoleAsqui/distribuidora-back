-- Fecha en que la cotización se envió con precios al cliente: la validez (1 semana) cuenta desde aquí.
ALTER TABLE pricing_quotes ADD COLUMN IF NOT EXISTS sent_at TIMESTAMPTZ;
