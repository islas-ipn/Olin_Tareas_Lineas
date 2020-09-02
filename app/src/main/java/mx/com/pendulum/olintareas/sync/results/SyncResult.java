package mx.com.pendulum.olintareas.sync.results;

import org.apache.http.Header;

import java.util.ArrayList;

import mx.com.pendulum.utilities.ErrorReport;

public class SyncResult {
    private boolean successful;
    private ErrorReport error;
    private String tag;
    private Header[] hedaer_exito;
    private Header[] hedaer_fail;
    private ArrayList<Object> subForms;

    public ArrayList<Object> getSubForms() {
        return subForms;
    }

    public void setSubForms(ArrayList<Object> subForms) {
        this.subForms = subForms;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public ErrorReport getError() {
        return error;
    }

    public void setError(ErrorReport error) {
        if (error != null)
            setSuccessful(false);
        this.error = error;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Header[] getHedaer_exito() {
        return hedaer_exito;
    }

    public void setHedaer_exito(Header[] hedaer_exito) {
        this.hedaer_exito = hedaer_exito;
    }

    public Header[] getHedaer_fail() {
        return hedaer_fail;
    }

    public void setHedaer_fail(Header[] hedaer_fail) {
        this.hedaer_fail = hedaer_fail;
    }

//    public boolean isWarning() {
//
//        if (isSuccessful() && error != null)
//            return true;
//        else
//            return false;
//    }

}
