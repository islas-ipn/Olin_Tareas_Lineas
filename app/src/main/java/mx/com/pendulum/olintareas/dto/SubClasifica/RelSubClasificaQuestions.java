package mx.com.pendulum.olintareas.dto.SubClasifica;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.CATALOG_FORM_SUB_REL_SUB_QNS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelSubClasificaQuestions implements Serializable {

    public final static String COL_ID = "_id";
    public final static String COL_SUB_CLA = "idSubclasifica";
    public final static String COL_STATUS = "idEstadoTarea";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected long _id;

    @DatabaseField
    private long idSubclasifica;
    @DatabaseField
    private long idQuestion;
    @DatabaseField
    private long idEstadoTarea;

    public long getIdSubclasifica() {
        return idSubclasifica;
    }

    public void setIdSubclasifica(long idSubclasifica) {
        this.idSubclasifica = idSubclasifica;
    }

    public long getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(long idQuestion) {
        this.idQuestion = idQuestion;
    }

    public long getIdEstadoTarea() {
        return idEstadoTarea;
    }

    public void setIdEstadoTarea(long idEstadoTarea) {
        this.idEstadoTarea = idEstadoTarea;
    }
}
