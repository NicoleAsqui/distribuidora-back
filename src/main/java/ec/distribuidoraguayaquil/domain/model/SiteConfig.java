package ec.distribuidoraguayaquil.domain.model;

public record SiteConfig(
        String id,
        String whatsappAdmin,
        String whatsappAsesora,
        String emailAsesor,
        int minLeadDays,
        String mapsUrl,
        String mapsEmbed
) {
}
