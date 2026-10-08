-- Ideas marcadas como «más vendidas» en el home / listado TOP.
ALTER TABLE ideas
  ADD COLUMN IF NOT EXISTS destacado boolean NOT NULL DEFAULT false;
