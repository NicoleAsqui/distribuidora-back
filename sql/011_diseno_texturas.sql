-- Texturas / colores del diseño (una vez por modelo). Todas las medidas heredan la misma lista.
-- Antes vivían en variante_texturas (por SKU), lo que repetía la misma config en cada medida.

CREATE TABLE IF NOT EXISTS diseno_texturas (
  id BIGSERIAL PRIMARY KEY,
  diseno_id BIGINT NOT NULL REFERENCES disenos (id) ON DELETE CASCADE,
  textura_id BIGINT NOT NULL REFERENCES texturas (id) ON DELETE RESTRICT,
  url TEXT NOT NULL,
  url_thumb TEXT,
  orden INTEGER NOT NULL DEFAULT 0,
  UNIQUE (diseno_id, textura_id)
);

CREATE INDEX IF NOT EXISTS idx_diseno_texturas_diseno
  ON diseno_texturas (diseno_id, orden ASC, id ASC);

-- Migrar datos existentes: una fila por (diseño, textura), tomando la primera variante.
INSERT INTO diseno_texturas (diseno_id, textura_id, url, url_thumb, orden)
SELECT DISTINCT ON (v.diseno_id, vt.textura_id)
  v.diseno_id,
  vt.textura_id,
  vt.url,
  vt.url_thumb,
  COALESCE(vt.orden, 0)
FROM variante_texturas vt
JOIN variantes v ON v.id = vt.variante_id
WHERE v.diseno_id IS NOT NULL
  AND vt.url IS NOT NULL
  AND TRIM(vt.url) <> ''
ORDER BY v.diseno_id, vt.textura_id, vt.id
ON CONFLICT (diseno_id, textura_id) DO NOTHING;
