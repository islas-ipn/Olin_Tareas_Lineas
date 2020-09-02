package mx.com.pendulum.olintareas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.USER_PERMISSION)
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserPermissions {
    @DatabaseField(generatedId = true)
    private int _id;
    @DatabaseField
    private Integer permisos_cobranza;
    @DatabaseField
    private String cobranza;
    @DatabaseField
    private Integer permisos_legal;
    @DatabaseField
    private String legal;
    @DatabaseField
    private Integer tipo_usuario;
    @DatabaseField
    private String tipo_usuario_desc;

    UserPermissions() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Integer getPermisos_cobranza() {
        return permisos_cobranza;
    }

    public void setPermisos_cobranza(Integer permisos_cobranza) {
        this.permisos_cobranza = permisos_cobranza;
    }

    public String getCobranza() {
        return cobranza;
    }

    public void setCobranza(String cobranza) {
        this.cobranza = cobranza;
    }

    public Integer getPermisos_legal() {
        return permisos_legal;
    }

    public void setPermisos_legal(Integer permisos_legal) {
        this.permisos_legal = permisos_legal;
    }

    public String getLegal() {
        return legal;
    }

    public void setLegal(String legal) {
        this.legal = legal;
    }

    public Integer getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(Integer tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    public String getTipo_usuario_desc() {
        return tipo_usuario_desc;
    }

    public void setTipo_usuario_desc(String tipo_usuario_desc) {
        this.tipo_usuario_desc = tipo_usuario_desc;
    }

}