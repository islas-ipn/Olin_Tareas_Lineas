package mx.com.pendulum.utilities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.camera.MyRoundedBitmapDisplayer;
import mx.com.pendulum.olintareas.config.util.ContextApplication;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.UserData;

public class Tools {
    public static boolean isDebug() {
//        return false;
//        return true;
        return (0 != (ContextApplication.getAppContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }


    public static double distance(LatLng origen, LatLng location) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(origen.latitude - location.latitude);
        double dLng = Math.toRadians(origen.longitude
                - location.longitude);
        double a = Math.sin(dLat / 2)
                * Math.sin(dLat / 2)
                + Math.cos(Math
                .toRadians(location.latitude))
                * Math.cos(Math.toRadians(origen.latitude))
                * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a),
                Math.sqrt(1 - a));
        double dist = earthRadius * c;
        double meterConversion = 1609.00;

        return dist *= meterConversion;

    }

    public static double distance(double latitud, double longitud, Location location) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(latitud - location.getLatitude());
        double dLng = Math.toRadians(longitud
                - location.getLongitude());
        double a = Math.sin(dLat / 2)
                * Math.sin(dLat / 2)
                + Math.cos(Math
                .toRadians(location
                        .getLatitude()))
                * Math.cos(Math.toRadians(latitud))
                * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a),
                Math.sqrt(1 - a));
        double dist = earthRadius * c;
        double meterConversion = 1609.00;

        return dist *= meterConversion;

    }

    public static int getKeyboard(String keyboard) {

        switch (keyboard) {
            default:
            case "ALPHANUMERIC":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            case "MONEY":
                return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            case "TEXT":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            case "NUMBER":
                return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            case "PHONE":
                return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            case "DATE":
                return InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            case "TIME":
                return InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            case "DECIMAL":
                return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            case "PASSWORD":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            case "EMAIL":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            case "URL":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            case "DATETIME":
                return InputType.TYPE_CLASS_DATETIME | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
        }
    }

    public static void fragmentChooser(int fragmentReplace,
                                       Fragment fragmentSiguiente,
                                       FragmentManager fragmentManager,
                                       String tag) {

        FragmentTransaction transactionList = fragmentManager
                .beginTransaction();
        transactionList.add(fragmentSiguiente, tag);
        transactionList.replace(fragmentReplace, fragmentSiguiente);
        transactionList.commit();
    }

    public static String getStringFromCusror(Cursor cursor, String colName) {
        String str = "";
        try {
            str = cursor.getString(cursor.getColumnIndex(colName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static int getIntFromCusror(Cursor cursor, String colName) {
        return cursor.getInt(cursor.getColumnIndex(colName));
    }

    public static boolean rotateAndCompressImage(String filePath, int quality, int orientation) {
        Boolean bool = true;

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        //loadBitmap
        Bitmap bm = BitmapFactory.decodeFile(filePath, options);

        Matrix matrix = new Matrix();


        switch (orientation) {
            case 3:
                matrix.postRotate(180);
                break;
            case 6:
                matrix.postRotate(90);
                break;
            case 8:
                matrix.postRotate(270);
                break;
            default:
                matrix.postRotate(0);
                break;
        }


        try {
            //rotateBitmap
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
//            Log.i(TAG, "bitmap compressed and rotated");
        } catch (Exception ofme) {
//            Log.e(TAG, ofme.getMessage(), ofme);
            bool = false;
        }


        File file = new File(filePath);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            //SaveBitmap
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                bool = false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            bool = false;
        }
        return bool;
    }

    public static HashMap<String, String> getExifParams(String filePath) {
        HashMap<String, String> hash = new HashMap<String, String>();

        if (filePath == null) return hash;
        if (filePath.equals("")) return hash;

        File file = new File(filePath);
        if (!file.exists()) return hash;

        ExifInterface ex;
        try {
            ex = new ExifInterface(filePath);
            hash.put(ExifInterface.TAG_ORIENTATION, (ex.getAttribute(ExifInterface.TAG_ORIENTATION) == null ? "" : ex.getAttribute(ExifInterface.TAG_ORIENTATION)));
            hash.put(ExifInterface.TAG_DATETIME, (ex.getAttribute(ExifInterface.TAG_DATETIME) == null ? "" : ex.getAttribute(ExifInterface.TAG_DATETIME)));
            hash.put(ExifInterface.TAG_MAKE, (ex.getAttribute(ExifInterface.TAG_MAKE) == null ? "" : ex.getAttribute(ExifInterface.TAG_MAKE)));
            hash.put(ExifInterface.TAG_MODEL, (ex.getAttribute(ExifInterface.TAG_MODEL) == null ? "" : ex.getAttribute(ExifInterface.TAG_MODEL)));
            hash.put(ExifInterface.TAG_FLASH, (ex.getAttribute(ExifInterface.TAG_FLASH) == null ? "" : ex.getAttribute(ExifInterface.TAG_FLASH)));
            hash.put(ExifInterface.TAG_IMAGE_WIDTH, (ex.getAttribute(ExifInterface.TAG_IMAGE_WIDTH) == null ? "" : ex.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)));
            hash.put(ExifInterface.TAG_IMAGE_LENGTH, (ex.getAttribute(ExifInterface.TAG_IMAGE_LENGTH) == null ? "" : ex.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)));
            hash.put(ExifInterface.TAG_GPS_LATITUDE, (ex.getAttribute(ExifInterface.TAG_GPS_LATITUDE) == null ? "" : ex.getAttribute(ExifInterface.TAG_GPS_LATITUDE)));
            hash.put(ExifInterface.TAG_GPS_LONGITUDE, (ex.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) == null ? "" : ex.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)));
            hash.put(ExifInterface.TAG_GPS_LATITUDE_REF, (ex.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF) == null ? "" : ex.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)));
            hash.put(ExifInterface.TAG_GPS_LONGITUDE_REF, (ex.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF) == null ? "" : ex.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)));
            hash.put(ExifInterface.TAG_EXPOSURE_TIME, (ex.getAttribute(ExifInterface.TAG_EXPOSURE_TIME) == null ? "" : ex.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)));
            hash.put(ExifInterface.TAG_APERTURE, (ex.getAttribute(ExifInterface.TAG_APERTURE) == null ? "" : ex.getAttribute(ExifInterface.TAG_APERTURE)));
            hash.put(ExifInterface.TAG_ISO, (ex.getAttribute(ExifInterface.TAG_ISO) == null ? "" : ex.getAttribute(ExifInterface.TAG_ISO)));
            hash.put(ExifInterface.TAG_GPS_ALTITUDE, (ex.getAttribute(ExifInterface.TAG_GPS_ALTITUDE) == null ? "" : ex.getAttribute(ExifInterface.TAG_GPS_ALTITUDE)));
            hash.put(ExifInterface.TAG_GPS_ALTITUDE_REF, (ex.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF) == null ? "" : ex.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF)));
            hash.put(ExifInterface.TAG_GPS_TIMESTAMP, (ex.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP) == null ? "" : ex.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP)));
            hash.put(ExifInterface.TAG_GPS_DATESTAMP, (ex.getAttribute(ExifInterface.TAG_GPS_DATESTAMP) == null ? "" : ex.getAttribute(ExifInterface.TAG_GPS_DATESTAMP)));
            hash.put(ExifInterface.TAG_WHITE_BALANCE, (ex.getAttribute(ExifInterface.TAG_WHITE_BALANCE) == null ? "" : ex.getAttribute(ExifInterface.TAG_WHITE_BALANCE)));
            hash.put(ExifInterface.TAG_FOCAL_LENGTH, (ex.getAttribute(ExifInterface.TAG_FOCAL_LENGTH) == null ? "" : ex.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)));
            hash.put(ExifInterface.TAG_GPS_PROCESSING_METHOD, (ex.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD) == null ? "" : ex.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD)));


        } catch (IOException e) {
            e.printStackTrace();
        }

        return hash;
    }


    public static boolean saveExifParams(HashMap<String, String> hashMap, String filePath) {


        if (hashMap == null) return false;
        if (filePath == null) return false;
        if (filePath.equals("")) return false;

        File file = new File(filePath);
        if (!file.exists()) return false;

        ExifInterface ex;
        try {
            ex = new ExifInterface(filePath);


            ex.setAttribute(ExifInterface.TAG_ORIENTATION, hashMap.get(ExifInterface.TAG_ORIENTATION));
            ex.setAttribute(ExifInterface.TAG_DATETIME, hashMap.get(ExifInterface.TAG_DATETIME));
            ex.setAttribute(ExifInterface.TAG_MAKE, hashMap.get(ExifInterface.TAG_MAKE));
            ex.setAttribute(ExifInterface.TAG_MODEL, hashMap.get(ExifInterface.TAG_MODEL));
//            ex.setAttribute(ExifInterface.TAG_MODEL, "");
            ex.setAttribute(ExifInterface.TAG_FLASH, hashMap.get(ExifInterface.TAG_FLASH));
            ex.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, hashMap.get(ExifInterface.TAG_IMAGE_WIDTH));
            ex.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, hashMap.get(ExifInterface.TAG_IMAGE_LENGTH));
            ex.setAttribute(ExifInterface.TAG_GPS_LATITUDE, hashMap.get(ExifInterface.TAG_GPS_LATITUDE));
            ex.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, hashMap.get(ExifInterface.TAG_GPS_LONGITUDE));
            ex.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, hashMap.get(ExifInterface.TAG_GPS_LATITUDE_REF));
            ex.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, hashMap.get(ExifInterface.TAG_GPS_LONGITUDE_REF));
            ex.setAttribute(ExifInterface.TAG_EXPOSURE_TIME, hashMap.get(ExifInterface.TAG_EXPOSURE_TIME));
            ex.setAttribute(ExifInterface.TAG_APERTURE, hashMap.get(ExifInterface.TAG_APERTURE));
            ex.setAttribute(ExifInterface.TAG_ISO, hashMap.get(ExifInterface.TAG_ISO));
            ex.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, hashMap.get(ExifInterface.TAG_GPS_ALTITUDE));
            ex.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, hashMap.get(ExifInterface.TAG_GPS_ALTITUDE_REF));
            ex.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, hashMap.get(ExifInterface.TAG_GPS_TIMESTAMP));
            ex.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, hashMap.get(ExifInterface.TAG_GPS_DATESTAMP));
            ex.setAttribute(ExifInterface.TAG_WHITE_BALANCE, hashMap.get(ExifInterface.TAG_WHITE_BALANCE));
            ex.setAttribute(ExifInterface.TAG_FOCAL_LENGTH, hashMap.get(ExifInterface.TAG_FOCAL_LENGTH));
            ex.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD, hashMap.get(ExifInterface.TAG_GPS_PROCESSING_METHOD));
            ex.saveAttributes();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


    }

    public static String getDateFileStr() {
        return new SimpleDateFormat(Properties.DATE_TIME_FORMAT_FILE).format(new Date());
    }

    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    public static long getEpoch() {
        return System.currentTimeMillis() / 1000;
    }

    public static String getTime(Calendar calendar) {
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        String _hora = "";
        if (hora < 10) {
            _hora = "0";
        }
        _hora += hora;
        String _min = "";
        if (min < 10) {
            _min = "0";
        }
        _min += min;
        return _hora + ":" + _min;
    }

    public static String getDatenumber(Calendar calendar, String separator) {
        if (separator == null) {
            separator = "-";
        }
        if (separator.isEmpty()) separator = "-";
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String _day = "";
        if (day < 10) {
            _day = "0";
        }
        _day += day;
        String _month = getMonthNumber(month + "");
        return _day + separator + _month + separator + year;
    }

    private static String getMonthNumber(String s) {
        return s.length() <= 1 ? "0" + s : s;
    }

    public static UserData getUserSecion(Context context) {
        UserData userData = new UserData();
        UserDatabaseHelper helper = UserDatabaseHelper.getHelper(context);
        try {
            userData = helper.getUserDataDao().getCurrentUser();
        } catch (Exception ignored) {
        } finally {
            helper.close();
        }
        return userData;
    }

    public static String getDate(Calendar calendar, String separator) {
        if (separator == null) {
            separator = "-";
        }
        if (separator.isEmpty()) separator = "-";
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String _day = "";
        if (day < 10) {
            _day = "0";
        }
        _day += day;
        String _month = getMonth(month);
        return _day + separator + _month + separator + year;
    }

    public static String getMonth(int month) {
        switch (month) {
            case 1:
                return "Ene";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Abr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Ago";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dic";
        }
        return "";
    }

    public static int checkAutoTimeZone() {
        int autoTime = -1;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                autoTime = Settings.Global.getInt(ContextApplication.getAppContext().getContentResolver(), Settings.Global.AUTO_TIME_ZONE);
            } else {
                //noinspection deprecation
                autoTime = android.provider.Settings.System.getInt(ContextApplication.getAppContext().getContentResolver(), android.provider.Settings.System.AUTO_TIME_ZONE, 0);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return autoTime;
    }

    public static int checkAutoTime() {
        int autoTime = -1;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                autoTime = Settings.Global.getInt(ContextApplication.getAppContext().getContentResolver(), Settings.Global.AUTO_TIME);
            } else {
                //noinspection deprecation
                autoTime = android.provider.Settings.System.getInt(ContextApplication.getAppContext().getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0);
            }

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return autoTime;
    }

    public static boolean openUbicuo(Context context, String packageName) {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            outputStream.writeBytes("am start -n mx.com.pendulum.ubicuo/.ui.activity.MainActivity" + " \n");
            outputStream.flush();
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return openApp(context, packageName);
        }
    }

    public static String getCurrentDate(String separator) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd" + separator + "MMM" + separator + "yyyy",
                new Locale("es", "ES"));
        String currentDate = dateFormat.format(new Date());
        return currentDate.replace("Sep", "sept");
    }

    public static Calendar convertStringToCalendar(String date) {
        try {
            String separator = date.substring(2, 3);
            String formatClean = date.replace(separator, "/").replace(".","");
            DateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            cal.setTime(df.parse(formatClean));
            return cal;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
            //throw new PackageManager.NameNotFoundException();
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
        return true;
    }

    public static void setupUI(final Activity activity, View view) {

        try {
            // Set up touch listener for non-text box views to hide keyboard.
            if (!(view instanceof EditText)) {
                view.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        hideSoftKeyboard(activity);
                        return false;
                    }
                });
            }

            //If a layout container, iterate over children and seed recursion.
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View innerView = ((ViewGroup) view).getChildAt(i);
                    setupUI(activity, innerView);
                }
            }
        } catch (Exception ignored) {
//            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            // Find the currently focused view, so we can grab the correct window
            // token from it.
            View view = activity.getCurrentFocus();
            // If no view currently has focus, create a new one, just so we can grab
            // a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
//                .taskExecutor(...)
//        .taskExecutorForCachedImages(...)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
//                .denyCacheImageMultipleSizesInMemory()
//                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
//                .memoryCacheSize(2 * 1024 * 1024)
//                .memoryCacheSizePercentage(13) // default
//                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
//                .diskCacheSize(50 * 1024 * 1024)
//                .diskCacheFileCount(100)
//                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
//                .imageDownloader(new BaseImageDownloader(context)) // default
//                .imageDecoder(new BaseImageDecoder()) // default
//                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
//                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static void appendLog(String text) {
        String path = Properties.SD_FILES_DIR;
        File logFile = new File(path, "log.file");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
                    true));
