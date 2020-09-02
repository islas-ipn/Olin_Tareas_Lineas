package mx.com.pendulum.olintareas.sync;

import java.util.ArrayList;

import mx.com.pendulum.utilities.ErrorReport;

public class SyncBatchResults {
    private boolean successful;
    private ArrayList<ErrorReport> errors = new ArrayList<ErrorReport>();
    public boolean isSuccessful() {
        return successful;
    }
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
    public ArrayList<ErrorReport> getErrors() {
        return errors;
    }
    public void addError(ErrorReport error) {
        if(error != null)
            setSuccessful(false);
        this.errors.add(error);
    }
}
