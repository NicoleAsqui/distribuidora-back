-- Un color del diseño puede estar habilitado sin foto.
-- La foto de textura (url) es opcional y se asigna desde las fotos del modelo.

ALTER TABLE diseno_texturas ALTER COLUMN url DROP NOT NULL;

ALTER TABLE diseno_texturas
  ADD COLUMN IF NOT EXISTS color_activo BOOLEAN NOT NULL DEFAULT TRUE;
