package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.CatalogCountsDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.IdeaDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.ProductCardDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.ProductPageDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.ProductVariantDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.DisenoEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaImagenEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaVarianteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MedidaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.PapelForroEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.PrecioEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteImagenEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VinilEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.DisenoRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.IdeaImagenRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.IdeaRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.IdeaVarianteRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.MedidaRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.PapelForroRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.PrecioRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VarianteImagenRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VarianteRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VinilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
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

    /** Cuántas tarjetas devuelve {@code ?top=true}. */
    private static final int TOP_LIMIT = 8;
    /** Un diseño con orden 1..3 se marca como destacado. */
    private static final int TOP_ORDEN_MAX = 3;

    private final DisenoRepository disenoRepository;
    private final MedidaRepository medidaRepository;
    private final VarianteRepository varianteRepository;
    private final PrecioRepository precioRepository;
    private final VarianteImagenRepository varianteImagenRepository;
    private final PapelForroRepository papelForroRepository;
    private final VinilRepository vinilRepository;
    private final IdeaRepository ideaRepository;
    private final IdeaImagenRepository ideaImagenRepository;
    private final IdeaVarianteRepository ideaVarianteRepository;

    public List<DisenoEntity> listDisenosActivos() {
        return disenoRepository.findByActivoTrueOrderByOrdenAscIdAsc();
    }


    public List<PapelForroEntity> listPapelesForroActivos() {
        return papelForroRepository.findByActivoTrueOrderByOrdenAscNombreAsc();
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
            Long disenoId = (Long) row[0];
            Long count = (Long) row[1];
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
            Long ideaId = (Long) row[0];
            Long count = (Long) row[1];
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
     * @param onlyTop         limita a las primeras {@value #TOP_LIMIT} por orden de diseño
     * @param designSlug      filtra por {@code disenos.slug} (opcional)
     * @param ideaSlug        filtra por variantes vinculadas a la idea (opcional)
     * @param includeInactive incluye variantes inactivas (uso admin)
     * @param q               búsqueda libre por SKU / diseño (opcional)
     * @param page            página 0-based
     * @param size            tamaño de página (1..100); con onlyTop se ignora
     */
    public ProductPageDto listProductCardsPage(boolean onlyTop, String designSlug, String ideaSlug,
                                               boolean includeInactive, String q, int page, int size) {
        int safeSize = onlyTop ? TOP_LIMIT : Math.min(100, Math.max(1, size));
        int safePage = onlyTop ? 0 : Math.max(0, page);
        String term = q == null ? "" : q.trim();
        boolean qBlank = term.isBlank();

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
                    orderedIds, includeInactive, disenoId, term, qBlank);
            Set<Long> matchedSet = new HashSet<>(matchedIds);
            List<Long> orderedMatched = orderedIds.stream().filter(matchedSet::contains).toList();
            if (onlyTop && orderedMatched.size() > TOP_LIMIT) {
                orderedMatched = orderedMatched.subList(0, TOP_LIMIT);
            }
            return pageFromIds(orderedMatched, safePage, safeSize);
        }

        // Catálogo general: paginación en DB (no carga todas las variantes).
        Page<VarianteEntity> result = varianteRepository.pageByFilters(
                includeInactive,
                disenoId,
                term,
                qBlank,
                PageRequest.of(safePage, safeSize));
        if (onlyTop) {
            List<VarianteEntity> content = result.getContent();
            if (content.size() > TOP_LIMIT) {
                content = content.subList(0, TOP_LIMIT);
            }
            return ProductPageDto.of(hydrateCards(content), 0, TOP_LIMIT, content.size());
        }
        return ProductPageDto.of(hydrateCards(result.getContent()), safePage, safeSize, result.getTotalElements());
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
        Map<Long, List<VarianteImagenEntity>> imagenes = groupBy(
                varianteImagenRepository.findByVarianteIdInOrderByPrincipalDescOrdenAscIdAsc(ids),
                VarianteImagenEntity::getVarianteId);

        return variantes.stream()
                .map(v -> toCard(v, disenos.get(v.getDisenoId()), medidas.get(v.getMedidaId()),
                        precios.getOrDefault(v.getId(), List.of()),
                        imagenes.getOrDefault(v.getId(), List.of())))
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
                varianteImagenRepository.findByVarianteIdOrderByPrincipalDescOrdenAscIdAsc(variante.getId()));
    }

    private ProductCardDto toCard(VarianteEntity variante,
                                  DisenoEntity diseno,
                                  MedidaEntity medida,
                                  List<PrecioEntity> precios,
                                  List<VarianteImagenEntity> imagenes) {
        String dims = dims(medida);
        String nombreDiseno = diseno == null ? "" : nullToEmpty(diseno.getNombre());
        String nombre = dims.isEmpty() ? nombreDiseno : (nombreDiseno + " " + dims).trim();
        String image = null;
        String imageThumb = null;
        if (!imagenes.isEmpty()) {
            VarianteImagenEntity principal = imagenes.getFirst();
            image = principal.getUrl();
            imageThumb = principal.getUrlThumb();
            if (imageThumb == null || imageThumb.isBlank()) {
                imageThumb = image;
            }
        }

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
                diseno != null && esDestacado(diseno),
                Boolean.TRUE.equals(variante.getActivo()),
                image,
                imageThumb,
                variants);
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
                        .map(iv -> new IdeaDto.IdeaVarianteDto(iv.getId(), iv.getVarianteId(),
                                skus.get(iv.getVarianteId()), iv.getTitulo(), iv.getDescripcion(), iv.getOrden()))
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
                    idea.getActivo(), idea.getOrden(),
                    // Listados usan miniatura; detalle puede pedir full vía imagenes[0]
                    thumb,
                    imgs.stream().map(IdeaImagenEntity::getUrl).toList(),
                    variantesFinal.getOrDefault(idea.getId(), List.of()));
        }).toList();
    }

    /** "30×20×10" a partir de la medida; cadena vacía si no hay medida. */
    public static String dims(MedidaEntity medida) {
        if (medida == null) {
            return "";
        }
        List<String> partes = new ArrayList<>(3);
        for (BigDecimal valor : List.of(
                nvl(medida.getLargo()), nvl(medida.getAncho()), nvl(medida.getAlto()))) {
            partes.add(valor.stripTrailingZeros().toPlainString());
        }
        return String.join("×", partes);
    }

    private static boolean esDestacado(DisenoEntity diseno) {
        Integer orden = diseno.getOrden();
        return orden != null && orden >= 1 && orden <= TOP_ORDEN_MAX;
    }

    private static BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
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
