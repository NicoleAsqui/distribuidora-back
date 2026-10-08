-- Distingue acabado/textura (blanca, kraft) de color de catálogo (rojo, azul…).
ALTER TABLE texturas
  ADD COLUMN IF NOT EXISTS tipo varchar(32) NOT NULL DEFAULT 'textura';

UPDATE texturas
SET tipo = 'textura'
WHERE lower(slug) IN ('blanca', 'kraft')
   OR lower(nombre) IN ('blanca', 'kraft');

COMMENT ON COLUMN texturas.tipo IS 'textura = acabado (blanca/kraft); color = color de catálogo';
