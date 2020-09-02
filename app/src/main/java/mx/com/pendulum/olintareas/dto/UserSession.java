package mx.com.pendulum.olintareas.dto;


import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigInteger;
import java.util.Date;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.USER_SESSION)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSession extends SyncDataOutObject {
    public final static String COL_LOGGED_IN = "logged_in";
    public final static String COL_NUM_GARANTIAS = "num_garantias";
    public final static String COL_NUM_GEOREFERENCIAS = "num_georeferencias";
    public final static String COL_NUM_GESTIONES = "num_gestiones";
    public final static String COL_NUM_LW = "num_lw";
    public final static String COL_NUM_DIRECCIONES = "num_direcciones";
    public final static String COL_NUM_CORREOS = "num_correos";
    public final static String COL_NUM_TELEFONOS = "num_telefonos";
    public final static String COL_NUM_PROBABILIDADES = "num_probabilidades";
    public final static String COL_NUM_NEGOCIACIONES = "num_negociaciones";
    public final static String COL_NUM_FACILIDADES = "num_facilidades";
    public final static String COL_NUM_TASK = "num_task";
    public final static String COL_NUM_NOTES = "num_notes";

    @DatabaseField
    private BigInteger session_id;
    @DatabaseField(dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    private Date fecha_login;
    @DatabaseField
    private String device_id;
    @DatabaseField(dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    private Date fecha_logout;
    @DatabaseField(columnName = COL_NUM_GARANTIAS)
    private int num_garantias;
    @DatabaseField(columnName = COL_NUM_GEOREFERENCIAS)
    private int num_georeferencias;
    @DatabaseField(columnName = COL_NUM_GESTIONES)
    private int num_gestiones;
    @DatabaseField(columnName = COL_NUM_LW)
    private int num_lw;
    @DatabaseField(canBeNull = false, columnName = COL_LOGGED_IN)
    @JsonIgnore
    private boolean logged_in;
    private String callFromApp;


    @DatabaseField(columnName = COL_NUM_DIRECCIONES)
    private int num_direcciones;
    @DatabaseField(columnName = COL_NUM_CORREOS)
    private int num_correos;
    @DatabaseField(columnName = COL_NUM_TELEFONOS)
    private int num_telefonos;
    @DatabaseField(columnName = COL_NUM_PROBABILIDADES)
    private int num_probabilidades;


    @DatabaseField(columnName = COL_NUM_NEGOCIACIONES)
    private int num_negociaciones;
    @DatabaseField(columnName = COL_NUM_FACILIDADES)
    private int num_facilidades;
    @DatabaseField(columnName = COL_NUM_TASK)
    private int num_task;
    @DatabaseField(columnName = COL_NUM_NOTES)
    private int num_notes;


    public int getNum_direcciones() {
        return num_direcciones;
    }

    public void setNum_direcciones(int num_direcciones) {
        this.num_direcciones = num_direcciones;
    }

    public int getNum_correos() {
        return num_correos;
    }

    public void setNum_correos(int num_correos) {
        this.num_correos = num_correos;
    }

    public int getNum_telefonos() {
        return num_telefonos;
    }

    public void setNum_telefonos(int num_telefonos) {
        this.num_telefonos = num_telefonos;
    }

    public int getNum_probabilidades() {
        return num_probabilidades;
    }

    public void setNum_probabilidades(int num_probabilidades) {
        this.num_probabilidades = num_probabilidades;
    }

    public BigInteger getSession_id() {
        return session_id;
    }

    public void setSession_id(BigInteger session_id) {
        this.session_id = session_id;
    }

    public Date getFecha_login() {
        return fecha_login;
    }

    public void setFecha_login(Date fecha_login) {
        this.fecha_login = fecha_login;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public Date getFecha_logout() {
        return fecha_logout;
    }

    public void setFecha_logout(Date fecha_logout) {
        this.fecha_logout = fecha_logout;
    }

    public int getNum_garantias() {
        return num_garantias;
    }

    public void setNum_garantias(int num_garantias) {
        this.num_garantias = num_garantias;
    }

    public int getNum_georeferencias() {
        return num_georeferencias;
    }

    public void setNum_georeferencias(int num_georeferencias) {
        this.num_georeferencias = num_georeferencias;
    }

    public int getNum_gestiones() {
        return num_gestiones;
    }

    public void setNum_gestiones(int num_gestiones) {
        this.num_gestiones = num_gestiones;
    }

    public int getNum_lw() {
        return num_lw;
    }

    public void setNum_lw(int num_lw) {
        this.num_lw = num_lw;
    }

    public boolean isLogged_in() {
        return logged_in;
    }

    public void setLogged_in(boolean logged_in) {
        this.logged_in = logged_in;
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
    }

    public int getNum_negociaciones() {
        return num_negociaciones;
    }

    public void setNum_negociaciones(int num_negociaciones) {
        this.num_negociaciones = num_negociaciones;
    }

    public int getNum_facilidades() {
        return num_facilidades;
    }

    public void setNum_facilidades(int num_facilidades) {
        this.num_facilidades = num_facilidades;
    }

    public int getNum_task() {
        return num_task;
    }

    public void setNum_task(int num_task) {
        this.num_task = num_task;
    }

    public int getNum_notes() {
        return num_notes;
    }

    public void setNum_notes(int num_notes) {
        this.num_notes = num_notes;
    }

    public String getCallFromApp() { return callFromApp; }
    public void setCallFromApp(String callFromApp) { this.callFromApp = callFromApp; }
}
