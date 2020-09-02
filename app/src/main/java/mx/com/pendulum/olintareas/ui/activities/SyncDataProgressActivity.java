package mx.com.pendulum.olintareas.ui.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.EpochDate;
import mx.com.pendulum.olintareas.dto.tareasV2.FileUploadDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NotaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NotasDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NoteResponseDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.ResponseTask;
import mx.com.pendulum.olintareas.dto.tareasV2.SeguimientoTarea;
import mx.com.pendulum.olintareas.dto.tareasV2.TemporalForm;
import mx.com.pendulum.olintareas.sync.SyncDataInService;
import mx.com.pendulum.olintareas.sync.SyncDataOutService;
import mx.com.pendulum.olintareas.ui.fragments.ProgressDialogFragment;
import mx.com.pendulum.utilities.ErrorReport;

public class SyncDataProgressActivity extends AppCompatActivity {

    protected static final String TAG = SyncDataProgressActivity.class.getSimpleName();

    private BroadcastReceiver syncInFinishedReceiver;
    private BroadcastReceiver syncOutFinishedReceiver;

    private ProgressDialogFragment mProgressDialog = null;

    private boolean finished = false;
    private String error = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (!invokedFromOtherApp(getIntent()))
            return;*/
        final SyncDataProgressActivity me = this;
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("finished"))
                finished = savedInstanceState.getBoolean("finished");
            if (savedInstanceState.containsKey("error"))
                error = savedInstanceState.getString("error");
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SyncDataInService.SYNC_FINISHED_BROADCAST);
        intentFilter.addAction(SyncDataInService.SYNC_FAILED_BROADCAST);
        intentFilter.addAction(SyncDataInService.SYNC_PROGRESS_BROADCAST);
        syncInFinishedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                removeProgressDialog();
                if (action != null) {
                    switch (action) {
                        case SyncDataInService.SYNC_FINISHED_BROADCAST:
                            SharedPreferences sharedPreferences = getSharedPreferences("IPs", MODE_PRIVATE);
                            boolean isFromAnotherApp = sharedPreferences.getBoolean(Properties.EXTRA_IS_FROM_ANOTHER_APP, false);
                            if (isFromAnotherApp) {
                                finished = true;
                                /*final AlertDialog alertDialog = new AlertDialog.Builder(me).create();
                                alertDialog.setTitle(getString(R.string.error));
                                alertDialog.setMessage("Ya termino");
                                alertDialog.setCancelable(false);
                                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                                        getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                alertDialog.dismiss();
                                                finish();
                                            }
                                        });
                                alertDialog.show();*/
                            } else {
                                finished = true;
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                            break;
                        case SyncDataInService.SYNC_FAILED_BROADCAST:
                            finished = true;
                            String userFriendlyError = intent.getStringExtra(ErrorReport.KEY_TYPE);
                            error = userFriendlyError;
                            if (userFriendlyError != null) {
                                final AlertDialog alertDialog = new AlertDialog.Builder(me).create();
                                alertDialog.setTitle(getString(R.string.error));
                                alertDialog.setMessage(userFriendlyError);
                                alertDialog.setCancelable(false);
                                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                                        getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                alertDialog.dismiss();
                                                finish();
                                            }
                                        });
                                if (getApplication() != null)
                                    alertDialog.show();
                            }
                            break;
                        case SyncDataInService.SYNC_PROGRESS_BROADCAST:
                            int progress = intent.getIntExtra(SyncDataInService.SYNC_PROGRESS, 0);
                            String description = intent.getStringExtra(SyncDataInService.SYNC_DSCRIPTION);
                            mProgressDialog.setProgress(progress, description);
                            break;
                    }
                }
            }
        };
        registerReceiver(syncInFinishedReceiver, intentFilter);
        intentFilter = new IntentFilter();
        intentFilter.addAction(SyncDataOutService.SYNC_FINISHED_BROADCAST);
        intentFilter.addAction(SyncDataOutService.SYNC_FAILED_BROADCAST);
        intentFilter.addAction(SyncDataOutService.SYNC_PROGRESS_BROADCAST);
        syncOutFinishedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                removeProgressDialog();
                if (action != null) {
                    switch (action) {
                        case SyncDataOutService.SYNC_FINISHED_BROADCAST:
                            finished = true;
                            ActivityCompat.finishAffinity(SyncDataProgressActivity.this);
                            break;
                        case SyncDataOutService.SYNC_FAILED_BROADCAST:
                            finished = true;
                            String userFriendlyError = intent
                                    .getStringExtra(ErrorReport.KEY_TYPE);
                            error = userFriendlyError;
                            if (userFriendlyError != null) {
                                final AlertDialog alertDialog = new AlertDialog.Builder(
                                        me).create();
                                alertDialog.setTitle(getString(R.string.error));
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage(userFriendlyError);
                                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                                        getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                alertDialog.dismiss();
                                                finish();
                                            }
                                        });
                                alertDialog.show();
                            }
                            break;
                        case SyncDataOutService.SYNC_PROGRESS_BROADCAST:
                            int progress = intent.getIntExtra(SyncDataOutService.SYNC_PROGRESS, 0);
                            String description = intent.getStringExtra(SyncDataOutService.SYNC_DSCRIPTION);
                            mProgressDialog.setProgress(progress, description);
                            break;
                    }
                }
            }
        };
        registerReceiver(syncOutFinishedReceiver, intentFilter);
        addProgressDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (finished) {
            if (error == null) {
                finish();
            } else {
                final AlertDialog alertDialog = new AlertDialog.Builder(
                        this).create();
                alertDialog.setTitle(getString(R.string.error));
                alertDialog.setMessage(error);
                alertDialog.setCancelable(false);
                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                        getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                alertDialog.dismiss();
                                finish();
                            }
                        });
                alertDialog.show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("finished", finished);
        if (error != null)
            outState.putString("error", error);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (syncInFinishedReceiver != null) {
            unregisterReceiver(syncInFinishedReceiver);
            syncInFinishedReceiver = null;
        }
        if (syncOutFinishedReceiver != null) {
            unregisterReceiver(syncOutFinishedReceiver);
            syncOutFinishedReceiver = null;
        }
    }

    private void moveFiles(String sSrcDir, String sDestDir, boolean deleteFile) {
        File srcDir = new File(sSrcDir);
        File destDir = new File(sDestDir);
        try {
            if (srcDir.exists()) {
                if (!destDir.exists()) {
                    Log.i("Directorio Creado: ", destDir + "  --> " + destDir.mkdirs());
                }
                File[] files = srcDir.listFiles();
                for (File f : files) {
                    String fileName = f.getName();
                    File destFile = new File(destDir + Properties.FILE_SEPERATOR + fileName);
                    copy(f, destFile);
                    if (deleteFile)
                        Log.d(Properties.TAG_DEVELOP, "Eliminado archivo " + f.getName() + " --> " + f.delete());
                }
            }
        } catch (Exception e) {
            Log.i("Exception", e.toString());
        }
    }

    public static void copy(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private ArrayList<NotaDTO> jsonTareas(String nameJson) {
        ArrayList<NotaDTO> notas = null;
        File file = new File(Properties.SD_CARD_IMAGES_DIR_RESP + Properties.SD_JSON_COBR_RESP + nameJson);
        if (file.exists()) {
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException ignored) {
            }
            String result = text.toString();
            ArrayList<TareaDTO> tareas = new Gson().fromJson(result, new TypeToken<ArrayList<TareaDTO>>() {
            }.getType());
            for (TareaDTO tsk : tareas) {
                try {
                    UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getApplicationContext());
                    Dao<TareaDTO, Long> tareaDTODao = helper.getDao(TareaDTO.class);
                    tareaDTODao.createOrUpdate(tsk);
                    File fileNotes = new File(Properties.SD_CARD_IMAGES_DIR_RESP + Properties.SD_JSON_COBR_RESP + Properties.NAME_JSON_NOTAS_RESP);
                    if (fileNotes.exists()) {
                        text = new StringBuilder();
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(fileNotes));
                            String line;
                            while ((line = br.readLine()) != null) {
                                text.append(line);
                                text.append('\n');
                            }
                            br.close();
                        } catch (IOException ignored) {
                        }
                        result = text.toString();
                        if (notas == null)
                            notas = new Gson().fromJson(result, new TypeToken<ArrayList<NotaDTO>>() {
                            }.getType());
                        NotaDTO nota;
                        for (int i = 0; i < notas.size(); i++) {
                            try {
                                nota = notas.get(i);
                                if (tsk.getIdRespaldo() == nota.getIdTarea()) {
                                    nota.setIdTarea(tsk.get_id().intValue());
                                    UserDatabaseHelper helperSeg = UserDatabaseHelper.getHelper(getApplicationContext());
                                    Dao<NotaDTO, Long> segDao = helperSeg.getDao(NotaDTO.class);
                                    NotaDTO notaDB = segDao.queryBuilder().where().eq(NotaDTO.COL_ID_TAREA, tsk.get_id()).queryForFirst();
                                    if (notaDB != null) {
                                        nota.set_id(notaDB.get_id());
                                        segDao.update(nota);
                                    } else
                                        segDao.create(nota);
                                    notas.remove(nota);
                                    i--;
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                Log.d(Properties.TAG_DEVELOP, "ERROR INSERTANDO NOTAS DE TAREAS");
                                return notas;
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.d(Properties.TAG_DEVELOP, "ERROR INSERTANDO NOTAS DE TAREAS");
                    return notas;
                }
            }
            Log.d(Properties.TAG_DEVELOP, "INSERTANDO NOTAS DE TAREAS");
        }
        return notas;
    }*/

    private void jsonNotas(ArrayList<NotaDTO> notas) {
        if (notas == null) {
            File fileNotes = new File(Properties.SD_CARD_IMAGES_DIR_RESP + Properties.SD_JSON_COBR_RESP + Properties.NAME_JSON_NOTAS_RESP);
            if (fileNotes.exists()) {
                StringBuilder text = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(fileNotes));
                    String line;
                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                } catch (IOException ignored) {
                }
                String result = text.toString();
                notas = new Gson().fromJson(result, new TypeToken<ArrayList<NotaDTO>>() {
                }.getType());
            }
        }
        if (notas != null) {
            for (NotaDTO nts : notas) {
                try {
                    UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getApplicationContext());
                    Dao<NotaDTO, Long> notasDTODao = helper.getDao(NotaDTO.class);
                    notasDTODao.create(nts);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.d(Properties.TAG_DEVELOP, "ERROR INSERTANDO NOTAS");
                    return;
                }
            }
        }
        Log.d(Properties.TAG_DEVELOP, "INSERTANDO NOTAS");
    }

    private void jsonNotas2() {
        ArrayList<NotasDTO> notas = null;
        File fileNotes = new File(Properties.SD_CARD_IMAGES_DIR_RESP + Properties.SD_JSON_COBR_RESP + Properties.NAME_JSON_NOTAS2_RESP);
        if (fileNotes.exists()) {
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(fileNotes));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException ignored) {
            }
            String result = text.toString();
            notas = new Gson().fromJson(result, new TypeToken<ArrayList<NotasDTO>>() {
            }.getType());
        }
        if (notas != null) {
            for (NotasDTO nts : notas) {
                try {
                    UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getApplicationContext());
                    Dao<NotasDTO, Long> notasDTODao = helper.getDao(NotasDTO.class);
                    notasDTODao.create(nts);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.d(Properties.TAG_DEVELOP, "ERROR INSERTANDO NOTAS");
                    return;
                }
            }
        }
        Log.d(Properties.TAG_DEVELOP, "INSERTANDO NOTAS");
    }


    private void searchJson(String nameJson, boolean deleteFiles) {
        File file = new File(Properties.SD_CARD_IMAGES_DIR_RESP + Properties.SD_JSON_COBR_RESP + nameJson);
        if (file.exists()) {
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException ignored) {
            }
            String result = text.toString();
            switch (nameJson) {
                case Properties.NAME_JSON_ANSWERS_RESP:
                    ArrayList<AnswerDTO> answers = new Gson().fromJson(result, new TypeToken<ArrayList<AnswerDTO>>() {
                    }.getType());
                    for (AnswerDTO ans : answers) {
                        try {
                            UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getApplicationContext());
                            Dao<AnswerDTO, Long> notasDTODao = helper.getDao(AnswerDTO.class);
                            notasDTODao.create(ans);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Log.d(Properties.TAG_DEVELOP, "ERROR INSERTANDO ANSWERS");
                            return;
                        }
                    }
                    Log.d(Properties.TAG_DEVELOP, "INSERTANDO ANSWERS");
                    break;
                case Properties.NAME_JSON_FILE_RESP:
                    ArrayList<FileUploadDTO> files = new Gson().fromJson(result, new TypeToken<ArrayList<FileUploadDTO>>() {
                    }.getType());
                    for (FileUploadDTO fl : files) {
                        try {
                            UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getApplicationContext());
                            Dao<FileUploadDTO, Long> notasDTODao = helper.getDao(FileUploadDTO.class);
                            notasDTODao.create(fl);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Log.d(Properties.TAG_DEVELOP, "ERROR INSERTANDO FILES UOLOADS");
                            return;
                        }
                    }
                    Log.d(Properties.TAG_DEVELOP, "INSERTANDO FILES UOLOADS");
                    break;
                case Properties.NAME_JSON_EPOCH_RESP:
                    ArrayList<EpochDate> epochs = new Gson().fromJson(result, new TypeToken<ArrayList<EpochDate>>() {
                    }.getType());
                    for (EpochDate epoo : epochs) {
                        try {
                            UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getApplicationContext());
                            Dao<EpochDate, Long> notasDTODao = helper.getDao(EpochDate.class);
                            notasDTODao.create(epoo);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Log.d(Properties.TAG_DEVELOP, "ERROR INSERTANDO EPOCH");
                            return;
                        }
                    }
                    Log.d(Properties.TAG_DEVELOP, "INSERTANDO EPOCH");
                    break;
                case Properties.NAME_JSON_TEMPORAL_FORM_RESP:
                    ArrayList<TemporalForm> temporals = new Gson().fromJson(result, new TypeToken<ArrayList<TemporalForm>>() {
                    }.getType());
                    for (TemporalForm temp : temporals) {
                        try {
                            UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getApplicationContext());
                            Dao<TemporalForm, Long> notasDTODao = helper.getDao(TemporalForm.class);
                            notasDTODao.create(temp);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Log.d(Properties.TAG_DEVELOP, "ERROR INSERTANDO TEMPORAL");
                            return;
                        }
                    }
                    Log.d(Properties.TAG_DEVELOP, "INSERTANDO TEMPORAL");
                    break;
                case Properties.NAME_JSON_SEGUIMIENTO_RESP:
                    ArrayList<SeguimientoTarea> seguimientos = new Gson().fromJson(result, new TypeToken<ArrayList<SeguimientoTarea>>() {
                    }.getType());
                    for (SeguimientoTarea seg : seguimientos) {
                        try {
                            UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getApplicationContext());
                            Dao<SeguimientoTarea, Long> notasDTODao = helper.getDao(SeguimientoTarea.class);
                            SeguimientoTarea seguimientoTarea = notasDTODao.queryBuilder().where().eq(SeguimientoTarea.ID, seg.getId()).queryForFirst();
                            if (seguimientoTarea != null) {
                                seg.set_id(seguimientoTarea.get_id());
                                notasDTODao.update(seg);
                            } else
                                notasDTODao.create(seg);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Log.d(Properties.TAG_DEVELOP, "ERROR INSERTANDO SEGUIMIENTO");
                            return;
                        }
                    }
                    Log.d(Properties.TAG_DEVELOP, "INSERTANDO SEGUIMIENTO");
                    break;
                case Properties.NAME_JSON_RESPONSE_TSK_RESP:
                    ArrayList<ResponseTask> responses = new Gson().fromJson(result, new TypeToken<ArrayList<ResponseTask>>() {
                    }.getType());
                    for (ResponseTask resp : responses) {
                        try {
                            UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getApplicationContext());
                            Dao<ResponseTask, Long> notasDTODao = helper.getDao(ResponseTask.class);
                            notasDTODao.create(resp);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Log.d(Properties.TAG_DEVELOP, "ERROR INSERTANDO RESPONSE");
                            return;
                        }
                    }
                    Log.d(Properties.TAG_DEVELOP, "INSERTANDO RESPONSE");
                    break;
                case Properties.NAME_JSON_NOTE_RSP_RESP:
                    ArrayList<NoteResponseDTO> ntResponses = new Gson().fromJson(result, new TypeToken<ArrayList<NoteResponseDTO>>() {
                    }.getType());
                    for (NoteResponseDTO ntResponse : ntResponses) {
                        try {
                            UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getApplicationContext());
                            Dao<NoteResponseDTO, Long> notasDTODao = helper.getDao(NoteResponseDTO.class);
                            notasDTODao.create(ntResponse);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Log.d(Properties.TAG_DEVELOP, "ERROR INSERTANDO RESPONSE NOTES");
                            return;
                        }
                    }
                    Log.d(Properties.TAG_DEVELOP, "INSERTANDO RESPONSE NOTES");
                    break;
            }
            if (deleteFiles)
                moveFiles(Properties.SD_CARD_IMAGES_DIR_RESP + "/json", Properties.SD_CARD_IMAGES_DIR_BACKUP_TSK + "/archivosTSK", true);
        }
    }

    private void addProgressDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        mProgressDialog = ProgressDialogFragment
                .newInstance(getString(R.string.synchronizing_data));
        mProgressDialog.show(fragmentTransaction, "fragment_dialog_progress");
    }

    private void removeProgressDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment lastProgressDialog = getSupportFragmentManager()
                .findFragmentByTag(ProgressDialogFragment.TAG);
        if (lastProgressDialog != null)
            fragmentTransaction.remove(lastProgressDialog);
    }

    /*public class ActionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("mx.com.pendulum.olintareas.MainActivity".equalsIgnoreCase(intent.getAction())) {
                Toast.makeText(context, "Funcionando", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context, "Funcionando", Toast.LENGTH_SHORT).show();
        }

        public ActionReceiver() {
            super();
            Toast.makeText(getApplicationContext(), "ActuibReceiver", Toast.LENGTH_SHORT).show();
        }

        @Override
        public IBinder peekService(Context myContext, Intent service) {
            Toast.makeText(myContext, "peekService", Toast.LENGTH_SHORT).show();
            return super.peekService(myContext, service);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        invokedFromOtherApp(intent);
    }

    private boolean invokedFromOtherApp(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(Properties.EXTRA_IS_FROM_ANOTHER_APP)) {
            //TODO QUE SEA DE OTRA APP Y BORRAR LOGS
            SharedPreferences sharedPreferences = getSharedPreferences("IPs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Properties.EXTRA_IS_FROM_ANOTHER_APP, true);
            Log.d(TAG, "Entra al externo");
            boolean isFromAnotherApp = extras.getBoolean(Properties.EXTRA_IS_FROM_ANOTHER_APP, false);
            editor.apply();
            return isFromAnotherApp;
        } else {
            Log.d(TAG, "Noporolo");
            return true;
        }
    }*/
}
