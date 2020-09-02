package mx.com.pendulum.olintareas.config.util;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import mx.com.pendulum.utilities.CustomExceptionHandler;
import mx.com.pendulum.utilities.Tools;

public class ContextApplication extends Application {
    private static final String TAG = ContextApplication.class.getSimpleName();

    private static Context context;

    public void onCreate() {
        super.onCreate();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler());
        }

        ContextApplication.context = getApplicationContext();

        Tools.initImageLoader(context);
//        ACRA.init(this);
    }

    public static Context getAppContext() {
        return ContextApplication.context;
    }



    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "ON TERMINATE");
    }
}
