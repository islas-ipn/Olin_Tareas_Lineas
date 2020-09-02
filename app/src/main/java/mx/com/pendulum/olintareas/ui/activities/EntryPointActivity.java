package mx.com.pendulum.olintareas.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;

import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.camera.CameraActivity;
import mx.com.pendulum.olintareas.config.util.ContextApplication;
import mx.com.pendulum.olintareas.db.CatalogDatabaseHelper;
import mx.com.pendulum.olintareas.db.SepomexDatabaseHelper;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.UserData;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.lbs.WorldBroadcastReceiver;
import mx.com.pendulum.olintareas.sync.SyncDataInService;
import mx.com.pendulum.olintareas.sync.SyncDataOutService;
import mx.com.pendulum.olintareas.tareas.views.ViewFile_upload;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;
import mx.com.pendulum.olintareas.ui.fragments.ProgressDialogFragment;
import mx.com.pendulum.utilities.AppPermissions;
import mx.com.pendulum.utilities.Util;
import mx.com.pendulum.utilities.asyncui.OrientationCompatFragmentActivity;
import mx.com.pendulum.utilities.asyncui.OrientationCompatTask;

public class EntryPointActivity extends OrientationCompatFragmentActivity {

    private static final String TAG = EntryPointActivity.class.getSimpleName();

