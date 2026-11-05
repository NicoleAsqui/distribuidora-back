-- Cotización aceptada → pedido: guarda el código del pedido creado (evita duplicarlo).
ALTER TABLE pricing_quotes ADD COLUMN IF NOT EXISTS order_code VARCHAR(40);
