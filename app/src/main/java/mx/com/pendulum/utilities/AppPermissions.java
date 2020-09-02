package mx.com.pendulum.utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.com.pendulum.olintareas.interfaces.Interfaces;


public class AppPermissions {

    private static final String TAG = AppPermissions.class.getSimpleName();
    private Context context;
    private Interfaces.OnResponse<Object> response;
    private int request;

    private static final String[] permissions = { // TODO AGREGAR PERMISOS
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            //Manifest.permission.CALL_PHONE,
            Manifest.permission.VIBRATE,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            //Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO
    };

    public AppPermissions(Context context, Interfaces.OnResponse<Object> response, int request) {
        this.context = context;
        this.response = response;
        this.request = request;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermissions();
        } else {
            appContinue();
        }

    }


    private void appContinue() {
        response.onResponse(request, null);
    }

    private void verifyPermissions() {
        final List<String> permissionList = new ArrayList<>();

        for (String permiso : permissions) {
            addPermission(permissionList, permiso);
        }

        if (permissionList.size() > 0) {
            response.onResponse(request, permissionList.toArray(new String[permissionList.size()]));
        } else {
            appContinue();
        }
    }

    private void addPermission(List<String> permissionList, String permission) {
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(permission);
        }
    }


    public static Map<String, Integer> getPermsHash() {
        Map<String, Integer> perms = new HashMap<>();
        // Initial

        for (String permiso : permissions) {
            perms.put(permiso, PackageManager.PERMISSION_GRANTED);
        }


        return perms;
    }

    public static boolean verifyMap(Map<String, Integer> perms) {

        boolean bool = true;
        for (String permiso : permissions) {

            bool = bool && perms.get(permiso) == PackageManager.PERMISSION_GRANTED;

        }

        return bool;


//        return perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.GET_TASKS) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) == PackageManager.PERMISSION_GRANTED &&
//
//                perms.get(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
//                perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

}
