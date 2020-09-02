package mx.com.pendulum.olintareas.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.support.ConnectionSource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.camera.CameraActivity;
import mx.com.pendulum.olintareas.config.util.ContextApplication;
import mx.com.pendulum.olintareas.db.CatalogDatabaseHelper;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.db.dao.UserDataDaoImpl;
import mx.com.pendulum.olintareas.dto.UserData;
import mx.com.pendulum.olintareas.dto.VersionApps;
import mx.com.pendulum.olintareas.dto.complementos.ManualTypeDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogCases;
import mx.com.pendulum.olintareas.dto.tareasV2.SeguimientoTarea;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.sync.SyncDataOutService;
import mx.com.pendulum.olintareas.sync.SyncUtilities;
import mx.com.pendulum.olintareas.sync.constants.IUrls;
import mx.com.pendulum.olintareas.tareas.views.ViewFile_upload;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.DialogManuales;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;
import mx.com.pendulum.olintareas.ui.fragments.FragmentInfoAll_;
import mx.com.pendulum.olintareas.ui.fragments.alertdialogs.InfoDialogFragment;
import mx.com.pendulum.olintareas.ui.fragments.tareas.TaskNewActivityHome;
import mx.com.pendulum.olintareas.ui.parents.AppCompatActivityParent;
import mx.com.pendulum.olintareas.ui.parents.FragmentParent;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.Util;
import mx.com.pendulum.utilities.http.CnxUtils;
import mx.com.pendulum.utilities.http.Download;
import mx.com.pendulum.utilities.http.HttpConstants;

public class MainActivity extends AppCompatActivityParent implements NavigationView.OnNavigationItemSelectedListener, Interfaces.OnResponse<Object> {

