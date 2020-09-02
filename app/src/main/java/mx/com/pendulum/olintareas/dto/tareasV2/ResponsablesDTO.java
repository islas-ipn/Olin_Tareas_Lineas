package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import mx.com.pendulum.olintareas.db.TableNames;

/**
 * Created by evaleriano on 8/15/2017.
 */
@DatabaseTable(tableName = TableNames.RESPONSABLES_DTO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsablesDTO {

    public final static String COL_ID = "_id";
    public final static String COLID = "id";
    public final static String NOMBRE = "nombre";
    public final static String EMAIL = "mail";
    public final static String SUPERVISOR = "supervisor";
    public final static String CVE_BUSQUEDA = "cvebusqueda";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected Long _id;
    @DatabaseField(columnName = COLID)
    private String id;
    @DatabaseField
    private String supervisor;
    @DatabaseField
    private String nombre;
    @DatabaseField
    private String mail;
    @DatabaseField
    private String cvebusqueda;
    @DatabaseField
    private String estatuscuenta;
    @DatabaseField
    private String iddynamics;
    @DatabaseField
    private String regional;
    @DatabaseField
    private String depto;


    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCvebusqueda() {
        return cvebusqueda;
    }

    public void setCvebusqueda(String cvebusqueda) {
        this.cvebusqueda = cvebusqueda;
    }

    public String getEstatuscuenta() {
        return estatuscuenta;
    }

    public void setEstatuscuenta(String estatuscuenta) {
        this.estatuscuenta = estatuscuenta;
    }

    public String getIddynamics() {
        return iddynamics;
    }

    public void setIddynamics(String iddynamics) {
        this.iddynamics = iddynamics;
    }

    public String getRegional() {
        return regional;
    }

    public void setRegional(String regional) {
        this.regional = regional;
    }

    public String getDepto() {
        return depto;
    }

    public void setDepto(String depto) {
        this.depto = depto;
    }


}
