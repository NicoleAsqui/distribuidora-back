package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.CatalogCountsDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.DisenoCardDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.DisenoImagenCardDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.IdeaDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.ProductCardDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.ProductPageDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.ProductTexturaDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.ProductVariantDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.DisenoEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.DisenoImagenEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.DisenoTexturaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaImagenEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaVarianteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MedidaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.PapelForroEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.PrecioEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.TexturaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VinilEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.DisenoImagenRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.DisenoRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.DisenoTexturaRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.IdeaImagenRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.IdeaRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.IdeaVarianteRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.MedidaRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.PapelForroRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.PrecioRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.TexturaRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VarianteRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VinilRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Lecturas públicas del catálogo nuevo (diseños → variantes → precios/imágenes).
 * Las tarjetas de producto del storefront se construyen a partir de cada variante.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CatalogQueryService {

    private static final Logger log = LoggerFactory.getLogger(CatalogQueryService.class);

    /** Tamaño de página para {@code ?top=true} (solo productos con destacado=true). */
    private static final int TOP_LIMIT = 8;

    private final DisenoRepository disenoRepository;
    private final DisenoImagenRepository disenoImagenRepository;
    private final DisenoTexturaRepository disenoTexturaRepository;
    private final MedidaRepository medidaRepository;
    private final VarianteRepository varianteRepository;
    private final PrecioRepository precioRepository;
    private final TexturaRepository texturaRepository;
    private final PapelForroRepository papelForroRepository;
    private final VinilRepository vinilRepository;
    private final IdeaRepository ideaRepository;
    private final IdeaImagenRepository ideaImagenRepository;
    private final IdeaVarianteRepository ideaVarianteRepository;

    public List<DisenoEntity> listDisenosActivos() {
        try {
            return disenoRepository.findByActivoTrueOrderByNombreAscIdAsc();
        } catch (DataAccessException e) {
            log.error("No se pudieron listar diseños activos (¿falta migración disenos.seccion?)", e);
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Catálogo de diseños no disponible: falta migración de base de datos.");
        }
    }

    /**
     * Modelos para galería: nombre + foto representativa + # de medidas.
     * La sección sale de {@code disenos.seccion}.
     */
    public List<DisenoCardDto> listDisenoCards() {
        try {
            return buildDisenoCards();
        } catch (DataAccessException e) {
            log.error("Error al armar tarjetas de diseño", e);
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Catálogo de diseños no disponible temporalmente.");
        }
    }

    private List<DisenoCardDto> buildDisenoCards() {
        List<DisenoEntity> diseños = listDisenosActivos();
        if (diseños.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> counts = new HashMap<>();
        for (Object[] row : varianteRepository.countActiveGroupedByDisenoId()) {
            long disenoId = toLong(row[0]);
            if (disenoId > 0) {
                counts.put(disenoId, toLong(row[1]));
            }
        }

        List<Long> disenoIds = diseños.stream().map(DisenoEntity::getId).toList();
        Map<Long, List<DisenoImagenEntity>> fotosByDiseno = Map.of();
        try {
            fotosByDiseno = groupBy(
                    disenoImagenRepository.findByDisenoIdInOrderByPrincipalDescOrdenAscIdAsc(disenoIds),
                    DisenoImagenEntity::getDisenoId);
        } catch (DataAccessException e) {
            log.warn("No se pudieron cargar diseno_imagenes; se usan columnas legacy del diseño", e);
        }

        Map<Long, List<DisenoImagenEntity>> fotosFinal = fotosByDiseno;
        List<DisenoCardDto> cards = diseños.stream().map(d -> {
            long n = counts.getOrDefault(d.getId(), 0L);
            List<DisenoImagenCardDto> imagenes = new ArrayList<>();
            for (DisenoImagenEntity img : fotosFinal.getOrDefault(d.getId(), List.of())) {
                String full = img.getUrl();
                String thumb = img.getUrlThumb();
                if (thumb == null || thumb.isBlank()) {
                    thumb = full;
                }
                imagenes.add(new DisenoImagenCardDto(full, thumb));
            }
            if (imagenes.isEmpty()) {
                String full = d.getImagenUrl();
                String thumb = d.getImagenThumbUrl();
                if (full != null && !full.isBlank()) {
                    full = full.trim();
                    thumb = thumb == null || thumb.isBlank() ? full : thumb.trim();
                    imagenes.add(new DisenoImagenCardDto(full, thumb));
                }
            }
            String full = imagenes.isEmpty() ? null : imagenes.getFirst().url();
            String thumb = imagenes.isEmpty() ? null : imagenes.getFirst().urlThumb();
            return new DisenoCardDto(
                    d.getId(),
                    d.getNombre(),
                    d.getSlug(),
                    d.getDescripcion(),
                    d.getOrden(),
                    normalizeProductoSeccion(d.getSeccion()),
                    normalizeMotor(d.getMotor()),
                    full,
                    thumb,
                    imagenes,
                    n,
                    blankToNull(d.getVideoUrl())
            );
        }).collect(Collectors.toCollection(ArrayList::new));

        cards.sort(Comparator
                .comparingInt((DisenoCardDto c) -> seccionOrden(c.seccion()))
                .thenComparing(DisenoCardDto::nombre, Comparator.nullsLast(String::compareToIgnoreCase)));
        return cards;
    }

    /** acetato | cartulina | mdf | carton | tarjetas | varios — default cartulina. */
    static String normalizeProductoSeccion(String raw) {
        if (raw == null || raw.isBlank()) {
            return "cartulina";
        }
        String s = raw.trim().toLowerCase();
        return switch (s) {
            case "acetato", "cartulina", "mdf", "carton", "tarjetas", "varios" -> s;
            default -> "cartulina";
        };
    }

    static String normalizeMotor(String raw) {
        if (raw == null || raw.isBlank()) {
            return "cartulina_tapa";
        }
        return raw.trim().toLowerCase().replace('-', '_');
    }

    private static int seccionOrden(String seccion) {
        return switch (normalizeProductoSeccion(seccion)) {
            case "cartulina" -> 0;
            case "acetato" -> 1;
            case "mdf" -> 2;
            case "carton" -> 3;
            case "tarjetas" -> 4;
            case "varios" -> 5;
            default -> 9;
        };
    }

    private static long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }


    public List<PapelForroEntity> listPapelesForroActivos() {
        return papelForroRepository.findByActivoTrueOrderByOrdenAscNombreAsc();
    }

    public List<TexturaEntity> listTexturasActivas(String seccion) {
        String sec = seccion == null || seccion.isBlank() ? "cartulina" : seccion.trim().toLowerCase();
        try {
            return texturaRepository.findByActivoTrueAndSeccionOrderByOrdenAscNombreAscIdAsc(sec);
        } catch (DataAccessException e) {
            log.warn("No se pudieron listar texturas", e);
            return List.of();
        }
    }

    public List<VinilEntity> listVinilesActivos(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return vinilRepository.findByActivoTrueOrderByOrdenAscNombreAsc();
        }
        String t = tipo.trim().toLowerCase();
        return vinilRepository.findByActivoTrueAndTipoOrderByOrdenAscNombreAsc(t);
    }


    /** Nombres de diseño activos: el frontend los usa como categorías/filtros. */
    public List<String> listCategorias() {
        Set<String> nombres = new LinkedHashSet<>();
        for (DisenoEntity d : listDisenosActivos()) {
            if (d.getNombre() != null && !d.getNombre().isBlank()) {
                nombres.add(d.getNombre());
            }
        }
        return List.copyOf(nombres);
    }

    /** Conteos de variantes activas por diseño e idea (filtros del menú). */
    public CatalogCountsDto listCatalogCounts() {
        long total = varianteRepository.countByActivoTrue();
        Map<Long, String> disenoSlugs = new HashMap<>();
        for (DisenoEntity d : disenoRepository.findAll()) {
            if (d.getSlug() != null) {
                disenoSlugs.put(d.getId(), d.getSlug());
            }
        }
        Map<String, Long> byDesign = new HashMap<>();
        for (Object[] row : varianteRepository.countActiveGroupedByDisenoId()) {
            long disenoId = toLong(row[0]);
            long count = toLong(row[1]);
            String slug = disenoSlugs.get(disenoId);
            if (slug != null) {
                byDesign.put(slug, count);
            }
        }

        Map<Long, String> ideaSlugs = new HashMap<>();
        for (IdeaEntity i : ideaRepository.findAll()) {
            if (i.getSlug() != null) {
                ideaSlugs.put(i.getId(), i.getSlug());
            }
        }
        Map<String, Long> byIdea = new HashMap<>();
        for (Object[] row : ideaVarianteRepository.countActiveProductsGroupedByIdeaId()) {
            long ideaId = toLong(row[0]);
            long count = toLong(row[1]);
            String slug = ideaSlugs.get(ideaId);
            if (slug != null) {
                byIdea.put(slug, count);
            }
        }
        return new CatalogCountsDto(total, byDesign, byIdea);
    }

    public List<IdeaDto> listIdeasActivas() {
        return toIdeaDtos(ideaRepository.findByActivoTrueOrderByOrdenAscIdAsc(), false);
    }

    public List<IdeaDto> listIdeas() {
        return toIdeaDtos(ideaRepository.findAllByOrderByOrdenAscIdAsc(), false);
    }

    public IdeaDto getIdeaBySlug(String slug) {
        IdeaEntity idea = ideaRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Idea no encontrada: " + slug));
        return toIdeaDtos(List.of(idea), true).getFirst();
    }

    /**
     * @param onlyTop         solo productos marcados como más vendida (destacado)
     * @param designSlug      filtra por {@code disenos.slug} (opcional)
     * @param ideaSlug        filtra por variantes vinculadas a la idea (opcional)
     * @param includeInactive incluye variantes inactivas (uso admin)
     * @param q               búsqueda por modelo (SKU / nombre de diseño)
     * @param largoCm         lado de la base en cm (opcional; intercambiable con ancho)
     * @param anchoCm         lado de la base en cm (opcional; intercambiable con largo)
     * @param altoCm          filtro exacto de alto en cm (opcional)
     * @param page            página 0-based
     * @param size            tamaño de página (1..100); con onlyTop se ignora
     */
    public ProductPageDto listProductCardsPage(boolean onlyTop, String designSlug, String ideaSlug,
                                               boolean includeInactive, String q, int page, int size) {
        return listProductCardsPage(onlyTop, designSlug, ideaSlug, includeInactive, q, null, null, null, page, size);
    }

    public ProductPageDto listProductCardsPage(boolean onlyTop, String designSlug, String ideaSlug,
                                               boolean includeInactive, String q,
                                               BigDecimal largoCm, BigDecimal anchoCm, BigDecimal altoCm,
                                               int page, int size) {
        int safeSize = onlyTop ? TOP_LIMIT : Math.min(100, Math.max(1, size));
        int safePage = onlyTop ? 0 : Math.max(0, page);
        String term = q == null ? "" : q.trim();
        boolean qBlank = term.isBlank();
        BigDecimal largo = normalizeDim(largoCm);
        BigDecimal ancho = normalizeDim(anchoCm);
        BigDecimal alto = normalizeDim(altoCm);

        Long disenoId = null;
        if (designSlug != null && !designSlug.isBlank()) {
            disenoId = designIdFromSlug(designSlug);
            if (disenoId == null) {
                return ProductPageDto.empty(safePage, safeSize);
            }
        }

        // Ideas: paginar IDs (barato) y solo hidratar la página.
        if (ideaSlug != null && !ideaSlug.isBlank()) {
            Set<Long> ordered = ideaVarianteOrder(ideaSlug);
            if (ordered == null || ordered.isEmpty()) {
                return ProductPageDto.empty(safePage, safeSize);
            }
            List<Long> orderedIds = List.copyOf(ordered);
            List<Long> matchedIds = varianteRepository.filterIdsByQuery(
                    orderedIds, includeInactive, disenoId, term, qBlank, largo, ancho, alto);
            Set<Long> matchedSet = new HashSet<>(matchedIds);
            List<Long> orderedMatched = orderedIds.stream().filter(matchedSet::contains).toList();
            if (onlyTop && orderedMatched.size() > TOP_LIMIT) {
                orderedMatched = orderedMatched.subList(0, TOP_LIMIT);
            }
            return pageFromIds(orderedMatched, safePage, safeSize);
        }

        // Catálogo general / TOP: paginación en DB (no carga todas las variantes).
        // onlyTop = solo productos con destacado=true (marcados en admin).
        Page<VarianteEntity> result = varianteRepository.pageByFilters(
                includeInactive,
                onlyTop,
                disenoId,
                term,
                qBlank,
                largo,
                ancho,
                alto,
                PageRequest.of(safePage, safeSize));
        if (onlyTop) {
            return ProductPageDto.of(
                    hydrateCards(result.getContent()), 0, safeSize, result.getTotalElements());
        }
        return ProductPageDto.of(hydrateCards(result.getContent()), safePage, safeSize, result.getTotalElements());
    }

    private static BigDecimal normalizeDim(BigDecimal v) {
        if (v == null) return null;
        if (v.compareTo(BigDecimal.ZERO) <= 0) return null;
        return v.stripTrailingZeros();
    }

    private ProductPageDto pageFromIds(List<Long> orderedIds, int page, int size) {
        long total = orderedIds.size();
        if (total == 0) {
            return ProductPageDto.empty(page, size);
        }
        int from = Math.min(page * size, (int) total);
        if (from >= total) {
            return ProductPageDto.of(List.of(), page, size, total);
        }
        int to = Math.min(from + size, (int) total);
        List<Long> sliceIds = orderedIds.subList(from, to);
        Map<Long, VarianteEntity> byId = byId(varianteRepository.findAllById(sliceIds), VarianteEntity::getId);
        List<VarianteEntity> slice = sliceIds.stream().map(byId::get).filter(Objects::nonNull).toList();
        return ProductPageDto.of(hydrateCards(slice), page, size, total);
    }

    /** Compatibilidad: lista completa (admin / top). Preferir {@link #listProductCardsPage}. */
    public List<ProductCardDto> listProductCards(boolean onlyTop, String designSlug, boolean includeInactive) {
        return listProductCards(onlyTop, designSlug, null, includeInactive);
    }

    public List<ProductCardDto> listProductCards(boolean onlyTop, String designSlug, String ideaSlug,
                                                 boolean includeInactive) {
        ProductPageDto page = listProductCardsPage(onlyTop, designSlug, ideaSlug, includeInactive, null, 0,
                onlyTop ? TOP_LIMIT : 10_000);
        return page.content();
    }

    private Long designIdFromSlug(String designSlug) {
        if (designSlug == null || designSlug.isBlank()) {
            return null;
        }
        return disenoRepository.findBySlug(designSlug.trim()).map(DisenoEntity::getId).orElse(null);
    }

    private Set<Long> ideaVarianteOrder(String ideaSlug) {
        if (ideaSlug == null || ideaSlug.isBlank()) {
            return null;
        }
        Optional<IdeaEntity> idea = ideaRepository.findBySlug(ideaSlug.trim());
        if (idea.isEmpty() || !Boolean.TRUE.equals(idea.get().getActivo())) {
            return Set.of();
        }
        return ideaVarianteRepository.findByIdeaIdOrderByOrdenAscIdAsc(idea.get().getId())
                .stream()
                .map(IdeaVarianteEntity::getVarianteId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** Carga precios/imágenes/diseño/medida solo para la página pedida. */
    private List<ProductCardDto> hydrateCards(List<VarianteEntity> variantes) {
        if (variantes.isEmpty()) {
            return List.of();
        }
        Collection<Long> ids = ids(variantes);
        Set<Long> disenoIds = new HashSet<>();
        Set<Long> medidaIds = new HashSet<>();
        for (VarianteEntity v : variantes) {
            if (v.getDisenoId() != null) {
                disenoIds.add(v.getDisenoId());
            }
            if (v.getMedidaId() != null) {
                medidaIds.add(v.getMedidaId());
            }
        }
        Map<Long, DisenoEntity> disenos = byId(disenoRepository.findAllById(disenoIds), DisenoEntity::getId);
        Map<Long, MedidaEntity> medidas = byId(medidaRepository.findAllById(medidaIds), MedidaEntity::getId);
        Map<Long, List<PrecioEntity>> precios = groupBy(
                precioRepository.findByVarianteIdInOrderByCantidadDesdeAsc(ids),
                PrecioEntity::getVarianteId);

        // Listados de medidas: sin foto por SKU (la galería vive en el diseño).
        return variantes.stream()
                .map(v -> toCard(v, disenos.get(v.getDisenoId()), medidas.get(v.getMedidaId()),
                        precios.getOrDefault(v.getId(), List.of()),
                        List.of(),
                        List.of()))
                .toList();
    }

    public ProductCardDto getProductCardBySku(String sku) {
        VarianteEntity variante = varianteRepository.findBySku(sku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + sku));
        DisenoEntity diseno = variante.getDisenoId() == null
                ? null
                : disenoRepository.findById(variante.getDisenoId()).orElse(null);
        MedidaEntity medida = variante.getMedidaId() == null
                ? null
                : medidaRepository.findById(variante.getMedidaId()).orElse(null);
        return toCard(variante, diseno, medida,
                precioRepository.findByVarianteIdOrderByCantidadDesdeAsc(variante.getId()),
                loadTexturasForDiseno(diseno),
                loadDisenoImagenes(diseno));
    }

    private List<DisenoImagenCardDto> loadDisenoImagenes(DisenoEntity diseno) {
        if (diseno == null || diseno.getId() == null) {
            return List.of();
        }
        List<DisenoImagenCardDto> out = new ArrayList<>();
        try {
            for (DisenoImagenEntity img : disenoImagenRepository
                    .findByDisenoIdInOrderByPrincipalDescOrdenAscIdAsc(List.of(diseno.getId()))) {
                String full = img.getUrl();
                String thumb = img.getUrlThumb();
                if (thumb == null || thumb.isBlank()) {
                    thumb = full;
                }
                if (full != null && !full.isBlank()) {
                    out.add(new DisenoImagenCardDto(full.trim(), thumb == null ? null : thumb.trim()));
                }
            }
        } catch (DataAccessException e) {
            log.warn("No se pudieron cargar fotos del diseño {} para el detalle de producto", diseno.getId(), e);
        }
        if (out.isEmpty()) {
            String full = diseno.getImagenUrl();
            String thumb = diseno.getImagenThumbUrl();
            if (full != null && !full.isBlank()) {
                if (thumb == null || thumb.isBlank()) {
                    thumb = full;
                }
                out.add(new DisenoImagenCardDto(full.trim(), thumb.trim()));
            }
        }
        return out;
    }

    private List<ProductTexturaDto> loadTexturasForDiseno(DisenoEntity diseno) {
        if (diseno == null || diseno.getId() == null) {
            return List.of();
        }
        try {
            List<DisenoTexturaEntity> links =
                    disenoTexturaRepository.findByDisenoIdOrderByOrdenAscIdAsc(diseno.getId());
            if (links.isEmpty()) {
                return List.of();
            }
            Map<Long, TexturaEntity> texturas = byId(
                    texturaRepository.findByIdIn(links.stream().map(DisenoTexturaEntity::getTexturaId).toList()),
                    TexturaEntity::getId);
            List<ProductTexturaDto> out = new ArrayList<>();
            for (DisenoTexturaEntity link : links) {
                if (Boolean.FALSE.equals(link.getColorActivo())) {
                    continue;
                }
                TexturaEntity t = texturas.get(link.getTexturaId());
                if (t == null) {
                    continue;
                }
                String swatch = t.getImagenUrl();
                String swatchThumb = t.getImagenThumbUrl();
                if (swatchThumb == null || swatchThumb.isBlank()) {
                    swatchThumb = swatch;
                }
                String image = link.getUrl();
                if (image != null && image.isBlank()) {
                    image = null;
                }
                String imageThumb = link.getUrlThumb();
                if (imageThumb != null && imageThumb.isBlank()) {
                    imageThumb = null;
                }
                if (imageThumb == null) {
                    imageThumb = image;
                }
                String tipo = t.getTipo() == null || t.getTipo().isBlank() ? "textura" : t.getTipo().trim().toLowerCase();
                if (!"color".equals(tipo)) {
                    tipo = "textura";
                }
                out.add(new ProductTexturaDto(
                        t.getId(),
                        t.getNombre(),
                        t.getSlug(),
                        tipo,
                        swatch,
                        swatchThumb,
                        image,
                        imageThumb,
                        link.getOrden() == null ? 0 : link.getOrden()));
            }
            return out;
        } catch (DataAccessException e) {
            log.warn("No se pudieron cargar texturas del diseño {}", diseno.getId(), e);
            return List.of();
        }
    }

    private ProductCardDto toCard(VarianteEntity variante,
                                  DisenoEntity diseno,
                                  MedidaEntity medida,
                                  List<PrecioEntity> precios,
                                  List<ProductTexturaDto> texturas,
                                  List<DisenoImagenCardDto> disenoImagenes) {
        String dims = dims(medida);
        String nombreDiseno = diseno == null ? "" : nullToEmpty(diseno.getNombre());
        String nombre = dims.isEmpty() ? nombreDiseno : (nombreDiseno + " " + dims).trim();
        // El SKU no tiene foto propia; la galería del diseño va en disenoImagenes (detalle).
        String image = null;
        String imageThumb = null;

        List<ProductVariantDto> variants = precios.isEmpty()
                ? List.of(new ProductVariantDto(dims, "", BigDecimal.ZERO, null))
                : precios.stream()
                        .map(p -> new ProductVariantDto(dims, "", p.getPrecio(),
                                p.getCantidadDesde() == null ? null : String.valueOf(p.getCantidadDesde())))
                        .toList();

        return new ProductCardDto(
                String.valueOf(variante.getId()),
                variante.getSku(),
                nombre.isEmpty() ? nullToEmpty(variante.getSku()) : nombre,
                nombreDiseno,
                diseno == null ? null : diseno.getId(),
                diseno == null ? null : diseno.getSlug(),
                diseno == null ? "" : nullToEmpty(diseno.getDescripcion()),
                Boolean.TRUE.equals(variante.getDestacado()),
                Boolean.TRUE.equals(variante.getActivo()),
                image,
                imageThumb,
                variants,
                texturas == null ? List.of() : texturas,
                disenoImagenes == null ? List.of() : disenoImagenes);
    }

    private List<IdeaDto> toIdeaDtos(List<IdeaEntity> ideas, boolean includeVariantes) {
        if (ideas.isEmpty()) {
            return List.of();
        }
        List<Long> ideaIds = ideas.stream().map(IdeaEntity::getId).toList();
        Map<Long, List<IdeaImagenEntity>> imagenes = groupBy(
                ideaImagenRepository.findByIdeaIdInOrderByPrincipalDescOrdenAscIdAsc(ideaIds),
                IdeaImagenEntity::getIdeaId);

        Map<Long, List<IdeaDto.IdeaVarianteDto>> variantesPorIdea = Map.of();
        if (includeVariantes) {
        List<IdeaVarianteEntity> enlaces = ideaVarianteRepository.findByIdeaIdInOrderByOrdenAscIdAsc(ideaIds);
        Map<Long, List<IdeaVarianteEntity>> porIdea = groupBy(enlaces, IdeaVarianteEntity::getIdeaId);
        Map<Long, String> skus = new HashMap<>();
        List<Long> varianteIds = enlaces.stream().map(IdeaVarianteEntity::getVarianteId).distinct().toList();
        if (!varianteIds.isEmpty()) {
            varianteRepository.findAllById(varianteIds)
                    .forEach(v -> skus.put(v.getId(), v.getSku()));
            }
            variantesPorIdea = new HashMap<>();
            for (Map.Entry<Long, List<IdeaVarianteEntity>> e : porIdea.entrySet()) {
                variantesPorIdea.put(e.getKey(), e.getValue().stream()
                        .map(iv -> {
                            String full = blankToNull(iv.getUrl());
                            String thumb = blankToNull(iv.getUrlThumb());
                            if (thumb == null) {
                                thumb = full;
                            }
                            return new IdeaDto.IdeaVarianteDto(
                                    iv.getId(),
                                    iv.getVarianteId(),
                                    skus.get(iv.getVarianteId()),
                                    iv.getTitulo(),
                                    iv.getDescripcion(),
                                    full,
                                    thumb,
                                    iv.getOrden());
                        })
                        .toList());
            }
        }

        Map<Long, List<IdeaDto.IdeaVarianteDto>> variantesFinal = variantesPorIdea;
        return ideas.stream().map(idea -> {
            List<IdeaImagenEntity> imgs = imagenes.getOrDefault(idea.getId(), List.of());
            String full = null;
            String thumb = null;
            if (!imgs.isEmpty()) {
                IdeaImagenEntity first = imgs.getFirst();
                full = first.getUrl();
                thumb = first.getUrlThumb();
                if (thumb == null || thumb.isBlank()) {
                    thumb = full;
                }
            }
            return new IdeaDto(
                    idea.getId(), idea.getNombre(), idea.getSlug(), idea.getDescripcion(),
                    idea.getActivo(),
                    Boolean.TRUE.equals(idea.getDestacado()),
                    idea.getOrden(),
                    // Listados usan miniatura; detalle puede pedir full vía imagenes[0]
                    thumb,
                    imgs.stream().map(IdeaImagenEntity::getUrl).toList(),
                    variantesFinal.getOrDefault(idea.getId(), List.of()));
        }).toList();
    }

    /** "30×20×10" a partir de la medida. Sin alto (Varios) queda "30×20". */
    public static String dims(MedidaEntity medida) {
        if (medida == null) {
            return "";
        }
        List<String> partes = new ArrayList<>(3);
        if (medida.getLargo() != null) {
            partes.add(medida.getLargo().stripTrailingZeros().toPlainString());
        }
        if (medida.getAncho() != null) {
            partes.add(medida.getAncho().stripTrailingZeros().toPlainString());
        }
        if (medida.getAlto() != null) {
            partes.add(medida.getAlto().stripTrailingZeros().toPlainString());
        }
        return String.join("×", partes);
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private static List<Long> ids(List<VarianteEntity> variantes) {
        return variantes.stream().map(VarianteEntity::getId).toList();
    }

    private static <T> Map<Long, T> byId(List<T> items, Function<T, Long> key) {
        Map<Long, T> map = new HashMap<>();
        items.forEach(i -> map.put(key.apply(i), i));
        return map;
    }

    private static <T> Map<Long, List<T>> groupBy(List<T> items, Function<T, Long> key) {
        Map<Long, List<T>> map = new HashMap<>();
        items.forEach(i -> map.computeIfAbsent(key.apply(i), k -> new ArrayList<>()).add(i));
        return map;
    }
}
