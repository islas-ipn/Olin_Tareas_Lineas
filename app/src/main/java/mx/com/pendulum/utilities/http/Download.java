package mx.com.pendulum.utilities.http;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;

public class Download  extends AsyncTask<Void, Void, String> {
    private static final String TAG = Download.class.getSimpleName();
    private static final int TIME_OUT = -1;
    private WeakReference<Activity> weakActivity;
    private String url;
    private String json;
    private Interfaces.OnResponseDownload response;
    private int request;
    private String messageError;
    private int rcode;

    public Download(Activity activity, Interfaces.OnResponseDownload response, int request) {
        this.weakActivity = new WeakReference<>(activity);
        this.request = request;
        this.response = response;
        this.messageError = "";
    }

    public Download config(String url, String json) {
        this.url = url;
        this.json = json;


        return this;
    }


    public void start() {


        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (weakActivity != null)
            CustomDialog.showProgressDialog(weakActivity.get(), true, weakActivity.get().getString(R.string.loading));
    }

    @Override
    protected String doInBackground(Void... params) {


//        if (!Tools.isOnline() && weakActivity != null) {
//
//
//            messageError = weakActivity.get().getString(R.string.no_internet);
//            return null;
//        } else {
//            messageError = "";
//        }

        Log.d(TAG, "URL:   " + url);
        Log.d(TAG, "JSON:  " + json + "");

        String requestMethod = json == null ? "GET" : "POST";


        if (requestMethod.equals("GET")) {
            return getConnection();
        } else return postConnection();


//        HttpURLConnection conn = CnxUtils.makeHttpConnection(url, null,
//                null, HttpConstants.GET);
//
//        if (conn != null) {
//            rcode = extractResponseCode(conn);
//            Log.d(TAG, "rcode: " + rcode + "");
//
//            switch (rcode) {
//                case HttpURLConnection.HTTP_OK:
//                    return extractResponseMessage(conn);
//                case -1:
//                    messageError = weakActivity.get().getString(R.string.error_no_conected_wlan);
//                    break;
//                default:
//                    messageError = "Error, " + rcode + " ";
//                    break;
//
//            }
//
//
//        }


    }


    private String getConnection() {
        HttpURLConnection conn = CnxUtils.makeHttpConnection(url, null, null, HttpConstants.GET);

        if (conn != null) {
            rcode = extractResponseCode(conn);
            Log.d(TAG, "rcode: " + rcode + "");

            switch (rcode) {
                case HttpURLConnection.HTTP_OK:
                    return extractResponseMessage(conn);
                case -1:
                    messageError = weakActivity.get().getString(R.string.error_no_conected_wlan);
                    break;
                default:
                    messageError = "Error, " + rcode + " ";
                    break;

            }


        }

        return null;
    }


    private String postConnection() {

        // Create a new HttpClient and Post Header
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        HttpResponse httpResponse;

        try {
            // Add your data


            if (json != null) {
                httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
                httpPost.setEntity(new StringEntity(json, "utf-8"));
            }

//            if (params != null) {
//                httpPost.setEntity(params);
//            }

            httpResponse = httpClient.execute(httpPost);

            if (httpResponse != null) {

                rcode = httpResponse.getStatusLine().getStatusCode();

                switch (rcode) {
                    case HttpURLConnection.HTTP_OK:

                        return entityToString(httpResponse.getEntity());
                    case -1:
                        messageError = weakActivity.get().getString(R.string.error_no_conected_wlan);
                        break;
                    default:
                        messageError = "Error, " + rcode + " ";
                        break;

                }

            }

        } catch (Exception ignored) {
            Log.i(TAG, "postConnection: ", ignored);
        }
        return null;


    }


    @SuppressWarnings("ConstantConditions")
    private String entityToString(HttpEntity entity) {
        InputStream is = null;
        try {
            is = entity.getContent();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder str = new StringBuilder();

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                str.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str.toString();
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (weakActivity == null) return;


        CustomDialog.showProgressDialog(weakActivity.get(), false, null);


        if (!messageError.isEmpty()) {
//            CustomDialog.showDisclaimer(weakActivity.get(), messageError, new Interfaces.OnResponse() {
//                @Override
//                public void onResponse(int handlerCode, Object o) {
//                    if (response != null) {
//                        response.onResponse(request, null);
//                    }
//                }
//            });
//            return;

            if (response != null) {
                response.onResponse(request, null, messageError );
            }
            return;
        }

        if (response != null) {
            response.onResponse(request, s,null);
        }
    }



    private int extractResponseCode(HttpURLConnection connection) throws SecurityException {
        int rCode;
        try {
            if (connection != null)
                rCode = connection.getResponseCode();
            else
                return 0;
        } catch (IOException ioe) {

            return TIME_OUT;
        } catch (Exception e) {
            return -2;
        }
        return rCode;
    }

    private String extractResponseMessage(HttpURLConnection connection) {
        String responseMessage = "";
        InputStream is;
        if (connection != null) {
            try {
                is = connection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }
                responseMessage = total.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, responseMessage);
        return responseMessage;
    }


}
