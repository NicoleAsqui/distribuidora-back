-- Índices para acelerar listados paginados del storefront.
-- Ejecutar una vez en Neon / Postgres.

CREATE INDEX IF NOT EXISTS idx_variantes_activo_diseno
  ON variantes (activo, diseno_id);

CREATE INDEX IF NOT EXISTS idx_variantes_diseno
  ON variantes (diseno_id);

CREATE INDEX IF NOT EXISTS idx_variantes_sku_lower
  ON variantes (LOWER(sku));

CREATE INDEX IF NOT EXISTS idx_disenos_slug
  ON disenos (slug);

CREATE INDEX IF NOT EXISTS idx_disenos_activo_orden
  ON disenos (activo, orden);

CREATE INDEX IF NOT EXISTS idx_idea_variantes_idea_orden
  ON idea_variantes (idea_id, orden, id);

CREATE INDEX IF NOT EXISTS idx_precios_variante_cantidad
  ON precios (variante_id, cantidad_desde);

CREATE INDEX IF NOT EXISTS idx_variante_imagenes_variante
  ON variante_imagenes (variante_id, principal DESC, orden, id);

CREATE INDEX IF NOT EXISTS idx_ideas_activo_orden
  ON ideas (activo, orden, id);

CREATE INDEX IF NOT EXISTS idx_idea_imagenes_idea
  ON idea_imagenes (idea_id, principal DESC, orden, id);
