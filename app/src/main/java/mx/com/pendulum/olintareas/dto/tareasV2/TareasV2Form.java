package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import mx.com.pendulum.olintareas.db.TableNames;

/**
 * Created by evaleriano on 6/26/2017.
 */


@DatabaseTable(tableName = TableNames.CATALOG_FORM)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TareasV2Form {

    public final static String COL_ID = "_id";
    public final static String COLID = "id";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected Long _id;

    @DatabaseField(columnName = COLID)
    private Long id;
    @DatabaseField
    private String descripcion;
    @DatabaseField
    private String tipo;
    @DatabaseField
    private String form;
    private String lista;

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getForm() {
        return form;
    }

    public void setForm(List<Questions> form) {

        String tmp = "";

        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
//            Gson gson = new Gson();
            tmp = gson.toJson(form);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.form = tmp;
    }


    public void setLista(List<Questions> form) {

        String tmp = "";

        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
//            Gson gson = new Gson();
            tmp = gson.toJson(form);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.form = tmp;
    }

//    public void setForm(String form) {
//        this.form = form;
//    }
}
