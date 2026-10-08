-- Presentación de venta en producto: unidad / paquete / cartón / caja.
-- Permite varios productos del mismo diseño+medida con distinta unidad de venta.

ALTER TABLE variantes
  ADD COLUMN IF NOT EXISTS unidad_venta text NOT NULL DEFAULT 'unidad';

ALTER TABLE variantes
  ADD COLUMN IF NOT EXISTS unidades_contenido integer NULL;

UPDATE variantes
SET unidad_venta = 'unidad'
WHERE unidad_venta IS NULL OR btrim(unidad_venta) = '';

ALTER TABLE variantes DROP CONSTRAINT IF EXISTS variantes_unidad_venta_check;
ALTER TABLE variantes
  ADD CONSTRAINT variantes_unidad_venta_check
  CHECK (unidad_venta IN ('unidad', 'paquete', 'carton', 'caja'));

ALTER TABLE variantes DROP CONSTRAINT IF EXISTS variantes_unidades_contenido_check;
ALTER TABLE variantes
  ADD CONSTRAINT variantes_unidades_contenido_check
  CHECK (unidades_contenido IS NULL OR unidades_contenido > 0);

ALTER TABLE variantes DROP CONSTRAINT IF EXISTS variantes_diseno_medida_unique;
DROP INDEX IF EXISTS variantes_diseno_medida_unique;

CREATE UNIQUE INDEX IF NOT EXISTS variantes_diseno_medida_unidad_uidx
  ON variantes (diseno_id, medida_id, unidad_venta);
