package mx.com.pendulum.utilities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import mx.com.pendulum.olintareas.config.util.ContextApplication;

public class Util {
    public static String md5(String input) {
        String res = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(input.getBytes());
            byte[] md5 = algorithm.digest();
            String tmp = "";
            for (int i = 0; i < md5.length; i++) {
                tmp = (Integer.toHexString(0xFF & md5[i]));
                if (tmp.length() == 1) {
                    res += "0" + tmp;
                } else {
                    res += tmp;
                }
            }
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public static boolean checkServiceExecution(Context context, String name) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (name.equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    public static String getCurrentApkVersion(Context context) {

        String version = "0.0.0";
        PackageManager manager = context.getPackageManager();
        try {
            version = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA).versionName;
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }

        return version.trim();
    }

    public static boolean isAppInstalled(String package_) {
        PackageManager pm = ContextApplication.getAppContext().getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(package_, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static boolean isSdPresent() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static boolean isWiFiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String fileToMD5(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead);
            }
            byte[] md5Bytes = digest.digest();
            return convertHashToString(md5Bytes);
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private static String convertHashToString(byte[] md5Bytes) {
        String returnVal = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            returnVal += Integer.toString((md5Bytes[i] & 0xff) + 0x100, 16)
                    .substring(1);
        }
        return returnVal.toUpperCase();
    }

    public static long convertStringToLong(String value) {
        long lValue;
        try {
            lValue = Long.valueOf(value);
            return lValue;
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    public static short convertStringToShort(String value) {
        short lValue;
        try {
            lValue = Short.valueOf(value);
            return lValue;
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    public static String getCurrentVerApks(Context context, String sPackage) {
        String version = "0.0.0";
        PackageManager manager = context.getPackageManager();
        try {
            version = manager.getPackageInfo(sPackage, PackageManager.GET_META_DATA).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version.trim();
    }

    public static long getCurrentVerCodeApks(Context context, String sPackage) {
        long version = 0;
        PackageManager manager = context.getPackageManager();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                version = manager.getPackageInfo(sPackage, PackageManager.GET_META_DATA).getLongVersionCode();
            } else
                version = manager.getPackageInfo(sPackage, PackageManager.GET_META_DATA).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        boolean found = true;
        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }
        return found;
    }
}
