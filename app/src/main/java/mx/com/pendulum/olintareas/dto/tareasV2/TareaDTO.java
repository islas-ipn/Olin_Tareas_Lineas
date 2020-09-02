package mx.com.pendulum.olintareas.dto.tareasV2;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collection;

import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.dto.SyncDataOutObject;

@DatabaseTable(tableName = TableNames.TAREA_DTO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TareaDTO extends SyncDataOutObject implements Serializable {

    private Long tmpTareaId;
    private Long tmpNotaId;

    @DatabaseField
    private String responsable;
    @DatabaseField
    private String quasarResponsable;
    @DatabaseField
    private String mailResponsable;
    @DatabaseField
    private String idSeccion;
    @DatabaseField
    private Integer idTipoTarea;
    @DatabaseField
    private String asunto;
    @DatabaseField
    private String juicio;
    @DatabaseField(columnName = "credito")
    @JsonIgnore
    private String cred;
    @DatabaseField
    private Integer idPrioridad;
    @DatabaseField
    private String lugar;
    @DatabaseField
    private Integer idEstado;
    @DatabaseField
    private Integer subTipo;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private EpochDate fechaInicio;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private EpochDate fechaCompromiso;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private EpochDate horaInicio;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private EpochDate horaFin;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private EpochDate fechaAlta;
    @DatabaseField
    private String usuAlta;
    @DatabaseField
    private String usuAltaCorreo;
    @DatabaseField
    private String usuAltaNombre;
    @DatabaseField
    private String idGoogleCalendar;
    @DatabaseField
    private String descripcion;
    @DatabaseField
    private String descripcionHtml;
    @ForeignCollectionField
    private Collection<AnswerDTO> answers;

    @DatabaseField
    @JsonIgnore
    private transient String estado;
    @DatabaseField
    @JsonIgnore
    private transient String estadoTarea;
    @DatabaseField
    @JsonIgnore
    private transient String nombreResponsable;
    @DatabaseField
    @JsonIgnore
    private transient String tipoTarea;
    @DatabaseField
    @JsonIgnore
    private transient String tipoTareaDesc;
    @DatabaseField
    @JsonIgnore
    private transient String de;
    @DatabaseField
    @JsonIgnore
    private boolean hasSubClasifica;
    @DatabaseField
    private short idSubClasifica;
    @DatabaseField
    @JsonIgnore
    private String subClasifica;

    public String getSubClasifica() {
        return subClasifica;
    }

    public void setSubClasifica(String subClasifica) {
        this.subClasifica = subClasifica;
    }

    public boolean isHasSubClasifica() {
        return hasSubClasifica;
    }

    public void setHasSubClasifica(boolean hasSubClasifica) {
        this.hasSubClasifica = hasSubClasifica;
    }

    public short getIdSubClasifica() {
        return idSubClasifica;
    }

    public void setIdSubClasifica(short idSubClasifica) {
        this.idSubClasifica = idSubClasifica;
    }


    private Long idGrupo = 0l;


    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getQuasarResponsable() {
        return quasarResponsable;
    }

    public void setQuasarResponsable(String quasarResponsable) {
        this.quasarResponsable = quasarResponsable;
    }

    public String getMailResponsable() {
        return mailResponsable;
    }

    public void setMailResponsable(String mailResponsable) {
        this.mailResponsable = mailResponsable;
    }

    public String getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(String idSeccion) {
        this.idSeccion = idSeccion;
    }

    public Integer getIdTipoTarea() {
        return idTipoTarea;
    }

    public void setIdTipoTarea(Integer idTipoTarea) {
        this.idTipoTarea = idTipoTarea;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getJuicio() {
        return juicio;
    }

    public void setJuicio(String juicio) {
        this.juicio = juicio;
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


    public EpochDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(EpochDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public EpochDate getFechaCompromiso() {
        return fechaCompromiso;
    }

    public void setFechaCompromiso(EpochDate fechaCompromiso) {
        this.fechaCompromiso = fechaCompromiso;
    }

    public Integer getIdPrioridad() {
        return idPrioridad;
    }

    public void setIdPrioridad(Integer idPrioridad) {
        this.idPrioridad = idPrioridad;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public EpochDate getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(EpochDate horaInicio) {
        this.horaInicio = horaInicio;
    }

    public EpochDate getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(EpochDate horaFin) {
        this.horaFin = horaFin;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public Integer getSubTipo() {
        return subTipo;
    }

    public void setSubTipo(Integer subTipo) {
        this.subTipo = subTipo;
    }

    public EpochDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(EpochDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getUsuAlta() {
        return usuAlta;
    }

    public void setUsuAlta(String usuAlta) {
        this.usuAlta = usuAlta;
    }

    public String getUsuAltaCorreo() {
        return usuAltaCorreo;
    }

    public void setUsuAltaCorreo(String usuAltaCorreo) {
        this.usuAltaCorreo = usuAltaCorreo;
    }

    public String getUsuAltaNombre() {
        return usuAltaNombre;
    }

    public void setUsuAltaNombre(String usuAltaNombre) {
        this.usuAltaNombre = usuAltaNombre;
    }

    public String getIdGoogleCalendar() {
        return idGoogleCalendar;
    }

    public void setIdGoogleCalendar(String idGoogleCalendar) {
        this.idGoogleCalendar = idGoogleCalendar;
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

    public Collection<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(Collection<AnswerDTO> answers) {
        this.answers = answers;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoTarea() {
        return estadoTarea;
    }

    public void setEstadoTarea(String estadoTarea) {
        this.estadoTarea = estadoTarea;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public String getTipoTarea() {
        return tipoTarea;
    }

    public void setTipoTarea(String tipoTarea) {
        this.tipoTarea = tipoTarea;
    }

    public String getTipoTareaDesc() {
        return tipoTareaDesc;
    }

    public void setTipoTareaDesc(String tipoTareaDesc) {
        this.tipoTareaDesc = tipoTareaDesc;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }


    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
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

    /*public long getIdRespaldo() {
        return idRespaldo;
    }

    public void setIdRespaldo(long idRespaldo) {
        this.idRespaldo = idRespaldo;
    }*/
}
