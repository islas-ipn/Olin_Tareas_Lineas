package mx.com.pendulum.olintareas.dto.login;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionData {
    private String version;
    private String url;
    private String ubicuo_version;
    private String url_play_sotre;
    private String url_ubicuo;

    public String getUbicuo_version() {
        return ubicuo_version;
    }

    public void setUbicuo_version(String ubicuo_version) {
        this.ubicuo_version = ubicuo_version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl_play_sotre() {
        return url_play_sotre;
    }

    public void setUrl_play_sotre(String url_play_sotre) {
        this.url_play_sotre = url_play_sotre;
    }

    public String getUrl_ubicuo() {
        return url_ubicuo;
    }

    public void setUrl_ubicuo(String url_ubicuo) {
        this.url_ubicuo = url_ubicuo;
    }

}
