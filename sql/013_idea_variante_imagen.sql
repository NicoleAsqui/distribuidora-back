-- Foto propia de cada idea individual (enlace idea ↔ producto).
-- El cliente ve título + foto de la idea; detrás sigue siendo la variante del catálogo.
ALTER TABLE idea_variantes ADD COLUMN IF NOT EXISTS url TEXT;
ALTER TABLE idea_variantes ADD COLUMN IF NOT EXISTS url_thumb TEXT;
