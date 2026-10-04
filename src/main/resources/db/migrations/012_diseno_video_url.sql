-- URL del reel / video de Instagram por diseño (opcional).
ALTER TABLE disenos
    ADD COLUMN IF NOT EXISTS video_url TEXT;
