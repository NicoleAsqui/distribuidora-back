-- Sección del producto en cada línea del pedido (cartulina, acetato, mdf, carton…).
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS seccion VARCHAR(40);
