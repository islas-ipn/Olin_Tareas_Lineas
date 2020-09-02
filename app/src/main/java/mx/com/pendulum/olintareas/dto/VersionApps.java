package mx.com.pendulum.olintareas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionApps {

    private String vLegal;
    private String vTareas;
    private String vOlin;
    private String vUbicuo;
    private long cLegal;
    private long cTareas;
    private long cOlin;
    private long cUbicuo;
    private String uUbicuo;

    public String getvLegal() { return vLegal; }
    public void setvLegal(String vLegal) { this.vLegal = vLegal; }

    public String getvTareas() { return vTareas; }
    public void setvTareas(String vTareas) { this.vTareas = vTareas; }

    public String getvOlin() { return vOlin; }
    public void setvOlin(String vOlin) { this.vOlin = vOlin; }

    public String getvUbicuo() { return vUbicuo; }
    public void setvUbicuo(String vUbicuo) { this.vUbicuo = vUbicuo; }

    public long getcLegal() { return cLegal; }
    public void setcLegal(long cLegal) { this.cLegal = cLegal; }

    public long getcTareas() { return cTareas; }
    public void setcTareas(long cTareas) { this.cTareas = cTareas; }

    public long getcOlin() { return cOlin; }
    public void setcOlin(long cOlin) { this.cOlin = cOlin; }

    public long getcUbicuo() { return cUbicuo; }
    public void setcUbicuo(long cUbicuo) { this.cUbicuo = cUbicuo; }

    public String getuUbicuo() { return uUbicuo; }
    public void setuUbicuo(String uUbicuo) { this.uUbicuo = uUbicuo; }
}