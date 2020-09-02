package mx.com.pendulum.olintareas.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.MAIL_CONFIGURACION)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeaderApp {
    @DatabaseField
    private String app_name;
    @DatabaseField
    private String message;
    private String callFromApp;

    public HeaderApp(String message, String user) {
        this.app_name = user;
        this.message = message;
    }

    // Constructor utilizaado por el Trackin
    public HeaderApp(){}

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCallFromApp() { return callFromApp; }
    public void setCallFromApp(String callFromApp) { this.callFromApp = callFromApp; }
}
