package mx.com.pendulum.olintareas.ui.fragments.alertdialogs;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.config.util.ContextApplication;
import mx.com.pendulum.olintareas.ui.activities.DocumentWebViewFragment;
import mx.com.pendulum.utilities.DownloadFileFromURL;
import mx.com.pendulum.utilities.JsonUtils;

public class DownloadFile extends DialogFragment {
    private static String _URL_FILE = "url";
    private static String _NAME = "name";
    private static String _OPC = "opc";

    private String url_file;
    private String name;
    private TextView txtPorcent;
    private ProgressBar idProgressbar;
    private DownloadFileFromURL donDownloadFileFromURL;
    private HandlerTask handlerTask;

    public static DownloadFile newInstance(String url_file, String name) {
        Bundle bundle = new Bundle();
        String tmp = encode(url_file);

        bundle.putString(_URL_FILE, tmp);
        bundle.putString(_NAME, name);
        DownloadFile downloadApk = new DownloadFile();
        downloadApk.setArguments(bundle);
        return downloadApk;
    }


    public static DownloadFile newInstance(String url_file, String name, int opc) {
        Bundle bundle = new Bundle();
        String tmp = encode(url_file);
        bundle.putString(_URL_FILE, tmp);
        bundle.putString(_NAME, name);
//		bundle.putInt(_OPC, value);
        DownloadFile downloadApk = new DownloadFile();
        downloadApk.setArguments(bundle);
        return downloadApk;
    }

    private static String encode(String urls) {

        URI uri = null;
        try {
            URL url= new URL(urls);
            uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }




        return uri.toASCIIString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AlertDialogStyle);
        url_file = getArguments().getString(_URL_FILE);
        name = getArguments().getString(_NAME);
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_download_file, container,
                false);


        final Button btnServer = (Button) view.findViewById(R.id.btnfile);
        TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);

        final LinearLayout ly = (LinearLayout) view
                .findViewById(R.id.lyDownload);
        idProgressbar = (ProgressBar) view.findViewById(R.id.progressBar_1);
        txtPorcent = (TextView) view.findViewById(R.id.txtPorcent);

        ly.setVisibility(View.VISIBLE);
        handlerTask = new HandlerTask();
        donDownloadFileFromURL = new DownloadFileFromURL(handlerTask);
        donDownloadFileFromURL.setName(name);
        donDownloadFileFromURL.execute(url_file);

        return view;
    }

    private
    @SuppressLint("HandlerLeak")
    class HandlerTask extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    txtPorcent.setText("0 %");
                    break;
                case 2:
                    idProgressbar.setProgress(msg.arg2);
                    txtPorcent.setText(msg.arg2 + " %");
                    break;
                case 3:

                    Log.i(DocumentWebViewFragment.class.getSimpleName(), "");
//                    if (existeFile(url_file))
                    openFile(new File(JsonUtils.path_file() + name),
                            getMimeType(url_file));
                    // Intent intent = new Intent();
                    // intent.setAction(android.content.Intent.ACTION_VIEW);
                    // File file = new File("/sdcard/test.avi");
                    // intent.setDataAndType(Uri.fromFile(file), "video/*");
                    // startActivity(intent);

                    // Intent intent = new Intent(Intent.ACTION_VIEW);
                    // intent.setDataAndType(Uri.fromFile(new
                    // File(IUrls.path_file()+name)),
                    // "application/vnd.android.package-archive");
                    // startActivity(intent);
                    // onDestroyView();
                    dismissAllowingStateLoss();
                    // getActivity().finish();
                    break;

                default:
                    break;
            }
        }
    }

    private void openFile(File f, String mimeType) {
        Intent viewIntent = new Intent();
        viewIntent.setAction(Intent.ACTION_VIEW);
        viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        viewIntent.setDataAndType(Uri.fromFile(f), mimeType);
        // using the packagemanager to query is faster than trying startActivity
        // and catching the activity not found exception, which causes a stack
        // unwind.
        List<ResolveInfo> resolved = ContextApplication.getAppContext()
                .getPackageManager().queryIntentActivities(viewIntent, 0);
        if (resolved != null && resolved.size() > 0) {
            startActivity(viewIntent);
        } /*else {
            // notify the user they can't open it.
        }*/

        // PackageManager pm =
        // ContextApplication.getAppContext().getPackageManager();
        // List<ResolveInfo> activities = pm.queryIntentActivities(viewIntent,
        // 0);
        // if (activities.size() > 0) {
        // startActivity(viewIntent);
        // } else {
        // // Do something else here. Maybe pop up a Dialog or Toast
        // }

    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        Log.i(DocumentWebViewFragment.class.getSimpleName(), "" + type);

        return type;
    }

    @Override
    public void onResume() {
        File f = new File(url_file);
        f.delete();
        super.onResume();
    }

    public boolean existeFile(String url) {
        boolean exito = false;
        String[] nameExtension = {"jpg", "jepg", "png"};
        for (int i = 0; i < nameExtension.length; i++) {
            if (url.toLowerCase().contains(nameExtension[i])) {
                exito = true;
                break;
            }
        }
        return exito;
    }
}
