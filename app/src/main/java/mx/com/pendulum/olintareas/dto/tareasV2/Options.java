package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.CATALOG_FORM_SUB_OPTIONS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Options implements Serializable {

    public final static String COL_ID = "_id";
    public final static String COLID = "id";
    public final static String COLID_QNS = "idquestion";
    public final static String COL_ORDER = "order";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected long _id;

    @DatabaseField(columnName = COLID)
    private Integer id;
    @DatabaseField
    private int idquestion;
    @DatabaseField
    private Double order;
    @DatabaseField
    private String option;
    @DatabaseField
    private Boolean requiredComment;
    @DatabaseField
    private Boolean requiredDocument;
    @DatabaseField
    private String regexp;
    @DatabaseField
    private String query;
    @DatabaseField
    private String queryResult;
    @DatabaseField
    private String placeHolder;
    @DatabaseField
    private Long length;
    @DatabaseField
    private String formatDate;
    @DatabaseField
    private String keyboard;
    @DatabaseField
    private Long status;
    @DatabaseField
    private int contienesubpregunta;

    private boolean isAlreadyCharged;
    private String questions;
    private Document document;
    private Comment comment;
    private ArrayList<Autocomplete> autocomplete;

    public boolean isAlreadyCharged() {
        return isAlreadyCharged;
    }

    public void setAlreadyCharged(boolean alreadyCharged) {
        isAlreadyCharged = alreadyCharged;
    }

    public int getIdquestion() {
        return idquestion;
    }

    public void setIdquestion(int idquestion) {
        this.idquestion = idquestion;
    }

    public int getContienesubpregunta() {
        return contienesubpregunta;
    }

    public void setContienesubpregunta(int contienesubpregunta) {
        this.contienesubpregunta = contienesubpregunta;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public ArrayList<Autocomplete> getAutocomplete() {
        return autocomplete;
    }

    public void setAutocomplete(ArrayList<Autocomplete> autocomplete) {
        this.autocomplete = autocomplete;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    private String respuesta;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getOrder() {
        return order;
    }

    public void setOrder(Double order) {
        this.order = order;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Boolean getRequiredComment() {
        return requiredComment;
    }

    public void setRequiredComment(Boolean requiredComment) {
        this.requiredComment = requiredComment;
    }

    public Boolean getRequiredDocument() {
        return requiredDocument;
    }

    public void setRequiredDocument(Boolean requiredDocument) {
        this.requiredDocument = requiredDocument;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(String queryResult) {
        this.queryResult = queryResult;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
    }

    public String getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(String keyboard) {
        this.keyboard = keyboard;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
}
