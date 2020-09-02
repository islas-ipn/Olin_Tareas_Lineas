package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.CATALOG_FORM_SUB_QUESTIONS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Questions implements Serializable {

    public final static String COL_ID = "_id";
    public final static String COLID = "id";
    public final static String COLID_CLA = "idSubClasifisica";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected long _id;

    @DatabaseField(columnName = COLID)
    private Long id;
    @DatabaseField
    private Double order;
    @DatabaseField(columnName = COLID_CLA)
    private long idSubClasifisica;
    @DatabaseField
    private String question;
    @DatabaseField
    private String type;
    @DatabaseField
    private Boolean required;
    @DatabaseField
    private Boolean requiredDocument;
    @DatabaseField
    private Long status;
    @DatabaseField
    private String type_doc;

    private Long section;
    private Document document;
    private ArrayList<Options> options;
    private int positionRow;
    private Long idEstado;
    private Long idSubclasifica;
    private boolean isExpanded = false;
    private boolean isError = false;
    private Object answer;
    private Object answer2;
    private Object object;
    private ArrayList<Obj> questionContainerDocsList = new ArrayList<>();
    private ArrayList<Obj> answerContainerDocsList = new ArrayList<>();
    private ArrayList<Obj> answerContainerCommList = new ArrayList<>();
    private boolean isFromClasifica;
    private boolean isFromSubClasifica;
    private int idPadre;
    private int idPropio;


    public boolean isFromClasifica() {
        return isFromClasifica;
    }

    public void setFromClasifica(boolean fromClasifica) {
        isFromClasifica = fromClasifica;
    }

    public boolean isFromSubClasifica() {
        return isFromSubClasifica;
    }

    public void setFromSubClasifica(boolean fromSubClasifica) {
        isFromSubClasifica = fromSubClasifica;
    }

    public int getIdPadre() {
        return idPadre;
    }

    public void setIdPadre(int idPadre) {
        this.idPadre = idPadre;
    }

    public int getIdPropio() {
        return idPropio;
    }

    public void setIdPropio(int idPropio) {
        this.idPropio = idPropio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getOrder() {
        return order;
    }

    public void setOrder(Double order) {
        this.order = order;
    }

    public long getIdSubClasifisica() {
        return idSubClasifisica;
    }

    public void setIdSubClasifisica(long idSubClasifisica) {
        this.idSubClasifisica = idSubClasifisica;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getRequiredDocument() {
        return requiredDocument;
    }

    public void setRequiredDocument(Boolean requiredDocument) {
        this.requiredDocument = requiredDocument;
    }

    public String getType_doc() {
        return type_doc;
    }

    public void setType_doc(String type_doc) {
        this.type_doc = type_doc;
    }

    public int getPositionRow() {
        return positionRow;
    }

    public void setPositionRow(int positionRow) {
        this.positionRow = positionRow;
    }


    public Long getSection() {
        return section;
    }

    public void setSection(Long section) {
        this.section = section;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public ArrayList<Options> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Options> options) {
        this.options = options;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Long idEstado) {
        this.idEstado = idEstado;
    }

    public Long getIdSubclasifica() {
        return idSubclasifica;
    }

    public void setIdSubclasifica(Long idSubclasifica) {
        this.idSubclasifica = idSubclasifica;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public Object getAnswer() {
        return answer;
    }

    public void setAnswer(Object answer) {
        this.answer = answer;
    }

    public Object getAnswer2() {
        return answer2;
    }

    public void setAnswer2(Object answer2) {
        this.answer2 = answer2;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public ArrayList<Obj> getQuestionContainerDocsList() {
        return questionContainerDocsList;
    }

    public void setQuestionContainerDocsList(ArrayList<Obj> questionContainerDocsList) {
        this.questionContainerDocsList = questionContainerDocsList;
    }

    public ArrayList<Obj> getAnswerContainerDocsList() {
        return answerContainerDocsList;
    }

    public void setAnswerContainerDocsList(ArrayList<Obj> answerContainerDocsList) {
        this.answerContainerDocsList = answerContainerDocsList;
    }

    public ArrayList<Obj> getAnswerContainerCommList() {
        return answerContainerCommList;
    }

    public void setAnswerContainerCommList(ArrayList<Obj> answerContainerCommList) {
        this.answerContainerCommList = answerContainerCommList;
    }


    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}



