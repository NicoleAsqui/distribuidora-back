-- Mismo nombre de diseño permitido en distintas secciones (materiales).
-- Ej.: «Caja Cono» en cartulina y en mdf.
-- El slug sigue siendo único global (URLs del catálogo).

ALTER TABLE disenos DROP CONSTRAINT IF EXISTS disenos_nombre_key;

DROP INDEX IF EXISTS disenos_nombre_key;
DROP INDEX IF EXISTS uq_disenos_nombre;

CREATE UNIQUE INDEX IF NOT EXISTS disenos_seccion_nombre_uidx
  ON disenos (seccion, nombre);