//            DateTime now = DateTime.now();
//            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
//            String mp = fmt.print(now);


            Date date = new Date();
            String strDateFormat = "yyyy-MM-dd HH:mm:ss";
            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
            String mp = dateFormat.format(date);
            buf.append(mp);
            buf.newLine();
            buf.append(text);
            buf.newLine();
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int getUbicuoVersionCode(Context context) {
        int verCode = 0;
        try {
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(Properties.pakageUbicuo, 0);
            verCode = pinfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verCode;
    }

    public static String sudoForResult(String... strings) {

        String res = "";
        DataOutputStream outputStream = null;
        InputStream response = null;
        try {
            Process su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            response = su.getInputStream();

            for (String s : strings) {
                outputStream.writeBytes(s + "\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            res = readFully(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(outputStream, response);
        }
        return res;
    }

    private static String readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString("UTF-8");
    }

    private static void closeSilently(Object... xs) {
        // Note: on Android API levels prior to 19 Socket does not implement Closeable
        for (Object x : xs) {
            if (x != null) {
                try {
                    Log.d("Tools", "closing: " + x);
                    if (x instanceof Closeable) {
                        ((Closeable) x).close();
                    } else if (x instanceof Socket) {
                        ((Socket) x).close();
                    } else if (x instanceof DatagramSocket) {
                        ((DatagramSocket) x).close();
                    } else {
                        Log.d("Tools", "cannot close: " + x);
                        throw new RuntimeException("cannot close " + x);
                    }
                } catch (Throwable e) {
                    Log.e("Tools", e.getMessage());
                }
            }
        }
    }

    public static void showSnack(View view, String text) throws IOException {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();
    }

    public static void showSnack(Activity activity, String text) {
        try {
            showSnack(activity.findViewById(android.R.id.content), text);
        } catch (IOException e) {
            Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
        }
    }

    public static void showSnack(Context context, String text) {
        try {
            showSnack(((Activity) context).findViewById(android.R.id.content), text);
        } catch (IOException e) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }
    }


    public static String getUbicuoVersionName(Context context) {
        String verName;
        try {
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(Properties.pakageUbicuo, 0);
            verName = pinfo.versionName;
        } catch (Exception e) {
            verName = "";
        }

        return verName;
    }

    public static DisplayImageOptions getImageLoaderOptions(int roundedRadious, int placeHolderResource) {
        return new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(placeHolderResource)
                .showImageForEmptyUri(placeHolderResource)
                .showImageOnFail(placeHolderResource)
//                .cacheInMemory(false)
//                .cacheOnDisk(false)
                .considerExifParams(true)
                .displayer(new MyRoundedBitmapDisplayer(roundedRadious))
//                .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();
    }

    public static DisplayImageOptions getImageLoaderOptions(int placeHolderResource) {
        return new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(placeHolderResource)
                .showImageForEmptyUri(placeHolderResource)
                .showImageOnFail(placeHolderResource)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
//                .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();
    }


    public static void openAppOrInstall(Context context, String packageName) {
        if (isAppInstalled(context, packageName))

            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(packageName));
        else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            context.startActivity(intent);
        }


    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }
}
