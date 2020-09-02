package mx.com.pendulum.utilities;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import mx.com.pendulum.olintareas.ui.activities.DocumentWebViewFragment;

public class DownloadFileFromURL extends AsyncTask<String, String, String> {

    private int progress_bar_type;
    private Handler handler;
    private String url ;
    private String name = null;
    private Message msg;


    public DownloadFileFromURL(Handler h)
    {
        this.handler=h;
    }

    @Override
    protected void onPreExecute() {
        // 
        super.onPreExecute();
        msg = handler.obtainMessage();
        msg.what=1;
        msg.arg2=progress_bar_type;
        handler.sendMessage(msg);
    }
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[0]);

            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();
            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            // Output stream to write fi sdle


            OutputStream output ;
            if(name!=null){
                output= new FileOutputStream(JsonUtils.path_file()+name);
                Log.i( DocumentWebViewFragment.class.getSimpleName(), "******* "  + JsonUtils.path_file()+name);
            }else{
                output= new FileOutputStream(JsonUtils.path_apk());
                Log.i( DocumentWebViewFragment.class.getSimpleName(), "------------"  +JsonUtils.path_apk());
            }

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress(""+(int)((total*100)/lenghtOfFile));
                // writing data to file
                output.write(data, 0, count);
            }
            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return null;
    }

    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
//        pDialog.setProgress(Integer.parseInt(progress[0]));
        msg = handler.obtainMessage();
        msg.what=2;
        msg.arg2= Integer.parseInt(progress[0]);
        handler.sendMessage(msg);
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String file_url) {
        msg = handler.obtainMessage();
        msg.what=3;
        msg.arg2=progress_bar_type;
        handler.sendMessage(msg);
//        // dismiss the dialog after the file was downloaded
//        dismissDialog(progress_bar_type);
//        // Displaying downloaded image into image view
//        // Reading image path from sdcard
//        String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
//        // setting downloaded into image view
//        my_image.setImageDrawable(Drawable.createFromPath(imagePath));
    }

    public void setName(String name) {
        this.name = name;
    }



}
