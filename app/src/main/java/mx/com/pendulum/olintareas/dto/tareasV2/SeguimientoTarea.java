package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;


import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.dto.SyncDataOutObject;
import mx.com.pendulum.utilities.Tools;

/**
 * Created by jgislas on 14/07/2017.
 */

@DatabaseTable(tableName = TableNames.SEGUIMIENTOTAREA)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeguimientoTarea extends SyncDataOutObject implements Serializable {

    public final static String FECHA_COMPPROMISO = "fechaCompromiso";
    public final static String ID = "id";
    public final static String COL_ID_TAREA = "idtarea";


    //////////////////////////////

    @DatabaseField
    private Integer id;
    @DatabaseField(columnName = COL_ID_TAREA)
    private Integer idtarea;
    @DatabaseField
    private String de;
    @DatabaseField
    private String asunto;//asunto
    @DatabaseField
    private String descripcion;//
    @DatabaseField
    private Long idPrioridad;//prioridad
    @DatabaseField
    private String descPrioridad;
    @DatabaseField
    private Integer idTipo;
    @DatabaseField
    private String descTipo;
    @DatabaseField
    private Long idEstado;//no se ocupa para agregar tarea   cerrado proceso asignado
    @DatabaseField
    private String descEstado;
    @DatabaseField
    private Integer idSubtipo;
    @DatabaseField
    private String descSubtipo;
    @DatabaseField(columnName = "credito")
    private String cred;
    @DatabaseField
    private String juicio;
    @DatabaseField
    private String lugar;
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
    private String solQuasar;
    @DatabaseField
    private String solNombre;
    @DatabaseField
    private String solPuesto;

    @DatabaseField

    private String solCorreo;

    @DatabaseField

    private String solSuperv;

    @DatabaseField

    private String origen;
    @DatabaseField
    private Integer mesaControl;
    @DatabaseField
    private String fechaFin;
    @DatabaseField
    private String fechaInicio;
    @DatabaseField
    private String fechaAlta;
    @DatabaseField
    private String fechaOriginal;
    @DatabaseField
    private String fechaCompromiso;
    private ArrayList<NotasDTO> notas;
    private ArrayList<ResponseTask> responseTasks;
    @DatabaseField
    private String urlQuasarLeg;
    @DatabaseField
    private String urlQuasarCob;
    @DatabaseField
    private String estatus;
    @Deprecated
    @DatabaseField
    private Integer tipotarea;
    @Deprecated
    @DatabaseField
    private Integer estado;
    @Deprecated
    @DatabaseField
    private Integer subtipo;
    @Deprecated
    @DatabaseField
    private String estadotarea;
    @Deprecated
    @DatabaseField
    private String tipotareaDesc;
    @Deprecated
    @DatabaseField
    private String prioridad;
    @Deprecated
    @DatabaseField
    private String resonsehtml;
    @DatabaseField
    private String deudorNombre;
    @DatabaseField
    private String deudorDireccion;
    private boolean notaParcialSave;
    private boolean isSeguimiento;

    @DatabaseField
    @JsonIgnore
    private boolean hasSubClasifica;
    @DatabaseField
    @JsonIgnore
    private short idSubClasifica;
    @DatabaseField
    @JsonIgnore
    private String subClasifica;

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

    public String getSubClasifica() {
        return subClasifica;
    }

    public void setSubClasifica(String subClasifica) {
        this.subClasifica = subClasifica;
    }

    public SeguimientoTarea() {
    }


    public SeguimientoTarea(String resNombre,
                            String descEstado,
                            String de,
                            String fechaOriginal,
                            String credito,
                            String solNombre,
                            String tipotareaDesc,
                            String asunto,
                            String fechaCompromiso,
                            String descripcion,
                            Integer estado,
                            Integer idSubtipo,
                            Integer id,
                            String juicio,
                            Integer tipoTarea,
                            String subClasifica) {

        this.resNombre = resNombre;
        this.descEstado = descEstado;
        this.de = de;
        this.fechaOriginal = fechaOriginal;
        this.credito = credito;
        this.solNombre = solNombre;
        this.tipotareaDesc = tipotareaDesc;
        this.asunto = asunto;
        this.subClasifica = subClasifica;
        this.fechaCompromiso = fechaCompromiso;
        this.descripcion = descripcion;
        this.estado = estado;
        this.idSubtipo = idSubtipo;
        this.id = id;
        this.juicio = juicio;
        this.tipotarea = tipoTarea;
        this.idtarea = id;

    }


    public static String convertirFecha(long epoch) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(epoch * 1000);
        return Tools.getDate(cal, "-");
//        return new java.text.SimpleDateFormat("dd/MMM/yyyy").format(new java.util.Date(epoch * 1000));
    }

    public static String convertirFecha(String fechaComp) {
//        return "";
        if (fechaComp == null) return "";
        Long epoch = Long.valueOf(fechaComp);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(epoch * 1000);
        return Tools.getDate(cal, "-");
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdtarea() {
        return idtarea;
    }

    public void setIdtarea(Integer idtarea) {
        this.idtarea = idtarea;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getIdPrioridad() {
        return idPrioridad;
    }

    public void setIdPrioridad(Long idPrioridad) {
        this.idPrioridad = idPrioridad;
    }

    public String getDescPrioridad() {
        return descPrioridad;
    }

    public void setDescPrioridad(String descPrioridad) {
        this.descPrioridad = descPrioridad;
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public String getDescTipo() {
        return descTipo;
    }

    public void setDescTipo(String descTipo) {
        this.descTipo = descTipo;
    }

    public Long getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Long idEstado) {
        this.idEstado = idEstado;
    }

    public String getDescEstado() {
        return descEstado;
    }

    public void setDescEstado(String descEstado) {
        this.descEstado = descEstado;
    }

    public Integer getIdSubtipo() {
        return idSubtipo;
    }

    public void setIdSubtipo(Integer idSubtipo) {
        this.idSubtipo = idSubtipo;
    }

    public String getDescSubtipo() {
        return descSubtipo;
    }

    public void setDescSubtipo(String descSubtipo) {
        this.descSubtipo = descSubtipo;
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

    public String getJuicio() {
        return juicio;
    }

    public void setJuicio(String juicio) {
        this.juicio = juicio;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
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

    public String getSolQuasar() {
        return solQuasar;
    }

    public void setSolQuasar(String solQuasar) {
        this.solQuasar = solQuasar;
    }

    public String getSolNombre() {
        return solNombre;
    }

    public void setSolNombre(String solNombre) {
        this.solNombre = solNombre;
    }

    public String getSolPuesto() {
        return solPuesto;
    }

    public void setSolPuesto(String solPuesto) {
        this.solPuesto = solPuesto;
    }

    public String getSolCorreo() {
        return solCorreo;
    }

    public void setSolCorreo(String solCorreo) {
        this.solCorreo = solCorreo;
    }

    public String getSolSuperv() {
        return solSuperv;
    }

    public void setSolSuperv(String solSuperv) {
        this.solSuperv = solSuperv;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public Integer getMesaControl() {
        return mesaControl;
    }

    public void setMesaControl(Integer mesaControl) {
        this.mesaControl = mesaControl;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getFechaOriginal() {
        return fechaOriginal;
    }

    public void setFechaOriginal(String fechaOriginal) {
        this.fechaOriginal = fechaOriginal;
    }

    public String getFechaCompromiso() {
        return fechaCompromiso;
    }

    public void setFechaCompromiso(String fechaCompromiso) {
        this.fechaCompromiso = fechaCompromiso;
    }

    public ArrayList<NotasDTO> getNotas() {
        return notas;
    }

    public void setNotas(ArrayList<NotasDTO> notas) {
        this.notas = notas;
    }

    public ArrayList<ResponseTask> getResponseTasks() {
        return responseTasks;
    }

    public void setResponseTasks(ArrayList<ResponseTask> responseTasks) {
        this.responseTasks = responseTasks;
    }

    public String getUrlQuasarLeg() {
        return urlQuasarLeg;
    }

    public void setUrlQuasarLeg(String urlQuasarLeg) {
        this.urlQuasarLeg = urlQuasarLeg;
    }

    public String getUrlQuasarCob() {
        return urlQuasarCob;
    }

    public void setUrlQuasarCob(String urlQuasarCob) {
        this.urlQuasarCob = urlQuasarCob;
    }

    public Integer getTipotarea() {
        return tipotarea;
    }

    public void setTipotarea(Integer tipotarea) {
        this.tipotarea = tipotarea;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getSubtipo() {
        return subtipo;
    }

    public void setSubtipo(Integer subtipo) {
        this.subtipo = subtipo;
    }

    public String getEstadotarea() {
        return estadotarea;
    }

    public void setEstadotarea(String estadotarea) {
        this.estadotarea = estadotarea;
    }

    public String getTipotareaDesc() {
        return tipotareaDesc;
    }

    public void setTipotareaDesc(String tipotareaDesc) {
        this.tipotareaDesc = tipotareaDesc;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public boolean isSeguimiento() {
        return isSeguimiento;
    }

    public void setSeguimiento(boolean seguimiento) {
        isSeguimiento = seguimiento;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getResonsehtml() {
        return resonsehtml;
    }

    public void setResonsehtml(String resonsehtml) {
        this.resonsehtml = resonsehtml;
    }

    public boolean isNotaParcialSave() {
        return notaParcialSave;
    }

    public void setNotaParcialSave(boolean notaParcialSave) {
        this.notaParcialSave = notaParcialSave;
    }

    public String getDeudorNombre() {
        return deudorNombre;
    }

    public void setDeudorNombre(String deudorNombre) {
        this.deudorNombre = deudorNombre;
    }

    public String getDeudorDireccion() {
        return deudorDireccion;
    }

    public void setDeudorDireccion(String deudorDireccion) {
        this.deudorDireccion = deudorDireccion;
    }
}
