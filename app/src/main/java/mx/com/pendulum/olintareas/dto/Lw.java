package mx.com.pendulum.olintareas.dto;


import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.utilities.Util;

import static mx.com.pendulum.olintareas.sync.constants.IUrls.CONTEXT_SEPERATOR;

@DatabaseTable(tableName = TableNames.LW)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lw extends SyncDataOutObject implements Serializable {
    private final static String COL_CATEGORY = "category";
    private final static String COL_DESCRIPTION = "description";
    private final static String COL_FILE_NAME = "file_name";
    private final static String COL_LATITUDE = "latitude";
    private final static String COL_LONGITUDE = "longitude";
    private final static String COL_INDICE_FILEGRID = "indice_filegrid";
    private final static String COL_MD5 = "md5";
    public static final int TYPE_LW = 1;

    @DatabaseField(columnName = COL_CATEGORY)
    private String categoria;
    @DatabaseField(columnName = COL_DESCRIPTION)
    private String descripcion;
    @DatabaseField(columnName = COL_FILE_NAME)
    private String nombre_archivo;
    private File imagen;
    @DatabaseField(columnName = COL_LATITUDE)
    private Double latitud;
    @DatabaseField(columnName = COL_LONGITUDE)
    private Double longitud;
    @DatabaseField(columnName = COL_MD5)
    private String md5;
    @DatabaseField(columnName = COL_INDICE_FILEGRID)
    private String indiceFilegrid;
    //1 - lw 2 - Lw_Facilidades 3 - Lw_Fotos_Facilidades
    @JsonIgnore
    private int source;

    private String getCategoria() {
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

    public String getNombre_archivo() {
        return nombre_archivo;
    }

    public void setNombre_archivo(String nombre_archivo) {
        this.nombre_archivo = nombre_archivo;
    }

    public File getImagen() {
        return imagen;
    }

    private void setImagen(File imagen) {
        this.imagen = imagen;
    }

    @JsonIgnore
    public MultipartEntity getMultipartEntity()
            throws UnsupportedEncodingException {
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.STRICT, null, Charset.forName("UTF-8"));
        entity.addPart("id", new StringBody(get_id() + ""));
        entity.addPart("username", new StringBody(getUsername()));
        entity.addPart("credito", new StringBody(getCredito()));
        entity.addPart("fecha_actividad", new StringBody(getFecha_actividad()));
        entity.addPart("categoria", new StringBody(getCategoria() == null ? " " : getCategoria(), Charset.forName("UTF-8")));
        entity.addPart("descripcion", new StringBody(getDescripcion(), Charset.forName("UTF-8")));
        entity.addPart("latitud", new StringBody("" + getLatitud()));
        entity.addPart("longitud", new StringBody("" + getLongitud()));
        entity.addPart("nombre_archivo", new StringBody(getNombre_archivo()));
        try {
            if (getNombre_archivo().contains("video"))
                entity.addPart("md5", new StringBody(Util.fileToMD5(Properties.SD_CARD_VIDEOS_DIR.concat(Properties.FILE_SEPERATOR.concat(getNombre_archivo())))));
            else
                entity.addPart("md5", new StringBody(Util.fileToMD5(Properties.SD_CARD_IMAGES_DIR.concat(Properties.FILE_SEPERATOR.concat(getNombre_archivo())))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getNombre_archivo().contains("video"))
            setImagen(new File(Properties.SD_CARD_VIDEOS_DIR, getNombre_archivo()));
        else
            setImagen(new File(Properties.SD_CARD_IMAGES_DIR, getNombre_archivo()));


        //setImagen(new File(Properties.SD_CARD_IMAGES_DIR, getNombre_archivo()));
        entity.addPart("imagen", new FileBody(imagen));

        return entity;
    }

    public boolean lwFileExists() {

        if (getNombre_archivo().contains("video"))
        return new File(Properties.SD_CARD_VIDEOS_DIR, getNombre_archivo()).exists();
            //path = Properties.SD_CARD_VIDEOS_DIR + CONTEXT_SEPERATOR + lw.getNombre_archivo();
        return new File(Properties.SD_CARD_IMAGES_DIR, getNombre_archivo()).exists();
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
    }

    private Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    private Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getIndiceFilegrid() {
        return indiceFilegrid;
    }

    public void setIndiceFilegrid(String indiceFilegrid) {
        this.indiceFilegrid = indiceFilegrid;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }
}
