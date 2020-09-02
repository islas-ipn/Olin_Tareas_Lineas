package mx.com.pendulum.olintareas.dto.SubClasifica;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;

@DatabaseTable(tableName = TableNames.CATALOG_FORM_SUB)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatTskSubclasificaTareas {

    public final static String COL_ID = "_id";
    public final static String COLID = "id";
    public final static String COLCAT = "id_clasifica";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected long _id;

    @DatabaseField(columnName = COLID)
    private long id;
    @DatabaseField(columnName = COLCAT)
    @SerializedName(COLCAT)
    private long id_clasifica;
    @DatabaseField
    private String description;
    @DatabaseField
    private String tipo;
    @DatabaseField
    private long sla;

    private String form;
    private String lista;

    public void setForm(List<Questions> form) {
        String tmp = "";
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
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
            tmp = gson.toJson(form);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.form = tmp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId_clasifica() { return id_clasifica; }

    public void setId_clasifica(long id_clasifica) { this.id_clasifica = id_clasifica; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public long getSla() {
        return sla;
    }

    public void setSla(long sla) {
        this.sla = sla;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getLista() {
        return lista;
    }

    public void setLista(String lista) {
        this.lista = lista;
    }
}
