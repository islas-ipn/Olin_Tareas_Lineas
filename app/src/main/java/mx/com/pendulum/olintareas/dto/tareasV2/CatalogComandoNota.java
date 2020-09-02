package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import mx.com.pendulum.olintareas.db.TableNames;


/**
 * Created by evaleriano on 7/26/2017.
 */
@DatabaseTable(tableName = TableNames.CATALOG_COMANDO_NOTA)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogComandoNota {
    public final static String COL_ID = "_id";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected Long _id;

    @DatabaseField
    private String codigo;
    @DatabaseField
    private String tipo;
    @DatabaseField
    private String descripcion;
    @DatabaseField
    private String comentarioDefault;
    @DatabaseField
    private String fechaDefault;
    @DatabaseField
    private Boolean comentarioRequerido;
    @DatabaseField
    private Boolean fechaRequerida;


    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getComentarioDefault() {
        return comentarioDefault;
    }

    public void setComentarioDefault(String comentarioDefault) {
        this.comentarioDefault = comentarioDefault;
    }

    public String getFechaDefault() {
        return fechaDefault;
    }

    public void setFechaDefault(String fechaDefault) {
        this.fechaDefault = fechaDefault;
    }

    public boolean isComentarioRequerido() {
        return comentarioRequerido;
    }

    public void setComentarioRequerido(boolean comentarioRequerido) {
        this.comentarioRequerido = comentarioRequerido;
    }

    public boolean isFechaRequerida() {
        return fechaRequerida;
    }

    public void setFechaRequerida(boolean fechaRequerida) {
        this.fechaRequerida = fechaRequerida;
    }
}
