package mx.com.pendulum.olintareas.ui.parents;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.config.util.ContextApplication;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.UserPermissions;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.utilities.AppPermissions;
import mx.com.pendulum.utilities.Tools;

public class AppCompatActivityParent extends AppCompatActivity {

    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @Override
    public void onResume() {
        super.onResume();
        Tools.setupUI(this, findViewById(android.R.id.content));
        verifyPermissions();
    }

    private void initActivity() {
        if (verifyAutomaticDateTimeEnabled()) {
            if (verifyAitomaticDateTimeZoneEnabled()) {
                verifyUbicuoPermissions();
            }
        }
        try {
            addTitle();
        } catch (Exception e) {
            Log.i("", "");
        }
    }

    public static void verifyGPS(final Activity activity) {
        String locationProviders = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            CustomDialog.showDisclaimer(activity, "Se necesita tener el GPS prendido para el funcionamiento de Olin ¿Deseas prender el GPS?", new Interfaces.OnResponse() {
                @Override
                public void onResponse(int handlerCode, Object o) {
                    activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    activity.finish();
                }
            });
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
    private void addTitle() {
        ActionBar a = getSupportActionBar();
        if (a != null) {
            String title = this.getIntent().getExtras().getString(FragmentParent.TITLE_FRAGMENT);
            Spanned t;
            try {
                if (title != null && title.length() >= 18) {
                    t = Html.fromHtml("<small>" + title + "</small>");
                } else {
                    if (title == null) title = "";
                    t = Html.fromHtml(title);
                }
                a.setTitle(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
            a.setSubtitle(this.getIntent().getExtras().getString(FragmentParent.SUB_TITLE_FRAGMENT));
            a.setDisplayHomeAsUpEnabled(this.getIntent().getExtras().getBoolean(FragmentParent.IS_HOME_ENABLED, false));
        }
    }

    public Activity getActivity() {
        return this;
    }

    public Bundle getArguments() {
        return getIntent().getExtras();
    }

    public void setArguments(Bundle args) {
        getIntent().putExtras(args);
    }

    public Activity getView() {
        return getActivity();
    }

    @Override
    protected void onPause() {
        Tools.hideSoftKeyboard(this);
        super.onPause();
    }

    private boolean verifyAutomaticDateTimeEnabled() {
        int autoTime = Tools.checkAutoTime(); // si estan en 1 es que si están habilitado
        if (autoTime == 0) {
            CustomDialog.showDisclaimer(this, "Favor de habilitar la hora y fecha automatica para el correcto funcionamiento de Olin", new Interfaces.OnResponse() {
                @Override
                public void onResponse(int handlerCode, Object o) {
                    startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                    finish();
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
            CustomDialog.showDisclaimer(this, "Favor de habilitar la zona horaria automatica para el correcto funcionamiento de Olin", new Interfaces.OnResponse() {
                @Override
                public void onResponse(int handlerCode, Object o) {
                    startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                    finish();
                }
            });
            return false;
        } else {
            return true;
        }
    }

    private void verifyUbicuoPermissions() {
        boolean hasPermissions = true;
     /*   try {
            Context con = createPackageContext(Properties.pakageUbicuo, 0);
            SharedPreferences pref = con.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            hasPermissions = pref.getBoolean(HAS_PERMISSION, false);


            Log.i("", "");

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Not data shared", e.toString());
        }
*/
        if (Tools.isDebug()) {
            return;
        }
        if (isContinueIfExterno()) {
            return;
        }
        if (hasPermissions) {
            verifyUbicuoVersion(getActivity());
        } else {
            CustomDialog.showDisclaimer(this, "Warning", "Para el correcto funcionamiento de Olin es necesario que Ubicuo este activo, ¿Desea abrir Ubicuo?", new Interfaces.OnResponse() {
                @Override
                public void onResponse(int handlerCode, Object o) {
//                    boolean bool = Tools.openUbicuo();
                    boolean bool = Tools.openUbicuo(getActivity(), Properties.pakageUbicuo);
                    if (bool) {
                        finish();
                    } else {
                        CustomDialog.showDisclaimer(getActivity(), "Warning", getString(R.string.mensaje_ubicuo), new Interfaces.OnResponse() {
                            @Override
                            public void onResponse(int handlerCode, Object o) {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Properties.pakageUbicuo)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Properties.pakageUbicuo)));
                                }
                                finish();
                            }
                        });
                    }
                }
            });
        }
    }

    public static void verifyUbicuoVersion(final Activity act) {
        if (Tools.getUbicuoVersionCode(act) < 57) {
            CustomDialog.showDisclaimer(act, "Por favor desinstale Ubicuo", new Interfaces.OnResponse() {
                @Override
                public void onResponse(int handlerCode, Object o) {
                    Uri packageUri = Uri.parse("package:" + Properties.pakageUbicuo);
                    Intent uninstallIntent =
                            new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                    act.startActivity(uninstallIntent);
                    act.finish();
                }
            });
        } else if (Tools.getUbicuoVersionCode(act) < 58) {
            CustomDialog.showDisclaimer(act, "Por favor actualice Ubicuo", new Interfaces.OnResponse() {
                @Override
                public void onResponse(int handlerCode, Object o) {
                    try {
                        act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Properties.pakageUbicuo)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Properties.pakageUbicuo)));
                    }
                    act.finish();
                }
            });
        }
    }

    /**
     * verifica que el usuario sea externo, si lo es no verifica la instalacion  y/o vercion de ubicuo
     * en caso de no tener secion iniciada te dejara pasar hasta que verifique que seas interno
     *
     */
    public static boolean isContinueIfExterno() {
        UserDatabaseHelper helper = UserDatabaseHelper.getHelper(ContextApplication.getAppContext());
        try {
            Dao<UserPermissions, Long> dao = helper.getDao(UserPermissions.class);
            UserPermissions userPermissions = dao.queryForAll().get(0);
            if (userPermissions.getTipo_usuario_desc().equals(Properties.PERMISSION_INTERNAL)) {
                return false;
            }
        } catch (Exception ignored) {
            return true;
        } finally {
            helper.close();
        }
        return true;
    }
}