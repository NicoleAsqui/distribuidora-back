-- Productos facturados que no están en el catálogo: código único FX-0001… reutilizable.
CREATE TABLE IF NOT EXISTS factura_productos (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(25) NOT NULL UNIQUE,
    clave VARCHAR(300) NOT NULL UNIQUE,
    descripcion VARCHAR(300) NOT NULL,
    ultimo_precio NUMERIC(14,6),
    veces INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