    private static final int DIALOG_SALIR = 543;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SeguimientoTarea seguimiento = isFromAnotherApp(getIntent());
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.addHeaderView(getHeader());
        navigationView.setCheckedItem(R.id.nav_tareas);
        TaskNewActivityHome taskActivityHome = new TaskNewActivityHome();
        Bundle b = new Bundle();
        b.putString(FragmentParent.TITLE_FRAGMENT, getString(R.string.tareas_pendientes));
        if (seguimiento != null) {
            b.putSerializable(Properties.EXTRA_SERIAL_SEG, seguimiento);
            b.putBoolean(Properties.EXTRA_IS_FROM_ANOTHER_APP, true);
        }
        updateTables();
        taskActivityHome.setArguments(b);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main2, taskActivityHome)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNumbers();
    }

    private void updateNumbers() {
        Menu sm = navigationView.getMenu();
        TextView Vis = (TextView) LayoutInflater.from(this).inflate(R.layout.menu_counter_task, null);
        Vis.setText(addCountDetail());
        sm.findItem(R.id.nav_tareas).setActionView(Vis);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        int count = fm.getBackStackEntryCount();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (count == 0) {
            CustomDialog.dialogChoice(this, this, DIALOG_SALIR, null, "¿Seguro que desea salir?");
        } else {
            if (count == 1) {
                NavigationView navigationView = findViewById(R.id.nav_view);
                navigationView.setCheckedItem(R.id.nav_tareas);
            }
            super.onBackPressed();
        }
    }

    private Spanned addCountDetail() {
        String query;
        Spanned tem = null;
        UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper.getHelper(this);
        query = "select \n" +
                "(SELECT count(\"updated\") from Tsk_TareaDTO WHERE \"updated\"= 1) + \n" +
                "(SELECT count(\"updated\") from Tsk_seguimientoTarea WHERE \"updated\"= 1) \n" +
                " modificados ,\n" +
                " (SELECT count(\"updated\") from Tsk_TareaDTO) +\n" +
                "(SELECT count(\"updated\") from Tsk_seguimientoTarea  /*where de in ('COBRANZA','ABA')*/) " +
                "todos;";
        Cursor c = userDatabaseHelper.getReadableDatabase().rawQuery(query, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            tem = getTextHtml(c.getString(c.getColumnIndex("modificados")), c.getString(c.getColumnIndex("todos")));
        }
        c.close();
        return tem;
    }

    public Spanned getTextHtml(String uno, String dos) {
        String str = "<font  color=\"red\">" + uno + "</font>/" + "<font color=\"blue\">" + dos + "</font>";
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(str);
        }
        return result;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fm = getFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        updateNumbers();
        switch (id) {
            case R.id.nav_tareas:
                onBackPressed();
                break;
            case R.id.nav_info:
                showInfoDialog(this);
                break;
            case R.id.nav_resumen:
                setContetFragment(new FragmentInfoAll_(), "Resumen de activades");
                break;
            case R.id.nav_log_out:
                if (SyncUtilities.wifiValidation(this.getApplicationContext())) {
                    CheckVersionServer che = new CheckVersionServer();
                    che.execute();
                } else {
                    Tools.showSnack(this, getString(R.string.error_no_conected_wlan));
                }
                break;
            case R.id.nav_soporte:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:10843511"));
                startActivity(intent);
                break;
            case R.id.nav_manuales:
                dialogMAnuales();
                break;
            case R.id.nav_complementos:
                LoginActivity.loadComplementos(this);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void dialogMAnuales() {
        String url = IUrls.MANUALEES;
        Download download = new Download(this, new Interfaces.OnResponseDownload() {
            @Override
            public void onResponse(int handlerCode, Object o, String errorString) {
                if (o != null) {
                    String str = (String) o;
                    Type listType = new TypeToken<ArrayList<ManualTypeDTO>>() {
                    }.getType();
                    ArrayList<ManualTypeDTO> list = new Gson().fromJson(str, listType);
                    DialogManuales dialogManuales = DialogManuales.newInstance(list);
                    dialogManuales.show(getFragmentManager(), "");
                }
            }
        }, 0);
        download.config(url, null);
        download.start();
    }

    protected boolean existeCredito(String credito) {
        boolean b;
        UserDatabaseHelper us = UserDatabaseHelper.getHelper(this);
        Cursor c = us.getReadableDatabase().rawQuery("SELECT * FROM Credit where credit = '" + credito + "'", null);
        b = c.getCount() > 0;
        return b;
    }

    private void logout() {
        ArrayList<String> list = new ArrayList<>();
        UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getActivity());
        try {
            String query = "SELECT idTarea FROM Tsk_NotaDTO where parcialSave = 1";
            Cursor c = helper.getReadableDatabase().rawQuery(query, null);
            c.moveToFirst();
            do {
                String idTarea = c.getString(c.getColumnIndex("idTarea"));
                if (idTarea != null)
                    if (!idTarea.trim().equals("")) {
                        list.add(idTarea);
                    }
            } while (c.moveToNext());
        } catch (Exception ignored) {
        } finally {
            helper.close();
        }
        if (list.size() > 0) {
            String str = list.toString();
            str = str.substring(1);
            str = str.substring(0, str.length() - 1);
            CustomDialog.showDisclaimer(getActivity(), "Favor de completar la(s) tarea(s): " + str, null);
            return;
        }
        Intent in = new Intent(getApplicationContext(), SyncDataProgressActivity.class);
        in.putExtra(Properties.EXTRA_IS_FROM_ANOTHER_APP, false);
        startActivity(in);
        startService(new Intent(this, SyncDataOutService.class));
    }

    private void compressAllImages() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.dlg_logout_title));
        alertDialog.setMessage(this.getString(R.string.dlg_logout_text));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getString(R.string.dlg_logout_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//						new CompressImages(activity, me, REQUEST_COMPRESSER);
                        logout();
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                getString(R.string.dlg_logout_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showInfoDialog(FragmentActivity activity) {
        UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper
                .getHelper(activity);
        UserData user = null;
        try {
            user = userDatabaseHelper.getUserDataDao().getCurrentUser();
        } catch (SQLException ignored) {
        }
        userDatabaseHelper.close();
        if (user != null) {
            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
            InfoDialogFragment infoDialog = InfoDialogFragment.newInstance(user.getUsername(), user.getNombre(), user.getSession().getFecha_login());
            infoDialog.setCancelable(false);
            infoDialog.addResponse(new Interfaces.OnResponse() {
                @Override
                public void onResponse(int handlerCode, Object o) {
                    compressAllImages();
                }
            }, 0);
            infoDialog.show(fragmentTransaction, "fragment_dialog_info");
        }
    }

    public View getHeader() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View v = layoutInflater.inflate(R.layout.nav_header_main, null);
        TextView txtNombre = v.findViewById(R.id.txtNombre);
        UserDatabaseHelper helper = UserDatabaseHelper.getHelper(this);
        UserDataDaoImpl userDataDao;
        try {
            userDataDao = helper.getUserDataDao();
            List<UserData> userDatas = userDataDao.queryForAll();
            UserData userData = userDatas.get(0);
            txtNombre.setText(userData.getNombre());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return v;
    }

    public void setContetFragment(Fragment fragment, String title) {

        Bundle b = new Bundle();
        b.putString(FragmentParent.TITLE_FRAGMENT, title);
        fragment.setArguments(b);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main2, fragment)
                .addToBackStack("pila")
                .commit();
    }

    public void setContetFragment(Fragment fragment, int title) {
        Bundle b = this.getIntent().getExtras();
        b.putString(FragmentParent.TITLE_FRAGMENT, this.getResources().getString(title));
        //OverviewFragment fragment = new OverviewFragment();
        fragment.setArguments(b);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main2, fragment)
                //.addToBackStack("pila")
                .commit();
    }

    @Override
    public void onResponse(int handlerCode, Object o) {
        switch (handlerCode) {
            case DIALOG_SALIR:
                if (o != null) {
                    boolean bool = (boolean) o;
                    if (bool) {
                        super.onBackPressed();
                    }
                }
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckVersionServer extends AsyncTask<Void, Void, String> {
        ProgressDialog p;

        void startProgress() {
            p = new ProgressDialog(MainActivity.this);
            p.setTitle("Validando Servidor");
            p.setMessage("Por favor espere a que desaparezca este mensaje");
            p.setCancelable(false);
            p.setIndeterminate(true);
            p.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String message = "";
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 150000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            int timeoutSocket = 90000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpEntity httpEntity;
            String url = IUrls.SERVER_TIME;
            Log.d(Properties.TAG_DEVELOP, url);
            HttpGet httpGet = new HttpGet(url);
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                HttpResponse response = httpClient.execute(httpGet);
                int status = response.getStatusLine().getStatusCode();
                Log.i("sync", "response >>>>>  " + response.getStatusLine().getStatusCode());
                switch (status) {
                    case HttpStatus.SC_OK:
                        httpEntity = response.getEntity();
                        String str = EntityUtils.toString(httpEntity, HTTP.UTF_8);
                        if (!str.contains("true")) {
                            message = "El servidor se enceuntra en mantenimiento de 11:00 pm a 03:00 am (Hora D.F:)\npor favor haga el logout en otro horario diferente";
                        }
                        break;
                    case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                    default:
                        message = "Error al comunicarse con el servidor";
                        break;
                }
            } catch (ClientProtocolException e) {
                Log.i("sync", "ClientProtocolException >>>>>  " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.i("sync", "IOException >>>>>  " + e.getMessage());
                e.printStackTrace();
                message = "Comprueba tu conexíon a internet.";
            }
            return message;
        }

        @Override
        protected void onPreExecute() {
            startProgress();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            stopProgress();
            if (message.equals("")) {
                Calendar ca = Calendar.getInstance();
                int hora = ca.get(Calendar.HOUR_OF_DAY);
                if (hora < 21 && hora > 5) {
                    checkAllVersionApps();
                } else {
                    message = "El servidor se encuentra en mantenimiento de 9:00 pm a 05:00 am \npor favor haga el logout en otro horario diferente";
                    Tools.showSnack(MainActivity.this, message);
                }
            } else {
                Tools.showSnack(MainActivity.this, message);
            }
        }

        void stopProgress() {
            p.dismiss();
        }
    }

    private SeguimientoTarea isFromAnotherApp(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(Properties.EXTRA_IS_FROM_ANOTHER_APP)) {
            boolean isFromAnother = extras.getBoolean(Properties.EXTRA_IS_FROM_ANOTHER_APP);
            if (isFromAnother && extras.containsKey(Properties.EXTRA_JSON_SEG)) {
                String jsonObject = extras.getString(Properties.EXTRA_JSON_SEG);
                Gson gson = new Gson();
                SeguimientoTarea seg = gson.fromJson(jsonObject, SeguimientoTarea.class);
                return seg;
            }
        }
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean resizeIMAGE = false;
        boolean isImageResize= false;
        String imageName = "image" + ViewFile_upload.SEPATATOR + 1 +
                ViewFile_upload.SEPATATOR;
    }

    @SuppressLint("StaticFieldLeak")
    private class AllVersionsValidation extends AsyncTask<Void, Void, String> {
        ProgressDialog p;

        void startProgress() {
            p = new ProgressDialog(MainActivity.this);
            p.setTitle("Validando Servidor");
            p.setMessage("Por favor espere a que desaparezca este mensaje");
            p.setCancelable(false);
            p.setIndeterminate(true);
            p.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String message = "";
            String sUrl = IUrls.ALL_VERSION_APPS;
            Log.d(Properties.TAG_DEVELOP, sUrl);
            int rcode;
            String response;
            HttpURLConnection conn = null;
            try {
                conn = CnxUtils.makeHttpConnection(sUrl, null, null,
                        HttpConstants.GET);
                if (conn != null) {
                    rcode = CnxUtils.extractResponseCode(conn);
                    if (rcode == HttpURLConnection.HTTP_OK) {
                        response = new String(CnxUtils.extractResponseData(conn));
                        if (response.length() > 0) {
                            VersionApps versionApps = new ObjectMapper().readValue(response, VersionApps.class);
                            if (versionApps != null) {
                                String tareasV = Util.getCurrentVerApks(getApplicationContext(), Properties.pakage_tareas);
                                long tareasVC = Util.getCurrentVerCodeApks(getApplicationContext(), Properties.pakage_tareas);
                                if (!tareasV.equalsIgnoreCase(versionApps.getvTareas()) || tareasVC != versionApps.getcTareas())
                                    message = getString(R.string.tareas_version_error);
                                else if (checUbicuoInstalled()) {
                                    String ubicuoV = Util.getCurrentVerApks(getApplicationContext(), Properties.pakage_ubicuo);
                                    long ubicuoVC = Util.getCurrentVerCodeApks(getApplicationContext(), Properties.pakage_ubicuo);
                                    if (!ubicuoV.equalsIgnoreCase(versionApps.getvUbicuo()) || ubicuoVC != versionApps.getcUbicuo()) {
                                        try {
                                            UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper
                                                    .getHelper(getApplicationContext());
                                            UserData user = userDatabaseHelper.getUserDataDao().getCurrentUser();
                                            if (user != null && user.getPermisos() != null) {
                                                if (user.getPermisos().getTipo_usuario_desc().equalsIgnoreCase(Properties.PERMISSION_INTERNAL)) {
                                                    message = getString(R.string.ubicuo_needed);
                                                }
                                            }
                                        } catch (SQLException e) {
                                            message = e.getMessage();
                                        }
                                    }
                                } else {
                                    try {
                                        UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper
                                                .getHelper(getApplicationContext());
                                        UserData user = userDatabaseHelper.getUserDataDao().getCurrentUser();
                                        if (user != null && user.getPermisos() != null) {
                                            if (user.getPermisos().getTipo_usuario_desc().equalsIgnoreCase(Properties.PERMISSION_INTERNAL)) {
                                                message = getString(R.string.ubicuo_needed);
                                            }
                                        }
                                    } catch (SQLException e) {
                                        message = e.getMessage();
                                    }
                                }
                            } else
                                message = getString(R.string.error_wrong_data);
                        } else
                            message = getString(R.string.error_database);
                    } else {
                        if (rcode == CnxUtils.TIME_OUT)
                            message = getString(R.string.error_serverconect_error);
                        else if (rcode == CnxUtils.SERVER_NOT_FOUND)
                            message = getString(R.string.error_server_notfound);
                        else if (rcode == CnxUtils.WRONG_REQUEST)
                            message = getString(R.string.error_wrong_request);
                        else
                            message = getString(R.string.error_http + rcode);
                    }
                    CnxUtils.closeConnection(conn);
                    conn = null;
                } else
                    message = CnxUtils.getLastError();
            } catch (Exception e) {
                message = e.getMessage();
            } finally {
                if (conn != null) {
                    CnxUtils.closeConnection(conn);
                }
            }
            return message;
        }

        @Override
        protected void onPreExecute() {
            startProgress();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            stopProgress();
            if (message.equals("")) {
                compressAllImages();
            } else {
                if (message.equalsIgnoreCase(getString(R.string.ubicuo_needed)))
                    showIncorrectVersionApp(getString(R.string.mensaje_ubicuo), Properties.pakage_ubicuo);
                else if (message.equalsIgnoreCase(message = getString(R.string.tareas_version_error)))
                    showIncorrectVersionApp(getString(R.string.tareas_version_error), Properties.pakage_tareas);
                else
                    CustomDialog.showNormalDialog(getActivity(), message);
            }
        }

        void stopProgress() {
            p.dismiss();
        }
    }

    private void checkAllVersionApps() {
        // compressAllImages();
        AllVersionsValidation allVersions = new AllVersionsValidation();
        allVersions.execute();
    }

    private void showIncorrectVersionApp(String msg, final String sPackage) {
        CustomDialog.showDisclaimer(getActivity(), msg, new Interfaces.OnResponse() {
            @Override
            public void onResponse(int handlerCode, Object o) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.url_play_Store) + sPackage));
                intent.setPackage(getString(R.string.package_play_Store));
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private boolean checUbicuoInstalled() {
        PackageManager pm = getBaseContext().getPackageManager();
        return Util.isPackageInstalled(Properties.pakage_ubicuo, pm);
    }

    private void updateTables() {
        CatalogDatabaseHelper catalogDatabaseHelper = CatalogDatabaseHelper
                .getHelper(getActivity().getApplicationContext());
        int vCatDB = catalogDatabaseHelper.getReadableDatabase().getVersion();
        if (vCatDB > 1) {
            ConnectionSource connectionSource = catalogDatabaseHelper.getConnectionSource();
            catalogDatabaseHelper.createTableIfNotExists(connectionSource, CatalogCases.class);
            //fillCategoryResolutionTable(catalogDatabaseHelper);
        }
    }
}
