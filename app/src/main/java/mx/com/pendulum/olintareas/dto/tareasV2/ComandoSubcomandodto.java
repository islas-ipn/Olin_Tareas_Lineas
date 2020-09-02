package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.CATALOGO_COMANDOS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComandoSubcomandodto {



    @DatabaseField(generatedId = true )
    private int _id;
    @DatabaseField
    private String comando;
    @DatabaseField
    private String descripcioncomando;
    @DatabaseField
    private String subcomando;
    @DatabaseField
    private String descripcionsubcomando;
    @DatabaseField
    private String comentario;
    @DatabaseField
    private int solicitar_pago;

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public String getDescripcioncomando() {
        return descripcioncomando;
    }

    public void setDescripcioncomando(String descripcioncomando) {
        this.descripcioncomando = descripcioncomando;
    }

    public String getSubcomando() {
        return subcomando;
    }

    public void setSubcomando(String subcomando) {
        this.subcomando = subcomando;
    }

    public String getDescripcionsubcomando() {
        return descripcionsubcomando;
    }

    public void setDescripcionsubcomando(String descripcionsubcomando) {
        this.descripcionsubcomando = descripcionsubcomando;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getSolicitar_pago() {
        return solicitar_pago;
    }

    public void setSolicitar_pago(int solicitar_pago) {
        this.solicitar_pago = solicitar_pago;
    }
}
