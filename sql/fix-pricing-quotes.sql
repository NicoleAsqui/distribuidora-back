-- ============================================================
-- Recrear tablas de cotizaciones (pricing)
-- Neon SQL Editor → pegar → Run
-- BORRA datos de pricing_quotes y quote_engine_configs
-- NO toca products, orders, catálogo, etc.
-- ============================================================

DROP TABLE IF EXISTS pricing_quotes CASCADE;
DROP TABLE IF EXISTS quote_engine_configs CASCADE;

CREATE TABLE pricing_quotes (
  id            VARCHAR(255) PRIMARY KEY,
  code          VARCHAR(255) NOT NULL UNIQUE,
  source        VARCHAR(255) NOT NULL,
  created_at    TIMESTAMPTZ NOT NULL,
  client_name   VARCHAR(255),
  client_phone  VARCHAR(255),
  client_email  VARCHAR(255),
  delivery_date VARCHAR(255),
  status        VARCHAR(255) NOT NULL,
  items_json    TEXT NOT NULL,
  notes         VARCHAR(4000),
  total         NUMERIC(14,2) NOT NULL DEFAULT 0
);

CREATE TABLE quote_engine_configs (
  motor       VARCHAR(255) PRIMARY KEY,
  config_json TEXT NOT NULL
);

-- Columnas de imagen en products (por si faltan)
ALTER TABLE products ADD COLUMN IF NOT EXISTS image_url VARCHAR(1000);
ALTER TABLE products ADD COLUMN IF NOT EXISTS image_thumb_url VARCHAR(1000);

-- Checkout en orders (por si falta)
ALTER TABLE orders ADD COLUMN IF NOT EXISTS checkout_json TEXT;
ALTER TABLE orders ALTER COLUMN notes TYPE TEXT;

SELECT 'pricing_quotes' AS tabla, COUNT(*) AS filas FROM pricing_quotes
UNION ALL
SELECT 'quote_engine_configs', COUNT(*) FROM quote_engine_configs;
