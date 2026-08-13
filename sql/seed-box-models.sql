-- Semilla catálogo auxiliar + modelos de cajas (catálogo 2026 + motores de cotización)
-- Ejecutar en Neon SQL Editor de Nicole.
-- Listas: separador || (StringListConverter Hibernate)

ALTER TABLE box_models ADD COLUMN IF NOT EXISTS category_ids varchar(2000);

-- Categorías de uso (un modelo puede tener varias)
INSERT INTO categories (id, name, slug) VALUES
  ('c1','Regalos','regalos'),
  ('c2','Corporativas','corporativas'),
  ('c3','Botellas','botellas'),
  ('c4','Cosméticos','cosmeticos'),
  ('c5','Alimentos','alimentos'),
  ('c6','Eventos','eventos'),
  ('c7','Joyería','joyeria'),
  ('c8','Flores','flores'),
  ('c9','Chocolates','chocolates'),
  ('c10','Navidad','navidad')
ON CONFLICT (id) DO UPDATE SET name=EXCLUDED.name, slug=EXCLUDED.slug;

-- Materiales (1 material principal por modelo; options = variantes Personaliza)
INSERT INTO materials (id, name, options) VALUES
  ('m1','Cartulina','Blanca||Kraft||Color'),
  ('m2','Cartón','Con forro||Sin forro||Con impresión'),
  ('m3','Madera','Natural||Pintada')
ON CONFLICT (id) DO UPDATE SET name=EXCLUDED.name, options=EXCLUDED.options;

INSERT INTO colors (id, name, hex, available) VALUES
  ('cl1','Negro','#111111',true),
  ('cl2','Blanco','#f5f5f0',true),
  ('cl3','Kraft','#c8a978',true),
  ('cl4','Dorado','#d4af37',true),
  ('cl5','Plateado','#c0c0c0',true),
  ('cl6','Azul Marino','#1e2a4a',true),
  ('cl7','Rojo','#c62828',true),
  ('cl8','Verde','#cfe7d8',true),
  ('cl9','Rosa','#f6c6c0',true),
  ('cl10','Azul','#cfd9ee',true)
ON CONFLICT (id) DO UPDATE SET name=EXCLUDED.name, hex=EXCLUDED.hex, available=EXCLUDED.available;

INSERT INTO finishes (id, name, description) VALUES
  ('f1','Papel','Forro de papel decorativo'),
  ('f2','Vinil','Forro de vinil texturizado'),
  ('f3','Full Color','Impresión CMYK a todo color'),
  ('f4','Foil Dorado','Estampado metalizado dorado'),
  ('f5','Foil Plateado','Estampado metalizado plateado'),
  ('f6','Grabado Láser','Grabado por láser en madera'),
  ('f7','UV','Barniz UV localizado'),
  ('f8','Barniz','Barniz brillo o mate')
ON CONFLICT (id) DO UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description;

INSERT INTO tags (id, name, color) VALUES
  ('t1','Premium','#d4af37'),
  ('t2','Lujo','#111111'),
  ('t3','Magnética','#00B8C4'),
  ('t4','Regalos','#f6c6c0'),
  ('t5','Flores','#e91e63'),
  ('t6','Chocolates','#795548'),
  ('t7','Corporativo','#1e2a4a'),
  ('t8','Navidad','#c62828'),
  ('t9','Minimalista','#607d8b')
ON CONFLICT (id) DO UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color;

