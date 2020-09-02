package mx.com.pendulum.olintareas.lbs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.activities.EntryPointActivity;
import mx.com.pendulum.utilities.AppPermissions;

public class WorldBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = WorldBroadcastReceiver.class
            .getSimpleName();
    public static final String ACTION_UPDATE_CONFIG = "mx.com.pendulum.olintareas.lbs.WorldBroadcastReceiver.UPDATE_PARAMS";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        Log.e("BROADCAST OLIN", TAG + TAG + TAG + TAG + "\n\n\nBroadcast Received: " + action);
        if (!action.equals(ACTION_UPDATE_CONFIG)) {
            new AppPermissions(context, new Interfaces.OnResponse<Object>() {
                @Override
                public void onResponse(int handlerCode, Object o) {
                    if (o != null) {
                        Intent intentone = new Intent(context.getApplicationContext(), EntryPointActivity.class);
                        intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intentone);
                    }
                }
            }, 0);

        }
    }
}
