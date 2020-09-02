package mx.com.pendulum.utilities.http;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import mx.com.pendulum.olintareas.R;

public class DownloadServicePDF extends IntentService {


    public DownloadServicePDF() {
        super("DownloadServicePDF");
    }

    @Override
    @SuppressLint("SdCardPath")
    protected void onHandleIntent(Intent intent) {
        try {
            int ID = new Random().nextInt(85 - 20) + 20;
            String urlToDownload = intent.getStringExtra("url");
            String nombreManual = intent.getStringExtra("nombre");


            NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    getApplicationContext()).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Descargando")
                    .setContentText(nombreManual);


            // Gets an instance of the NotificationManager service//

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this);
//
//        //Create the intent that’ll fire when the user taps the notification//
//
////        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.androidauthority.com/"));
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        mBuilder.setContentIntent(pendingIntent);
//
//        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
//        mBuilder.setContentTitle("My notification");
//        mBuilder.setContentText("Hello World!");
//
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            mBuilder.setProgress(100, 0, false);
            mNotificationManager.notify(ID, mBuilder.build());

            File file = new File("/sdcard/OlinManuales/");
            if (!file.exists()) {
                file.mkdirs();
            }
            // download the file

            String path = "/sdcard/OlinManuales/" + nombreManual  + ".pdf";
            file = new File(path);
            if(file.exists()){
                file.delete();
            }


            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(path);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
//                Bundle resultData = new Bundle();
//                resultData.putInt("progress", );
                int progress = (int) (total * 100 / fileLength);
                output.write(data, 0, count);
                mBuilder.setProgress(100, progress, false);
                mNotificationManager.notify(ID, mBuilder.build());

            }

            output.flush();
            output.close();
            input.close();


            Intent resultIntent = new Intent(Intent.ACTION_VIEW);
            resultIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );


            mBuilder.setContentTitle("Descarga completa")
                    .setProgress(100, 100, false)
                    .setContentIntent(resultPendingIntent).setAutoCancel(true);
            mNotifyManager.notify(ID, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}