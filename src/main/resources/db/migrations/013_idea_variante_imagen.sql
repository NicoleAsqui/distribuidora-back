-- Foto propia de cada idea individual (enlace idea ↔ producto).
ALTER TABLE idea_variantes ADD COLUMN IF NOT EXISTS url TEXT;
ALTER TABLE idea_variantes ADD COLUMN IF NOT EXISTS url_thumb TEXT;
