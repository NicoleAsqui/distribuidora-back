-- Trim Caja Base y Tapa con Ventana (diseno_id=3) to 74 official measures
BEGIN;
DELETE FROM variante_imagenes WHERE variante_id IN (272,262,263,265,137,138,139,140,269,261,266,268,244,156,264,267,242,285,759,248,813,754);
DELETE FROM variante_texturas WHERE variante_id IN (272,262,263,265,137,138,139,140,269,261,266,268,244,156,264,267,242,285,759,248,813,754);
DELETE FROM variante_tags WHERE variante_id IN (272,262,263,265,137,138,139,140,269,261,266,268,244,156,264,267,242,285,759,248,813,754);
DELETE FROM variante_configuraciones WHERE variante_id IN (272,262,263,265,137,138,139,140,269,261,266,268,244,156,264,267,242,285,759,248,813,754);
DELETE FROM variante_componentes WHERE variante_id IN (272,262,263,265,137,138,139,140,269,261,266,268,244,156,264,267,242,285,759,248,813,754);
DELETE FROM variante_atributos WHERE variante_id IN (272,262,263,265,137,138,139,140,269,261,266,268,244,156,264,267,242,285,759,248,813,754);
DELETE FROM idea_variantes WHERE variante_id IN (272,262,263,265,137,138,139,140,269,261,266,268,244,156,264,267,242,285,759,248,813,754);
DELETE FROM precios WHERE variante_id IN (272,262,263,265,137,138,139,140,269,261,266,268,244,156,264,267,242,285,759,248,813,754);
DELETE FROM variantes WHERE id IN (272,262,263,265,137,138,139,140,269,261,266,268,244,156,264,267,242,285,759,248,813,754) AND diseno_id = 3;

CREATE TEMP TABLE tmp_ventana_oficial (
  largo int, ancho int, alto int,
  p1 numeric(10,4), p3 numeric(10,4), p12 numeric(10,4), p100 numeric(10,4), p1000 numeric(10,4)
) ON COMMIT DROP;

INSERT INTO tmp_ventana_oficial VALUES
(10,10,3,0.38,0.34,0.31,0.25,0.19),
(10,10,5,0.41,0.37,0.34,0.28,0.21),
(10,10,10,0.47,0.42,0.38,0.32,0.24),
(10,12,10,0.5,0.45,0.41,0.35,0.27),
(10,15,3,0.49,0.4,0.4,0.36,0.25),
(10,15,5,0.51,0.46,0.42,0.38,0.27),
(10,15,8,0.58,0.52,0.48,0.43,0.32),
(10,15,10,0.6,0.53,0.49,0.44,0.33),
(10,30,5,0.73,0.67,0.62,0.52,0.41),
(10,30,6,0.78,0.72,0.66,0.54,0.43),
(10,30,8,0.84,0.78,0.71,0.59,0.47),
(10,30,10,0.86,0.79,0.72,0.6,0.48),
(10,35,5,0.83,0.76,0.7,0.58,0.47),
(10,35,8,0.92,0.85,0.78,0.65,0.53),
(10,35,10,0.95,0.88,0.8,0.67,0.54),
(12,12,4,0.47,0.42,0.39,0.33,0.24),
(12,12,12,0.58,0.52,0.5,0.45,0.32),
(15,15,3,0.57,0.51,0.47,0.42,0.31),
(15,15,5,0.61,0.54,0.5,0.45,0.34),
(15,15,6,0.65,0.58,0.53,0.47,0.36),
(15,15,10,0.71,0.64,0.58,0.52,0.41),
(15,15,15,0.73,0.65,0.59,0.53,0.42),
(15,20,5,0.67,0.62,0.57,0.49,0.39),
(15,20,8,0.76,0.7,0.65,0.53,0.43),
(15,20,20,1.01,0.93,0.85,0.71,0.57),
(15,25,5,0.77,0.72,0.66,0.55,0.45),
(15,25,6,0.83,0.77,0.71,0.59,0.49),
(15,25,8,0.88,0.81,0.74,0.62,0.52),
(15,30,5,0.89,0.82,0.76,0.63,0.52),
(15,30,8,0.91,0.84,0.77,0.64,0.53),
(15,30,10,0.92,0.85,0.78,0.65,0.54),
(15,35,5,0.94,0.87,0.8,0.67,0.56),
(15,35,8,1.01,0.93,0.85,0.71,0.61),
(15,35,10,1.15,1.05,0.96,0.81,0.68),
(15,40,5,0.98,0.9,0.83,0.69,0.58),
(15,40,10,1.17,1.07,0.98,0.82,0.7),
(20,20,3,0.76,0.68,0.62,0.56,0.43),
(20,20,5,0.78,0.7,0.64,0.57,0.44),
(20,20,6,0.79,0.71,0.65,0.58,0.45),
(20,20,8,1.06,0.98,0.89,0.74,0.61),
(20,20,10,1.08,0.99,0.9,0.75,0.62),
(20,20,20,1.12,1.03,0.94,0.78,0.65),
(20,25,5,0.84,0.77,0.71,0.59,0.5),
(20,25,6,0.87,0.81,0.74,0.62,0.52),
(20,25,8,1.09,1.0,0.92,0.77,0.65),
(20,25,10,1.11,1.02,0.93,0.78,0.66),
(20,30,5,0.94,0.87,0.8,0.67,0.58),
(20,30,6,0.95,0.88,0.81,0.68,0.59),
(20,30,8,1.15,1.06,0.96,0.81,0.7),
(20,30,10,1.16,1.07,0.98,0.82,0.71),
(20,35,5,1.02,0.95,0.87,0.73,0.62),
(20,35,8,1.22,1.12,1.03,0.87,0.73),
(20,35,10,1.23,1.13,1.04,0.88,0.74),
(20,40,8,1.27,1.17,1.07,0.9,0.75),
(20,40,10,1.28,1.18,1.08,0.91,0.77),
(20,45,10,1.34,1.24,1.13,0.96,0.84),
(25,25,5,0.92,0.86,0.79,0.66,0.54),
(25,25,6,1.11,1.03,0.94,0.79,0.65),
(25,25,8,1.13,1.04,0.95,0.8,0.66),
(25,25,10,1.14,1.05,0.96,0.81,0.67),
(25,25,25,1.61,1.47,1.34,1.13,0.91),
(25,30,5,0.98,0.91,0.83,0.7,0.62),
(25,30,6,1.17,1.08,0.99,0.84,0.73),
(25,30,8,1.19,1.09,1.0,0.85,0.74),
(25,30,10,1.2,1.11,1.01,0.86,0.75),
(25,40,8,1.33,1.22,1.12,0.95,0.79),
(25,45,10,1.42,1.31,1.2,1.03,0.89),
(30,30,5,1.47,1.36,1.24,1.06,0.9),
(30,30,6,1.49,1.37,1.26,1.07,0.91),
(30,30,8,1.5,1.38,1.27,1.08,0.92),
(30,30,10,1.52,1.4,1.28,1.1,0.93),
(30,30,30,2.1,1.92,1.75,1.5,1.15),
(35,35,35,2.99,2.73,2.47,2.13,1.62),
(40,40,40,3.02,2.76,2.5,2.15,1.7);