-- Modelos (photos vacío: subir imagen desde Admin → Modelos)

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('acetato_forrada','Acetato y forrada','c1','c1||c2','Modelo del catálogo 2026: Acetato y forrada. Material principal según Personaliza.','','m2','f1||f2||f3||f4||f5','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t1||t9||t7',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('cartulina_mdf','Cartulina / MDF (calculadora)','c1','c1||c5','Modelo del catálogo 2026: Cartulina / MDF (calculadora). Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('cartulina_tapa','Cartulina base + tapa','c1','c1||c4','Modelo del catálogo 2026: Cartulina base + tapa. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('porta_torta','Porta torta','c5','c5||c6','Modelo del catálogo 2026: Porta torta. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('ventana_divisiones','Ventana + divisiones','c1','c1||c5','Modelo del catálogo 2026: Ventana + divisiones. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('una_pieza','Una sola pieza','c1','c1||c6','Modelo del catálogo 2026: Una sola pieza. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('una_pieza_div','Una pieza + div + ventana','c1','c1||c5','Modelo del catálogo 2026: Una pieza + div + ventana. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('cakes','Cakes (2–4) / porta bocados','c5','c5||c6','Modelo del catálogo 2026: Cakes (2–4) / porta bocados. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('cakes_seis','Seis cakes','c5','c5||c6','Modelo del catálogo 2026: Seis cakes. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_desayuno','MDF desayuno llanas','c1','c1||c5','Modelo del catálogo 2026: MDF desayuno llanas. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t1||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_circulo','MDF desayuno círculo','c1','c1||c5','Modelo del catálogo 2026: MDF desayuno círculo. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t1||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_tapa_entero','MDF tapa cuerpo entero','c1','c1||c2','Modelo del catálogo 2026: MDF tapa cuerpo entero. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t1||t9||t7',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_ventana_corrediza','MDF ventana corrediza','c1','c1||c7','Modelo del catálogo 2026: MDF ventana corrediza. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t2||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_ventana_vidrio','MDF ventana vidrio','c1','c1||c7','Modelo del catálogo 2026: MDF ventana vidrio. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t2||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_tapa_base','MDF tapa y base','c1','c1||c2','Modelo del catálogo 2026: MDF tapa y base. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t1||t9||t7',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_soga','MDF con soga','c1','c1||c2','Modelo del catálogo 2026: MDF con soga. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t1||t9||t7',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('juguete','Caja juguete','c1','c1||c6','Modelo del catálogo 2026: Caja juguete. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('juguete_doble','Caja juguete doble','c1','c1||c6','Modelo del catálogo 2026: Caja juguete doble. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('lonchera_cartulina','Lonchera cartulina','c5','c5||c1','Modelo del catálogo 2026: Lonchera cartulina. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('lonchera_acetato','Lonchera acetato','c5','c5||c1','Modelo del catálogo 2026: Lonchera acetato. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('portaglobos','Portaglobos','c6','c6||c1','Modelo del catálogo 2026: Portaglobos. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_vinera','MDF vinera sin tapa','c3','c3||c2','Modelo del catálogo 2026: MDF vinera sin tapa. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t1||t9||t7',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_vinera_tapa','MDF vinera tapa+soga','c3','c3||c2','Modelo del catálogo 2026: MDF vinera tapa+soga. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t2||t9||t7',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_vinera_media','MDF vinera media tapa','c3','c3||c1','Modelo del catálogo 2026: MDF vinera media tapa. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t1||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_hexagono','MDF hexágono','c1','c1||c7','Modelo del catálogo 2026: MDF hexágono. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t1||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_corazon','MDF corazón','c1','c1||c6','Modelo del catálogo 2026: MDF corazón. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t2||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_corazon_tapa','MDF corazón con tapa','c1','c1||c6','Modelo del catálogo 2026: MDF corazón con tapa. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t2||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('bordes_dobles','Bordes dobles','c1','c1||c5','Modelo del catálogo 2026: Bordes dobles. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t1||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('portabocado_recuadro','Portabocado recuadro','c5','c5||c6','Modelo del catálogo 2026: Portabocado recuadro. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('ventana_formas','Ventana en formas','c1','c1||c6','Modelo del catálogo 2026: Ventana en formas. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('tapa_color_formas','Tapa color + formas','c1','c1||c6','Modelo del catálogo 2026: Tapa color + formas. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t1||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('ventana_l','Ventana en L','c1','c1||c4','Modelo del catálogo 2026: Ventana en L. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('cuatro_ventanas','4 ventanas acetato','c1','c1||c4','Modelo del catálogo 2026: 4 ventanas acetato. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t1||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('caja_china','Caja china','c1','c1||c4','Modelo del catálogo 2026: Caja china. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('china_base','China con base','c1','c1||c4','Modelo del catálogo 2026: China con base. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('pluma','Pluma / llavero','c2','c2||c7','Modelo del catálogo 2026: Pluma / llavero. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t7||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('cono','Cono cartulina','c6','c6||c1','Modelo del catálogo 2026: Cono cartulina. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('base_acetato','Base + cuerpo acetato','c1','c1||c5','Modelo del catálogo 2026: Base + cuerpo acetato. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t1||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('base_doble','Base cartulina doble','c1','c1||c5','Modelo del catálogo 2026: Base cartulina doble. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('caja_dora','Caja dora','c1','c1||c7','Modelo del catálogo 2026: Caja dora. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t1||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('fosforo','Tipo fósforo','c7','c7||c4','Modelo del catálogo 2026: Tipo fósforo. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('esferas','Porta esferas','c6','c6||c1','Modelo del catálogo 2026: Porta esferas. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t8||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('dora_contra','Dora contrapiso','c1','c1||c7','Modelo del catálogo 2026: Dora contrapiso. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t1||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('doble_ovalo','Doble óvalo cartulina','c1','c1||c6','Modelo del catálogo 2026: Doble óvalo cartulina. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mohadilla','Mohadilla','c7','c7||c4','Modelo del catálogo 2026: Mohadilla. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('buzon','Buzón','c6','c6||c1','Modelo del catálogo 2026: Buzón. Material principal según Personaliza.','','m1','f3||f4||f5||f7||f8','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_ovalo','MDF doble óvalo','c1','c1||c6','Modelo del catálogo 2026: MDF doble óvalo. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t1||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_pizarra','MDF pizarra','c2','c2||c1','Modelo del catálogo 2026: MDF pizarra. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t7||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_pizarra_contorno','MDF pizarra contorno','c2','c2||c1','Modelo del catálogo 2026: MDF pizarra contorno. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t7||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_porta_foto','MDF porta foto','c1','c1||c6','Modelo del catálogo 2026: MDF porta foto. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t1||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_numero','MDF número/letras','c6','c6||c1','Modelo del catálogo 2026: MDF número/letras. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_rejas','MDF rejas 2 pisos','c1','c1||c5','Modelo del catálogo 2026: MDF rejas 2 pisos. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t1||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_circulos','MDF círculos','c1','c1||c6','Modelo del catálogo 2026: MDF círculos. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t4||t9',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mdf_vitrina_div','MDF vitrina división','c7','c7||c2','Modelo del catálogo 2026: MDF vitrina división. Material principal según Personaliza.','','m3','f6||f8','cl3||cl1||cl2||cl6',10,21,'t2||t9||t7',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mo_tipo_libro','Caja tipo libro','c1','c1||c2','Modelo del catálogo 2026: Caja tipo libro. Material principal según Personaliza.','','m2','f1||f2||f3||f4||f5','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t1||t2||t3||t7',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mo_magnetica','Caja magnética','c1','c1||c2','Modelo del catálogo 2026: Caja magnética. Material principal según Personaliza.','','m2','f1||f2||f3||f4||f5','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t1||t3||t7',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mo_cilindro','Caja cilindro forrada','c1','c1||c6','Modelo del catálogo 2026: Caja cilindro forrada. Material principal según Personaliza.','','m2','f1||f2||f3||f4||f5','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t1||t2',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mo_explosiva','Caja explosiva','c1','c1||c6','Modelo del catálogo 2026: Caja explosiva. Material principal según Personaliza.','','m2','f1||f2||f3||f4||f5','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t1||t4',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;

INSERT INTO box_models (id, name, category_id, category_ids, description, photos, materials, finishes, colors, min_qty, lead_days, tags, active)
VALUES ('mo_deluxe','Caja deluxe','c1','c1||c2','Modelo del catálogo 2026: Caja deluxe. Material principal según Personaliza.','','m2','f1||f2||f3||f4||f5','cl1||cl2||cl3||cl4||cl5||cl6||cl7||cl8||cl9||cl10',25,14,'t1||t2||t7',true)
ON CONFLICT (id) DO UPDATE SET
  name=EXCLUDED.name,
  category_id=EXCLUDED.category_id,
  category_ids=EXCLUDED.category_ids,
  description=EXCLUDED.description,
  materials=EXCLUDED.materials,
  finishes=EXCLUDED.finishes,
  colors=EXCLUDED.colors,
  min_qty=EXCLUDED.min_qty,
  lead_days=EXCLUDED.lead_days,
  tags=EXCLUDED.tags,
  active=EXCLUDED.active;


-- Nota: las imágenes (photos) se cargan en Admin → Modelos (GCS).
-- Tras subir, photos queda como URL completa (una o varias separadas por ||).
