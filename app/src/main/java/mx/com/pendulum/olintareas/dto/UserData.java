package mx.com.pendulum.olintareas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.db.dao.UserDataDaoImpl;

@DatabaseTable(tableName = TableNames.USER_DATA, daoClass = UserDataDaoImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserData {
    public final static String COL_ID = "_id";
    public final static String COL_ID_PERMISSION = "id_permission";
    public final static String COL_ID_SESSION = "id_session";

    @DatabaseField(generatedId = true)
    private int _id;
    @DatabaseField(index = true)
    private String username;
    @DatabaseField
    private String password;
    @DatabaseField
    private String nombre;
    @DatabaseField(canBeNull = false, foreign = true, columnName = COL_ID_PERMISSION, foreignAutoRefresh = true)
    private UserPermissions permisos = new UserPermissions();
    @DatabaseField
    private int estatus_contrasenia;
    @DatabaseField
    private String estado_usuario;
    @DatabaseField(canBeNull = false, foreign = true, columnName = COL_ID_SESSION, foreignAutoRefresh = true)
    private UserSession session = new UserSession();
    @DatabaseField
    private String correo;
    @DatabaseField
    private String userLock;

    @DatabaseField
    private Integer empresaId;


    public Integer getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Integer empresaId) {
        this.empresaId = empresaId;
    }


    public UserData() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public UserPermissions getPermisos() {
        return permisos;
    }

    public void setPermisos(UserPermissions permisos) {
        this.permisos = permisos;
    }

    public int getEstatus_contrasenia() {
        return estatus_contrasenia;
    }

    public void setEstatus_contrasenia(int estatus_contrasenia) {
        this.estatus_contrasenia = estatus_contrasenia;
    }

    public String getEstado_usuario() {
        return estado_usuario;
    }

    public void setEstado_usuario(String estado_usuario) {
        this.estado_usuario = estado_usuario;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        session.setUsername(getUsername());
        this.session = session;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUserLock() {
        return userLock;
    }

    public void setUserLock(String userLock) {
        this.userLock = userLock;
    }

}
