package mx.com.pendulum.olintareas.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SyncDataOutObject implements Serializable {
    public final static String INVALID_ID = "id";
    public final static String COL_ID = "_id";
    public final static String COL_USER = "username";
    public final static String COL_CREDIT_NUMBER = "credit";
    public final static String COL_UPDATED = "updated";
    public final static String COL_TAREA_ID = "updated";
    public static final String COL_ACTIVITY_DATE = "date";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    @JsonProperty("id")
    protected Long _id;

    @DatabaseField(columnName = COL_USER, persisted = false)
    protected String username;

    @DatabaseField(columnName = COL_CREDIT_NUMBER)
    protected String credito;

    @DatabaseField(canBeNull = false, columnName = COL_UPDATED)
    @JsonIgnore
    protected boolean updated;

    @DatabaseField(columnName = COL_ACTIVITY_DATE)
    protected String fecha_actividad;

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCredito() {
        return credito;
    }

    public void setCredito(String credito) {
        this.credito = credito;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public String getFecha_actividad() {
        return fecha_actividad;
    }

    public void setFecha_actividad(String fecha_actividad) {
        this.fecha_actividad = fecha_actividad;
    }

}
