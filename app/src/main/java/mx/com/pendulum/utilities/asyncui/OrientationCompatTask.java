package mx.com.pendulum.utilities.asyncui;

import android.os.AsyncTask;

public class OrientationCompatTask extends AsyncTask<Object, Void, Boolean> {
    private OrientationCompatFragmentActivity activity = null;
    private boolean busy = false;
    private boolean finished = false;

    private boolean result = false;
    private String message = null;

    public OrientationCompatTask() {
        super();
    }

    public OrientationCompatTask(OrientationCompatFragmentActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        busy = true;
        notifyActivityBusy();
    }


    @Override
    protected Boolean doInBackground(Object... objects) {
        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        setResult(result);
        setBusy(false);
        setFinished(true);
        notifyActivityTaskFinished();
    }

    public OrientationCompatFragmentActivity getActivity() {
        return activity;
    }

    public void setActivity(OrientationCompatFragmentActivity activity) {
        this.activity = activity;
        if (busy)
            notifyActivityBusy();
        if (finished)
            notifyActivityTaskFinished();
    }

    private void setBusy(boolean busy) {
        this.busy = busy;
    }

    private void setFinished(boolean finished) {
        this.finished = finished;
    }

    protected void notifyActivityBusy() {
        if (activity != null)
            activity.onStillBusy();
    }

    protected void notifyActivityTaskFinished() {
        if (activity != null)
            activity.onTaskCompleted(getResult(), getMessage());
    }

    public boolean getResult() {
        return result;
    }

    protected void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
