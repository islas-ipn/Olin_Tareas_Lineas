package mx.com.pendulum.olintareas.ui.activities;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ApplicationErrorReport;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import java.lang.Object;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.databind.ObjectMapper;

import  com.google.android.gms.common.AccountPicker;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import mx.com.pendulum.complementos.ComplementosAdapter;
import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.config.util.ContextApplication;
import mx.com.pendulum.olintareas.db.CatalogDatabaseHelper;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.developer.DialogDeveloperPassword;
import mx.com.pendulum.olintareas.dto.UserData;
import mx.com.pendulum.olintareas.dto.complementos.Item;
import mx.com.pendulum.olintareas.dto.login.VersionData;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.sync.SyncDataInService;
import mx.com.pendulum.olintareas.sync.SyncUtilities;
import mx.com.pendulum.olintareas.sync.constants.IUrls;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.activities.EntryPointActivity.InitialLoadTask;
import mx.com.pendulum.olintareas.ui.fragments.ProgressDialogFragment;
import mx.com.pendulum.olintareas.ui.fragments.alertdialogs.DownloadApk;
import mx.com.pendulum.utilities.EquipmentProperties;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.Util;
import mx.com.pendulum.utilities.UtilPhone;
import mx.com.pendulum.utilities.asyncui.OrientationCompatFragmentActivity;
import mx.com.pendulum.utilities.asyncui.OrientationCompatTask;
import mx.com.pendulum.utilities.http.CnxUtils;
import mx.com.pendulum.utilities.http.Download;
import mx.com.pendulum.utilities.http.HttpConstants;

import static mx.com.pendulum.olintareas.Properties.isReleaseApp;

public class LoginActivity extends OrientationCompatFragmentActivity implements View.OnClickListener {

    private static final int CHECK_ACCOUNT = 2;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private String accountNameTempo ="";
    private ProgressDialogFragment mProgressDialog;
    private String url_play;
    private String ubicuo_version = "";
    private String url_sever;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView logo;
    private int clickCounter = 0;
    private EditText editTextPassword;
    private EditText edtgetuser;
    private String urlUibucuo;
    private int counter = 0;

