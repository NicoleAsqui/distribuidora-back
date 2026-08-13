package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.IdeaDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.ProductCardDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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

    public List<IdeaDto> listIdeasActivas() {
        return toIdeaDtos(ideaRepository.findByActivoTrueOrderByOrdenAscIdAsc());
    }

    public List<IdeaDto> listIdeas() {
        return toIdeaDtos(ideaRepository.findAllByOrderByOrdenAscIdAsc());
    }

    public IdeaDto getIdeaBySlug(String slug) {
        IdeaEntity idea = ideaRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Idea no encontrada: " + slug));
        return toIdeaDtos(List.of(idea)).getFirst();
    }

    /**
     * @param onlyTop        limita a las primeras {@value #TOP_LIMIT} por orden de diseño
     * @param designSlug     filtra por {@code disenos.slug} (opcional)
     * @param includeInactive incluye variantes inactivas (uso admin)
     */
    public List<ProductCardDto> listProductCards(boolean onlyTop, String designSlug, boolean includeInactive) {
        Long designFilterId = null;
        if (designSlug != null && !designSlug.isBlank()) {
            Optional<DisenoEntity> diseno = disenoRepository.findBySlug(designSlug.trim());
            if (diseno.isEmpty()) {
                return List.of();
            }
            designFilterId = diseno.get().getId();
        }

        List<VarianteEntity> variantes = includeInactive
                ? varianteRepository.findAll()
                : varianteRepository.findByActivoTrue();
        if (designFilterId != null) {
            Long target = designFilterId;
            variantes = variantes.stream().filter(v -> target.equals(v.getDisenoId())).toList();
        }
        if (variantes.isEmpty()) {
            return List.of();
        }

        Map<Long, DisenoEntity> disenos = byId(disenoRepository.findAll(), DisenoEntity::getId);
        Map<Long, MedidaEntity> medidas = byId(medidaRepository.findAll(), MedidaEntity::getId);
        Map<Long, List<PrecioEntity>> precios = groupBy(
                precioRepository.findByVarianteIdInOrderByCantidadDesdeAsc(ids(variantes)),
                PrecioEntity::getVarianteId);
        Map<Long, List<VarianteImagenEntity>> imagenes = groupBy(
                varianteImagenRepository.findByVarianteIdInOrderByPrincipalDescOrdenAscIdAsc(ids(variantes)),
                VarianteImagenEntity::getVarianteId);

        List<ProductCardDto> cards = variantes.stream()
                .sorted(Comparator
                        .comparingInt((VarianteEntity v) -> ordenDe(disenos.get(v.getDisenoId())))
                        .thenComparing(VarianteEntity::getId))
                .map(v -> toCard(v, disenos.get(v.getDisenoId()), medidas.get(v.getMedidaId()),
                        precios.getOrDefault(v.getId(), List.of()),
                        imagenes.getOrDefault(v.getId(), List.of())))
                .collect(Collectors.toList());

        if (onlyTop && cards.size() > TOP_LIMIT) {
            return List.copyOf(cards.subList(0, TOP_LIMIT));
        }
        return cards;
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
        String image = imagenes.isEmpty() ? null : imagenes.getFirst().getUrl();

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
                image,
                variants);
    }

    private List<IdeaDto> toIdeaDtos(List<IdeaEntity> ideas) {
        if (ideas.isEmpty()) {
            return List.of();
        }
        List<Long> ideaIds = ideas.stream().map(IdeaEntity::getId).toList();
        Map<Long, List<IdeaImagenEntity>> imagenes = groupBy(
                ideaImagenRepository.findByIdeaIdInOrderByPrincipalDescOrdenAscIdAsc(ideaIds),
                IdeaImagenEntity::getIdeaId);
        List<IdeaVarianteEntity> enlaces = ideaVarianteRepository.findByIdeaIdInOrderByOrdenAscIdAsc(ideaIds);
        Map<Long, List<IdeaVarianteEntity>> porIdea = groupBy(enlaces, IdeaVarianteEntity::getIdeaId);
        Map<Long, String> skus = new HashMap<>();
        List<Long> varianteIds = enlaces.stream().map(IdeaVarianteEntity::getVarianteId).distinct().toList();
        if (!varianteIds.isEmpty()) {
            varianteRepository.findAllById(varianteIds)
                    .forEach(v -> skus.put(v.getId(), v.getSku()));
        }

        return ideas.stream().map(idea -> {
            List<IdeaImagenEntity> imgs = imagenes.getOrDefault(idea.getId(), List.of());
            List<IdeaDto.IdeaVarianteDto> variantes = porIdea.getOrDefault(idea.getId(), List.of()).stream()
                    .map(iv -> new IdeaDto.IdeaVarianteDto(iv.getId(), iv.getVarianteId(),
                            skus.get(iv.getVarianteId()), iv.getTitulo(), iv.getDescripcion(), iv.getOrden()))
                    .toList();
            return new IdeaDto(
                    idea.getId(), idea.getNombre(), idea.getSlug(), idea.getDescripcion(),
                    idea.getActivo(), idea.getOrden(),
                    imgs.isEmpty() ? null : imgs.getFirst().getUrl(),
                    imgs.stream().map(IdeaImagenEntity::getUrl).toList(),
                    variantes);
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

    private static int ordenDe(DisenoEntity diseno) {
        if (diseno == null || diseno.getOrden() == null) {
            return Integer.MAX_VALUE;
        }
        return diseno.getOrden();
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
