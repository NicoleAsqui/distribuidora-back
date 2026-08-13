-- Elimina el catálogo de "colores" (reemplazado por papeles_forro y viniles).
-- Ejecutar una vez en Neon / DB de producción.

-- Quitar FK color de componentes de variante
ALTER TABLE variante_componentes DROP CONSTRAINT IF EXISTS variante_componentes_unique;
ALTER TABLE variante_componentes DROP CONSTRAINT IF EXISTS variante_componentes_color_id_fkey;
ALTER TABLE variante_componentes DROP COLUMN IF EXISTS color_id;
ALTER TABLE variante_componentes
  ADD CONSTRAINT variante_componentes_unique
  UNIQUE (variante_id, componente_id, material_id, gramaje_id);

DROP TABLE IF EXISTS material_colores CASCADE;
DROP TABLE IF EXISTS colores CASCADE;