    String []projection = new String[]
    {
        ContactsContract.Profile._ID,
                ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
                ContactsContract.Profile.LOOKUP_KEY,
                ContactsContract.Profile.PHOTO_THUMBNAIL_URI
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Entro al create");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        configureComplementos();
        logo = findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Tools.isDebug()) {
                    showData();
                    return;
                }
                if (clickCounter < 10)
                    clickCounter++;
                else {
                    showData();
                    clickCounter = 0;
                }
            }
        });
        findViewById(R.id.textViewVersion).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Tools.isDebug()) {
                    edtgetuser.setEnabled(true);
                    edtgetuser.setText("");
                    return;
                }
                if (counter == 10) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogStyle);
                    Random random = new Random();
                    final int s = random.nextInt(900) + 100;
                    final EditText edittext = new EditText(LoginActivity.this);
                    edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                    alert.setCancelable(false);
                    alert.setTitle("Inserta password de administrador ( " + s + " ): ");
                    alert.setView(edittext);
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String pass = edittext.getText().toString();
                            boolean shouldPass;
                            try {
                                int password = Integer.parseInt(pass);
                                shouldPass = password == DialogDeveloperPassword.calculateValue(s);
                            } catch (Exception e) {
                                shouldPass = false;
                            }
                            if (shouldPass) {
                                edtgetuser.setEnabled(true);
                                edtgetuser.setText("");
                            } else {
                                Toast.makeText(LoginActivity.this, "Password incorecto.", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });
                    alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                    counter = 0;
                }
                counter++;
            }
        });
        // Dir();
        findViewById(R.id.buttonSendLogin).setOnClickListener(this);
        ((TextView) findViewById(R.id.textViewVersion)).setText(Util.getCurrentApkVersion(this));
        editTextPassword = findViewById(R.id.editTextPassword);
        edtgetuser = findViewById(R.id.editTextUser);
        boolean isRoot = isDeviceRooted();

         String cuenta = UtilPhone.getAcountGmail();

        if (cuenta!= null && !cuenta.equals("")) {
            edtgetuser.setEnabled(false);
            edtgetuser.setText(cuenta.replace("@pendulum.com.mx", ""));
        }
        if (!isReleaseApp) {
            editTextPassword.setText(getString(R.string.password_developer));
        }
        if (savedInstanceState != null) {
            mProgressDialog = (ProgressDialogFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState,
                            ProgressDialogFragment.class.getName());
        }
        Object retained = onRetainNonConfigurationInstance();
        if (retained instanceof InitialLoadTask) {
            LoginTask loginTask = (LoginTask) retained;
            loginTask.setActivity(this);
        }
        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                obtainLoginInfo();
                return false;
            }
        });
        Log.i(TAG, "al final del create");
        String t = EquipmentProperties.getIdentifier(getActivity());
        Log.i(TAG, ""+t);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Retrieves the profile from the Contacts Provider
        Cursor profileCursor =
                getContentResolver().query(
                        ContactsContract.Profile.CONTENT_URI,
                        projection ,
                        null,
                        null,
                        null);
    }

    protected void showData() {
        new DialogDeveloperPassword(getActivity(), new Interfaces.OnResponse() {
            @Override
            public void onResponse(int handlerCode, Object o) {
            }
        }, 0).showDisclaimerPassword();
    }

    public void showInfoDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        DownloadApk downloadApk = DownloadApk.newInstance(url_sever, url_play);
        downloadApk.setCancelable(false);
        downloadApk.show(fragmentTransaction, "fragment_dialog_info");
    }

    public void obtainLoginInfo() {
        String user = ((EditText) findViewById(R.id.editTextUser)).getText()
                .toString().trim();
        String password = ((EditText) findViewById(R.id.editTextPassword))
                .getText().toString().trim();
        login(user, password);
    }

    public void login(String user, String password) {
        if (user.isEmpty() || password.isEmpty()) {
            Tools.showSnack(getActivity(), getText(R.string.error_empty_form).toString());
        } else if (!Util.isSdPresent()) {
            Tools.showSnack(getActivity(), getText(R.string.error_no_sdcard).toString());
        } else {
            LoginTask loginTask = new LoginTask();
            loginTask.setActivity(this);
            loginTask.execute(user, password);
        }
    }

    @Override
    public void onTaskCompleted(boolean validated, String message) {
        super.onTaskCompleted(validated, message);
        if (validated) {
            if (mProgressDialog != null) {
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalStateException ise) {
                    Log.d(Properties.TAG_DEVELOP, ise.getMessage());
                }
                mProgressDialog = null;
            }
            Intent in = new Intent(getApplicationContext(), SyncDataProgressActivity.class);
            in.putExtra(Properties.EXTRA_IS_FROM_ANOTHER_APP, false);
            startActivity(in);
            startService(new Intent(this, SyncDataInService.class));
            finish();
        } else if (message != null) {
            if (mProgressDialog != null) {
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalStateException ise) {
                }
                mProgressDialog = null;
            }
            Tools.showSnack(getActivity(), message);
        }
    }

    @Override
    public void addProgressDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        mProgressDialog = ProgressDialogFragment
                .newInstance(getString(R.string.authenticating_credentials));
        mProgressDialog.show(fragmentTransaction,
                ProgressDialogFragment.class.getName());
    }

    @SuppressLint("Recycle")
    @Override
    public void removeProgressDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        ProgressDialogFragment lastProgressDialog = (ProgressDialogFragment) getSupportFragmentManager()
                .findFragmentByTag(ProgressDialogFragment.TAG);
        if (lastProgressDialog != null) {
            lastProgressDialog.dismiss();
            fragmentTransaction.detach(lastProgressDialog);
            fragmentTransaction.remove(lastProgressDialog);
        }
    }

    @Override
    protected void addFinishedDialog() {
    }

    @Override
    protected void removeFinishedDialog() {
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSendLogin) {
            obtainLoginInfo();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("UnusedAssignment")
    class LoginTask extends OrientationCompatTask {


        @Override
        protected Boolean doInBackground(Object... credentials) {
            boolean isValid = false;
            if (SyncUtilities.wifiValidation(getActivity())) {
                isValid = versionValidation(
                        EquipmentProperties.getIdentifier(getActivity()),
                        Util.getCurrentApkVersion(getApplicationContext()));
                if (isValid) {
                    isValid = loginValidation((String) credentials[0],
                            (String) credentials[1]);
                }
            } else
                setMessage(getActivity().getString(
                        R.string.error_no_conected_wlan));
            return isValid;
        }

        private void showIncorrectVersionApp(final String msg, final String pakage) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CustomDialog.showDisclaimer(getActivity(), msg, new Interfaces.OnResponse() {
                        @Override
                        public void onResponse(int handlerCode, Object o) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(
                                    getString(R.string.url_play_Store) + pakage));
                            intent.setPackage(getString(R.string.package_play_Store));
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });
                }
            });
        }

        private boolean versionValidation(String deviceId, String currentVersion) {
            boolean isValidVersion = false;
            int rcode;
            String response = null;
            HttpURLConnection conn = null;

            try {
                ///Log.i(TAG, "URL ver " + url.toString());
                Log.i(TAG, "URL ver " + currentVersion);
                Log.i(TAG, "URL ver " + deviceId);
                Log.i(TAG, "URL ver " + URLEncoder.encode(currentVersion, "ISO-8859-1"));
                Log.i(TAG, "URL ver " + deviceId);
                StringBuilder url = new StringBuilder();
                Log.i(TAG, "URL ver " + IUrls.VALIDATE_VERSION);
                if (!Properties.isReleaseApp) {
                    deviceId = "234";
                }
                        url.append(IUrls.VALIDATE_VERSION);
                        url.append("?");
                        url.append("version=");
                        url.append(URLEncoder.encode(currentVersion, "ISO-8859-1"));
                        url.append("&device_id=");
                        url.append(URLEncoder.encode(deviceId, "ISO-8859-1"));
//                String url2 =IUrls.VALIDATE_VERSION;
//                url2+="?";
//                url2+="version=";
//                url2+=URLEncoder.encode(currentVersion, "ISO-8859-1");
//                url2+="&device_id=";
//                url2+=URLEncoder.encode(deviceId, "ISO-8859-1");
//
//                //Log.i(TAG, "URL ver " + url.toString());
//                Log.i(TAG, "URL ver " + url2);

                conn = CnxUtils.makeHttpConnection(url.toString(), null, null,
                        HttpConstants.GET);
                if (conn != null) {
                    rcode = CnxUtils.extractResponseCode(conn);
                    if (rcode == HttpURLConnection.HTTP_OK) {
                        response = new String(
                                CnxUtils.extractResponseData(conn));
                        // Valid version data
                        if (response != null && response.length() > 0) {
                            VersionData version = new ObjectMapper().readValue(response, VersionData.class);
                            if (version != null && version.getVersion() != null) {
                                sharedPreferences = getSharedPreferences("IPs", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("url_play", version.getUrl_ubicuo());
                                editor.apply();
                                url_play = version.getUrl_ubicuo();
                                urlUibucuo = version.getUrl_ubicuo();
                                ubicuo_version = version.getUbicuo_version();
                                if (version.getVersion().equals(currentVersion)) { // Valid
                                    // version
                                    isValidVersion = true;
                                } else if (version.getUrl().startsWith("http://")) {
                                    //showInfoDialog(version.getUrl(), version.getUrl_play_sotre());
                                    showIncorrectVersionApp(getString(R.string.error_version_obsolete), Properties.pakage_tareas);
                                } else
                                    showIncorrectVersionApp(getString(R.string.error_version_obsolete), Properties.pakage_tareas);
                                //setMessage(getActivity().getString(R.string.error_version_obsolete));
                            } else
                                setMessage(getActivity().getString(
                                        R.string.error_wrong_data));
                        } else
                            setMessage(getActivity().getString(
                                    R.string.error_database));
                    } else {
                        if (rcode == CnxUtils.TIME_OUT)
                            setMessage(getActivity().getString(
                                    R.string.error_serverconect_error));

                        else if (rcode == CnxUtils.SERVER_NOT_FOUND)
                            setMessage(getActivity().getString(
                                    R.string.error_server_notfound));

                        else if (rcode == CnxUtils.WRONG_REQUEST)
                            setMessage(getActivity().getString(
                                    R.string.error_wrong_request));
                        else
                            setMessage(getActivity().getString(
                                    R.string.error_http)
                                    + rcode);
                    }
                    CnxUtils.closeConnection(conn);
                    conn = null;
                } else
                    setMessage(CnxUtils.getLastError());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                setMessage(e.getMessage());
            } finally {
                if (conn != null) {
                    CnxUtils.closeConnection(conn);
                    conn = null;
                }
            }
            return isValidVersion;
        }

        boolean loginValidation(String username, String password) {
            boolean isValidUser = false;
            int rcode;
            String response = null;
            HttpURLConnection conn = null;
            try {
                FirebaseMessaging.getInstance().subscribeToTopic("news");
                String token;
                int i= 0;
                do {
                    token = FirebaseInstanceId.getInstance().getToken();
                    if (i == 3) {
                        token = "tyipokjgkuygf";
                        break;
                    }
//                    if (token != null) {
//                        Log.e("TAREAS TOKEN", "TOKEN -> " + token);
//                    }
                    i++;
                } while (token == null);
                String identifier = EquipmentProperties.getIdentifier(getActivity());
                if (!isReleaseApp && (token == null || token.isEmpty())) {
                    token = "tyipokjgkuygf";
                }
                if (!isReleaseApp && (identifier == null || identifier.isEmpty())) {
                    identifier = "tyipokjgkuygf";
                }
                StringBuilder url = new StringBuilder();
                String b = String.valueOf(Util.getCurrentVerCodeApks(getApplicationContext(), Properties.pakage_tareas));
                url.append(IUrls.USER_LOGINLDAP);
                        url.append("?");
                        url.append("username=");
                        url.append(URLEncoder.encode(username, "ISO-8859-1"));
                        url.append("&password=");
                        url.append(URLEncoder.encode(password, "ISO-8859-1"));
                        url.append("&device_id=");
                        url.append(URLEncoder.encode(identifier, "ISO-8859-1"));
                        url.append("&reg_id=");
                        url.append(URLEncoder.encode(token, "ISO-8859-1"));
                        url.append("&appname=");
                        url.append(URLEncoder.encode(Properties.APP_NAME, "ISO-8859-1"));
                        url.append("&versionapp=");
                        url.append(URLEncoder.encode(Util.getCurrentVerApks(getApplicationContext(), Properties.pakage_tareas), "ISO-8859-1"));
                        url.append("&versioncode=");
                        url.append(URLEncoder.encode(String.valueOf(Util.getCurrentVerCodeApks(getApplicationContext(), Properties.pakage_tareas)), "ISO-8859-1"));
                ;
                Log.d("url", url.toString());
                conn = CnxUtils.makeHttpConnection(url.toString(), null, null, HttpConstants.GET);
                if (conn != null) {
                    rcode = CnxUtils.extractResponseCode(conn);
                    if (rcode == HttpURLConnection.HTTP_OK) {
                        response = new String(
                                CnxUtils.extractResponseData(conn));
                        if (response.length() > 0) {
                            UserData user = new ObjectMapper().readValue(response, UserData.class);
                            if (user != null) {
                                String md5Password = Util.md5(password).toUpperCase();
                                user.setPassword(md5Password);
                                user.setUserLock(username);
                                user.getSession().setDevice_id(EquipmentProperties.getIdentifier(getActivity()));
                                user.getSession().setFecha_login(new Date());
                                if (user.getEstatus_contrasenia() != 3) {
                                    if (user.getPermisos().getTipo_usuario_desc().equals(Properties.PERMISSION_INTERNAL)) {
                                        String _package = urlUibucuo.split("id=")[1];
                                        if (!Util.isAppInstalled(_package)) {
                                            if (!Tools.isDebug()) {
                                                showInfoDialog();
                                                return false;
                                            }
                                        }
                                        String ubicuoVersrionDevice = Tools.getUbicuoVersionName(getActivity());
                                        if (!isReleaseApp) {
                                            ubicuoVersrionDevice = ubicuo_version;
                                        }
                                        if (!ubicuoVersrionDevice.equals(ubicuo_version)) {
                                            showIncorrectVersionApp(getString(R.string.msg_ubicuo_obsolet), Properties.pakage_ubicuo);
                                            return false;
                                        }
                                        if(!accountNameTempo.equals("") && !accountNameTempo.toUpperCase().contains("PENDULUM.COM.MX")){
                                            accountNameTempo = UtilPhone.getAcountGmail();
                                        }


                                        if (accountNameTempo.equals("") ) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    String message = getString(R.string.msg_pendulum_without_account);
                                                    if (mProgressDialog != null)
                                                        try {
                                                            mProgressDialog.dismiss();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                            CustomDialog.showDisclaimer(getActivity(), message, new Interfaces.OnResponse() {

                                                        @Override
                                                        public void onResponse(int handlerCode, Object o) {
                                                                seleccionarCuentaPendulum();
                                                        }
                                                    });
                                                }
                                            });
                                            return false;
                                        }
                                        CatalogDatabaseHelper catalogDatabaseHelper = CatalogDatabaseHelper
                                                .getHelper(getApplicationContext());
                                        catalogDatabaseHelper.clearTables();
                                        catalogDatabaseHelper.close();
                                        catalogDatabaseHelper = null;
                                        UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper
                                                .getHelper(getApplicationContext());
                                        userDatabaseHelper.getUserDataDao();
                                        userDatabaseHelper.clearTables();
                                        SharedPreferences sh = ContextApplication.getAppContext().getSharedPreferences("USER_AUTENTICHATION", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sh.edit();
                                        editor.putString("_USUARIO", edtgetuser.getText().toString());
                                        editor.putString("_NOMBRE", user.getNombre());
                                        editor.putString("_CORREO", user.getCorreo());
                                        editor.apply();
                                        // Write new user data
                                        userDatabaseHelper.getUserDataDao().create(user);
                                        userDatabaseHelper.close();
                                        userDatabaseHelper = null;
                                        isValidUser = true;
                                        // showInfoDialog();
                                        // }
                                    } else {
                                        // Clear previous users data
                                        CatalogDatabaseHelper catalogDatabaseHelper = CatalogDatabaseHelper.getHelper(getApplicationContext());
                                        catalogDatabaseHelper.clearTables();
                                        catalogDatabaseHelper.close();
                                        catalogDatabaseHelper = null;
                                        UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper.getHelper(getApplicationContext());
                                        userDatabaseHelper.getUserDataDao();
                                        userDatabaseHelper.clearTables();
                                        // Write new user data
                                        userDatabaseHelper.getUserDataDao().create(user);
                                        userDatabaseHelper.close();
                                        userDatabaseHelper = null;
                                        isValidUser = true;
                                    }
                                } else
                                    setMessage(getActivity().getString(
                                            R.string.error_user_blocked));
                            }
                        } else
                            setMessage(getActivity().getString(
                                    R.string.error_auth_credentials));
                    } else
                        setMessage(getActivity().getString(
                                R.string.error_auth_credentials));
                    CnxUtils.closeConnection(conn);
                    conn = null;
                } else
                    setMessage(CnxUtils.getLastError());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                setMessage(e.getMessage());
            } finally {
                if (conn != null) {
                    CnxUtils.closeConnection(conn);
                    conn = null;
                }
            }
            return isValidUser;
        }
    }

    private void seleccionarCuentaPendulum() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null);
        startActivityForResult(intent, CHECK_ACCOUNT);
    }

    private void configureComplementos() {
        findViewById(R.id.llComplementos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadComplementos(LoginActivity.this);
            }
        });