    private Vector<AlertDialog> dialogs = new Vector<>();
    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    ProgressDialogFragment mProgressDialog = null;
    private int contadorPermisos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(new Intent(getApplicationContext(),
                WorldBroadcastReceiver.class)
                .setAction(WorldBroadcastReceiver.ACTION_UPDATE_CONFIG));
        verifyPermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDialogs();
    }

    private void verifyPermissions() {
        new AppPermissions(this, new Interfaces.OnResponse<Object>() {
            @Override
            public void onResponse(int handlerCode, final Object o) {
                if (o != null) {
                    try {
                        String message = "Se necesitan autorizar los siguientes permisos para el correcto funcionamiento de Olin";
                        AlertDialog dialog = new AlertDialog.Builder(EntryPointActivity.this, R.style.AlertDialogStyle)
                                .setMessage(message)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        EntryPointActivity.this.requestPermissions((String[]) o, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                    }
                                })
                                .setCancelable(false)
                                .create();
                        dialogs.add(dialog);
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

    private void initActivity() {
        InitialLoadTask iLoadTask;
        Object retained = onRetainNonConfigurationInstance();
        if (retained instanceof InitialLoadTask) {
            iLoadTask = (InitialLoadTask) retained;
            iLoadTask.setActivity(this);
        } else {
            iLoadTask = new InitialLoadTask();
            iLoadTask.setActivity(this);
            iLoadTask.execute();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            Map<String, Integer> perms = AppPermissions.getPermsHash();
            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
            if (AppPermissions.verifyMap(perms)) {
                initActivity();
            } else {
                if (contadorPermisos == 1) {
                    AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setTitle("Advertencia!!!")
                            .setMessage("Todos los permisos son necesarios para el correcto funcionamiento de Olin.\n\n En la siguiente pantalla, dentro de la sección de Accesos o Permisos los podrá activar.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .create();
                    dialogs.add(dialog);
                    dialog.show();
                } else {
                    contadorPermisos++;
                    verifyPermissions();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onTaskCompleted(boolean result, String message) {
        super.onTaskCompleted(result, message);
        finish();
    }

    @Override
    public void addProgressDialog() {
        try {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            mProgressDialog = ProgressDialogFragment
                    .newInstance(getString(R.string.loading));
            mProgressDialog.show(fragmentTransaction, ProgressDialogFragment.TAG);
        } catch (Exception e) {
            recreate();
        }
    }

    @SuppressLint("CommitTransaction")
    @Override
    public void removeProgressDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment lastProgressDialog = getSupportFragmentManager()
                .findFragmentByTag(ProgressDialogFragment.TAG);
        if (lastProgressDialog != null)
            fragmentTransaction.remove(lastProgressDialog);
    }

    @Override
    protected void addFinishedDialog() {
    }

    @Override
    protected void removeFinishedDialog() {
    }

    @SuppressLint("StaticFieldLeak")
    public class InitialLoadTask extends OrientationCompatTask {
        // Creates the DB first time around.
        @Override
        protected Boolean doInBackground(Object... unused) {
            boolean userLoggedIn = false;
            UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper
                    .getHelper(getApplicationContext());
            UserData user = null;
            try {
                user = userDatabaseHelper.getUserDataDao().getCurrentUser();
                if (user != null && user.getUsername() != null
                        && !user.getUsername().isEmpty())
                    userLoggedIn = user.getSession().isLogged_in();
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            userDatabaseHelper.close();
            CatalogDatabaseHelper catalogDatabaseHelper = CatalogDatabaseHelper.getHelper(getApplicationContext());
            catalogDatabaseHelper.close();
            SepomexDatabaseHelper sepomexDatabaseHelper = SepomexDatabaseHelper.getHelper(getApplicationContext());
            sepomexDatabaseHelper.prepareDataBase();
            sepomexDatabaseHelper.close();
            if (userLoggedIn) {
                boolean resizeIMAGE = false;
                boolean isImageResize= false;
                String imageName = "image" + ViewFile_upload.SEPATATOR + 1 +
                        ViewFile_upload.SEPATATOR;
                Intent intent = new Intent(ContextApplication.getAppContext(), CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_FILE_NAME, imageName);
                intent.putExtra(CameraActivity.KEY_FILE_PATH, Properties.SD_CARD_IMAGES_DIR);
                intent.putExtra(CameraActivity.QUESTION_TYPE_IMAGE_RESIZE, resizeIMAGE);
                intent.putExtra(CameraActivity.KEY_IMAGE_RESIZE, isImageResize);
                intent.putExtra(CameraActivity.KEY_NUMBER_OF_PHOTOS, 5);
                intent.putExtra(CameraActivity.KEY_ALREADY_TAKEN, 0);
                //intent.putExtra(CameraActivity.KEY_FREE_SPACES, freeSpaces);

                //startActivity(intent);

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } // User data exists and data is being synchronized in or out.
            else if (user != null
                    && (Util.checkServiceExecution(getApplicationContext(),
                    SyncDataInService.class.getName()) && !SyncDataInService
                    .isFinished())
                    || (Util.checkServiceExecution(getApplicationContext(),
                    SyncDataOutService.class.getName()) && !SyncDataOutService
                    .isFinished())) {
                Intent in = new Intent(getApplicationContext(), SyncDataProgressActivity.class);
                in.putExtra(Properties.EXTRA_IS_FROM_ANOTHER_APP, false);
                startActivity(in);
            } else {
                boolean resizeIMAGE = false;
                boolean isImageResize = false;
                String imageName = "image" + ViewFile_upload.SEPATATOR + 1 +
                        ViewFile_upload.SEPATATOR;
                Intent intent = new Intent(ContextApplication.getAppContext(), CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_FILE_NAME, imageName);
                intent.putExtra(CameraActivity.KEY_FILE_PATH, Properties.SD_CARD_IMAGES_DIR);
                intent.putExtra(CameraActivity.QUESTION_TYPE_IMAGE_RESIZE, resizeIMAGE);
                intent.putExtra(CameraActivity.KEY_IMAGE_RESIZE, isImageResize);
                intent.putExtra(CameraActivity.KEY_NUMBER_OF_PHOTOS, 5);
                intent.putExtra(CameraActivity.KEY_ALREADY_TAKEN, 0);
                //intent.putExtra(CameraActivity.KEY_FREE_SPACES, freeSpaces);

                //startActivity(intent);

                // No user data exists, login.
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
            return true;
        }
    }

    public void closeDialogs() {
        for (AlertDialog dialog : dialogs)
            if (dialog.isShowing()) dialog.dismiss();
    }
}
