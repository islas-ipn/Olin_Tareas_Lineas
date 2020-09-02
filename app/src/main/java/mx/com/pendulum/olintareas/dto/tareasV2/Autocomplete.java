package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.CATALOG_FORM_SUB_OPT_AUTOCOMPLETE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Autocomplete implements Serializable {

    public final static String COL_ID = "_id";
    public final static String COL_REL = "idRelSql";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected long _id;

    @DatabaseField
    private String value;
    @DatabaseField
    private String label;
    @DatabaseField
    private long idRelSql;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getIdRelSql() { return idRelSql; }

    public void setIdRelSql(long idRelSql) { this.idRelSql = idRelSql; }

    @Override
    public String toString() {
        return "{" +
                "\"value\":\"" + value + "\"" +
                ", \"label\":\"" + label + "\"" +
                '}';
    }
}
