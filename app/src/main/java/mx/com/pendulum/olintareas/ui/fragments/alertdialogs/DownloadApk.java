package mx.com.pendulum.olintareas.ui.fragments.alertdialogs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.utilities.DownloadFileFromURL;
import mx.com.pendulum.utilities.JsonUtils;

public class DownloadApk extends DialogFragment {
    private static String _URL_PLAY = "url_play";
    private static String _URL_SERVER = "url_server";
    private static String _OPC = "opc";
    private String urlPlay;
    private String urlServer;
    private TextView txtPorcent;
    private ProgressBar idProgressbar;
    private DownloadFileFromURL donDownloadFileFromURL;
    private HandlerTask handlerTask;
    private int opc = 0;

    public static DownloadApk newInstance(String url_server, String url_play) {
        Bundle bundle = new Bundle();
        bundle.putString(_URL_PLAY, url_play);
        bundle.putString(_URL_SERVER, url_server);
        DownloadApk downloadApk = new DownloadApk();
        downloadApk.setArguments(bundle);
        return downloadApk;
    }

    public static DownloadApk newInstance(String url_server, String url_play,
                                          int opc) {
        Bundle bundle = new Bundle();
        bundle.putString(_URL_PLAY, url_play);
        bundle.putString(_URL_SERVER, url_server);
        bundle.putInt(_OPC, opc);
        DownloadApk downloadApk = new DownloadApk();
        downloadApk.setArguments(bundle);
        return downloadApk;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AlertDialogStyle);
        urlPlay = getArguments().getString(_URL_PLAY);
        urlServer = getArguments().getString(_URL_SERVER);
        try {
            opc = getArguments().getInt(_OPC);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater  .inflate(R.layout.dialog_download, container, false);

        final Button btnServer = (Button) view.findViewById(R.id.btnServer);
        final Button btnPlay = (Button) view.findViewById(R.id.btnPlay);
        TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        Log.i("LOG_", opc + "");
        if (opc > 0) {
            txtTitle.setText(opc);
        }
        final LinearLayout ly = (LinearLayout) view
                .findViewById(R.id.lyDownload);
        idProgressbar = (ProgressBar) view.findViewById(R.id.progressBar_1);
        txtPorcent = (TextView) view.findViewById(R.id.txtPorcent);
        try {
            if (urlServer != null)
                if (!urlServer.equals("")) {
                    btnServer.setVisibility(View.VISIBLE);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ly.setVisibility(View.VISIBLE);
                handlerTask = new HandlerTask();
                donDownloadFileFromURL = new DownloadFileFromURL(handlerTask);
                donDownloadFileFromURL.execute(urlServer);
                btnServer.setVisibility(View.GONE);
                btnPlay.setVisibility(View.GONE);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(urlPlay)));
                getActivity().finish();
            }
        });

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
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(JsonUtils.path_apk())),
                            "application/vnd.android.package-archive");
                    startActivity(intent);
                    onDestroyView();
                    dismissAllowingStateLoss();
                    getActivity().finish();
                    break;

                default:
                    break;
            }
        }
    }

}
