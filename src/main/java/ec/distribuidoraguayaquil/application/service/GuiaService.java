package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.GuiaDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.GuiaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.GuiaImagenEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.GuiaImagenRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.GuiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GuiaService {

    private final GuiaRepository guiaRepository;
    private final GuiaImagenRepository imagenRepository;

    @Transactional(readOnly = true)
    public List<GuiaDto> listPublic(String seccion) {
        String sec = normalizeSeccion(seccion);
        return toDtos(guiaRepository.findBySeccionAndActivoTrueOrderByOrdenAscIdAsc(sec));
    }

    @Transactional(readOnly = true)
    public List<GuiaDto> listAdmin(String seccion) {
        String sec = seccion == null || seccion.isBlank() ? null : normalizeSeccion(seccion);
        List<GuiaEntity> rows = sec == null
                ? guiaRepository.findAll()
                : guiaRepository.findBySeccionOrderByOrdenAscIdAsc(sec);
        rows.sort((a, b) -> {
            int c = String.valueOf(a.getSeccion()).compareTo(String.valueOf(b.getSeccion()));
            if (c != 0) return c;
            int o = Integer.compare(a.getOrden() == null ? 0 : a.getOrden(), b.getOrden() == null ? 0 : b.getOrden());
            if (o != 0) return o;
            return Long.compare(a.getId(), b.getId());
        });
        return toDtos(rows);
    }

    @Transactional
    public GuiaDto create(GuiaDto body) {
        GuiaEntity e = new GuiaEntity();
        apply(e, body, null);
        e = guiaRepository.save(e);
        replaceImages(e.getId(), body.imagenes());
        return toDto(e, loadImages(List.of(e.getId())).getOrDefault(e.getId(), List.of()));
    }

    @Transactional
    public GuiaDto update(Long id, GuiaDto body) {
        GuiaEntity e = guiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo no encontrado"));
        apply(e, body, id);
        e = guiaRepository.save(e);
        replaceImages(e.getId(), body.imagenes());
        return toDto(e, loadImages(List.of(e.getId())).getOrDefault(e.getId(), List.of()));
    }

    @Transactional
    public void delete(Long id) {
        if (!guiaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo no encontrado");
        }
        imagenRepository.deleteByGuiaId(id);
        guiaRepository.deleteById(id);
    }

    private void apply(GuiaEntity e, GuiaDto body, Long currentId) {
        if (body == null || body.titulo() == null || body.titulo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El título es obligatorio");
        }
        String seccion = normalizeSeccion(body.seccion() == null || body.seccion().isBlank() ? e.getSeccion() : body.seccion());
        String slug = slugify(body.slug() == null || body.slug().isBlank() ? body.titulo() : body.slug());
        if (slug.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El slug es obligatorio");
        }
        guiaRepository.findBySeccionAndSlug(seccion, slug).ifPresent(other -> {
            if (currentId == null || !currentId.equals(other.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un artículo con ese enlace en esta sección");
            }
        });
        e.setSeccion(seccion);
        e.setSlug(slug);
        e.setTitulo(body.titulo().trim());
        e.setResumen(blankToNull(body.resumen()));
        e.setEtiqueta(blankToNull(body.etiqueta()));
        e.setCuerpo(body.cuerpo() == null ? "" : body.cuerpo());
        e.setVideoUrl(normalizeVideo(body.videoUrl()));
        e.setActivo(body.activo() == null ? Boolean.TRUE : body.activo());
        e.setOrden(body.orden() == null ? 0 : body.orden());
    }

    private void replaceImages(Long guiaId, List<GuiaDto.Imagen> lines) {
        imagenRepository.deleteByGuiaId(guiaId);
        imagenRepository.flush();
        if (lines == null) return;
        int orden = 0;
        for (GuiaDto.Imagen line : lines) {
            if (line == null || line.url() == null || line.url().isBlank()) continue;
            GuiaImagenEntity img = new GuiaImagenEntity();
            img.setGuiaId(guiaId);
            img.setUrl(line.url().trim());
            img.setUrlThumb(line.urlThumb() == null || line.urlThumb().isBlank() ? line.url().trim() : line.urlThumb().trim());
            img.setOrden(orden++);
            imagenRepository.save(img);
        }
    }

    private List<GuiaDto> toDtos(List<GuiaEntity> rows) {
        List<Long> ids = rows.stream().map(GuiaEntity::getId).toList();
        Map<Long, List<GuiaDto.Imagen>> images = loadImages(ids);
        List<GuiaDto> out = new ArrayList<>();
        for (GuiaEntity e : rows) {
            out.add(toDto(e, images.getOrDefault(e.getId(), List.of())));
        }
        return out;
    }

    private Map<Long, List<GuiaDto.Imagen>> loadImages(List<Long> ids) {
        Map<Long, List<GuiaDto.Imagen>> map = new LinkedHashMap<>();
        if (ids.isEmpty()) return map;
        for (GuiaImagenEntity img : imagenRepository.findByGuiaIdInOrderByOrdenAscIdAsc(ids)) {
            map.computeIfAbsent(img.getGuiaId(), k -> new ArrayList<>())
                    .add(new GuiaDto.Imagen(img.getId(), img.getUrl(), img.getUrlThumb(), img.getOrden()));
        }
        return map;
    }

    private static GuiaDto toDto(GuiaEntity e, List<GuiaDto.Imagen> imagenes) {
        return new GuiaDto(
                e.getId(),
                e.getSeccion(),
                e.getSlug(),
                e.getTitulo(),
                e.getResumen(),
                e.getEtiqueta(),
                e.getCuerpo(),
                e.getVideoUrl(),
                e.getActivo(),
                e.getOrden(),
                imagenes
        );
    }

    private static String normalizeSeccion(String raw) {
        String s = raw == null ? "" : raw.trim().toLowerCase();
        if (s.equals("informacion") || s.equals("tutoriales")) return s;
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sección inválida. Usa informacion o tutoriales");
    }

    private static String normalizeVideo(String raw) {
        String t = blankToNull(raw);
        if (t == null) return null;
        String lower = t.toLowerCase();
        boolean youtube = lower.contains("youtube.com/") || lower.contains("youtu.be/");
        if (!youtube || !(lower.startsWith("http://") || lower.startsWith("https://"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El video debe ser un enlace de YouTube");
        }
        return t;
    }

    private static String blankToNull(String value) {
        if (value == null) return null;
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    static String slugify(String value) {
        String normalized = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.length() > 80) {
            normalized = normalized.substring(0, 80).replaceAll("-$", "");
        }
        return normalized;
    }
}
