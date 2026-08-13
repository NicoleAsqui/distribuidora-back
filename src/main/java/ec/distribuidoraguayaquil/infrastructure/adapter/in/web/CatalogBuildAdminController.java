package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.application.service.NewCatalogAdminService;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.AtributoEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.AtributoValorEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.ComponenteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.ConfiguracionInteriorDetalleEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.ConfiguracionInteriorEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.CostoComponenteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteAtributoEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteComponenteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteConfiguracionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Administración de construcción: componentes, configuraciones, atributos y costos. */
@RestController
@RequestMapping("/api/admin/catalog")
@RequiredArgsConstructor
public class CatalogBuildAdminController {

    private final NewCatalogAdminService service;

    // -------------------------------------------------------------- componentes

    @GetMapping("/componentes")
    public List<ComponenteEntity> listComponentes() {
        return service.listComponentes();
    }

    @PostMapping("/componentes")
    public ComponenteEntity createComponente(@RequestBody ComponenteEntity body) {
        return service.createComponente(body);
    }

    @PutMapping("/componentes/{id}")
    public ComponenteEntity updateComponente(@PathVariable Long id, @RequestBody ComponenteEntity body) {
        return service.updateComponente(id, body);
    }

    @DeleteMapping("/componentes/{id}")
    public void deleteComponente(@PathVariable Long id) {
        service.deleteComponente(id);
    }

    // ----------------------------------------------------- variante-componentes

    @GetMapping("/variante-componentes")
    public List<VarianteComponenteEntity> listVarianteComponentes(@RequestParam(required = false) Long varianteId) {
        return service.listVarianteComponentes(varianteId);
    }

    @GetMapping("/variante-componentes/{id}")
    public VarianteComponenteEntity getVarianteComponente(@PathVariable Long id) {
        return service.getVarianteComponente(id);
    }

    @PostMapping("/variante-componentes")
    public VarianteComponenteEntity createVarianteComponente(@RequestBody VarianteComponenteEntity body) {
        return service.createVarianteComponente(body);
    }

    @PutMapping("/variante-componentes/{id}")
    public VarianteComponenteEntity updateVarianteComponente(@PathVariable Long id,
                                                             @RequestBody VarianteComponenteEntity body) {
        return service.updateVarianteComponente(id, body);
    }

    @DeleteMapping("/variante-componentes/{id}")
    public void deleteVarianteComponente(@PathVariable Long id) {
        service.deleteVarianteComponente(id);
    }

    // ----------------------------------------------- configuraciones-interiores

    @GetMapping("/configuraciones-interiores")
    public List<ConfiguracionInteriorEntity> listConfiguraciones() {
        return service.listConfiguracionesInteriores();
    }

    @GetMapping("/configuraciones-interiores/{id}")
    public ConfiguracionInteriorEntity getConfiguracion(@PathVariable Long id) {
        return service.getConfiguracionInterior(id);
    }

    @PostMapping("/configuraciones-interiores")
    public ConfiguracionInteriorEntity createConfiguracion(@RequestBody ConfiguracionInteriorEntity body) {
        return service.createConfiguracionInterior(body);
    }

    @PutMapping("/configuraciones-interiores/{id}")
    public ConfiguracionInteriorEntity updateConfiguracion(@PathVariable Long id,
                                                           @RequestBody ConfiguracionInteriorEntity body) {
        return service.updateConfiguracionInterior(id, body);
    }

    @DeleteMapping("/configuraciones-interiores/{id}")
    public void deleteConfiguracion(@PathVariable Long id) {
        service.deleteConfiguracionInterior(id);
    }

    // --------------------------------------- configuracion-interior-detalles

    @GetMapping("/configuracion-interior-detalles")
    public List<ConfiguracionInteriorDetalleEntity> listConfiguracionDetalles(
            @RequestParam(required = false) Long configuracionId) {
        return service.listConfiguracionDetalles(configuracionId);
    }

    @PostMapping("/configuracion-interior-detalles")
    public ConfiguracionInteriorDetalleEntity createConfiguracionDetalle(
            @RequestBody ConfiguracionInteriorDetalleEntity body) {
        return service.createConfiguracionDetalle(body);
    }

    @PutMapping("/configuracion-interior-detalles/{id}")
    public ConfiguracionInteriorDetalleEntity updateConfiguracionDetalle(
            @PathVariable Long id, @RequestBody ConfiguracionInteriorDetalleEntity body) {
        return service.updateConfiguracionDetalle(id, body);
    }

