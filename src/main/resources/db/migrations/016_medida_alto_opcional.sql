-- Alto opcional: la sección Varios puede no tener altura.
ALTER TABLE medidas ALTER COLUMN alto DROP NOT NULL;

ALTER TABLE medidas DROP CONSTRAINT IF EXISTS medidas_dims_unique;

CREATE UNIQUE INDEX IF NOT EXISTS medidas_dims_unique
    ON medidas (largo, ancho, (COALESCE(alto, (-1)::numeric)), unidad);
