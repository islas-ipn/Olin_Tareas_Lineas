package mx.com.pendulum.olintareas.sync.resources;

import org.json.JSONArray;

import mx.com.pendulum.olintareas.dto.Lw;

public class SyncOutResource extends Resource {
    public static int TYPE_JSON = 1;
    public static int TYPE_LW = 2;

    private String username;
    private String selectionColumn;
    private JSONArray json;
    private Lw lw;
    private int type;
    private String strJson;

    public SyncOutResource(String url, Class<? extends Object> clz,
                           String username, String selectionColumn) {
        super(url, clz);
        this.username = username;
        this.selectionColumn = selectionColumn;
        if (clz.equals(Lw.class))
            setType(TYPE_LW);
        else
            setType(TYPE_JSON);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSelectionColumn() {
        return selectionColumn;
    }

    public void setSelectionColumn(String selectionColumn) { this.selectionColumn = selectionColumn; }

    public JSONArray getJson() {
        return json;
    }

    public void setJson(JSONArray json) {
        this.json = json;
    }

    public Lw getLw() {
        return lw;
    }

    public void setLw(Lw lw) {
        this.lw = lw;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStrJson() {
        return strJson;
    }

    public void setStrJson(String strJson) {
        this.strJson = strJson;
    }

    @Override
    public String toString() {
        return "SyncOutResource [username=" + username + ", selectionColumn="
                + selectionColumn + ", json=" + json
                + ", lw=" + lw
                + ", type=" + type + ", strJson=" + strJson + ", incidencia="
               + "]";
    }
}