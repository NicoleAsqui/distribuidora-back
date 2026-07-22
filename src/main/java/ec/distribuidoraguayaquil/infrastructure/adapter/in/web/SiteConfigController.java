package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.domain.model.SiteConfig;
import ec.distribuidoraguayaquil.domain.port.in.SiteConfigUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class SiteConfigController {

    private final SiteConfigUseCase siteConfigUseCase;

    public SiteConfigController(SiteConfigUseCase siteConfigUseCase) {
        this.siteConfigUseCase = siteConfigUseCase;
    }

    @GetMapping
    public SiteConfig get() {
        return siteConfigUseCase.get();
    }

    @PutMapping
    public SiteConfig update(@RequestBody SiteConfig config) {
        return siteConfigUseCase.update(config);
    }
}
