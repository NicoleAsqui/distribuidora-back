-- Artículos de Información y Tutoriales, editables desde el admin.

CREATE TABLE IF NOT EXISTS guias (
  id BIGSERIAL PRIMARY KEY,
  seccion VARCHAR(32) NOT NULL,
  slug VARCHAR(120) NOT NULL,
  titulo TEXT NOT NULL,
  resumen TEXT,
  etiqueta VARCHAR(80),
  cuerpo TEXT,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  orden INTEGER NOT NULL DEFAULT 0,
  UNIQUE (seccion, slug)
);

CREATE INDEX IF NOT EXISTS idx_guias_seccion
  ON guias (seccion, activo, orden, id);

CREATE TABLE IF NOT EXISTS guia_imagenes (
  id BIGSERIAL PRIMARY KEY,
  guia_id BIGINT NOT NULL REFERENCES guias (id) ON DELETE CASCADE,
  url TEXT NOT NULL,
  url_thumb TEXT,
  orden INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_guia_imagenes_guia
  ON guia_imagenes (guia_id, orden, id);

INSERT INTO guias (seccion, slug, titulo, resumen, etiqueta, cuerpo, activo, orden)
SELECT 'informacion', 'tipos-de-forrado', 'Tipos de forrado',
  'Papel, vinil mate, brillante e impreso: cuándo conviene cada acabado.',
  'Acabados',
  $g$El forrado viste la caja de cartón: protege, da color y define la primera impresión al abrir.

## Papel de forro
Texturas y colores de catálogo (familias y gramajes). Ideal cuando quieres un look artesanal o corporativo sin impresión digital sobre vinil.

## Vinil mate
Acabado suave, sin brillo agresivo. Se ve premium en regalos y retail. Fácil de limpiar en superficies lisas.

## Vinil brillante
Refleja luz y destaca colores vivos. Muy usado en lanzamientos y cajas de escaparate.

## Vinil impreso
Lleva tu arte o logo. Suele requerir archivo de diseño; en Personaliza puedes subir la referencia para cotizar.

## Tip rápido
Si el uso es solo presentación en mesa, un papel bien elegido alcanza. Si la caja viaja o se manipula mucho, prioriza vinil o un cartón más firme.$g$,
  TRUE, 1
WHERE NOT EXISTS (SELECT 1 FROM guias WHERE seccion = 'informacion' AND slug = 'tipos-de-forrado');

INSERT INTO guias (seccion, slug, titulo, resumen, etiqueta, cuerpo, activo, orden)
SELECT 'informacion', 'cuadriculas-internas', 'Cuadrículas y divisiones internas',
  'Separadores para cupcakes, bombones y productos frágiles.',
  'Interior',
  $g$Las cuadrículas (divisiones) evitan que cupcakes, bombones o piezas sueltas se golpeen entre sí.

## Para qué sirven
- Separar unidades del mismo tamaño (dulces, jabones, frascos chicos).
- Mantener el producto centrado y con buena presentación al abrir.
- Reducir movimiento en trayectos cortos o entrega a domicilio.

## Cómo elegir
Cuenta cuántas cavidades necesitas y mide el diámetro o la base de cada pieza. Luego busca en el catálogo cajas con división o solicita la configuración en Personaliza.

## Tip rápido
Si tu producto es irregular, a veces conviene relleno (papel o espuma) en lugar de una cuadrícula rígida. Pregúntanos por WhatsApp con una foto.$g$,
  TRUE, 2
WHERE NOT EXISTS (SELECT 1 FROM guias WHERE seccion = 'informacion' AND slug = 'cuadriculas-internas');

INSERT INTO guias (seccion, slug, titulo, resumen, etiqueta, cuerpo, activo, orden)
SELECT 'informacion', 'tapas-de-acetato', 'Tipos de tapa de acetato',
  'Ventana, tapa completa o parcial: visibilidad sin perder protección.',
  'Acetato',
  $g$El acetato aporta transparencia: el cliente ve el producto sin abrir la caja.

## Ventana en tapa
Una abertura con acetato en la tapa o en un panel. Muestra el producto y mantiene estructura de cartulina o cartón alrededor.

## Tapa de acetato completa
La tapa (o gran parte) es transparente. Ideal para pastelería y regalos donde el color del interior importa.

## Acetato parcial / formas
Ventanas en L, óvalos u otras formas del catálogo. Equilibran visibilidad y privacidad del contenido.

## Tip rápido
El acetato se raya con facilidad: para envíos largos, valora un embalaje externo o un material más opaco. Para vitrina o entrega en mano, es una excelente carta de presentación.$g$,
  TRUE, 3
WHERE NOT EXISTS (SELECT 1 FROM guias WHERE seccion = 'informacion' AND slug = 'tapas-de-acetato');

INSERT INTO guias (seccion, slug, titulo, resumen, etiqueta, cuerpo, activo, orden)
SELECT 'tutoriales', 'como-medir', 'Cómo medir tu producto y elegir la caja',
  'Paso a paso: Largo × Ancho × Alto, qué caja te sirve y recomendaciones de material.',
  'Medidas',
  $g$En el catálogo las medidas van siempre así: Largo × Ancho × Alto. El Alto es la última cifra (altura de la caja).

## Paso a paso
- Coloca el producto como lo vas a guardar (horizontal o de pie).
- Largo: mide el lado más largo de la base del producto.
- Ancho: el otro lado de la base (perpendicular al largo).
- Alto: de la base hasta arriba. Esa es la medida final.
- Suma un margen (aprox. 0,5–1 cm por lado) si el producto es frágil o lleva papel o relleno.
- En Productos filtra por Largo, Ancho y Alto.

## Recomendaciones de material
- Cartulina: más liviana y económica; ideal para regalo, retail y presentación.
- Cartón rígido: más duro y estable; mejor para envíos o productos con más peso.
- Acetato: deja ver el contenido (ventana o tapa). Combina bien con cartulina.
- MDF: mayor presencia y rigidez; desayunos, regalos premium y piezas reutilizables.

¿Necesitas una medida fuera de catálogo? Arma tu pedido en Personaliza.$g$,
  TRUE, 1
WHERE NOT EXISTS (SELECT 1 FROM guias WHERE seccion = 'tutoriales' AND slug = 'como-medir');

INSERT INTO guias (seccion, slug, titulo, resumen, etiqueta, cuerpo, activo, orden)
SELECT 'tutoriales', 'cajas-mas-vendidas', 'Más info sobre Cajas más vendidas',
  'Por qué conviene empezar por las más populares y cómo decidir según tu marca.',
  'Catálogo',
  $g$## ¿Por qué elegir nuestras cajas más populares?
Las cajas más vendidas no son sólo las más solicitadas. Son soluciones que concentran tres factores clave:

- Funcionan en múltiples sectores: alimentación y repostería, cosmética, moda o productos handmade.
- Están optimizadas para uso real: protegen el producto, facilitan el montaje y mejoran la experiencia de unboxing.
- Permiten escalar: empiezas con pedidos ajustados y, cuando crece el volumen, pasas a personalización.

En DistribuidoraGuayaquil no se trata solo de vender cajas: te ayudamos a decidir el packaging según tu producto y el momento de tu marca.

## ¿No sabes cuál elegir?
Si tienes dudas de tamaño, resistencia o cierre, hazte estas 3 preguntas:

## ¿Qué uso le voy a dar a mi caja?
¿Envío, regalo, retail o alimentación? Prioriza resistencia para ecommerce; cartulina si buscas color y acabados de presentación.

## ¿Cuánto mide mi producto?
Revisa siempre las medidas interiores (Largo × Ancho × Alto). El tamaño correcto protege y optimiza costos.

## ¿En qué momento está mi marca?
¿Empezando, creciendo o consolidada? Puedes empezar con una caja versátil y sticker, o invertir en personalización que refuerce tu branding.$g$,
  TRUE, 2
WHERE NOT EXISTS (SELECT 1 FROM guias WHERE seccion = 'tutoriales' AND slug = 'cajas-mas-vendidas');

INSERT INTO guias (seccion, slug, titulo, resumen, etiqueta, cuerpo, activo, orden)
SELECT 'tutoriales', 'apuntes-rapidos', 'Apuntes rápidos de packaging',
  'Notas cortas para no olvidar: material, medida, uso y siguiente paso.',
  'Notas',
  $g$## Apuntes rápidos
- Medidas = Largo × Ancho × Alto (alto al final).
- Cartulina = liviana / menos resistente. Cartón rígido = más duro / más costo.
- Acetato = ver el producto. MDF = premium y rígido.
- Siempre deja un poco de holgura si hay relleno o papel.
- Empieza por cajas más vendidas; personaliza cuando escalas.
- En Productos e Ideas filtra por Largo, Ancho y Alto.
- Mínimos: cartulina y acetato desde 12 u. MDF y forradas desde 3 u.
- Local: sábados 9:00–18:00. WhatsApp en el botón verde.$g$,
  TRUE, 3
WHERE NOT EXISTS (SELECT 1 FROM guias WHERE seccion = 'tutoriales' AND slug = 'apuntes-rapidos');
