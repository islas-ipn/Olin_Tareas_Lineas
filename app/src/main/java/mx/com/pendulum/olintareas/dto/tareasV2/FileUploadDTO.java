package mx.com.pendulum.olintareas.dto.tareasV2;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.dto.Lw;

@DatabaseTable(tableName = TableNames.FILE_UPLOAD_DTO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileUploadDTO implements Serializable {


    public static final String ANSWER_DTO_ID_FIELD_NAME = "answerDTO_id";

    public final static String COL_ID = "_id";
    @DatabaseField(generatedId = true, columnName = COL_ID)
    @JsonProperty("id")
    protected Long _id;

    @JsonIgnore
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = ANSWER_DTO_ID_FIELD_NAME)
    private AnswerDTO answerDTO;

    @DatabaseField
    private Long id;
    @DatabaseField
    private Long date;


    @DatabaseField(foreign = true, columnName = "id_lw", foreignAutoRefresh = true, foreignAutoCreate = true)
    private Lw lw;


    public AnswerDTO getAnswerDTO() {
        return answerDTO;
    }

    public void setAnswerDTO(AnswerDTO answerDTO) {
        this.answerDTO = answerDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Lw getLw() {
        return lw;
    }

    public void setLw(Lw lw) {
        this.lw = lw;
    }
}
