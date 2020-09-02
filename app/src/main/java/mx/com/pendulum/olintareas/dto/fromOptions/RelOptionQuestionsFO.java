package mx.com.pendulum.olintareas.dto.fromOptions;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.CATALOG_FORM_REL_OPT_QNS_FO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelOptionQuestionsFO implements Serializable {

    public final static String COL_ID = "_id";
    public final static String COL_INDICE = "idoption";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected long _id;

    @DatabaseField
    private long idoption;
    @DatabaseField
    private long idquestion;

    public long getIdoption() {
        return idoption;
    }

    public void setIdoption(long idoption) {
        this.idoption = idoption;
    }

    public long getIdquestion() {
        return idquestion;
    }

    public void setIdquestion(long idquestion) {
        this.idquestion = idquestion;
    }
}
