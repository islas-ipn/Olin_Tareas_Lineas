package mx.com.pendulum.olintareas.dto.tareasV2;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.EpochDate)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpochDate implements Serializable {


    public final static String COL_ID = "_id";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    @JsonIgnore
    protected Long _id;


    @DatabaseField
    private Long epoch;
    @DatabaseField
    private String timeZone;
    @DatabaseField
    private String dateStr;
    @DatabaseField
    //ORIGEN -> OLIN_COBRANZA= OLC, CRM = CRM, OLIN_LEGAL= OLL
    private String origen = "OLC";


    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public Long getEpoch() {
        return epoch;
    }

    public void setEpoch(Long epoch) {
        this.epoch = epoch;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
