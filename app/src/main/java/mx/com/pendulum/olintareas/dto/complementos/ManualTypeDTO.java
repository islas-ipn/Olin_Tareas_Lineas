package mx.com.pendulum.olintareas.dto.complementos;

import java.io.Serializable;
import java.util.List;

public class ManualTypeDTO implements Serializable {
    private long id;
    private String nombre;
    private List<ManualDTO> manualList;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<ManualDTO> getManualList() {
        return manualList;
    }

    public void setManualList(List<ManualDTO> manualList) {
        this.manualList = manualList;
    }
}
