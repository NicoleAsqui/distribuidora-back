ALTER TABLE pricing_quotes
    ADD COLUMN IF NOT EXISTS edit_token VARCHAR(64);

UPDATE pricing_quotes
SET edit_token = gen_random_uuid()::text
WHERE edit_token IS NULL OR edit_token = '';

CREATE UNIQUE INDEX IF NOT EXISTS idx_pricing_quotes_edit_token
    ON pricing_quotes (edit_token)
    WHERE edit_token IS NOT NULL AND edit_token <> '';
