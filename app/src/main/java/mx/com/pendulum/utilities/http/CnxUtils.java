package mx.com.pendulum.utilities.http;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import mx.com.pendulum.olintareas.Properties;

public class CnxUtils {
    private static final String TAG = CnxUtils.class.getSimpleName();

    private static String error = "";
    public static final int TIME_OUT = -1;
    public static final int SERVER_NOT_FOUND = 404;
    public static final int WRONG_REQUEST = 400;

    public static HttpURLConnection makeHttpConnection(String url, HttpHeaders requestHeaders, byte[] postData, String requestMethod) throws SecurityException {

        Log.i("url", url);
        URL urlObject = null;
        HttpURLConnection conn = null;
        DataOutputStream out = null;
        error = "";
        try {
            urlObject = new URL(url);
            conn = (HttpURLConnection) urlObject.openConnection();
            if (requestHeaders != null) {
                int size = requestHeaders.size();
                for (int i = 0; i < size; ) {
                    String header = requestHeaders.getPropertyKey(i);

                    String value = requestHeaders.getPropertyValue(i++);
                    if (value != null) {
                        conn.setRequestProperty(header, value);
                    }
                }
            }

            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("Accept", "application/json;charset=utf-8");

            if (postData == null)
                conn.setDoInput(true);
            else if (postData != null) {
                if (postData != null)
                    conn.setFixedLengthStreamingMode(postData.length);

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                HttpURLConnection.setDefaultAllowUserInteraction(false);

                out = new DataOutputStream(conn.getOutputStream());
                if (postData != null)
                    out.write(postData);
                out.flush();

            }

        } catch (IOException ioe) {
            Log.e(TAG, "IOException: " + ioe.toString());
            error = ioe.getMessage() + " " + ioe;
            return null;
        } finally {
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e2) {
                    Log.e(TAG, e2.getMessage(), e2);
                    return null;
                }
            }
        }

        return conn;
    }

    public static int extractResponseCode(HttpURLConnection connection) throws SecurityException {
        int rCode = TIME_OUT;
        try {
            if (connection != null)
                rCode = connection.getResponseCode();
            else
                return 0;
        } catch (IOException ioe) {
            Log.e(TAG, "rCode: " + rCode + "IOException: " + ioe.toString() + " message= " + ioe.getMessage());

            return TIME_OUT;
        } catch (Exception e) {
            Log.e(TAG, "rCode= " + rCode + "Exception: " + e.toString() + " message= " + e.getMessage());

            return -2;
        }
        //Log.d("Connections::extractResponseCode", "Response-Code " + rCode + " Url: " + (connection!=null?connection.getURL():"Connection is NULL"));
        return rCode;
    }

    public static byte[] extractResponseData(HttpURLConnection connection) throws SecurityException {
        InputStream in = null;
        ByteArrayOutputStream bytesArray = null;

        try {
            in = connection.getInputStream();
            // Removed due a bug in devices that causes the device to not download all the bytes
            // with big data files, particularly on GSM networks.
                /*
                long contentLength = connection.getContentLength(); // -1;//
				if(contentLength != -1) { // Buffered fetch.
					bytesArray = new ByteArrayOutputStream();
					byte bytes[] = new byte[(int)contentLength];
					in.read(bytes);
					bytesArray.write(bytes);
				}
				else */
            { // Buffered fetch of unknown length.
                bytesArray = new ByteArrayOutputStream();
                byte[] bytes = new byte[8192];
                int len = 0;
                while ((len = in.read(bytes)) != -1)
                    bytesArray.write(bytes, 0, len);
            }
        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage(), ioe);
            return new byte[0];
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return new byte[0];
        } finally {
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (IOException ioe) {
                    Log.e("Connections", ioe.getMessage(), ioe);
                    return new byte[0];
                }
            }
        }
        return bytesArray.toByteArray();
    }

    private static String readInputStreamToString(HttpURLConnection connection) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        } catch (Exception e) {
            Log.i(TAG, "Error reading InputStream");
            result = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.i(TAG, "Error closing InputStream");
                }
            }
        }

        return result;
    }

    public static boolean downloadResponseDataToFile(HttpURLConnection connection, File tempDownload, String json) {
        boolean completed = false;
        //   String campo = readInputStreamToString(connection);
        InputStream input = null;
        FileOutputStream output = null;
        String result = null;
        StringBuffer sb = new StringBuffer();
//        StringBuilder text = new StringBuilder();
        byte[] buffer = new byte[8192];//[65536];
        int lenRead = 0;
        try {
            input = new BufferedInputStream(connection.getInputStream());
            output = new FileOutputStream(tempDownload);
            while ((lenRead = input.read(buffer)) > 0) {
                output.write(buffer, 0, lenRead);
            }
            output.flush();
//            input.reset();
//            BufferedReader br = new BufferedReader(new FileReader(tempDownload));
//            String line;
//
//            while ((line = br.readLine()) != null) {
//                text.append(line);
//                json+=line;
//                text.append('\n');
//            }
            //json = text.toString();
//            br.close();


            completed = true;
        } catch (Exception ioe) {
            Log.e(TAG, ioe.getMessage(), ioe);
        } finally {
            if (input != null) {
                try {
                    input.close();
                    input = null;
                } catch (IOException ioe) {
                    Log.e("Connections", ioe.getMessage(), ioe);
                }
                input = null;
            }
            if (output != null) {
                try {
                    output.close();
                    output = null;
                } catch (IOException ioe) {
                    Log.e("Connections", ioe.getMessage(), ioe);
                }
                output = null;
            }
        }
        return completed;
    }


    /*public static boolean downloadResponseDataToFile(HttpURLConnection connection, File tempDownload, String json) {
        boolean completed = false;
        String campo = readInputStreamToString(connection);
        InputStream input = null;
        FileOutputStream output = null;

        byte[] buffer = new byte[8192];//[65536];
        int lenRead = 0;
        try {
            input = new BufferedInputStream(connection.getInputStream());
            output = new FileOutputStream(tempDownload);
            while ((lenRead = input.read(buffer)) > 0)
                output.write(buffer, 0, lenRead);
            output.flush();
            completed = true;
        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage(), ioe);
        } finally {
            if (input != null) {
                try {
                    input.close();
                    input = null;
                } catch (IOException ioe) {
                    Log.e("Connections", ioe.getMessage(), ioe);
                }
                input = null;
            }
            if (output != null) {
                try {
                    output.close();
                    output = null;
                } catch (IOException ioe) {
                    Log.e("Connections", ioe.getMessage(), ioe);
                }
                output = null;
            }
        }
        return completed;
    }*/
    public static void closeConnection(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }

    public static String getLastError() {
        return error;
    }

    public static HttpURLConnection makeHttpConnection(String url, HttpHeaders _headers, byte[] postData) {
        if (postData == null)
            return makeHttpConnection(url, _headers, postData, HttpConstants.GET);
        else
            return makeHttpConnection(url, _headers, postData, HttpConstants.POST);
    }

    public static String getpeticionGET(String url) {
        StringBuilder response = new StringBuilder();
        HttpClient httpClient = null;
        HttpEntity httpEntity = null;
        HttpResponse httpResponse = null;
        HttpParams httpParameters = null;
        httpClient = new DefaultHttpClient();
        httpEntity = null;
        httpParameters = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(httpParameters, Properties.TIME_UOT);
        HttpConnectionParams.setSoTimeout(httpParameters,
                Properties.SOKECT_TIME_UOUT);

        httpClient = new DefaultHttpClient();
        httpParameters = new BasicHttpParams();
        Log.i("Sycn", url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-Type", "application/x-zip");
        try {
            httpResponse = httpClient.execute(httpGet);
            int status = httpResponse.getStatusLine().getStatusCode();
            httpEntity = httpResponse.getEntity();
            response.append(EntityUtils.toString(httpEntity, HTTP.UTF_8));
            Log.d("Sycn", "status" + status);
            Log.d("Sycn", "respuesta: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}
