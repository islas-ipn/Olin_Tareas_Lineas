package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import mx.com.pendulum.olintareas.db.TableNames;


@DatabaseTable(tableName = TableNames.TEMP_FORM)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemporalForm implements Serializable {


    public final static String COL_ID_OPTION = "idOption";
    public final static String COL_ID_QUESTION = "idQuestion";
    public final static String COL_ID_ANSWERS = "answers";
    public final static String COL_PARCIAL_SAVE = "parcialSave";
    public final static String COL_ID_NOTA = "idNota";
    public final static String COL_ID_TAREA = "idTarea";


    @DatabaseField
    private Integer idOption;
    @DatabaseField
    private Long idQuestion;
    @DatabaseField
    private String answers;//respuestas del cuestionario
    @DatabaseField
    private boolean parcialSave;
    @DatabaseField
    private Long idTarea;
    @DatabaseField
    private Long idNota;


    public Integer getIdOption() {
        return idOption;
    }

    public void setIdOption(Integer idOption) {
        this.idOption = idOption;
    }

    public Long getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(Long idQuestion) {
        this.idQuestion = idQuestion;
    }

    public String getAnswers() {
        return answers;
    }


    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public boolean isParcialSave() {
        return parcialSave;
    }

    public void setParcialSave(boolean parcialSave) {
        this.parcialSave = parcialSave;
    }


    public Long getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }

    public Long getIdNota() {
        return idNota;
    }

    public void setIdNota(Long idNota) {
        this.idNota = idNota;
    }
}
