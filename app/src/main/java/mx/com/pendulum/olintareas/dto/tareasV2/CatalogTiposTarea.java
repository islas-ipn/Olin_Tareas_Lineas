package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import mx.com.pendulum.olintareas.db.TableNames;


/**
 * Created by evaleriano on 7/11/2017.
 */
@DatabaseTable(tableName = TableNames.CATALOG_GET_TIPOS_TAREA)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogTiposTarea {
    public final static String COL_ID = "_id";
    public final static String COLID = "id";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected Long _id;
    @DatabaseField(columnName = COLID)
    private String id;
    @DatabaseField
    private String value;
    @DatabaseField
    private short requiereCredito;
    @DatabaseField
    private short requiereJuicio;


    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public short getRequiereCredito() { return requiereCredito; }

    public void setRequiereCredito(short requiereCredito) { this.requiereCredito = requiereCredito; }

    public short getRequiereJuicio() { return requiereJuicio; }

    public void setRequiereJuicio(short requiereJuicio) { this.requiereJuicio = requiereJuicio; }
}
