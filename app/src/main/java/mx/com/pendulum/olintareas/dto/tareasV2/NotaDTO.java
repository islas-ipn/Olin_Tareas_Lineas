package mx.com.pendulum.olintareas.dto.tareasV2;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collection;

import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.dto.SyncDataOutObject;

@DatabaseTable(tableName = TableNames.NOTA_DTO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotaDTO extends SyncDataOutObject implements Serializable {

//    public final static String COL_ID = "_id";
//    @DatabaseField(generatedId = true, columnName = COL_ID)
//    @JsonProperty("id")
//    protected Long _id;

    public final static String COL_ID_TAREA = "idTarea";
    public final static String COL_PARCIAL_SAVE = "parcialSave";


    private Long tmpTareaId;
    private Long tmpNotaId;

    @DatabaseField
    @JsonIgnore
    private boolean parcialSave;

    @DatabaseField
    private Long idEstado;   //
    @DatabaseField(columnName = COL_ID_TAREA)
    private Integer idTarea;//
    @DatabaseField(foreign = true, columnName = "id_fechaComp", foreignAutoRefresh = true, foreignAutoCreate = true)
    private EpochDate fechaComp;  //fecha fin
    @DatabaseField(foreign = true, columnName = "id_fechaAlta", foreignAutoRefresh = true, foreignAutoCreate = true)
    private EpochDate fechaAlta;//fecha en que que se captura
    @DatabaseField(foreign = true, columnName = "id_horaIni", foreignAutoRefresh = true, foreignAutoCreate = true)
    private EpochDate horaIni;//en caso cita hora inico
    @DatabaseField(foreign = true, columnName = "id_horaFin", foreignAutoRefresh = true, foreignAutoCreate = true)
    private EpochDate horaFin;//en caso cita hora fin
    @DatabaseField
    private Integer tipoTarea;//cita o tarea
    @DatabaseField
    private String quasar;//usuario
    @DatabaseField
    private String correoQuasar;//correo usuarioi
    @DatabaseField
    private String nombreQuasar;//nombre usuario
    @DatabaseField(columnName = "credito")
    @JsonIgnore
    private String cred;
    @DatabaseField
    private Long juicio;//
    @DatabaseField
    private String comandoAccion;//
    @DatabaseField
    private String comandoResultado;//
    @DatabaseField
    private String descripcion;//max 4000 chars
    @DatabaseField
    private String descripcionHtml;//igual a descripccion
    @DatabaseField
    private String descripcionBitacora;//descripcion en caso de que sea codigo de accion y resultado a 2000 chars
    @DatabaseField
    private String nvoResNombre;//reasignadi
    @DatabaseField
    private String nvoResQuasar;//reasignado quasar
    @DatabaseField
    private String nvoResCorreo; //reasignado correo
    @ForeignCollectionField
    private Collection<AnswerDTO> answers;//respuestas del cuestionario

    @DatabaseField
    @JsonIgnore
    private String estado;

    //Olin cobranza = 2, olin legal = 3
    private Short procedencia = 2;


    public Short getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(Short procedencia) {
        this.procedencia = procedencia;
    }


    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Long idEstado) {
        this.idEstado = idEstado;
    }

    public EpochDate getFechaComp() {
        return fechaComp;
    }

    public void setFechaComp(EpochDate fechaComp) {
        this.fechaComp = fechaComp;
    }

    public Integer getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Integer idTarea) {
        this.idTarea = idTarea;
    }

    public EpochDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(EpochDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public EpochDate getHoraIni() {
        return horaIni;
    }

    public void setHoraIni(EpochDate horaIni) {
        this.horaIni = horaIni;
    }

    public EpochDate getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(EpochDate horaFin) {
        this.horaFin = horaFin;
    }

    public Integer getTipoTarea() {
        return tipoTarea;
    }

    public void setTipoTarea(Integer tipoTarea) {
        this.tipoTarea = tipoTarea;
    }


    public String getQuasar() {
        return quasar;
    }

    public void setQuasar(String quasar) {
        this.quasar = quasar;
    }

    public String getCorreoQuasar() {
        return correoQuasar;
    }

    public void setCorreoQuasar(String correoQuasar) {
        this.correoQuasar = correoQuasar;
    }

    public String getNombreQuasar() {
        return nombreQuasar;
    }

    public void setNombreQuasar(String nombreQuasar) {
        this.nombreQuasar = nombreQuasar;
    }

    @Override
    public String getCredito() {
        return credito;
    }

    @Override
    public void setCredito(String credito) {
        this.cred = credito;
        this.credito = credito;
    }

    public String getCred() {
        return credito;
    }

    public void setCred(String cred) {
        this.cred = cred;
    }


    public Long getJuicio() {
        return juicio;
    }

    public void setJuicio(Long juicio) {
        this.juicio = juicio;
    }

    public String getComandoAccion() {
        return comandoAccion;
    }

    public void setComandoAccion(String comandoAccion) {
        this.comandoAccion = comandoAccion;
    }

    public String getComandoResultado() {
        return comandoResultado;
    }

    public void setComandoResultado(String comandoResultado) {
        this.comandoResultado = comandoResultado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcionHtml() {
        return descripcionHtml;
    }

    public void setDescripcionHtml(String descripcionHtml) {
        this.descripcionHtml = descripcionHtml;
    }

    public String getDescripcionBitacora() {
        return descripcionBitacora;
    }

    public void setDescripcionBitacora(String descripcionBitacora) {
        this.descripcionBitacora = descripcionBitacora;
    }

    public String getNvoResNombre() {
        return nvoResNombre;
    }

    public void setNvoResNombre(String nvoResNombre) {
        this.nvoResNombre = nvoResNombre;
    }

    public String getNvoResQuasar() {
        return nvoResQuasar;
    }

    public void setNvoResQuasar(String nvoResQuasar) {
        this.nvoResQuasar = nvoResQuasar;
    }

    public String getNvoResCorreo() {
        return nvoResCorreo;
    }

    public void setNvoResCorreo(String nvoResCorreo) {
        this.nvoResCorreo = nvoResCorreo;
    }

    public Collection<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(Collection<AnswerDTO> answers) {
        this.answers = answers;
    }

    public boolean isParcialSave() {
        return parcialSave;
    }

    public void setParcialSave(boolean parcialSave) {
        this.parcialSave = parcialSave;
    }

    public Long getTmpTareaId() {
        return tmpTareaId;
    }

    public void setTmpTareaId(Long tmpTareaId) {
        this.tmpTareaId = tmpTareaId;
    }

    public Long getTmpNotaId() {
        return tmpNotaId;
    }

    public void setTmpNotaId(Long tmpNotaId) {
        this.tmpNotaId = tmpNotaId;
    }


    //    @Override
//    public String toString() {
//
//        return new Gson().toJson(this);
//
//
//    }
}