//        TextView tv = findViewById(R.id.tvComplementos);
//        Animation anim = new AlphaAnimation(0.0f, 1.0f);
//        anim.setDuration(1500); //You can manage the blinking time with this parameter
//        anim.setStartOffset(20);
//        anim.setRepeatMode(Animation.REVERSE);
//        anim.setRepeatCount(Animation.INFINITE);
//        tv.startAnimation(anim);
    }

    public static void loadComplementos(final Activity activity) {
        String url = IUrls.COMPLEMENTOS;
        Download download = new Download(activity, new Interfaces.OnResponseDownload() {
            @Override
            public void onResponse(int handlerCode, Object o, String errorString) {
                if (o != null) {
                    String str = (String) o;
                    Type listType = new TypeToken<ArrayList<Item>>() {
                    }.getType();
                    final List<Item> list = new Gson().fromJson(str, listType);
                    ComplementosAdapter adapter = new ComplementosAdapter(list, activity);
                    View view = View.inflate(activity, R.layout.complementos_view, null);
                    ListView listView = view.findViewById(R.id.listView);
//                            gridView.setNumColumns(4);               // Number of columns
                    listView.setAdapter(adapter);
                    listView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setView(view);
                    builder.setTitle("Selecciona una aplicación");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // do something here
                            Item item = list.get(position);
                            if (item.getPackageName().startsWith("http")) {
                                String url = item.getPackageName();
                                Uri uri = Uri.parse("googlechrome://navigate?url=" + url);
                                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                                if (i.resolveActivity(activity.getPackageManager()) == null) {
                                    i.setData(Uri.parse(url));
                                }
                                activity.startActivity(i);
                            } else {
                                Tools.openAppOrInstall(activity, item.getPackageName());
                            }
                            activity.finish();
                        }
                    });
                    builder.show();
                }
            }


        }, 0);
        download.config(url, null);
        download.start();
    }

    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECK_ACCOUNT && resultCode == RESULT_OK) {
            accountNameTempo = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            if(!accountNameTempo.toUpperCase().contains("PENDULUM.COM.MX")){
                CustomDialog.showDisclaimer(getActivity(), "La cuenta selecionada no pertenece a pendulum", new Interfaces.OnResponse() {
                    @Override
                    public void onResponse(int handlerCode, Object o) {
                        seleccionarCuentaPendulum();
                    }
                });
            }
        }
    }
}




//if(!accountNameTempo.equals("") && !accountNameTempo.toUpperCase().contains("PENDULUM.COM.MX")){