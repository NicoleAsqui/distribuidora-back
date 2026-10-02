-- Motor de precios por diseño (fórmula Excel / quote-engine).
ALTER TABLE disenos
    ADD COLUMN IF NOT EXISTS motor VARCHAR(64);

-- Seed desde slugs conocidos (mapa histórico frontend).
UPDATE disenos SET motor = 'cartulina_tapa' WHERE slug IN ('caja-base-y-tapa', 'caja-base-y-tapa-ventana') AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'porta_torta' WHERE slug = 'porta-torta' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'una_pieza' WHERE slug IN ('caja-una-pieza', 'caja-una-pieza-ventana') AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'acetato_forrada' WHERE slug IN ('caja-acetato-base-y-tapa', 'caja-forrada', 'caja-acetato-un-cuerpo') AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'juguete' WHERE slug = 'caja-juguete' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'juguete_doble' WHERE slug = 'caja-juguete-doble' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'ventana_l' WHERE slug = 'caja-ventana-en-l' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'cuatro_ventanas' WHERE slug = 'caja-4-ventanas' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'fosforo' WHERE slug = 'caja-tipo-fosforo' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'mdf_hexagono' WHERE slug = 'caja-hexagono' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'mdf_corazon' WHERE slug = 'caja-corazon' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'caja_china' WHERE slug = 'caja-china' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'pluma' WHERE slug = 'caja-pluma-llavero' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'cono' WHERE slug = 'caja-cono' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'caja_dora' WHERE slug = 'caja-dora' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'base_acetato' WHERE slug = 'caja-acetato-base-cartulina' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'portaglobos' WHERE slug = 'caja-portaglobos' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'doble_ovalo' WHERE slug = 'caja-doble-ovalo' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'lonchera_cartulina' WHERE slug = 'caja-lonchera' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'mohadilla' WHERE slug = 'caja-mohadilla' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'buzon' WHERE slug = 'caja-buzon' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'mdf_desayuno' WHERE slug = 'caja-mdf' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'mdf_tapa_base' WHERE slug = 'caja-mdf-base-y-tapa' AND (motor IS NULL OR motor = '');
UPDATE disenos SET motor = 'mdf_ventana_corrediza' WHERE slug = 'caja-mdf-ventana' AND (motor IS NULL OR motor = '');

-- Fallback por sección si aún no hay motor.
UPDATE disenos SET motor = 'cartulina_tapa' WHERE (motor IS NULL OR motor = '') AND lower(seccion) = 'cartulina';
UPDATE disenos SET motor = 'acetato_forrada' WHERE (motor IS NULL OR motor = '') AND lower(seccion) IN ('acetato', 'carton');
UPDATE disenos SET motor = 'cartulina_mdf' WHERE (motor IS NULL OR motor = '') AND lower(seccion) = 'mdf';
UPDATE disenos SET motor = 'cartulina_tapa' WHERE motor IS NULL OR motor = '';
