package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collection;

import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.dto.SyncDataOutObject;


/**
 * Created by jgislas on 14/07/2017.
 */

@DatabaseTable(tableName = TableNames.RESPONSETASK)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseTask extends SyncDataOutObject implements Serializable {

    public final static String COL_ID_NOTA = "idNota";

    @DatabaseField
    private Integer id;
    @DatabaseField
    private Integer order;
    @DatabaseField
    private Integer idresponse;
    @DatabaseField
    private String questionType;
    @DatabaseField
    private Integer requireddocument;
    @DatabaseField
    private String response;
    @DatabaseField
    private String question;
    @DatabaseField
    private Integer idsql;
    @DatabaseField
    private String sqlquery;
    @DatabaseField
    private Integer idtarea;
    @DatabaseField(columnName = COL_ID_NOTA)
    private Integer idNota;
    @ForeignCollectionField
    private Collection<DocumentResponse> documentResponses;


    public ResponseTask() {
    }

    public ResponseTask(String question, String response) {
        this.question = question;
        this.response = response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getIdresponse() {
        return idresponse;
    }

    public void setIdresponse(Integer idresponse) {
        this.idresponse = idresponse;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Integer getRequireddocument() {
        return requireddocument;
    }

    public void setRequireddocument(Integer requireddocument) {
        this.requireddocument = requireddocument;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getIdsql() {
        return idsql;
    }

    public void setIdsql(Integer idsql) {
        this.idsql = idsql;
    }

    public String getSqlquery() {
        return sqlquery;
    }

    public void setSqlquery(String sqlquery) {
        this.sqlquery = sqlquery;
    }

    public Integer getIdtarea() {
        return idtarea;
    }

    public void setIdtarea(Integer idtarea) {
        this.idtarea = idtarea;
    }

    public Collection<DocumentResponse> getDocumentResponses() {
        return documentResponses;
    }

    public void setDocumentResponses(Collection<DocumentResponse> documentResponses) {
        this.documentResponses = documentResponses;
    }

    public Integer getIdNota() {
        return idNota;
    }

    public void setIdNota(Integer idNota) {
        this.idNota = idNota;
    }
}
