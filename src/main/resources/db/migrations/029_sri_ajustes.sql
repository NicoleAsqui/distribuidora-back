-- Datos del emisor y ambiente SRI editables desde el admin (reemplazan las variables de entorno).
CREATE TABLE IF NOT EXISTS sri_ajustes (
    id INT PRIMARY KEY,
    datos_json TEXT NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(80)
);
