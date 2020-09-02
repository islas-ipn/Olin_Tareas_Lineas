package mx.com.pendulum.olintareas.dto.SubClasifica;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.CATALOG_FORM_SUB_REL_OPTIONS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelOptionQuery implements Serializable {

    public final static String COL_ID = "_id";
    public final static String COL_INDICE = "idoptionssql";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected long _id;

    @DatabaseField
    private long indice;
    @DatabaseField
    private long idoptionssql;

    public long getIndice() {
        return indice;
    }

    public void setIndice(long indice) {
        this.indice = indice;
    }

    public long getIdoptionssql() {
        return idoptionssql;
    }

    public void setIdoptionssql(long idoptionssql) {
        this.idoptionssql = idoptionssql;
    }
}
