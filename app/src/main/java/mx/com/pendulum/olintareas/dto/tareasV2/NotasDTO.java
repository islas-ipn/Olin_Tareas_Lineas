package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;
import java.util.List;

import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.dto.SyncDataOutObject;


/**
 * Created by evaleriano on 7/27/2017.
 */
@DatabaseTable(tableName = TableNames.NOTAS_DTO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotasDTO extends SyncDataOutObject {


    public final static String COLID = "id";
    public final static String COL_ID_TAREA = "idTarea";
//    @DatabaseField(generatedId = true, columnName = COL_ID)
//    @JsonProperty("id")
//    protected Long _id;


    @DatabaseField(columnName = COLID)
    private Long id;
    @DatabaseField(columnName = COL_ID_TAREA)
    private Long idTarea;
    @DatabaseField
    private String comentario;
    @DatabaseField
    private String estado;
    @DatabaseField
    private String motivo;
    @DatabaseField
    private String accion;
    @DatabaseField
    private String respuesta;
    @DatabaseField
    private String resQuasar;
    @DatabaseField
    private String resNombre;
    @DatabaseField
    private String resPuesto;
    @DatabaseField
    private String resCorreo;
    @DatabaseField
    private String resSuperv;
    @DatabaseField
    private String horaIni;
    @DatabaseField
    private String horaFin;
    @DatabaseField
    private String fechaAlta;
    @DatabaseField
    private String fechaComp;

    private boolean fromPencel;
    private List<AnswerDTO> answerDTOList;

    private Collection<ResponseTask> responseNotes;

    private List<NoteResponseDTO> noteResponseDTO;

    private boolean parcialSave;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getAccion() {
        return accion;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getResQuasar() {
        return resQuasar;
    }

    public void setResQuasar(String resQuasar) {
        this.resQuasar = resQuasar;
    }

    public String getResNombre() {
        return resNombre;
    }

    public void setResNombre(String resNombre) {
        this.resNombre = resNombre;
    }

    public String getResPuesto() {
        return resPuesto;
    }

    public void setResPuesto(String resPuesto) {
        this.resPuesto = resPuesto;
    }

    public String getResCorreo() {
        return resCorreo;
    }

    public void setResCorreo(String resCorreo) {
        this.resCorreo = resCorreo;
    }

    public String getResSuperv() {
        return resSuperv;
    }

    public void setResSuperv(String resSuperv) {
        this.resSuperv = resSuperv;
    }

    public String getHoraIni() {
        return horaIni;
    }

    public void setHoraIni(String horaIni) {
        this.horaIni = horaIni;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getFechaComp() {
        return fechaComp;
    }

    public void setFechaComp(String fechaComp) {
        this.fechaComp = fechaComp;
    }

    public Collection<ResponseTask> getResponseNotes() {
        return responseNotes;
    }

    public void setResponseNotes(Collection<ResponseTask> responseNotes) {
        this.responseNotes = responseNotes;
    }

    public boolean isFromPencel() {
        return fromPencel;
    }

    public void setFromPencel(boolean fromPencel) {
        this.fromPencel = fromPencel;
    }

    public List<NoteResponseDTO> getNoteResponseDTO() {
        return noteResponseDTO;
    }

    public void setNoteResponseDTO(List<NoteResponseDTO> noteResponseDTO) {
        this.noteResponseDTO = noteResponseDTO;
    }

    public List<AnswerDTO> getAnswerDTOList() {
        return answerDTOList;
    }

    public void setAnswerDTOList(List<AnswerDTO> answerDTOList) {
        this.answerDTOList = answerDTOList;
    }

    public boolean isParcialSave() {
        return parcialSave;
    }

    public void setParcialSave(boolean parcialSave) {
        this.parcialSave = parcialSave;
    }
}
