package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import mx.com.pendulum.olintareas.db.TableNames;

/**
 * Created by evaleriano on 8/1/2017.
 */

@DatabaseTable(tableName = TableNames.DOCUMENT_RESPONSE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentResponse implements Serializable {


    @JsonIgnore
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private ResponseTask responseTask;


    @JsonIgnore
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private NoteResponseDTO noteResponseDTO;

    @DatabaseField
    private Long id;
    @DatabaseField
    private String categoria;
    @DatabaseField
    private String descripcion;
    @DatabaseField
    private String pathdomain;
    @DatabaseField
    private String extension;





    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPathdomain() {
        return pathdomain;
    }

    public void setPathdomain(String pathdomain) {
        this.pathdomain = pathdomain;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public ResponseTask getResponseTask() {
        return responseTask;
    }

    public void setResponseTask(ResponseTask responseTask) {
        this.responseTask = responseTask;
    }

    public NoteResponseDTO getNoteResponseDTO() {
        return noteResponseDTO;
    }

    public void setNoteResponseDTO(NoteResponseDTO noteResponseDTO) {
        this.noteResponseDTO = noteResponseDTO;
    }
}
