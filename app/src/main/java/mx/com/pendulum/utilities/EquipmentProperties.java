package mx.com.pendulum.utilities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import android.telephony.TelephonyManager;
import android.util.Log;

import mx.com.pendulum.olintareas.config.util.ContextApplication;

public class EquipmentProperties {
    public static boolean isEmulator(Context context) {
        return Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") ? true : false;
    }

    public static String getIdentifier(Context context) {
        String imei = "No-dispone";
        TelephonyManager t = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

        if (t == null) {
            return imei;
        }
        try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }

                imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getImei();


        } else
            imei = t.getDeviceId();
        //Log.i("LoginActivity", "URL ver lo que esta recuperando " +  ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getImei());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            imei = "No-dispone";
        }
        if (imei != null)
            return imei;
        else {
            return "No-dispone";
        }
    }

    public static int getRemainingBatteryPercent(Context context) {
        Intent batteryLevelIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryLevelIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = batteryLevelIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int battery = (level * 100) / scale;

        return battery;
    }
}
