package mx.com.pendulum.olintareas.sync.resources;

import mx.com.pendulum.olintareas.sync.constants.IUrls;

public class Resource {
    private String name;
    private String url;
    private String notifName;
    private Class<? extends Object> clz;

    public Resource(String url, Class<? extends Object> clz) {
        this.url = url;
        this.clz = clz;
        this.name = url.substring(url.lastIndexOf('/')).toUpperCase();
        this.notifName = url.replace(IUrls.BASE_URL, "");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Class<? extends Object> getClz() {
        return clz;
    }

    public void setClz(Class<? extends Object> clz) {
        this.clz = clz;
    }

    public String toString() {
        return name;
    }

    public String getNotifName() {
        return notifName;
    }
}
