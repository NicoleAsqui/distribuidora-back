package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "site_config")
public class SiteConfigEntity {
    @Id
    private String id;
    @Column(name = "whatsapp_admin")
    private String whatsappAdmin;
    @Column(name = "whatsapp_asesora")
    private String whatsappAsesora;
    @Column(name = "email_asesor")
    private String emailAsesor;
    @Column(name = "min_lead_days")
    private int minLeadDays;
    @Column(name = "maps_url")
    private String mapsUrl;
    @Column(name = "maps_embed", length = 1000)
    private String mapsEmbed;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getWhatsappAdmin() { return whatsappAdmin; }
    public void setWhatsappAdmin(String whatsappAdmin) { this.whatsappAdmin = whatsappAdmin; }
    public String getWhatsappAsesora() { return whatsappAsesora; }
    public void setWhatsappAsesora(String whatsappAsesora) { this.whatsappAsesora = whatsappAsesora; }
    public String getEmailAsesor() { return emailAsesor; }
    public void setEmailAsesor(String emailAsesor) { this.emailAsesor = emailAsesor; }
    public int getMinLeadDays() { return minLeadDays; }
    public void setMinLeadDays(int minLeadDays) { this.minLeadDays = minLeadDays; }
    public String getMapsUrl() { return mapsUrl; }
    public void setMapsUrl(String mapsUrl) { this.mapsUrl = mapsUrl; }
    public String getMapsEmbed() { return mapsEmbed; }
    public void setMapsEmbed(String mapsEmbed) { this.mapsEmbed = mapsEmbed; }
}
