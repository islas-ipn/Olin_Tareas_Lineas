package mx.com.pendulum.olintareas.dto.tareasV2;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collection;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.ANSWER_DTO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnswerDTO implements Serializable {
    public static final String TAREA_DTO_ID_FIELD_NAME = "tareaDTO_id";
    public static final String NOTA_DTO_ID_FIELD_NAME = "notaDTO_id";

    public final static String COL_ID = "_id";
    @DatabaseField(generatedId = true, columnName = COL_ID)
    @JsonProperty("id")
    protected Long _id;

    @JsonIgnore
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = TAREA_DTO_ID_FIELD_NAME)
    private TareaDTO tareaDTO;

    @JsonIgnore
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = NOTA_DTO_ID_FIELD_NAME)
    private NotaDTO notaDTO;

    @DatabaseField
    private Long id_question;
    @DatabaseField
    private Integer id_option;
    @DatabaseField
    private String value;
    @DatabaseField
    private String comment;
    @DatabaseField(foreign = true, columnName = "id_epochDate", foreignAutoRefresh = true, foreignAutoCreate = true)
    private EpochDate epochDate;
    @DatabaseField
    private String type;
    @ForeignCollectionField
    private Collection<FileUploadDTO> files;
    @DatabaseField
    private String indices;

    @JsonIgnore
    @DatabaseField
    private String question;
    @JsonIgnore
    @DatabaseField
    private String response;


//    private List<DateStartEndDTO> values;





    public TareaDTO getTareaDTO() {
        return tareaDTO;
    }

    public void setTareaDTO(TareaDTO tareaDTO) {
        this.tareaDTO = tareaDTO;
    }

    public NotaDTO getNotaDTO() {
        return notaDTO;
    }

    public void setNotaDTO(NotaDTO notaDTO) {
        this.notaDTO = notaDTO;
    }

    public Long getId_question() {
        return id_question;
    }

    public void setId_question(Long id_question) {
        this.id_question = id_question;
    }

    public Integer getId_option() {
        return id_option;
    }

    public void setId_option(Integer id_option) {
        this.id_option = id_option;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public EpochDate getEpochDate() {
        return epochDate;
    }

    public void setEpochDate(EpochDate epochDate) {
        this.epochDate = epochDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Collection<FileUploadDTO> getFiles() {
        return files;
    }

    public void setFiles(Collection<FileUploadDTO> files) {
        this.files = files;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getIndices() {
        return indices;
    }

    public void setIndices(String indices) {
        this.indices = indices;
    }
}
