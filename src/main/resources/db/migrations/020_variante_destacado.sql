-- Productos marcados como «más vendida» / TOP en el storefront.
ALTER TABLE variantes
  ADD COLUMN IF NOT EXISTS destacado boolean NOT NULL DEFAULT false;
