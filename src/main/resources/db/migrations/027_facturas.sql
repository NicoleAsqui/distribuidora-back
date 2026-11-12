-- Facturas electrónicas emitidas al SRI (firma propia).
CREATE TABLE IF NOT EXISTS facturas (
    id VARCHAR(40) PRIMARY KEY,
    ambiente VARCHAR(1) NOT NULL,
    secuencial INT NOT NULL,
    numero VARCHAR(20) NOT NULL,
    clave_acceso VARCHAR(49) NOT NULL UNIQUE,
    estado VARCHAR(30) NOT NULL,
    fecha_emision VARCHAR(10) NOT NULL,
    order_id VARCHAR(255),
    order_code VARCHAR(40),
    cliente_identificacion VARCHAR(20),
    cliente_nombre VARCHAR(300),
    cliente_email VARCHAR(200),
    cliente_telefono VARCHAR(40),
    subtotal NUMERIC(14,2),
    iva NUMERIC(14,2),
    total NUMERIC(14,2),
    datos_json TEXT NOT NULL,
    xml_firmado TEXT,
    numero_autorizacion VARCHAR(60),
    fecha_autorizacion VARCHAR(60),
    mensajes TEXT,
    ride_url TEXT,
    email_enviado_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(80)
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_facturas_numero ON facturas (ambiente, numero);
CREATE INDEX IF NOT EXISTS idx_facturas_order ON facturas (order_id);
