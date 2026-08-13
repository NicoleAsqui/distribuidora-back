-- Actualiza la ubicación del local (Francisco Jácome Mz 278 Villa 1).
-- Ejecutar una vez en Neon / DB de producción si ya existe site_config.

UPDATE site_config
SET maps_url = 'https://maps.app.goo.gl/tnnQgcCEQ1Y9raVM6',
    maps_embed = 'https://www.google.com/maps?q=Francisco+J%C3%A1come+Mz+278+Villa+1,+Florida+Norte,+Guayaquil,+Ecuador&output=embed'
WHERE id = 'default'
   OR maps_url IS NOT NULL;
