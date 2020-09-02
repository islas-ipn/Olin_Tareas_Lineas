package mx.com.pendulum.olintareas.ui.parents;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.utilities.AppPermissions;
import mx.com.pendulum.utilities.Tools;

public class FragmentParent extends Fragment {
    public static final String TITLE_FRAGMENT = "title_fragment";
    public static final String SUB_TITLE_FRAGMENT = "sub_title_fragment";
    public static final String IS_HOME_ENABLED = "home_enabled_fragment";
    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @Override
    public void onResume() {
        Tools.setupUI(getActivity(), getActivity().findViewById(android.R.id.content));

        super.onResume();
        AppCompatActivityParent.verifyGPS(getActivity());
        verifyPermissions();
    }

    private void initActivity() {

        if (verifyAutomaticDateTimeEnabled()) {
            if (verifyAitomaticDateTimeZoneEnabled()) {
                verifyUbicuoPermissions();
            }
        }

        ActionBar a = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (a != null) {
            a.setTitle(this.getArguments().getString(TITLE_FRAGMENT));
            a.setSubtitle(this.getArguments().getString(SUB_TITLE_FRAGMENT));
        }
    }

    private void verifyPermissions() {
        new AppPermissions(getActivity(), new Interfaces.OnResponse<Object>() {
            @Override
            public void onResponse(int handlerCode, final Object o) {
                if (o != null) {
                    try {
                        String message = "Se necesitan autorizar los siguientes permisos para el correcto funcionamiento de Olin";
                        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                                .setMessage(message)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().requestPermissions((String[]) o, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                    }
                                })
                                .setCancelable(false)
                                .create();
                        dialog.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    initActivity();
                }
            }
        }, 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setMenuVisibility(true);
    }


    public void setTitle(String title) {
        ActionBar a = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (a != null) {
            a.setTitle(" " + title);
        }
    }

    @Override
    public void onPause() {

        Tools.hideSoftKeyboard(getActivity());
        super.onPause();
    }


    private void verifyUbicuoPermissions() {
        boolean hasPermissions = true;
       /* try {
            Context con = getActivity().createPackageContext(Properties.pakageUbicuo, 0);
            SharedPreferences pref = con.getSharedPreferences(AppCompatActivityParent.PREFERENCE_NAME, Context.MODE_PRIVATE);
            hasPermissions = pref.getBoolean(AppCompatActivityParent.HAS_PERMISSION, false);


            Log.i("", "");

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Not data shared", e.toString());
        }
       */

        if (Tools.isDebug()) {
            return;
        }
        if (AppCompatActivityParent.isContinueIfExterno()) {
            return;
        }


        if (!hasPermissions) {
            CustomDialog.showDisclaimer(getActivity(), "Warning", "Para el correcto funcionamiento de Olin es necesario que Ubicuo este activo, ¿Desea abrir Ubicuo?", new Interfaces.OnResponse() {
                @Override
                public void onResponse(int handlerCode, Object o) {

//                    boolean bool = Tools.openUbicuo();
                    boolean bool = Tools.openUbicuo(getActivity(), Properties.pakageUbicuo);
                    if (bool) {
                        getActivity().finish();
                    } else {


                        CustomDialog.showDisclaimer(getActivity(), "Warning", getString(R.string.mensaje_ubicuo), new Interfaces.OnResponse() {

                            @Override
                            public void onResponse(int handlerCode, Object o) {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Properties.pakageUbicuo)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Properties.pakageUbicuo)));
                                }

                                getActivity().finish();
                            }
                        });
                    }
                }
            });
        } else {
            AppCompatActivityParent.verifyUbicuoVersion(getActivity());
        }
    }


    private boolean verifyAutomaticDateTimeEnabled() {
        int autoTime = Tools.checkAutoTime(); // si estan en 1 es que si están habilitado

        if (autoTime == 0) {
            CustomDialog.showDisclaimer(getActivity(), "Favor de habilitar la hora y fecha automatica para el correcto funcionamiento de Olin", new Interfaces.OnResponse() {
                @Override
                public void onResponse(int handlerCode, Object o) {
                    startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                    getActivity().finish();
                }
            });
            return false;
        } else {
            return true;
        }


    }

    private boolean verifyAitomaticDateTimeZoneEnabled() {
        int autoTimeZone = Tools.checkAutoTimeZone();// si estan en 1 es que si están habilitado
        if (autoTimeZone == 0) {
            CustomDialog.showDisclaimer(getActivity(), "Favor de habilitar la zona horaria automatica para el correcto funcionamiento de Olin", new Interfaces.OnResponse() {
                @Override
                public void onResponse(int handlerCode, Object o) {
                    startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                    getActivity().finish();
                }
            });
            return false;
        } else {
            return true;
        }
    }
}
