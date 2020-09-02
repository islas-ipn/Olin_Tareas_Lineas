package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.CATALOG_FORM_SUB_QUES_DOCUMENTS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document implements Serializable {

    public final static String COL_ID = "_id";
    public final static String COLID = "id";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected long _id;

    @DatabaseField(columnName = COLID)
    private Long id;
    @DatabaseField
    private Long size;
    @DatabaseField
    private Integer required;
    @DatabaseField
    private String type_doc;
    @DatabaseField
    private String extension;
    @DatabaseField
    private Long max;
    @DatabaseField
    private String docDescription;

    public String getType_doc() { return type_doc; }

    public void setType_doc(String type_doc) { this.type_doc = type_doc; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getDocDescription() {
        return docDescription;
    }

    public void setDocDescription(String docDescription) {
        this.docDescription = docDescription;
    }
}
