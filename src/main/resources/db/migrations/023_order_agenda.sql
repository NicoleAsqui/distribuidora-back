-- Agenda de pedidos: estados Pagado/Hecho, fecha agendada, abonos, historial,
-- trabajadores con PIN y link privado del calendario. Idempotente.

-- Hibernate pudo crear un CHECK con los estados viejos; los nuevos (PAID, DONE) lo romperían.
ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_status_check;

ALTER TABLE orders ADD COLUMN IF NOT EXISTS source VARCHAR(20) NOT NULL DEFAULT 'web';
ALTER TABLE orders ADD COLUMN IF NOT EXISTS scheduled_at TIMESTAMPTZ;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS paid_at TIMESTAMPTZ;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS done_at TIMESTAMPTZ;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS done_by VARCHAR(80);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS paid_amount NUMERIC(14,2) NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_orders_status_scheduled ON orders (status, scheduled_at);

-- Historial: quién hizo qué y cuándo (nunca se borra).
CREATE TABLE IF NOT EXISTS order_events (
    id BIGSERIAL PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    actor VARCHAR(80) NOT NULL,
    type VARCHAR(30) NOT NULL,
    message TEXT,
    amount NUMERIC(14,2)
);
CREATE INDEX IF NOT EXISTS idx_order_events_order ON order_events (order_id, created_at);

-- Trabajadores (Miguel, Sarita…): cada uno con su PIN para firmar lo que mueve.
CREATE TABLE IF NOT EXISTS staff_members (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    pin_hash VARCHAR(128) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Valores secretos de la app (p. ej. token del link del calendario).
CREATE TABLE IF NOT EXISTS app_secrets (
    clave VARCHAR(60) PRIMARY KEY,
    valor VARCHAR(200) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