-- match medida with commutative largo/ancho
WITH matched AS (
  SELECT v.id AS variante_id, t.p1, t.p3, t.p12, t.p100, t.p1000
  FROM variantes v
  JOIN medidas m ON m.id = v.medida_id
  JOIN tmp_ventana_oficial t ON m.alto = t.alto
    AND LEAST(m.largo, m.ancho) = LEAST(t.largo, t.ancho)
    AND GREATEST(m.largo, m.ancho) = GREATEST(t.largo, t.ancho)
  WHERE v.diseno_id = 3
)
DELETE FROM precios p USING matched m WHERE p.variante_id = m.variante_id;


INSERT INTO precios (variante_id, cantidad_desde, precio)
SELECT variante_id, 1, p1 FROM (
  SELECT v.id AS variante_id, t.p1, t.p3, t.p12, t.p100, t.p1000
  FROM variantes v
  JOIN medidas m ON m.id = v.medida_id
  JOIN tmp_ventana_oficial t ON m.alto = t.alto
    AND LEAST(m.largo, m.ancho) = LEAST(t.largo, t.ancho)
    AND GREATEST(m.largo, m.ancho) = GREATEST(t.largo, t.ancho)
  WHERE v.diseno_id = 3
) matched;


INSERT INTO precios (variante_id, cantidad_desde, precio)
SELECT variante_id, 3, p3 FROM (
  SELECT v.id AS variante_id, t.p1, t.p3, t.p12, t.p100, t.p1000
  FROM variantes v
  JOIN medidas m ON m.id = v.medida_id
  JOIN tmp_ventana_oficial t ON m.alto = t.alto
    AND LEAST(m.largo, m.ancho) = LEAST(t.largo, t.ancho)
    AND GREATEST(m.largo, m.ancho) = GREATEST(t.largo, t.ancho)
  WHERE v.diseno_id = 3
) matched;


INSERT INTO precios (variante_id, cantidad_desde, precio)
SELECT variante_id, 12, p12 FROM (
  SELECT v.id AS variante_id, t.p1, t.p3, t.p12, t.p100, t.p1000
  FROM variantes v
  JOIN medidas m ON m.id = v.medida_id
  JOIN tmp_ventana_oficial t ON m.alto = t.alto
    AND LEAST(m.largo, m.ancho) = LEAST(t.largo, t.ancho)
    AND GREATEST(m.largo, m.ancho) = GREATEST(t.largo, t.ancho)
  WHERE v.diseno_id = 3
) matched;


INSERT INTO precios (variante_id, cantidad_desde, precio)
SELECT variante_id, 100, p100 FROM (
  SELECT v.id AS variante_id, t.p1, t.p3, t.p12, t.p100, t.p1000
  FROM variantes v
  JOIN medidas m ON m.id = v.medida_id
  JOIN tmp_ventana_oficial t ON m.alto = t.alto
    AND LEAST(m.largo, m.ancho) = LEAST(t.largo, t.ancho)
    AND GREATEST(m.largo, m.ancho) = GREATEST(t.largo, t.ancho)
  WHERE v.diseno_id = 3
) matched;


INSERT INTO precios (variante_id, cantidad_desde, precio)
SELECT variante_id, 1000, p1000 FROM (
  SELECT v.id AS variante_id, t.p1, t.p3, t.p12, t.p100, t.p1000
  FROM variantes v
  JOIN medidas m ON m.id = v.medida_id
  JOIN tmp_ventana_oficial t ON m.alto = t.alto
    AND LEAST(m.largo, m.ancho) = LEAST(t.largo, t.ancho)
    AND GREATEST(m.largo, m.ancho) = GREATEST(t.largo, t.ancho)
  WHERE v.diseno_id = 3
) matched;

COMMIT;