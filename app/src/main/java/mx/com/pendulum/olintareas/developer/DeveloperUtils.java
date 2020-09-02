package mx.com.pendulum.olintareas.developer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.sync.constants.IUrls;

public class DeveloperUtils {

    static void importDB(Activity activity, String dbName) {
        File currentDB;
        currentDB = activity.getDatabasePath(dbName);
        String toImportDBPath = Properties.pathDataBaseExportFolder + dbName;
        File importDB = new File(toImportDBPath);
        if (!importDB.exists()) {
            Toast.makeText(activity, dbName + "  no encontrada!", Toast.LENGTH_SHORT).show();
            return;
        }
        FileChannel source;
        FileChannel destination;
        try {
            source = new FileInputStream(importDB).getChannel();
            destination = new FileOutputStream(currentDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(activity, dbName + "  importada!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void exportDB(Activity activity, String dbName) {
        File currentDB;
        currentDB = activity.getDatabasePath(dbName);
        //String backupDBPath = Environment.getExternalStorageDirectory() +
        // File.separator  + activity.getString(R.string.app_name);
        String backupDBPath = Properties.pathDataBaseExportFolder;
        File file = new File(backupDBPath);
        if (!file.exists()) {
            if (file.mkdir())
                Log.d("", "");
        }
        File backupDB = new File(backupDBPath, dbName);
        FileChannel source;
        FileChannel destination;
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(activity, dbName + "  respaldada!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void respaladarBD(Activity activity) {
        exportDB(activity, "user.db");
        exportDB(activity, "catalog.db");
    }

    static void showServersAddress(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Información")
                .setCancelable(false)
                .setMessage("URL: " + IUrls.BASE_URL_SYTEM)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }
}