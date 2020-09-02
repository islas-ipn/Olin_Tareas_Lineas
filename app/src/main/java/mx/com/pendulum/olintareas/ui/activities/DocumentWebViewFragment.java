package mx.com.pendulum.olintareas.ui.activities;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.ZoomButtonsController;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.fragments.alertdialogs.DownloadFile;
import mx.com.pendulum.utilities.DownloadFileFromURL;
import mx.com.pendulum.utilities.Util;

public class DocumentWebViewFragment extends AppCompatActivity {
    private static final String LOG = DocumentWebViewFragment.class
            .getSimpleName();

    public static String _CREDITO = "credito";
    public static String _USUARIO = "usuario";
    public static String _SNBUTTON = "notButton";
    public static String _URL = "_url";
    private String credito;
    private String usuario;
    private boolean notButton;
    private WebView wb;
    private String url = "";

    private ProgressBar idProgressbar;
    private DownloadFileFromURL donDownloadFileFromURL;
    private HandlerTask handlerTask;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_frgment);

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			//getActionBar().setDisplayHomeAsUpEnabled(true);
//			getActionBar().setHomeButtonEnabled(true);
//		}

        super.onResume();
        ActionBar a =getSupportActionBar();
        if (a != null) {
            a.setDisplayHomeAsUpEnabled(true);
            a.setHomeButtonEnabled(true);
            a.setTitle("Lw");
        }

        credito = getIntent().getExtras().getString(_CREDITO);
        usuario = getIntent().getExtras().getString(_USUARIO);
        url = getIntent().getExtras().getString(DocumentWebViewFragment._URL);
        try {

            notButton = getIntent().getExtras().getBoolean(_SNBUTTON);

        } catch (Exception e) {

        }
        init();
        initView();
        onStart_();
    }

    private void init() {
        url += credito + "&usr=" + usuario;
    }

    public void showInfoDialog(String url) {
        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction();
        DownloadFile downloadFile = DownloadFile.newInstance(url,
                getNameFile(url));
        downloadFile.setCancelable(false);
        downloadFile.show(fragmentTransaction, "fragment_dialog_info");
    }

    private void initView() {
        wb = (WebView) findViewById(R.id.webView1);
        idProgressbar = (ProgressBar) findViewById(R.id.progressBar_1);
    }

    private void onStart_() {

        if (!Util.isAppInstalled(Properties.pakageFile_managet)) {
//            Util.showInfoDialog_("", Properties.pathPlay, this);
            CustomDialog.dialogChoice(this, new Interfaces.OnResponse<Object>() {
                @Override
                public void onResponse(int handlerCode, Object o) {

                    boolean bool = (boolean) o;
                    if (bool) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Properties.pathPlay));
                        startActivity(intent);
                        finish();
                    }
                }
            }, 0, null, getString(R.string.dialog_file_manager), "Descargar", null);
        }

        wb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wb.setWebViewClient(new WebViewClient_());
        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setUseWideViewPort(true);
        wb.getSettings().setBuiltInZoomControls(true);
        wb.getSettings().setUseWideViewPort(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            // Use the API 11+ calls to disable the controls
            // Use a seperate class to obtain 1.6 compatibility
            new Runnable() {
                public void run() {
                    wb.getSettings().setDisplayZoomControls(false);
                }
            }.run();
        } else {
            ZoomButtonsController zoom_controll;
            try {
                zoom_controll = (ZoomButtonsController) wb.getClass()
                        .getMethod("getZoomButtonsController").invoke(wb, null);
                zoom_controll.getContainer().setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        progressDialog = new ProgressDialog(DocumentWebViewFragment.this);
        progressDialog.setMessage("cargando...");
        progressDialog.show();
        wb.loadUrl(url);
    }

    @SuppressWarnings("unused")
    private class WebViewClient_ extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
//			progressDialog = new ProgressDialog(DocumentWebViewFragment.this);
//			progressDialog.setMessage("cargando...");
//			progressDialog.show();
        }

        @Override
        public void onPageStarted(WebView view, String url_, Bitmap favicon) {
            super.onPageStarted(view, url_, favicon);
            if (!url_.contains(url)) {
                showInfoDialog(url_);
            } else {
//				progressDialog.show();
            }
            Log.i(LOG, url_);
            // progressDialog.dismiss();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i(LOG, "FINISH CARGO LA APLICAION|");
            progressDialog.dismiss();
            try {
                if (progressDialog.isShowing()) {
                    progressDialog = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String getNameFile(String url) {
        String name = "";
        char c = '/';
        int size = url.length();
        int posi = 0;
        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) == c) {
                posi = i;
            }
        }
        return url.substring(posi + 1);
    }

    private class HandlerTask extends Handler {
        @Override
        public String getMessageName(Message message) {

            return super.getMessageName(message);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}