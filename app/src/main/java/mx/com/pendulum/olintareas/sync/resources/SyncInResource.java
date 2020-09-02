package mx.com.pendulum.olintareas.sync.resources;

import java.io.File;

import mx.com.pendulum.olintareas.interfaces.GenericDaoHelper;

public class SyncInResource  extends Resource {
    private GenericDaoHelper helper;
    private File file;
    private String json;

	public SyncInResource(String url, Class<? extends Object> clz,
            GenericDaoHelper helper) {
        super(url, clz);
        this.helper = helper;
    }

	public SyncInResource(String url, Class<? extends Object> clz,
            GenericDaoHelper helper, String json) {
        super(url, clz);
        this.helper = helper;
        this.json= json;
    }

    public GenericDaoHelper getHelper() {
        return helper;
    }

    public void setHelper(GenericDaoHelper helper) {
        this.helper = helper;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
