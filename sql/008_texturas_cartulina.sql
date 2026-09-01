-- Colores / texturas de cartulina (blanca, kraft, etc.) + foto de producto por textura.

CREATE TABLE IF NOT EXISTS texturas (
  id BIGSERIAL PRIMARY KEY,
  nombre VARCHAR(128) NOT NULL,
  slug VARCHAR(128) NOT NULL UNIQUE,
  imagen_url TEXT,
  imagen_thumb_url TEXT,
  seccion VARCHAR(32) NOT NULL DEFAULT 'cartulina',
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  orden INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_texturas_seccion_activo
  ON texturas (seccion, activo, orden, id);

CREATE TABLE IF NOT EXISTS variante_texturas (
  id BIGSERIAL PRIMARY KEY,
  variante_id BIGINT NOT NULL REFERENCES variantes (id) ON DELETE CASCADE,
  textura_id BIGINT NOT NULL REFERENCES texturas (id) ON DELETE RESTRICT,
  url TEXT NOT NULL,
  url_thumb TEXT,
  orden INTEGER NOT NULL DEFAULT 0,
  UNIQUE (variante_id, textura_id)
);

CREATE INDEX IF NOT EXISTS idx_variante_texturas_variante
  ON variante_texturas (variante_id, orden ASC, id ASC);

INSERT INTO texturas (nombre, slug, orden)
VALUES ('Blanca', 'blanca', 0), ('Kraft', 'kraft', 1)
ON CONFLICT (slug) DO NOTHING;
