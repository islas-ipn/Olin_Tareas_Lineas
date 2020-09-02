package mx.com.pendulum.olintareas.config.util;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigSharedPreferences {

    public static final String FIRST_RUN = "firstRun";
    public static final String START_MINUTE_FIELD = "sMinute";
    public static final String END_MINUTE_FIELD = "eMinute";
    public static final String TRACKING_INTERVAL_FIELD = "interval";
    public static final String DISTANCE_THRESHOLD_FIELD = "distance";
    public static final String ACCURACY_FIELD = "accuracy";
    public static final String REPORTING_INTERVAL_FIELD = "rInterval";
    public static final String IDLE_TIMEOUT_FIELD = "idleTimeout";
    private static final String APP_SHARED_PREFS = "Config";
    private static final int DEFAULT_START_MINUTE = 480;
    private static final int DEFAULT_END_MINUTE = 1320;
    private static final int DEFAULT_INTERVAL = 10 * 60 * 1000;
    private static final int DEFAULT_DISTANCE_THRESHOLD = 300;
    private static final int DEFAULT_ACCURACY = 100;
    private static final int DEFAULT_REPORTING_INTERVAL = 30 * 60 * 1000;
    private static final int DEFAULT_IDLE_TIMEOUT = 30 * 60 * 1000;

    private SharedPreferences appTrackingSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public ConfigSharedPreferences(Context context) {
        this.appTrackingSharedPrefs = context.getSharedPreferences(
                APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.prefsEditor = appTrackingSharedPrefs.edit();
    }

    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        this.listener = listener;
        appTrackingSharedPrefs
                .registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterListener() {
        appTrackingSharedPrefs
                .unregisterOnSharedPreferenceChangeListener(listener);
    }

    public boolean loadFirstRun() {
        return appTrackingSharedPrefs.getBoolean(FIRST_RUN,
                true);
    }

    public void saveFirstRun(boolean firstRun) {
        prefsEditor.putBoolean(FIRST_RUN, firstRun);
        prefsEditor.apply();
    }

    public int loadStartMinute() {
        return appTrackingSharedPrefs.getInt(START_MINUTE_FIELD,
                DEFAULT_START_MINUTE);
    }

    public void saveStartMinute(int sMinute) {
        if (sMinute >= 0) {
            prefsEditor.putInt(START_MINUTE_FIELD, sMinute);
            prefsEditor.apply();
        }
    }

    public int loadEndMinute() {
        return appTrackingSharedPrefs.getInt(END_MINUTE_FIELD,
                DEFAULT_END_MINUTE);
    }

    public void saveEndMinute(int eMinute) {
        if (eMinute >= 0) {
            prefsEditor.putInt(END_MINUTE_FIELD, eMinute);
            prefsEditor.apply();
        }
    }

    public int loadInterval() {
        return 30 * 1000;
    }

    public int loadDistanceThreshold() {
        return 10;
    }

    public int loadReportingInterval() {
        return 30 * 1000;
    }

    public void saveInterval(int interval) {
        if (interval >= 0) {
            prefsEditor.putInt(TRACKING_INTERVAL_FIELD, interval * 60 * 1000);
            prefsEditor.apply();
        }
    }

    public void saveDistanceThreshold(int distance) {
        if (distance >= 0) {
            prefsEditor.putInt(DISTANCE_THRESHOLD_FIELD, distance);
            prefsEditor.apply();
        }
    }

    public float loadPreferredAccuracy() {
        return appTrackingSharedPrefs.getInt(ACCURACY_FIELD, DEFAULT_ACCURACY);
    }

    public void savePreferredAccuracy(int accuracy) {
        if (accuracy >= 0) {
            prefsEditor.putInt(ACCURACY_FIELD, accuracy);
            prefsEditor.apply();
        }
    }

    public void saveReportingInterval(int interval) {
        if (interval >= 0) {
            prefsEditor.putInt(REPORTING_INTERVAL_FIELD, interval * 60 * 1000);
            prefsEditor.apply();
        }
    }

    public int loadIdleTimeout() {
        return appTrackingSharedPrefs.getInt(IDLE_TIMEOUT_FIELD,
                DEFAULT_IDLE_TIMEOUT);
    }

    public void saveIdleTimeout(int timeout) {
        if (timeout >= 0) {
            prefsEditor.putInt(IDLE_TIMEOUT_FIELD, timeout * 60 * 1000);
            prefsEditor.apply();
        }
    }
}