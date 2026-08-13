-- Miniatura separada de la imagen full (listados vs detalle).
ALTER TABLE variante_imagenes
  ADD COLUMN IF NOT EXISTS url_thumb TEXT;

ALTER TABLE idea_imagenes
  ADD COLUMN IF NOT EXISTS url_thumb TEXT;

-- Relleno: si no hay thumb, usar la misma URL (compatibilidad con datos existentes).
UPDATE variante_imagenes SET url_thumb = url WHERE url_thumb IS NULL OR url_thumb = '';
UPDATE idea_imagenes SET url_thumb = url WHERE url_thumb IS NULL OR url_thumb = '';
