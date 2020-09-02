package mx.com.pendulum.olintareas.dto.complementos;

import java.io.Serializable;

public class ManualDTO implements Serializable {

    private long id;
    private long id_manual_type;
    private String nombre_manual;
    private String version;
    private String public_path;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId_manual_type() {
        return id_manual_type;
    }

    public void setId_manual_type(long id_manual_type) {
        this.id_manual_type = id_manual_type;
    }

    public String getNombre_manual() {
        return nombre_manual;
    }

    public void setNombre_manual(String nombre_manual) {
        this.nombre_manual = nombre_manual;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPublic_path() {
        return public_path;
    }

    public void setPublic_path(String public_path) {
        this.public_path = public_path;
    }
}
