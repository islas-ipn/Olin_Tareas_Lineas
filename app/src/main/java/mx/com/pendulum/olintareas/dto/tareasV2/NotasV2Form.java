package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

import mx.com.pendulum.olintareas.db.TableNames;

/**
 * Created by evaleriano on 7/28/2017.
 */
@DatabaseTable(tableName = TableNames.CATALOG_NOTE_FORM)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotasV2Form  implements Serializable {

    public final static String COL_ID = "_id";
    public final static String COL_ID_ESTADO = "idEstado";
    public final static String COL_ID_SUBCLASIFICA = "idSubclasifica";
    public final static String COLQUESTIOND = "questions";
    public final static String COL_ID_TAREA = "idTarea";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected Long _id;

    @DatabaseField(columnName = COL_ID_ESTADO)
    private Short idEstado;
    @DatabaseField(columnName = COL_ID_SUBCLASIFICA)
    private Short idSubclasifica;
    @DatabaseField(columnName = COLQUESTIOND)
    private String questions;
    @DatabaseField(columnName = COL_ID_TAREA)
    private Long idTarea;

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public Short getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Short idEstado) {
        this.idEstado = idEstado;
    }

    public Short getIdSubclasifica() {
        return idSubclasifica;
    }

    public void setIdSubclasifica(Short idSubclasifica) {
        this.idSubclasifica = idSubclasifica;
    }

    public Long getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(List<Questions> questions) {
        String tmp = "";

        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
//            Gson gson = new Gson();
            tmp = gson.toJson(questions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.questions = tmp;
    }
}
