-- Galería de fotos por modelo + columnas de imagen/sección en disenos.

ALTER TABLE disenos
  ADD COLUMN IF NOT EXISTS seccion VARCHAR(32) NOT NULL DEFAULT 'cartulina';

ALTER TABLE disenos
  ADD COLUMN IF NOT EXISTS imagen_url TEXT;

ALTER TABLE disenos
  ADD COLUMN IF NOT EXISTS imagen_thumb_url TEXT;

CREATE TABLE IF NOT EXISTS diseno_imagenes (
  id BIGSERIAL PRIMARY KEY,
  diseno_id BIGINT NOT NULL REFERENCES disenos (id) ON DELETE CASCADE,
  url TEXT NOT NULL,
  url_thumb TEXT,
  principal BOOLEAN NOT NULL DEFAULT FALSE,
  orden INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_diseno_imagenes_diseno
  ON diseno_imagenes (diseno_id, principal DESC, orden ASC, id ASC);

-- Relleno: thumb = url si falta.
UPDATE diseno_imagenes SET url_thumb = url WHERE url_thumb IS NULL OR url_thumb = '';