    @DeleteMapping("/configuracion-interior-detalles/{id}")
    public void deleteConfiguracionDetalle(@PathVariable Long id) {
        service.deleteConfiguracionDetalle(id);
    }

    // ------------------------------------------------- variante-configuraciones

    @GetMapping("/variante-configuraciones")
    public List<VarianteConfiguracionEntity> listVarianteConfiguraciones(
            @RequestParam(required = false) Long varianteId) {
        return service.listVarianteConfiguraciones(varianteId);
    }

    @PostMapping("/variante-configuraciones")
    public VarianteConfiguracionEntity createVarianteConfiguracion(
            @RequestBody VarianteConfiguracionEntity body) {
        return service.createVarianteConfiguracion(body);
    }

    @DeleteMapping("/variante-configuraciones/{varianteId}/{configuracionId}")
    public void deleteVarianteConfiguracion(@PathVariable Long varianteId, @PathVariable Long configuracionId) {
        service.deleteVarianteConfiguracion(varianteId, configuracionId);
    }

    // ---------------------------------------------------------------- atributos

    @GetMapping("/atributos")
    public List<AtributoEntity> listAtributos() {
        return service.listAtributos();
    }

    @PostMapping("/atributos")
    public AtributoEntity createAtributo(@RequestBody AtributoEntity body) {
        return service.createAtributo(body);
    }

    @PutMapping("/atributos/{id}")
    public AtributoEntity updateAtributo(@PathVariable Long id, @RequestBody AtributoEntity body) {
        return service.updateAtributo(id, body);
    }

    @DeleteMapping("/atributos/{id}")
    public void deleteAtributo(@PathVariable Long id) {
        service.deleteAtributo(id);
    }

    // ---------------------------------------------------------- atributo-valores

    @GetMapping("/atributo-valores")
    public List<AtributoValorEntity> listAtributoValores(@RequestParam(required = false) Long atributoId) {
        return service.listAtributoValores(atributoId);
    }

    @PostMapping("/atributo-valores")
    public AtributoValorEntity createAtributoValor(@RequestBody AtributoValorEntity body) {
        return service.createAtributoValor(body);
    }

    @PutMapping("/atributo-valores/{id}")
    public AtributoValorEntity updateAtributoValor(@PathVariable Long id, @RequestBody AtributoValorEntity body) {
        return service.updateAtributoValor(id, body);
    }

    @DeleteMapping("/atributo-valores/{id}")
    public void deleteAtributoValor(@PathVariable Long id) {
        service.deleteAtributoValor(id);
    }

    // ------------------------------------------------------- variante-atributos

    @GetMapping("/variante-atributos")
    public List<VarianteAtributoEntity> listVarianteAtributos(@RequestParam(required = false) Long varianteId) {
        return service.listVarianteAtributos(varianteId);
    }

    @PostMapping("/variante-atributos")
    public VarianteAtributoEntity createVarianteAtributo(@RequestBody VarianteAtributoEntity body) {
        return service.createVarianteAtributo(body);
    }

    @DeleteMapping("/variante-atributos/{varianteId}/{atributoValorId}")
    public void deleteVarianteAtributo(@PathVariable Long varianteId, @PathVariable Long atributoValorId) {
        service.deleteVarianteAtributo(varianteId, atributoValorId);
    }

    // ------------------------------------------------------- costos-componentes

    @GetMapping("/costos-componentes")
    public List<CostoComponenteEntity> listCostosComponentes() {
        return service.listCostosComponentes();
    }

    @GetMapping("/costos-componentes/{id}")
    public CostoComponenteEntity getCostoComponente(@PathVariable Long id) {
        return service.getCostoComponente(id);
    }

    @PostMapping("/costos-componentes")
    public CostoComponenteEntity createCostoComponente(@RequestBody CostoComponenteEntity body) {
        return service.createCostoComponente(body);
    }

    @PutMapping("/costos-componentes/{id}")
    public CostoComponenteEntity updateCostoComponente(@PathVariable Long id,
                                                       @RequestBody CostoComponenteEntity body) {
        return service.updateCostoComponente(id, body);
    }

    @DeleteMapping("/costos-componentes/{id}")
    public void deleteCostoComponente(@PathVariable Long id) {
        service.deleteCostoComponente(id);
    }
}
