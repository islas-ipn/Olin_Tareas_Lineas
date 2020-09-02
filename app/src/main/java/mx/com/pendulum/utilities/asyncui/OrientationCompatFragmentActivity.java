package mx.com.pendulum.utilities.asyncui;

import mx.com.pendulum.olintareas.ui.parents.AppCompatActivityParent;

public abstract class OrientationCompatFragmentActivity extends AppCompatActivityParent {

    private OrientationCompatTask orientationCompatTask = null;


    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (orientationCompatTask != null)
            orientationCompatTask.setActivity(null);
        return orientationCompatTask;
    }

    protected void setOrientationSafeAsyncTask(OrientationCompatTask orientationCompatTask) {
        this.orientationCompatTask = orientationCompatTask;
    }

    public void onStillBusy() {
        removeProgressDialog();
        addProgressDialog();
    }

    public void onTaskCompleted(boolean result, String message) {
        removeProgressDialog();
        addFinishedDialog();
    }

    abstract protected void addProgressDialog();

    abstract protected void removeProgressDialog();

    abstract protected void addFinishedDialog();

    abstract protected void removeFinishedDialog();
}
