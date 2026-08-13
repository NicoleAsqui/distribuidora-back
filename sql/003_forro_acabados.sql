-- Acabados de forro: papeles con textura y vinilos.
-- Solo aplican a cajas de cartón forrado (NO a cartulina maule / MDF / acetato).
-- Ejecutar una vez en Neon / DB de producción.

CREATE TABLE IF NOT EXISTS papeles_forro (
  id BIGSERIAL PRIMARY KEY,
  familia TEXT NOT NULL,                 -- TEXTURADOS, NACARADOS, SPLENDORLUX, LISOS NACARADOS, etc.
  nombre TEXT NOT NULL,
  medidas TEXT,                          -- ej. 72X101
  gramajes TEXT,                         -- ej. 95/140/200/250/300 GRS
  imagen_url TEXT,                       -- textura / muestra visual
  imagen_thumb_url TEXT,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  orden INTEGER NOT NULL DEFAULT 0,
  CONSTRAINT papeles_forro_nombre_unique UNIQUE (nombre)
);

CREATE TABLE IF NOT EXISTS viniles (
  id BIGSERIAL PRIMARY KEY,
  tipo TEXT NOT NULL,                    -- mate | brillante | impreso
  nombre TEXT NOT NULL,
  codigo_hex TEXT,                       -- color aproximado opcional
  imagen_url TEXT,
  imagen_thumb_url TEXT,
  requiere_arte BOOLEAN NOT NULL DEFAULT FALSE,  -- true para vinil impreso
  precio_ref NUMERIC(12,4),              -- referencia opcional (USD, IVA incl.)
  unidad_precio TEXT,                    -- metro | rollo | pack
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  orden INTEGER NOT NULL DEFAULT 0,
  CONSTRAINT viniles_tipo_check CHECK (tipo IN ('mate', 'brillante', 'impreso')),
  CONSTRAINT viniles_nombre_unique UNIQUE (nombre)
);

CREATE INDEX IF NOT EXISTS idx_papeles_forro_familia ON papeles_forro (familia);
CREATE INDEX IF NOT EXISTS idx_viniles_tipo ON viniles (tipo);

-- Pocos ejemplos (el catálogo completo se carga después)
INSERT INTO papeles_forro (familia, nombre, medidas, gramajes, activo, orden) VALUES
  ('TEXTURADOS', 'Tintoretto Gesso', '72×101', '95/140/200/250/300 GRS', TRUE, 10),
  ('LISOS NACARADOS', 'Stardream Crystal', '72×102', '120/240/285 GRS', TRUE, 20),
  ('SPLENDORLUX', 'Splendorlux Premium White', '71×100', '215 GRS', TRUE, 30),
  ('TEXTURADO MATE', 'Elle Erre Celeste', '70×100', '220 GRS', TRUE, 40)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO viniles (tipo, nombre, codigo_hex, requiere_arte, precio_ref, unidad_precio, activo, orden) VALUES
  ('mate', 'Vinil adhesivo dorado matte', '#c9a24a', FALSE, 4.75, 'metro', TRUE, 10),
  ('brillante', 'Vinil alemán adhesivo brillante amarillo', '#f5c518', FALSE, 5.01, 'metro', TRUE, 20),
  ('impreso', 'Vinil impreso (arte del cliente)', NULL, TRUE, NULL, NULL, TRUE, 30)
ON CONFLICT (nombre) DO NOTHING;
