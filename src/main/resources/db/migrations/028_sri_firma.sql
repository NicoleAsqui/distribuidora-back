-- Firma electrónica (.p12) cifrada. La clave de la firma nunca se guarda.
CREATE TABLE IF NOT EXISTS sri_firma (
    id INT PRIMARY KEY,
    p12_cifrado TEXT NOT NULL,
    titular VARCHAR(300),
    valida_desde TIMESTAMPTZ,
    valida_hasta TIMESTAMPTZ,
    subida_at TIMESTAMPTZ NOT NULL,
    subida_por VARCHAR(80)
);
