package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.IdeaAdminDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.ProductoAdminDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.AtributoEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.AtributoValorEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.ComponenteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.ConfiguracionInteriorDetalleEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.ConfiguracionInteriorEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.CostoComponenteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.DisenoEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.GramajeEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaImagenEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaVarianteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MaterialEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MaterialImagenEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MedidaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.PrecioEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.TipoMaterialEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.TagEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteAtributoEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteComponenteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteConfiguracionEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteImagenEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteTagEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.PapelForroEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VinilEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.AtributoRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.AtributoValorRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.ComponenteRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.ConfiguracionInteriorDetalleRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.ConfiguracionInteriorRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.CostoComponenteRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.DisenoRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.GramajeRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.IdeaImagenRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.IdeaRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.IdeaVarianteRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.MaterialImagenRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.MaterialRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.MedidaRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.PapelForroRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.PrecioRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.TagRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.TipoMaterialRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VarianteAtributoRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VarianteComponenteRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VarianteConfiguracionRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VarianteImagenRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VarianteRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VarianteTagRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VinilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * CRUD de administración del catálogo nuevo. Las entidades son planas (FKs como Long),
 * así que se usan directamente como cuerpo JSON de entrada/salida.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class NewCatalogAdminService {

    private final DisenoRepository disenoRepository;
    private final MedidaRepository medidaRepository;
    private final VarianteRepository varianteRepository;
    private final VarianteImagenRepository varianteImagenRepository;
    private final PrecioRepository precioRepository;
    private final TipoMaterialRepository tipoMaterialRepository;
    private final MaterialRepository materialRepository;
    private final MaterialImagenRepository materialImagenRepository;
    private final GramajeRepository gramajeRepository;
    private final PapelForroRepository papelForroRepository;
    private final VinilRepository vinilRepository;
    private final ComponenteRepository componenteRepository;
    private final VarianteComponenteRepository varianteComponenteRepository;
    private final ConfiguracionInteriorRepository configuracionInteriorRepository;
    private final ConfiguracionInteriorDetalleRepository configuracionInteriorDetalleRepository;
    private final VarianteConfiguracionRepository varianteConfiguracionRepository;
    private final AtributoRepository atributoRepository;
    private final AtributoValorRepository atributoValorRepository;
    private final VarianteAtributoRepository varianteAtributoRepository;
    private final IdeaRepository ideaRepository;
    private final IdeaImagenRepository ideaImagenRepository;
    private final IdeaVarianteRepository ideaVarianteRepository;
    private final TagRepository tagRepository;
    private final VarianteTagRepository varianteTagRepository;
    private final CostoComponenteRepository costoComponenteRepository;

    // ------------------------------------------------------------------ diseños

    @Transactional(readOnly = true)
    public List<DisenoEntity> listDisenos() {
        return disenoRepository.findAllByOrderByOrdenAscIdAsc();
    }

    @Transactional(readOnly = true)
    public DisenoEntity getDiseno(Long id) {
        return find(disenoRepository, id, "Diseño");
    }

    public DisenoEntity createDiseno(DisenoEntity body) {
        DisenoEntity e = new DisenoEntity();
        applyDiseno(e, body);
        return disenoRepository.save(e);
    }

    public DisenoEntity updateDiseno(Long id, DisenoEntity body) {
        DisenoEntity e = find(disenoRepository, id, "Diseño");
        applyDiseno(e, body);
        return disenoRepository.save(e);
    }

    public void deleteDiseno(Long id) {
        delete(disenoRepository, id, "Diseño");
    }

    private void applyDiseno(DisenoEntity e, DisenoEntity body) {
        e.setNombre(required(body.getNombre(), "nombre"));
        e.setSlug(blank(body.getSlug()) ? slugify(e.getNombre()) : body.getSlug().trim());
        e.setDescripcion(body.getDescripcion());
        e.setActivo(nvl(body.getActivo(), Boolean.TRUE));
        e.setOrden(nvl(body.getOrden(), 0));
        e.setSeccion(normalizeDisenoSeccion(body.getSeccion()));
    }

    /** acetato | cartulina | mdf | carton */
    private static String normalizeDisenoSeccion(String raw) {
        String s = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        return switch (s) {
            case "acetato", "cartulina", "mdf", "carton" -> s;
            case "" -> "cartulina";
            default -> throw badRequest("Sección inválida. Usa: acetato, cartulina, mdf o carton");
        };
    }

    // ------------------------------------------------------------------ medidas

    @Transactional(readOnly = true)
    public List<MedidaEntity> listMedidas() {
        return medidaRepository.findAllByOrderByLargoAscAnchoAscAltoAsc();
    }

    @Transactional(readOnly = true)
    public MedidaEntity getMedida(Long id) {
        return find(medidaRepository, id, "Medida");
    }

    public MedidaEntity createMedida(MedidaEntity body) {
        MedidaEntity e = new MedidaEntity();
        applyMedida(e, body);
        return medidaRepository.save(e);
    }

    public MedidaEntity updateMedida(Long id, MedidaEntity body) {
        MedidaEntity e = find(medidaRepository, id, "Medida");
        applyMedida(e, body);
        return medidaRepository.save(e);
    }

    public void deleteMedida(Long id) {
        delete(medidaRepository, id, "Medida");
    }

    private void applyMedida(MedidaEntity e, MedidaEntity body) {
        e.setLargo(requiredNumber(body.getLargo(), "largo"));
        e.setAncho(requiredNumber(body.getAncho(), "ancho"));
        e.setAlto(requiredNumber(body.getAlto(), "alto"));
        e.setUnidad(blank(body.getUnidad()) ? "cm" : body.getUnidad().trim());
    }

    // ---------------------------------------------------------------- variantes

    @Transactional(readOnly = true)
    public List<VarianteEntity> listVariantes(Long disenoId) {
        return disenoId == null
                ? varianteRepository.findAll()
                : varianteRepository.findByDisenoIdOrderByIdAsc(disenoId);
    }

    @Transactional(readOnly = true)
    public VarianteEntity getVariante(Long id) {
        return find(varianteRepository, id, "Variante");
    }

    public VarianteEntity createVariante(VarianteEntity body) {
        VarianteEntity e = new VarianteEntity();
        applyVariante(e, body);
        return varianteRepository.save(e);
    }

    public VarianteEntity updateVariante(Long id, VarianteEntity body) {
        VarianteEntity e = find(varianteRepository, id, "Variante");
        applyVariante(e, body);
        return varianteRepository.save(e);
    }

    public void deleteVariante(Long id) {
        delete(varianteRepository, id, "Variante");
    }

    private void applyVariante(VarianteEntity e, VarianteEntity body) {
        requireFk(disenoRepository, body.getDisenoId(), "disenoId");
        requireFk(medidaRepository, body.getMedidaId(), "medidaId");
        e.setDisenoId(body.getDisenoId());
        e.setMedidaId(body.getMedidaId());
        String sku = body.getSku() == null ? "" : body.getSku().trim();
        if (sku.isBlank()) {
            if (e.getId() == null || e.getSku() == null || e.getSku().isBlank()) {
                sku = allocateNextDgSku();
            } else {
                sku = e.getSku();
            }
        }
        e.setSku(sku);
        e.setActivo(nvl(body.getActivo(), Boolean.TRUE));
    }

    /** Siguiente SKU del patrón DG-0001, DG-0002, … */
    public String allocateNextDgSku() {
        int next = 1;
        String latest = varianteRepository.findLatestDgSku().orElse(null);
        if (latest != null && latest.length() > 3) {
            try {
                next = Integer.parseInt(latest.substring(3)) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }
        String candidate;
        do {
            candidate = String.format(Locale.ROOT, "DG-%04d", next);
            next++;
        } while (varianteRepository.findBySku(candidate).isPresent());
        return candidate;
    }

    // ----------------------------------------------------------- producto ficha

    @Transactional(readOnly = true)
    public ProductoAdminDto getProducto(Long id) {
        VarianteEntity v = find(varianteRepository, id, "Producto");
        return toProductoDto(v);
    }

    public ProductoAdminDto createProducto(ProductoAdminDto body) {
        VarianteEntity v = new VarianteEntity();
        applyVarianteFromProducto(v, body, true);
        v = varianteRepository.save(v);
        syncProductoChildren(v.getId(), body);
        return toProductoDto(v);
    }

    public ProductoAdminDto updateProducto(Long id, ProductoAdminDto body) {
        VarianteEntity v = find(varianteRepository, id, "Producto");
        applyVarianteFromProducto(v, body, false);
        v = varianteRepository.save(v);
        syncProductoChildren(v.getId(), body);
        return toProductoDto(v);
    }

    private void applyVarianteFromProducto(VarianteEntity e, ProductoAdminDto body, boolean creating) {
        requireFk(disenoRepository, body.disenoId(), "disenoId");
        Long medidaId = resolveMedidaId(body);
        e.setDisenoId(body.disenoId());
        e.setMedidaId(medidaId);
        if (creating) {
            String sku = body.sku() == null ? "" : body.sku().trim();
            e.setSku(sku.isBlank() ? allocateNextDgSku() : sku);
        } else if (e.getSku() == null || e.getSku().isBlank()) {
            e.setSku(allocateNextDgSku());
        }
        // En edición el SKU no se cambia (mantiene el histórico).
        e.setActivo(nvl(body.activo(), Boolean.TRUE));
    }

    /** Medida desde largo×ancho×alto (preferido) o medidaId legado. */
    private Long resolveMedidaId(ProductoAdminDto body) {
        if (body.largo() != null && body.ancho() != null && body.alto() != null) {
            return findOrCreateMedida(body.largo(), body.ancho(), body.alto(), body.unidad()).getId();
        }
        if (body.medidaId() != null) {
            requireFk(medidaRepository, body.medidaId(), "medidaId");
            return body.medidaId();
        }
        throw badRequest("Indica largo, ancho y alto de la caja (cm)");
    }

    private MedidaEntity findOrCreateMedida(BigDecimal largo, BigDecimal ancho, BigDecimal alto, String unidad) {
        if (largo == null || ancho == null || alto == null) {
            throw badRequest("largo, ancho y alto son obligatorios");
        }
        if (largo.signum() <= 0 || ancho.signum() <= 0 || alto.signum() <= 0) {
            throw badRequest("largo, ancho y alto deben ser mayores a 0");
        }
        BigDecimal l = largo.setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal a = ancho.setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal h = alto.setScale(2, java.math.RoundingMode.HALF_UP);
        String u = blank(unidad) ? "cm" : unidad.trim();
        return medidaRepository.findFirstByLargoAndAnchoAndAltoAndUnidad(l, a, h, u)
                .orElseGet(() -> {
                    MedidaEntity e = new MedidaEntity();
                    e.setLargo(l);
                    e.setAncho(a);
                    e.setAlto(h);
                    e.setUnidad(u);
                    return medidaRepository.save(e);
                });
    }

    private void syncProductoChildren(Long varianteId, ProductoAdminDto body) {
        syncPrecios(varianteId, body.precios() == null ? List.of() : body.precios());
        syncImagenes(varianteId, body.imagenes() == null ? List.of() : body.imagenes());
        syncComponentes(varianteId, body.componentes() == null ? List.of() : body.componentes());
        syncTags(varianteId, body.tagIds() == null ? List.of() : body.tagIds());
    }

    private void syncPrecios(Long varianteId, List<ProductoAdminDto.PrecioLine> lines) {
        precioRepository.deleteByVarianteId(varianteId);
        precioRepository.flush();
        Set<Integer> seen = new HashSet<>();
        for (ProductoAdminDto.PrecioLine line : lines) {
            if (line == null || line.cantidadDesde() == null || line.precio() == null) {
                continue;
            }
            if (line.cantidadDesde() < 1) {
                throw badRequest("cantidadDesde debe ser mayor a 0");
            }
            if (!seen.add(line.cantidadDesde())) {
                throw badRequest("Cantidad desde duplicada: " + line.cantidadDesde());
            }
            if (line.precio().signum() < 0) {
                throw badRequest("precio no puede ser negativo");
            }
            PrecioEntity e = new PrecioEntity();
            e.setVarianteId(varianteId);
            e.setCantidadDesde(line.cantidadDesde());
            e.setPrecio(line.precio());
            precioRepository.save(e);
        }
    }

    private void syncImagenes(Long varianteId, List<ProductoAdminDto.ImagenLine> lines) {
        varianteImagenRepository.deleteByVarianteId(varianteId);
        varianteImagenRepository.flush();
        int i = 0;
        for (ProductoAdminDto.ImagenLine line : lines) {
            if (line == null || line.url() == null || line.url().isBlank()) {
                continue;
            }
            VarianteImagenEntity e = new VarianteImagenEntity();
            e.setVarianteId(varianteId);
            e.setUrl(line.url().trim());
            String thumb = line.urlThumb();
            e.setUrlThumb(thumb == null || thumb.isBlank() ? e.getUrl() : thumb.trim());
            e.setPrincipal(nvl(line.principal(), i == 0));
            e.setOrden(nvl(line.orden(), i));
            varianteImagenRepository.save(e);
            i++;
        }
    }

    private void syncComponentes(Long varianteId, List<ProductoAdminDto.ComponenteLine> lines) {
        varianteComponenteRepository.deleteByVarianteId(varianteId);
        varianteComponenteRepository.flush();
        for (ProductoAdminDto.ComponenteLine line : lines) {
            if (line == null || line.componenteId() == null || line.materialId() == null) {
                continue;
            }
            requireFk(componenteRepository, line.componenteId(), "componenteId");
            requireFk(materialRepository, line.materialId(), "materialId");
            if (line.gramajeId() != null) {
                requireFk(gramajeRepository, line.gramajeId(), "gramajeId");
            }
            VarianteComponenteEntity e = new VarianteComponenteEntity();
            e.setVarianteId(varianteId);
            e.setComponenteId(line.componenteId());
            e.setMaterialId(line.materialId());
            e.setGramajeId(line.gramajeId());
            e.setCantidad(line.cantidad() == null ? BigDecimal.ONE : line.cantidad());
            varianteComponenteRepository.save(e);
        }
    }

    private void syncTags(Long varianteId, List<Long> tagIds) {
        varianteTagRepository.deleteByVarianteId(varianteId);
        varianteTagRepository.flush();
        Set<Long> seen = new HashSet<>();
        for (Long tagId : tagIds) {
            if (tagId == null || !seen.add(tagId)) {
                continue;
            }
            requireFk(tagRepository, tagId, "tagId");
            VarianteTagEntity e = new VarianteTagEntity();
            e.setVarianteId(varianteId);
            e.setTagId(tagId);
            varianteTagRepository.save(e);
        }
    }

    private ProductoAdminDto toProductoDto(VarianteEntity v) {
        Long id = v.getId();
        List<ProductoAdminDto.PrecioLine> precios = new ArrayList<>();
        for (PrecioEntity p : precioRepository.findByVarianteIdOrderByCantidadDesdeAsc(id)) {
            precios.add(new ProductoAdminDto.PrecioLine(p.getId(), p.getCantidadDesde(), p.getPrecio()));
        }
        List<ProductoAdminDto.ImagenLine> imagenes = new ArrayList<>();
        for (VarianteImagenEntity img : varianteImagenRepository
                .findByVarianteIdOrderByPrincipalDescOrdenAscIdAsc(id)) {
            imagenes.add(new ProductoAdminDto.ImagenLine(
                    img.getId(), img.getUrl(), img.getUrlThumb(), img.getPrincipal(), img.getOrden()));
        }
        List<ProductoAdminDto.ComponenteLine> componentes = new ArrayList<>();
        for (VarianteComponenteEntity c : varianteComponenteRepository.findByVarianteIdOrderByIdAsc(id)) {
            componentes.add(new ProductoAdminDto.ComponenteLine(
                    c.getId(), c.getComponenteId(), c.getMaterialId(), c.getGramajeId(), c.getCantidad()));
        }
        List<Long> tagIds = new ArrayList<>();
        for (VarianteTagEntity t : varianteTagRepository.findByVarianteId(id)) {
            tagIds.add(t.getTagId());
        }
        MedidaEntity medida = v.getMedidaId() == null
                ? null
                : medidaRepository.findById(v.getMedidaId()).orElse(null);
        return new ProductoAdminDto(
                id,
                v.getDisenoId(),
                v.getMedidaId(),
                medida == null ? null : medida.getLargo(),
                medida == null ? null : medida.getAncho(),
                medida == null ? null : medida.getAlto(),
                medida == null ? "cm" : medida.getUnidad(),
                v.getSku(),
                v.getActivo(),
                precios,
                imagenes,
                componentes,
                tagIds
        );
    }

    // -------------------------------------------------------- variante imágenes

    @Transactional(readOnly = true)
    public List<VarianteImagenEntity> listVarianteImagenes(Long varianteId) {
        return varianteId == null
                ? varianteImagenRepository.findAll()
                : varianteImagenRepository.findByVarianteIdOrderByPrincipalDescOrdenAscIdAsc(varianteId);
    }

    public VarianteImagenEntity createVarianteImagen(VarianteImagenEntity body) {
        VarianteImagenEntity e = new VarianteImagenEntity();
        applyVarianteImagen(e, body);
        return varianteImagenRepository.save(e);
    }

    public VarianteImagenEntity updateVarianteImagen(Long id, VarianteImagenEntity body) {
        VarianteImagenEntity e = find(varianteImagenRepository, id, "Imagen de variante");
        applyVarianteImagen(e, body);
        return varianteImagenRepository.save(e);
    }

    public void deleteVarianteImagen(Long id) {
        delete(varianteImagenRepository, id, "Imagen de variante");
    }

    private void applyVarianteImagen(VarianteImagenEntity e, VarianteImagenEntity body) {
        requireFk(varianteRepository, body.getVarianteId(), "varianteId");
        e.setVarianteId(body.getVarianteId());
        e.setUrl(required(body.getUrl(), "url"));
        String thumb = body.getUrlThumb();
        e.setUrlThumb(thumb == null || thumb.isBlank() ? e.getUrl() : thumb.trim());
        e.setPrincipal(nvl(body.getPrincipal(), Boolean.FALSE));
        e.setOrden(nvl(body.getOrden(), 0));
    }

    // ------------------------------------------------------------------ precios

    @Transactional(readOnly = true)
    public List<PrecioEntity> listPrecios(Long varianteId) {
        return varianteId == null
                ? precioRepository.findAll()
                : precioRepository.findByVarianteIdOrderByCantidadDesdeAsc(varianteId);
    }

    @Transactional(readOnly = true)
    public PrecioEntity getPrecio(Long id) {
        return find(precioRepository, id, "Precio");
    }

    public PrecioEntity createPrecio(PrecioEntity body) {
        PrecioEntity e = new PrecioEntity();
        applyPrecio(e, body);
        return precioRepository.save(e);
    }

    public PrecioEntity updatePrecio(Long id, PrecioEntity body) {
        PrecioEntity e = find(precioRepository, id, "Precio");
        applyPrecio(e, body);
        return precioRepository.save(e);
    }

    public void deletePrecio(Long id) {
        delete(precioRepository, id, "Precio");
    }

    private void applyPrecio(PrecioEntity e, PrecioEntity body) {
        requireFk(varianteRepository, body.getVarianteId(), "varianteId");
        if (body.getCantidadDesde() == null || body.getCantidadDesde() < 1) {
            throw badRequest("cantidadDesde debe ser mayor a 0");
        }
        BigDecimal precio = requiredNumber(body.getPrecio(), "precio");
        if (precio.signum() < 0) {
            throw badRequest("precio no puede ser negativo");
        }
        e.setVarianteId(body.getVarianteId());
        e.setCantidadDesde(body.getCantidadDesde());
        e.setPrecio(precio);
    }

    // ----------------------------------------------------------- tipos material

    @Transactional(readOnly = true)
    public List<TipoMaterialEntity> listTiposMaterial() {
        return tipoMaterialRepository.findAllByOrderByNombreAsc();
    }

    public TipoMaterialEntity createTipoMaterial(TipoMaterialEntity body) {
        TipoMaterialEntity e = new TipoMaterialEntity();
        e.setNombre(required(body.getNombre(), "nombre"));
        return tipoMaterialRepository.save(e);
    }

    public TipoMaterialEntity updateTipoMaterial(Long id, TipoMaterialEntity body) {
        TipoMaterialEntity e = find(tipoMaterialRepository, id, "Tipo de material");
        e.setNombre(required(body.getNombre(), "nombre"));
        return tipoMaterialRepository.save(e);
    }

    public void deleteTipoMaterial(Long id) {
        delete(tipoMaterialRepository, id, "Tipo de material");
    }

    // --------------------------------------------------------------- materiales

    @Transactional(readOnly = true)
    public List<MaterialEntity> listMateriales(Long tipoMaterialId) {
        return tipoMaterialId == null
                ? materialRepository.findAllByOrderByOrdenAscNombreAsc()
                : materialRepository.findByTipoMaterialIdOrderByOrdenAscNombreAsc(tipoMaterialId);
    }

    @Transactional(readOnly = true)
    public MaterialEntity getMaterial(Long id) {
        return find(materialRepository, id, "Material");
    }

    public MaterialEntity createMaterial(MaterialEntity body) {
        MaterialEntity e = new MaterialEntity();
        applyMaterial(e, body);
        return materialRepository.save(e);
    }

    public MaterialEntity updateMaterial(Long id, MaterialEntity body) {
        MaterialEntity e = find(materialRepository, id, "Material");
        applyMaterial(e, body);
        return materialRepository.save(e);
    }

    public void deleteMaterial(Long id) {
        delete(materialRepository, id, "Material");
    }

    private void applyMaterial(MaterialEntity e, MaterialEntity body) {
        requireFk(tipoMaterialRepository, body.getTipoMaterialId(), "tipoMaterialId");
        e.setTipoMaterialId(body.getTipoMaterialId());
        e.setNombre(required(body.getNombre(), "nombre"));
        e.setActivo(nvl(body.getActivo(), Boolean.TRUE));
        e.setOrden(nvl(body.getOrden(), 0));
    }

    // -------------------------------------------------------- material imágenes

    @Transactional(readOnly = true)
    public List<MaterialImagenEntity> listMaterialImagenes(Long materialId) {
        return materialId == null
                ? materialImagenRepository.findAll()
                : materialImagenRepository.findByMaterialIdOrderByPrincipalDescOrdenAscIdAsc(materialId);
    }

    public MaterialImagenEntity createMaterialImagen(MaterialImagenEntity body) {
        MaterialImagenEntity e = new MaterialImagenEntity();
        applyMaterialImagen(e, body);
        return materialImagenRepository.save(e);
    }

    public MaterialImagenEntity updateMaterialImagen(Long id, MaterialImagenEntity body) {
        MaterialImagenEntity e = find(materialImagenRepository, id, "Imagen de material");
        applyMaterialImagen(e, body);
        return materialImagenRepository.save(e);
    }

    public void deleteMaterialImagen(Long id) {
        delete(materialImagenRepository, id, "Imagen de material");
    }

    private void applyMaterialImagen(MaterialImagenEntity e, MaterialImagenEntity body) {
        requireFk(materialRepository, body.getMaterialId(), "materialId");
        e.setMaterialId(body.getMaterialId());
        e.setUrl(required(body.getUrl(), "url"));
        e.setPrincipal(nvl(body.getPrincipal(), Boolean.FALSE));
        e.setOrden(nvl(body.getOrden(), 0));
    }

    // ------------------------------------------------------------- papeles-forro

    @Transactional(readOnly = true)
    public List<PapelForroEntity> listPapelesForro() {
        return papelForroRepository.findAllByOrderByOrdenAscNombreAsc();
    }

    @Transactional(readOnly = true)
    public PapelForroEntity getPapelForro(Long id) {
        return find(papelForroRepository, id, "Papel forro");
    }

    public PapelForroEntity createPapelForro(PapelForroEntity body) {
        PapelForroEntity e = new PapelForroEntity();
        applyPapelForro(e, body);
        return papelForroRepository.save(e);
    }

    public PapelForroEntity updatePapelForro(Long id, PapelForroEntity body) {
        PapelForroEntity e = find(papelForroRepository, id, "Papel forro");
        applyPapelForro(e, body);
        return papelForroRepository.save(e);
    }

    public void deletePapelForro(Long id) {
        delete(papelForroRepository, id, "Papel forro");
    }

    private void applyPapelForro(PapelForroEntity e, PapelForroEntity body) {
        e.setFamilia(required(body.getFamilia(), "familia"));
        e.setNombre(required(body.getNombre(), "nombre"));
        e.setMedidas(trimToNull(body.getMedidas()));
        e.setGramajes(trimToNull(body.getGramajes()));
        e.setImagenUrl(trimToNull(body.getImagenUrl()));
        e.setImagenThumbUrl(trimToNull(body.getImagenThumbUrl()));
        e.setActivo(nvl(body.getActivo(), Boolean.TRUE));
        e.setOrden(body.getOrden() == null ? 0 : body.getOrden());
    }

    // ------------------------------------------------------------------ viniles

    @Transactional(readOnly = true)
    public List<VinilEntity> listViniles() {
        return vinilRepository.findAllByOrderByOrdenAscNombreAsc();
    }

    @Transactional(readOnly = true)
    public VinilEntity getVinil(Long id) {
        return find(vinilRepository, id, "Vinil");
    }

    public VinilEntity createVinil(VinilEntity body) {
        VinilEntity e = new VinilEntity();
        applyVinil(e, body);
        return vinilRepository.save(e);
    }

    public VinilEntity updateVinil(Long id, VinilEntity body) {
        VinilEntity e = find(vinilRepository, id, "Vinil");
        applyVinil(e, body);
        return vinilRepository.save(e);
    }

    public void deleteVinil(Long id) {
        delete(vinilRepository, id, "Vinil");
    }

    private void applyVinil(VinilEntity e, VinilEntity body) {
        String tipo = required(body.getTipo(), "tipo").toLowerCase(Locale.ROOT);
        if (!tipo.equals("mate") && !tipo.equals("brillante") && !tipo.equals("impreso")) {
            throw badRequest("tipo de vinil inválido (use mate, brillante o impreso)");
        }
        e.setTipo(tipo);
        e.setNombre(required(body.getNombre(), "nombre"));
        e.setCodigoHex(trimToNull(body.getCodigoHex()));
        e.setImagenUrl(trimToNull(body.getImagenUrl()));
        e.setImagenThumbUrl(trimToNull(body.getImagenThumbUrl()));
        boolean requiereArte = Boolean.TRUE.equals(body.getRequiereArte()) || "impreso".equals(tipo);
        e.setRequiereArte(requiereArte);
        e.setPrecioRef(body.getPrecioRef());
        e.setUnidadPrecio(trimToNull(body.getUnidadPrecio()));
        e.setActivo(nvl(body.getActivo(), Boolean.TRUE));
        e.setOrden(body.getOrden() == null ? 0 : body.getOrden());
    }

    // ----------------------------------------------------------------- gramajes

    @Transactional(readOnly = true)
    public List<GramajeEntity> listGramajes(Long materialId) {
        return materialId == null
                ? gramajeRepository.findAll()
                : gramajeRepository.findByMaterialIdOrderByValorAsc(materialId);
    }

    @Transactional(readOnly = true)
    public GramajeEntity getGramaje(Long id) {
        return find(gramajeRepository, id, "Gramaje");
    }

    public GramajeEntity createGramaje(GramajeEntity body) {
        GramajeEntity e = new GramajeEntity();
        applyGramaje(e, body);
        return gramajeRepository.save(e);
    }

    public GramajeEntity updateGramaje(Long id, GramajeEntity body) {
        GramajeEntity e = find(gramajeRepository, id, "Gramaje");
        applyGramaje(e, body);
        return gramajeRepository.save(e);
    }

    public void deleteGramaje(Long id) {
        delete(gramajeRepository, id, "Gramaje");
    }

    private void applyGramaje(GramajeEntity e, GramajeEntity body) {
        requireFk(materialRepository, body.getMaterialId(), "materialId");
        e.setMaterialId(body.getMaterialId());
        e.setValor(requiredNumber(body.getValor(), "valor"));
        e.setUnidad(blank(body.getUnidad()) ? "g" : body.getUnidad().trim());
    }

    // -------------------------------------------------------------- componentes

    @Transactional(readOnly = true)
    public List<ComponenteEntity> listComponentes() {
        return componenteRepository.findAllByOrderByNombreAsc();
    }

    public ComponenteEntity createComponente(ComponenteEntity body) {
        ComponenteEntity e = new ComponenteEntity();
        e.setNombre(required(body.getNombre(), "nombre"));
        return componenteRepository.save(e);
    }

    public ComponenteEntity updateComponente(Long id, ComponenteEntity body) {
        ComponenteEntity e = find(componenteRepository, id, "Componente");
        e.setNombre(required(body.getNombre(), "nombre"));
        return componenteRepository.save(e);
    }

    public void deleteComponente(Long id) {
        delete(componenteRepository, id, "Componente");
    }

    // ----------------------------------------------------- variante-componentes

    @Transactional(readOnly = true)
    public List<VarianteComponenteEntity> listVarianteComponentes(Long varianteId) {
        return varianteId == null
                ? varianteComponenteRepository.findAll()
                : varianteComponenteRepository.findByVarianteIdOrderByIdAsc(varianteId);
    }

    @Transactional(readOnly = true)
    public VarianteComponenteEntity getVarianteComponente(Long id) {
        return find(varianteComponenteRepository, id, "Componente de variante");
    }

    public VarianteComponenteEntity createVarianteComponente(VarianteComponenteEntity body) {
        VarianteComponenteEntity e = new VarianteComponenteEntity();
        applyVarianteComponente(e, body);
        return varianteComponenteRepository.save(e);
    }

    public VarianteComponenteEntity updateVarianteComponente(Long id, VarianteComponenteEntity body) {
        VarianteComponenteEntity e = find(varianteComponenteRepository, id, "Componente de variante");
        applyVarianteComponente(e, body);
        return varianteComponenteRepository.save(e);
    }

    public void deleteVarianteComponente(Long id) {
        delete(varianteComponenteRepository, id, "Componente de variante");
    }

    private void applyVarianteComponente(VarianteComponenteEntity e, VarianteComponenteEntity body) {
        requireFk(varianteRepository, body.getVarianteId(), "varianteId");
        requireFk(componenteRepository, body.getComponenteId(), "componenteId");
        requireFk(materialRepository, body.getMaterialId(), "materialId");
        optionalFk(gramajeRepository, body.getGramajeId(), "gramajeId");
        e.setVarianteId(body.getVarianteId());
        e.setComponenteId(body.getComponenteId());
        e.setMaterialId(body.getMaterialId());
        e.setGramajeId(body.getGramajeId());
        e.setCantidad(nvl(body.getCantidad(), BigDecimal.ONE));
    }

    // --------------------------------------------- configuraciones interiores

    @Transactional(readOnly = true)
    public List<ConfiguracionInteriorEntity> listConfiguracionesInteriores() {
        return configuracionInteriorRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public ConfiguracionInteriorEntity getConfiguracionInterior(Long id) {
        return find(configuracionInteriorRepository, id, "Configuración interior");
    }

    public ConfiguracionInteriorEntity createConfiguracionInterior(ConfiguracionInteriorEntity body) {
        ConfiguracionInteriorEntity e = new ConfiguracionInteriorEntity();
        e.setNombre(required(body.getNombre(), "nombre"));
        e.setDescripcion(body.getDescripcion());
        return configuracionInteriorRepository.save(e);
    }

    public ConfiguracionInteriorEntity updateConfiguracionInterior(Long id, ConfiguracionInteriorEntity body) {
        ConfiguracionInteriorEntity e = find(configuracionInteriorRepository, id, "Configuración interior");
        e.setNombre(required(body.getNombre(), "nombre"));
        e.setDescripcion(body.getDescripcion());
        return configuracionInteriorRepository.save(e);
    }

    public void deleteConfiguracionInterior(Long id) {
        delete(configuracionInteriorRepository, id, "Configuración interior");
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionInteriorDetalleEntity> listConfiguracionDetalles(Long configuracionId) {
        return configuracionId == null
                ? configuracionInteriorDetalleRepository.findAll()
                : configuracionInteriorDetalleRepository.findByConfiguracionIdOrderByIdAsc(configuracionId);
    }

    public ConfiguracionInteriorDetalleEntity createConfiguracionDetalle(ConfiguracionInteriorDetalleEntity body) {
        ConfiguracionInteriorDetalleEntity e = new ConfiguracionInteriorDetalleEntity();
        applyConfiguracionDetalle(e, body);
        return configuracionInteriorDetalleRepository.save(e);
    }

    public ConfiguracionInteriorDetalleEntity updateConfiguracionDetalle(
            Long id, ConfiguracionInteriorDetalleEntity body) {
        ConfiguracionInteriorDetalleEntity e = find(
                configuracionInteriorDetalleRepository, id, "Detalle de configuración");
        applyConfiguracionDetalle(e, body);
        return configuracionInteriorDetalleRepository.save(e);
    }

    public void deleteConfiguracionDetalle(Long id) {
        delete(configuracionInteriorDetalleRepository, id, "Detalle de configuración");
    }

    private void applyConfiguracionDetalle(ConfiguracionInteriorDetalleEntity e,
                                           ConfiguracionInteriorDetalleEntity body) {
        requireFk(configuracionInteriorRepository, body.getConfiguracionId(), "configuracionId");
        e.setConfiguracionId(body.getConfiguracionId());
        e.setCantidad(body.getCantidad());
        e.setAlto(body.getAlto());
        e.setDiametro(body.getDiametro());
        e.setDescripcion(body.getDescripcion());
    }

    // ------------------------------------------------- variante-configuraciones

    @Transactional(readOnly = true)
    public List<VarianteConfiguracionEntity> listVarianteConfiguraciones(Long varianteId) {
        return varianteId == null
                ? varianteConfiguracionRepository.findAll()
                : varianteConfiguracionRepository.findByVarianteId(varianteId);
    }

    public VarianteConfiguracionEntity createVarianteConfiguracion(VarianteConfiguracionEntity body) {
        requireFk(varianteRepository, body.getVarianteId(), "varianteId");
        requireFk(configuracionInteriorRepository, body.getConfiguracionId(), "configuracionId");
        VarianteConfiguracionEntity e = new VarianteConfiguracionEntity();
        e.setVarianteId(body.getVarianteId());
        e.setConfiguracionId(body.getConfiguracionId());
        return varianteConfiguracionRepository.save(e);
    }

    public void deleteVarianteConfiguracion(Long varianteId, Long configuracionId) {
        VarianteConfiguracionEntity.Key key = new VarianteConfiguracionEntity.Key(varianteId, configuracionId);
        if (!varianteConfiguracionRepository.existsById(key)) {
            throw notFound("Relación variante-configuración no encontrada");
        }
        varianteConfiguracionRepository.deleteById(key);
    }

    // ---------------------------------------------------------------- atributos

    @Transactional(readOnly = true)
    public List<AtributoEntity> listAtributos() {
        return atributoRepository.findAllByOrderByNombreAsc();
    }

    public AtributoEntity createAtributo(AtributoEntity body) {
        AtributoEntity e = new AtributoEntity();
        e.setNombre(required(body.getNombre(), "nombre"));
        return atributoRepository.save(e);
    }

    public AtributoEntity updateAtributo(Long id, AtributoEntity body) {
        AtributoEntity e = find(atributoRepository, id, "Atributo");
        e.setNombre(required(body.getNombre(), "nombre"));
        return atributoRepository.save(e);
    }

    public void deleteAtributo(Long id) {
        delete(atributoRepository, id, "Atributo");
    }

    @Transactional(readOnly = true)
    public List<AtributoValorEntity> listAtributoValores(Long atributoId) {
        return atributoId == null
                ? atributoValorRepository.findAll()
                : atributoValorRepository.findByAtributoIdOrderByValorAsc(atributoId);
    }

    public AtributoValorEntity createAtributoValor(AtributoValorEntity body) {
        AtributoValorEntity e = new AtributoValorEntity();
        applyAtributoValor(e, body);
        return atributoValorRepository.save(e);
    }

    public AtributoValorEntity updateAtributoValor(Long id, AtributoValorEntity body) {
        AtributoValorEntity e = find(atributoValorRepository, id, "Valor de atributo");
        applyAtributoValor(e, body);
        return atributoValorRepository.save(e);
    }

    public void deleteAtributoValor(Long id) {
        delete(atributoValorRepository, id, "Valor de atributo");
    }

    private void applyAtributoValor(AtributoValorEntity e, AtributoValorEntity body) {
        requireFk(atributoRepository, body.getAtributoId(), "atributoId");
        e.setAtributoId(body.getAtributoId());
        e.setValor(required(body.getValor(), "valor"));
    }

    @Transactional(readOnly = true)
    public List<VarianteAtributoEntity> listVarianteAtributos(Long varianteId) {
        return varianteId == null
                ? varianteAtributoRepository.findAll()
                : varianteAtributoRepository.findByVarianteId(varianteId);
    }

    public VarianteAtributoEntity createVarianteAtributo(VarianteAtributoEntity body) {
        requireFk(varianteRepository, body.getVarianteId(), "varianteId");
        requireFk(atributoValorRepository, body.getAtributoValorId(), "atributoValorId");
        VarianteAtributoEntity e = new VarianteAtributoEntity();
        e.setVarianteId(body.getVarianteId());
        e.setAtributoValorId(body.getAtributoValorId());
        return varianteAtributoRepository.save(e);
    }

    public void deleteVarianteAtributo(Long varianteId, Long atributoValorId) {
        VarianteAtributoEntity.Key key = new VarianteAtributoEntity.Key(varianteId, atributoValorId);
        if (!varianteAtributoRepository.existsById(key)) {
            throw notFound("Relación variante-atributo no encontrada");
        }
        varianteAtributoRepository.deleteById(key);
    }

    // -------------------------------------------------------------------- ideas

    @Transactional(readOnly = true)
    public List<IdeaEntity> listIdeas() {
        return ideaRepository.findAllByOrderByOrdenAscIdAsc();
    }

    @Transactional(readOnly = true)
    public IdeaEntity getIdea(Long id) {
        return find(ideaRepository, id, "Idea");
    }

    public IdeaEntity createIdea(IdeaEntity body) {
        IdeaEntity e = new IdeaEntity();
        applyIdea(e, body);
        return ideaRepository.save(e);
    }

    public IdeaEntity updateIdea(Long id, IdeaEntity body) {
        IdeaEntity e = find(ideaRepository, id, "Idea");
        applyIdea(e, body);
        return ideaRepository.save(e);
    }

    public void deleteIdea(Long id) {
        find(ideaRepository, id, "Idea");
        ideaImagenRepository.deleteByIdeaId(id);
        ideaVarianteRepository.deleteByIdeaId(id);
        ideaImagenRepository.flush();
        ideaVarianteRepository.flush();
        delete(ideaRepository, id, "Idea");
    }

    private void applyIdea(IdeaEntity e, IdeaEntity body) {
        e.setNombre(required(body.getNombre(), "nombre"));
        e.setSlug(blank(body.getSlug()) ? slugify(e.getNombre()) : body.getSlug().trim());
        e.setDescripcion(body.getDescripcion());
        e.setActivo(nvl(body.getActivo(), Boolean.TRUE));
        e.setOrden(nvl(body.getOrden(), 0));
    }

    // ----------------------------------------------------------- idea ficha

    @Transactional(readOnly = true)
    public IdeaAdminDto getIdeaAdmin(Long id) {
        return toIdeaAdminDto(find(ideaRepository, id, "Idea"));
    }

    public IdeaAdminDto createIdeaAdmin(IdeaAdminDto body) {
        IdeaEntity e = new IdeaEntity();
        applyIdeaFromAdmin(e, body);
        e = ideaRepository.save(e);
        syncIdeaChildren(e.getId(), body);
        return toIdeaAdminDto(e);
    }

    public IdeaAdminDto updateIdeaAdmin(Long id, IdeaAdminDto body) {
        IdeaEntity e = find(ideaRepository, id, "Idea");
        applyIdeaFromAdmin(e, body);
        e = ideaRepository.save(e);
        syncIdeaChildren(e.getId(), body);
        return toIdeaAdminDto(e);
    }

    private void applyIdeaFromAdmin(IdeaEntity e, IdeaAdminDto body) {
        e.setNombre(required(body.nombre(), "nombre"));
        String slug = body.slug() == null ? "" : body.slug().trim();
        e.setSlug(slug.isBlank() ? slugify(e.getNombre()) : slug);
        e.setDescripcion(body.descripcion());
        e.setActivo(nvl(body.activo(), Boolean.TRUE));
        e.setOrden(nvl(body.orden(), 0));
    }

    private void syncIdeaChildren(Long ideaId, IdeaAdminDto body) {
        syncIdeaImagenes(ideaId, body.imagenes() == null ? List.of() : body.imagenes());
        syncIdeaProductos(ideaId, body.productos() == null ? List.of() : body.productos());
    }

    private void syncIdeaImagenes(Long ideaId, List<IdeaAdminDto.ImagenLine> lines) {
        ideaImagenRepository.deleteByIdeaId(ideaId);
        ideaImagenRepository.flush();
        int i = 0;
        for (IdeaAdminDto.ImagenLine line : lines) {
            if (line == null || line.url() == null || line.url().isBlank()) {
                continue;
            }
            IdeaImagenEntity e = new IdeaImagenEntity();
            e.setIdeaId(ideaId);
            e.setUrl(line.url().trim());
            String thumb = line.urlThumb();
            e.setUrlThumb(thumb == null || thumb.isBlank() ? e.getUrl() : thumb.trim());
            e.setPrincipal(nvl(line.principal(), i == 0));
            e.setOrden(nvl(line.orden(), i));
            ideaImagenRepository.save(e);
            i++;
        }
    }

    private void syncIdeaProductos(Long ideaId, List<IdeaAdminDto.ProductoLine> lines) {
        ideaVarianteRepository.deleteByIdeaId(ideaId);
        ideaVarianteRepository.flush();
        Set<Long> seen = new HashSet<>();
        int i = 0;
        for (IdeaAdminDto.ProductoLine line : lines) {
            if (line == null || line.varianteId() == null) {
                continue;
            }
            if (!seen.add(line.varianteId())) {
                continue;
            }
            requireFk(varianteRepository, line.varianteId(), "varianteId");
            IdeaVarianteEntity e = new IdeaVarianteEntity();
            e.setIdeaId(ideaId);
            e.setVarianteId(line.varianteId());
            e.setTitulo(line.titulo());
            e.setDescripcion(line.descripcion());
            e.setOrden(nvl(line.orden(), i));
            ideaVarianteRepository.save(e);
            i++;
        }
    }

    private IdeaAdminDto toIdeaAdminDto(IdeaEntity idea) {
        Long id = idea.getId();
        List<IdeaAdminDto.ImagenLine> imagenes = new ArrayList<>();
        for (IdeaImagenEntity img : ideaImagenRepository.findByIdeaIdOrderByPrincipalDescOrdenAscIdAsc(id)) {
            imagenes.add(new IdeaAdminDto.ImagenLine(
                    img.getId(), img.getUrl(), img.getUrlThumb(), img.getPrincipal(), img.getOrden()));
        }
        List<IdeaAdminDto.ProductoLine> productos = new ArrayList<>();
        for (IdeaVarianteEntity iv : ideaVarianteRepository.findByIdeaIdOrderByOrdenAscIdAsc(id)) {
            productos.add(new IdeaAdminDto.ProductoLine(
                    iv.getId(), iv.getVarianteId(), iv.getTitulo(), iv.getDescripcion(), iv.getOrden()));
        }
        return new IdeaAdminDto(
                id,
                idea.getNombre(),
                idea.getSlug(),
                idea.getDescripcion(),
                idea.getActivo(),
                idea.getOrden(),
                imagenes,
                productos
        );
    }

    @Transactional(readOnly = true)
    public List<IdeaImagenEntity> listIdeaImagenes(Long ideaId) {
        return ideaId == null
                ? ideaImagenRepository.findAll()
                : ideaImagenRepository.findByIdeaIdOrderByPrincipalDescOrdenAscIdAsc(ideaId);
    }

    public IdeaImagenEntity createIdeaImagen(IdeaImagenEntity body) {
        IdeaImagenEntity e = new IdeaImagenEntity();
        applyIdeaImagen(e, body);
        return ideaImagenRepository.save(e);
    }

    public IdeaImagenEntity updateIdeaImagen(Long id, IdeaImagenEntity body) {
        IdeaImagenEntity e = find(ideaImagenRepository, id, "Imagen de idea");
        applyIdeaImagen(e, body);
        return ideaImagenRepository.save(e);
    }

    public void deleteIdeaImagen(Long id) {
        delete(ideaImagenRepository, id, "Imagen de idea");
    }

    private void applyIdeaImagen(IdeaImagenEntity e, IdeaImagenEntity body) {
        requireFk(ideaRepository, body.getIdeaId(), "ideaId");
        e.setIdeaId(body.getIdeaId());
        e.setUrl(required(body.getUrl(), "url"));
        String thumb = body.getUrlThumb();
        e.setUrlThumb(thumb == null || thumb.isBlank() ? e.getUrl() : thumb.trim());
        e.setPrincipal(nvl(body.getPrincipal(), Boolean.FALSE));
        e.setOrden(nvl(body.getOrden(), 0));
    }

    // ----------------------------------------------------------- idea-variantes

    @Transactional(readOnly = true)
    public List<IdeaVarianteEntity> listIdeaVariantes(Long ideaId) {
        return ideaId == null
                ? ideaVarianteRepository.findAll()
                : ideaVarianteRepository.findByIdeaIdOrderByOrdenAscIdAsc(ideaId);
    }

    @Transactional(readOnly = true)
    public IdeaVarianteEntity getIdeaVariante(Long id) {
        return find(ideaVarianteRepository, id, "Idea-variante");
    }

    public IdeaVarianteEntity createIdeaVariante(IdeaVarianteEntity body) {
        IdeaVarianteEntity e = new IdeaVarianteEntity();
        applyIdeaVariante(e, body);
        return ideaVarianteRepository.save(e);
    }

    public IdeaVarianteEntity updateIdeaVariante(Long id, IdeaVarianteEntity body) {
        IdeaVarianteEntity e = find(ideaVarianteRepository, id, "Idea-variante");
        applyIdeaVariante(e, body);
        return ideaVarianteRepository.save(e);
    }

    public void deleteIdeaVariante(Long id) {
        delete(ideaVarianteRepository, id, "Idea-variante");
    }

    private void applyIdeaVariante(IdeaVarianteEntity e, IdeaVarianteEntity body) {
        requireFk(ideaRepository, body.getIdeaId(), "ideaId");
        requireFk(varianteRepository, body.getVarianteId(), "varianteId");
        e.setIdeaId(body.getIdeaId());
        e.setVarianteId(body.getVarianteId());
        e.setTitulo(body.getTitulo());
        e.setDescripcion(body.getDescripcion());
        e.setOrden(nvl(body.getOrden(), 0));
    }

    // --------------------------------------------------------------------- tags

    @Transactional(readOnly = true)
    public List<TagEntity> listTags() {
        return tagRepository.findAllByOrderByNombreAsc();
    }

    public TagEntity createTag(TagEntity body) {
        TagEntity e = new TagEntity();
        e.setNombre(required(body.getNombre(), "nombre"));
        return tagRepository.save(e);
    }

    public TagEntity updateTag(Long id, TagEntity body) {
        TagEntity e = find(tagRepository, id, "Tag");
        e.setNombre(required(body.getNombre(), "nombre"));
        return tagRepository.save(e);
    }

    public void deleteTag(Long id) {
        delete(tagRepository, id, "Tag");
    }

    @Transactional(readOnly = true)
    public List<VarianteTagEntity> listVarianteTags(Long varianteId) {
        return varianteId == null
                ? varianteTagRepository.findAll()
                : varianteTagRepository.findByVarianteId(varianteId);
    }

    public VarianteTagEntity createVarianteTag(VarianteTagEntity body) {
        requireFk(varianteRepository, body.getVarianteId(), "varianteId");
        requireFk(tagRepository, body.getTagId(), "tagId");
        VarianteTagEntity e = new VarianteTagEntity();
        e.setVarianteId(body.getVarianteId());
        e.setTagId(body.getTagId());
        return varianteTagRepository.save(e);
    }

    public void deleteVarianteTag(Long varianteId, Long tagId) {
        VarianteTagEntity.Key key = new VarianteTagEntity.Key(varianteId, tagId);
        if (!varianteTagRepository.existsById(key)) {
            throw notFound("Relación variante-tag no encontrada");
        }
        varianteTagRepository.deleteById(key);
    }

    // -------------------------------------------------------- costos componentes

    @Transactional(readOnly = true)
    public List<CostoComponenteEntity> listCostosComponentes() {
        return costoComponenteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CostoComponenteEntity getCostoComponente(Long id) {
        return find(costoComponenteRepository, id, "Costo de componente");
    }

    public CostoComponenteEntity createCostoComponente(CostoComponenteEntity body) {
        CostoComponenteEntity e = new CostoComponenteEntity();
        applyCostoComponente(e, body);
        return costoComponenteRepository.save(e);
    }

    public CostoComponenteEntity updateCostoComponente(Long id, CostoComponenteEntity body) {
        CostoComponenteEntity e = find(costoComponenteRepository, id, "Costo de componente");
        applyCostoComponente(e, body);
        return costoComponenteRepository.save(e);
    }

    public void deleteCostoComponente(Long id) {
        delete(costoComponenteRepository, id, "Costo de componente");
    }

    private void applyCostoComponente(CostoComponenteEntity e, CostoComponenteEntity body) {
        requireFk(varianteComponenteRepository, body.getVarianteComponenteId(), "varianteComponenteId");
        BigDecimal costo = requiredNumber(body.getCosto(), "costo");
        if (costo.signum() < 0) {
            throw badRequest("costo no puede ser negativo");
        }
        e.setVarianteComponenteId(body.getVarianteComponenteId());
        e.setCosto(costo);
    }

    // ------------------------------------------------------------------ helpers

    private static <T> T find(JpaRepository<T, Long> repository, Long id, String label) {
        if (id == null) {
            throw badRequest("Falta el id de " + label);
        }
        return repository.findById(id)
                .orElseThrow(() -> notFound(label + " no encontrado: " + id));
    }

    private void delete(JpaRepository<?, Long> repository, Long id, String label) {
        if (id == null || !repository.existsById(id)) {
            throw notFound(label + " no encontrado: " + id);
        }
        try {
            repository.deleteById(id);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    label + " está en uso y no se puede eliminar");
        }
    }

    private static void requireFk(JpaRepository<?, Long> repository, Long id, String field) {
        if (id == null) {
            throw badRequest("Falta " + field);
        }
        if (!repository.existsById(id)) {
            throw badRequest(field + " no existe: " + id);
        }
    }

    private static void optionalFk(JpaRepository<?, Long> repository, Long id, String field) {
        if (id != null && !repository.existsById(id)) {
            throw badRequest(field + " no existe: " + id);
        }
    }

    private static String required(String value, String field) {
        if (blank(value)) {
            throw badRequest("Falta " + field);
        }
        return value.trim();
    }

    private static BigDecimal requiredNumber(BigDecimal value, String field) {
        if (value == null) {
            throw badRequest("Falta " + field);
        }
        return value;
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private static String trimToNull(String value) {
        if (value == null) return null;
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private static <T> T nvl(T value, T fallback) {
        return value == null ? fallback : value;
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private static ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    static String slugify(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? "item" : normalized;
    }
}
