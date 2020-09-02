package mx.com.pendulum.olintareas.sync;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.config.util.ContextApplication;
import mx.com.pendulum.olintareas.db.CatalogDatabaseHelper;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.db.dao.UserDataDaoImpl;
import mx.com.pendulum.olintareas.dto.SubClasifica.RelSubClasificaQuestions;
import mx.com.pendulum.olintareas.dto.fromOptions.AutocompleteFO;
import mx.com.pendulum.olintareas.dto.SubClasifica.CatTskSubclasificaTareas;
import mx.com.pendulum.olintareas.dto.SubClasifica.RelOptionQuery;
import mx.com.pendulum.olintareas.dto.fromOptions.RelOptionQueryFO;
import mx.com.pendulum.olintareas.dto.fromOptions.RelOptionQuestionsFO;
import mx.com.pendulum.olintareas.dto.UserData;
import mx.com.pendulum.olintareas.dto.fromOptions.DocumentsFO;
import mx.com.pendulum.olintareas.dto.fromOptions.OptionsFO;
import mx.com.pendulum.olintareas.dto.fromOptions.QuestionsFO;
import mx.com.pendulum.olintareas.dto.tareasV2.Autocomplete;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogActividad;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogCases;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogCuentas;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogEstadosNota;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogPrioridades;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogSubTipos;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogTiposTarea;
import mx.com.pendulum.olintareas.dto.tareasV2.ComandoSubcomandodto;
import mx.com.pendulum.olintareas.dto.tareasV2.Document;
import mx.com.pendulum.olintareas.dto.tareasV2.NotasDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NotasV2Form;
import mx.com.pendulum.olintareas.dto.tareasV2.NoteResponseDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.OptionsQuestionsForm;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.dto.tareasV2.ResponsablesDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.ResponseTask;
import mx.com.pendulum.olintareas.dto.tareasV2.SeguimientoTarea;
import mx.com.pendulum.olintareas.dto.tareasV2.TareasV2Form;
import mx.com.pendulum.olintareas.interfaces.GenericDaoHelper;
import mx.com.pendulum.olintareas.sync.constants.IUrls;
import mx.com.pendulum.olintareas.sync.resources.SyncInResource;
import mx.com.pendulum.olintareas.sync.results.SyncResult;
import mx.com.pendulum.olintareas.ui.activities.EntryPointActivity;
import mx.com.pendulum.utilities.EquipmentProperties;
import mx.com.pendulum.utilities.ErrorReport;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.Util;
import mx.com.pendulum.utilities.http.CnxUtils;
import mx.com.pendulum.utilities.http.HttpConstants;

public class SyncDataInService extends IntentService {
    public static final String SYNC_FINISHED_BROADCAST = "mx.com.pendulum.olintareas.sync.SyncDataInService.SYNC_FINISHED_BROADCAST";
    public static final String SYNC_FAILED_BROADCAST = "mx.com.pendulum.olintareas.sync.SyncDataInService.SYNC_FAILED_BROADCAST";
    public static final String SYNC_PROGRESS_BROADCAST = "mx.com.pendulum.olintareas.sync.SyncDataInService.SYNC_PROGRESS_BROADCAST";
    public static final String SYNC_PROGRESS = "SYNC_PROGRESS";
    public static final String SYNC_DSCRIPTION = "SYNC_DSCRIPTION";
    public static final String SYNC_FINISHED_BROADCAST_FROM = ".sync.SyncDataInService.SYNC_FINISHED_BROADCAST_TASKS";
    public static final String SYNC_FAILED_BROADCAST_FROM = ".sync.SyncDataInService.SYNC_FAILED_BROADCAST_TASKS";
    public static final String SYNC_QUANTITY_BROADCAST_TASKS = ".sync.SyncDataInService.SYNC_QUANTITY_BROADCAST_TASKS";
    public static final String SYNC_ADD_PROGRESS_TASK = ".sync.SyncDataInService.SYNC_ADD_PROGRESS_TASK";
    public static final String SYNC_ADD_PROGRESS_TASK_FINISH = ".sync.SyncDataInService.SYNC_ADD_PROGRESS_TASK_FINISH";

    private String packageWhoCalled = "";


    private static final String TAG = SyncDataInService.class.getSimpleName();
    private static int NOTIFICATION_ID = 23296486;

    private UserDatabaseHelper userDatabaseHelper = null;
    private CatalogDatabaseHelper catalogDatabaseHelper = null;


    private static boolean isFinished = false;

    private NotificationManager notificationManager = null;
    private Notification notification = null;
    private AtomicInteger progress = new AtomicInteger(0);
    private AtomicInteger totalResourceOperations = null;

    private SyncResult downloadResult = null;
    private SyncResult databaseResult = null;


    private String user;

    public SyncDataInService() {
        super(TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (userDatabaseHelper != null) {
            userDatabaseHelper.close();
            userDatabaseHelper = null;
        }
        if (catalogDatabaseHelper != null) {
            catalogDatabaseHelper.close();
            catalogDatabaseHelper = null;
        }
    }

    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
    @Override
    protected void onHandleIntent(Intent intent) {
        setFinished(false);
        catalogDatabaseHelper = CatalogDatabaseHelper.getHelper(this);
        userDatabaseHelper = UserDatabaseHelper.getHelper(this);
        Bundle bun = intent.getExtras();
        boolean isFromAnotherApp = false;
        Intent resultIntent = new Intent();
        ErrorReport error = null;
        UserData user = null;
        UserDataDaoImpl userDataDao = null;
        if (bun != null) {
            isFromAnotherApp = bun.getBoolean(Properties.EXTRA_IS_FROM_ANOTHER_APP);
            if (isFromAnotherApp) {
                boolean needQuantityWS = bun.getBoolean(Properties.EXTRA_IS_QUANITY_NEEDED);
                if (needQuantityWS) {
                    packageWhoCalled = bun.getString(Properties.CALL_FROM_PACKAGE_NAME);
                    String act = packageWhoCalled + SYNC_QUANTITY_BROADCAST_TASKS;
                    resultIntent.setAction(act);
                    resultIntent.putExtra(Properties.QUANITY_TASKS_WS, loadResourceList("").size());
                    sendBroadcast(resultIntent);
                    setFinished(true);
                    stopSelf();
                    return;
                }
                userDatabaseHelper.clearTables();
                catalogDatabaseHelper.clearTables();
                String sUser = bun.getString(Properties.EXTRA_USER_LOGIN);
                String pass = bun.getString(Properties.EXTRA_PASS_LOGIN);
                packageWhoCalled = bun.getString(Properties.CALL_FROM_PACKAGE_NAME);
                String msgErrorLogin = loginValidation(sUser, pass, packageWhoCalled);
                if (!msgErrorLogin.isEmpty()) {
                    String act = packageWhoCalled + SYNC_FAILED_BROADCAST_FROM;
                    resultIntent.setAction(act);
                    resultIntent.putExtra("Error", msgErrorLogin);
                    error = new ErrorReport(new Exception());
                    error.setType(msgErrorLogin);
                    resultIntent.putExtras(error.toBundle());
                    sendBroadcast(resultIntent);
                    //buildFinishedNotification(error);
                    setFinished(true);
                    stopSelf();
                    return;
                }
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_name), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Properties.EXTRA_IS_FROM_ANOTHER_APP, false);
                editor.apply();
            }
        }
        try {
            userDataDao = userDatabaseHelper.getUserDataDao();
            user = userDataDao.getCurrentUser();
            if (user != null) {
                this.user = user.getUsername();
                //ArrayList<SyncInResource> resourceList = loadResourceList(user.getUsername());
                ArrayList<SyncInResource> resourceList = loadResourceList(this.user);
                SyncResult syncResult = processResourceList(resourceList, isFromAnotherApp, packageWhoCalled);
                if (syncResult.isSuccessful() && error == null) {
                    user.getSession().setLogged_in(true);
                    userDataDao.update(user);
                } else
                    error = syncResult.getError();
            } else { // UserData Error
                error = new ErrorReport(new Exception());
                error.setType(getString(R.string.error_user_data));
            }
        } catch (SQLException e) { // SQL Error
            error = new ErrorReport(e);
            error.setType(getString(R.string.error_user_data));
        }
        if (error == null) {
            if (isFromAnotherApp) {
                String act = packageWhoCalled + SYNC_FINISHED_BROADCAST_FROM;
                resultIntent.setAction(act);
            } else
                resultIntent.setAction(SYNC_FINISHED_BROADCAST);
        } else {
            if (isFromAnotherApp) {
                String act = packageWhoCalled + SYNC_FAILED_BROADCAST_FROM;
                resultIntent.setAction(act);
            } else
                resultIntent.setAction(SYNC_FAILED_BROADCAST);
            resultIntent.putExtras(error.toBundle());
            SyncUtilities.logout(this, userDatabaseHelper, user, packageWhoCalled);
            // If error erase DB's.
            userDatabaseHelper.clearTables();
            catalogDatabaseHelper.clearTables();
        }
        sendBroadcast(resultIntent);
        buildFinishedNotification(error, isFromAnotherApp);
        setFinished(true);
        stopSelf();
    }

    public ArrayList<SyncInResource> loadResourceList(String user) {
        ArrayList<SyncInResource> resourceList = new ArrayList<>();
        //TAREAS V2
        resourceList.add(new SyncInResource(IUrls.CATALOG_ACTIVIDAD, CatalogActividad.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_GET_TIPOS_TAREA, CatalogTiposTarea.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_GET_PRIORIDADES, CatalogPrioridades.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_GET_SUBTIPOS, CatalogSubTipos.class, catalogDatabaseHelper));

        resourceList.add(new SyncInResource(IUrls.CATALOG_GET_ESTADO_NOTA, CatalogEstadosNota.class, catalogDatabaseHelper));
        //obtiene las tareas
        resourceList.add(new SyncInResource(IUrls.SEGUIMIENTO_TAREA.replace(IUrls.CONTEXT_USER, user), SeguimientoTarea.class, userDatabaseHelper));
        //obtiene preguntas contestadas de una tarea
        resourceList.add(new SyncInResource(IUrls.SEGUIMIENTO_TAREA_RESPONSE.replace(IUrls.CONTEXT_USER, user), ResponseTask.class, userDatabaseHelper));
        //obtiene preguntas contestadas de una nota
        resourceList.add(new SyncInResource(IUrls.NOTE_RESPONSE.replace(IUrls.CONTEXT_USER, user), NoteResponseDTO.class, userDatabaseHelper));
        //obtiene las notas de seguimientocatalogForm
        resourceList.add(new SyncInResource(IUrls.SEGUIMIENTO_NOTA_RESPONSE.replace(IUrls.CONTEXT_USER, user), NotasDTO.class, userDatabaseHelper));
        //Catalogo user quasar
        resourceList.add(new SyncInResource(IUrls.RESPONSABLES_DTO, ResponsablesDTO.class, catalogDatabaseHelper));
        // Sub Categotias
        resourceList.add(new SyncInResource(IUrls.CATALOG_GET_SUB_CATEGORIES.replace(IUrls.CONTEXT_USER, user), CatTskSubclasificaTareas.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_GET_SUB_REL_CAT_QNS.replace(IUrls.CONTEXT_USER, user), RelSubClasificaQuestions.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_GET_SUB_QUESTIONS.replace(IUrls.CONTEXT_USER, user), Questions.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_GET_SUB_QUES_DOCUMENTS.replace(IUrls.CONTEXT_USER, user), Document.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_GET_SUB_QUES_OPTIONS.replace(IUrls.CONTEXT_USER, user), Options.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_GET_SUB_REL_OPTIONS.replace(IUrls.CONTEXT_USER, user), RelOptionQuery.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_GET_SUB_OPT_AUTOCOMPLETE.replace(IUrls.CONTEXT_USER, user), Autocomplete.class, catalogDatabaseHelper));
        // Options Questions
        resourceList.add(new SyncInResource(IUrls.CATALOG_RELATION_OPT_QRY_FO.replace(IUrls.CONTEXT_USER, user), RelOptionQueryFO.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_RELATION_OPT_QNS_FO.replace(IUrls.CONTEXT_USER, user), RelOptionQuestionsFO.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_QUESTIONS_FO.replace(IUrls.CONTEXT_USER, user), QuestionsFO.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_OPTIONS_FO.replace(IUrls.CONTEXT_USER, user), OptionsFO.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_DOCUMENTS_FO.replace(IUrls.CONTEXT_USER, user), DocumentsFO.class, catalogDatabaseHelper));
        resourceList.add(new SyncInResource(IUrls.CATALOG_AUTOCOMPLETE_FO.replace(IUrls.CONTEXT_USER, user), AutocompleteFO.class, catalogDatabaseHelper));
        if(!Properties.isTempWSRelease) {
            resourceList.add(new SyncInResource(IUrls.CATALOG_FORM, TareasV2Form.class, catalogDatabaseHelper));
            resourceList.add(new SyncInResource(IUrls.CATALOG_NOTE_FORM.replace(IUrls.CONTEXT_USER, user), NotasV2Form.class, catalogDatabaseHelper));
            resourceList.add(new SyncInResource(IUrls.OPTION_QUESTION_FORM, OptionsQuestionsForm.class, catalogDatabaseHelper));
            resourceList.add(new SyncInResource(IUrls.CATALOG_COMANDOS_ALL.replace(IUrls.CONTEXT_USER, user), ComandoSubcomandodto.class, catalogDatabaseHelper));
            //resourceList.add(new SyncInResource(IUrls.CATALOG_CUENTAS, CatalogCuentas.class, catalogDatabaseHelper));
            resourceList.add(new SyncInResource(IUrls.CATALOG_CREDITS_USER.replace(IUrls.CONTEXT_USER, user), CatalogCuentas.class, catalogDatabaseHelper));
            resourceList.add(new SyncInResource(IUrls.CATALOG_CASES_USER.replace(IUrls.CONTEXT_USER, user), CatalogCases.class, catalogDatabaseHelper));
        } else {
            resourceList.add(new SyncInResource(IUrls.CATALOG_FORM_TEMP, TareasV2Form.class, catalogDatabaseHelper));
            resourceList.add(new SyncInResource(IUrls.CATALOG_NOTE_FORM_TEMP.replace(IUrls.CONTEXT_USER, user), NotasV2Form.class, catalogDatabaseHelper));
            resourceList.add(new SyncInResource(IUrls.OPTION_QUESTION_FORMT_TEMP, OptionsQuestionsForm.class, catalogDatabaseHelper));
            resourceList.add(new SyncInResource(IUrls.CATALOG_COMANDOS_ALL_TEMP.replace(IUrls.CONTEXT_USER, user), ComandoSubcomandodto.class, catalogDatabaseHelper));
            //resourceList.add(new SyncInResource(IUrls.CATALOG_CUENTAS_TEMP, CatalogCuentas.class, catalogDatabaseHelper));
            resourceList.add(new SyncInResource(IUrls.CATALOG_CREDITS_USER_TEMP.replace(IUrls.CONTEXT_USER, user), CatalogCuentas.class, catalogDatabaseHelper));
            resourceList.add(new SyncInResource(IUrls.CATALOG_CASES_USER_TEMP.replace(IUrls.CONTEXT_USER, user), CatalogCases.class, catalogDatabaseHelper));
        }
        return resourceList;
    }

    /*private ArrayList<SyncInResource> loadResourceListSubQuestions(String idForm) {
        ArrayList<SyncInResource> resourceList = new ArrayList<>();
        resourceList.add(new SyncInResource(IUrls.SUB_CATALOG_FORM.replace(IUrls.CONTEXT_ID_FORM, idForm), TareasV2Form.class, catalogDatabaseHelper));
        return resourceList;
    }*/

    private SyncResult processResourceList(ArrayList<SyncInResource> resourceList, boolean isFromAnotherApp, String packageWhoCalled) {
        SyncResult syncResult = new SyncResult();
        //long start = System.currentTimeMillis();
        totalResourceOperations = new AtomicInteger(2 * resourceList.size());
        if (!isFromAnotherApp) {
            buildProgressNotification(totalResourceOperations.get());
        }
        LinkedBlockingQueue<SyncInResource> readyToParseQueue = new LinkedBlockingQueue<>(resourceList.size());
        ResourceDownloader resourceDownloader = new ResourceDownloader(this, resourceList, readyToParseQueue, isFromAnotherApp);
        new Thread(resourceDownloader).start();
        ResourceParser resourceParser = new ResourceParser(this, resourceDownloader, readyToParseQueue, isFromAnotherApp, false, packageWhoCalled);
        new Thread(resourceParser).start();
        try {
            while (!resourceDownloader.isFinished() || !resourceParser.isFinished()) {
                if (hasError())
                    break;
                else
                    Thread.sleep(500);
            }
        } catch (InterruptedException ie) {
            Log.e(TAG, ie.getMessage(), ie);
        }
        if ((downloadResult == null || (downloadResult.isSuccessful() && downloadResult.getError() == null))
                && (databaseResult == null || (databaseResult.isSuccessful() && databaseResult.getError() == null))) {
            syncResult.setSuccessful(true);
        } else if (downloadResult != null && !downloadResult.isSuccessful() && downloadResult.getError() != null)
            syncResult.setError(downloadResult.getError());
        else if (databaseResult != null && !databaseResult.isSuccessful() && databaseResult.getError() != null)
            syncResult.setError(databaseResult.getError());
        else {
            syncResult.setSuccessful(false);
            syncResult.setError(new ErrorReport(new Exception()));
        }
        setFinished(true);
        //Log.d(TAG, "Total Time:" + (System.currentTimeMillis() - start) / 1000 + "s.");
        return syncResult;
    }

    private SyncResult processResourceListSubQuestions(ArrayList<SyncInResource> resourceList, boolean isFromAnotherApp, String packageWhoCalled) {
        SyncResult syncResult = new SyncResult();
        totalResourceOperations = new AtomicInteger(2 * resourceList.size());
        if (!isFromAnotherApp) {
            buildProgressNotification(totalResourceOperations.get());
        } else {
            Intent resultIntent = new Intent();
            String act = packageWhoCalled + SYNC_QUANTITY_BROADCAST_TASKS;
            resultIntent.setAction(act);
            resultIntent.putExtra(Properties.QUANITY_TASKS_WS, totalResourceOperations.get());
            sendBroadcast(resultIntent);
        }
        LinkedBlockingQueue<SyncInResource> readyToParseQueue = new LinkedBlockingQueue<>(resourceList.size());
        ResourceDownloader resourceDownloader = new ResourceDownloader(this, resourceList, readyToParseQueue, isFromAnotherApp);
        new Thread(resourceDownloader).start();
        ResourceParser resourceParser = new ResourceParser(this, resourceDownloader, readyToParseQueue, isFromAnotherApp, true, packageWhoCalled);
        new Thread(resourceParser).start();
        try {
            while (!resourceDownloader.isFinished() || !resourceParser.isFinished()) {
                if (hasError())
                    break;
                else
                    Thread.sleep(500);
            }
        } catch (InterruptedException ie) {
            Log.e(TAG, ie.getMessage(), ie);
        }

        if ((downloadResult == null || (downloadResult.isSuccessful() && downloadResult.getError() == null))
                && (databaseResult == null || (databaseResult.isSuccessful() && databaseResult.getError() == null))) {
            if (databaseResult != null && databaseResult.getSubForms() != null)
                syncResult.setSubForms(databaseResult.getSubForms());
            syncResult.setSuccessful(true);
        } else if (downloadResult != null && !downloadResult.isSuccessful() && downloadResult.getError() != null)
            syncResult.setError(downloadResult.getError());
        else if (databaseResult != null && !databaseResult.isSuccessful() && databaseResult.getError() != null)
            syncResult.setError(databaseResult.getError());
        else {
            syncResult.setSuccessful(false);
            syncResult.setError(new ErrorReport(new Exception()));
        }
        setFinished(true);
        //Log.d(TAG, "Total Time:" + (System.currentTimeMillis() - start) / 1000 + "s.");
        return syncResult;
    }

    public boolean hasError() {
        return (downloadResult != null && !downloadResult.isSuccessful()) || (databaseResult != null && !databaseResult.isSuccessful());
    }

    @SuppressWarnings("deprecation")
    public void incrementAndUpdateProgressNotification(SyncInResource resource) {
        float progressInt = (float) progress.addAndGet(1);
        float totalInt = (float) totalResourceOperations.get();
        int progress = (int) (progressInt / totalInt * 100);
        if (notification != null && notification.contentView != null) {
            notification.contentView.setProgressBar(R.id.progressBar, (int) totalInt, (int) progressInt, false);
            notification.contentView.setTextViewText(R.id.resourceTextView, progress + "%");
            notificationManager.notify(NOTIFICATION_ID, notification);
            Intent resultIntent = new Intent();
            resultIntent.setAction(SYNC_PROGRESS_BROADCAST);
            resultIntent.putExtra(SYNC_PROGRESS, progress);
            if (resource == null) {
                resultIntent.putExtra(SYNC_DSCRIPTION, "");
            } else {
                resultIntent.putExtra(SYNC_DSCRIPTION, resource.getNotifName().replace(this.user, ""));
            }
            sendBroadcast(resultIntent);
        }
    }

    @SuppressWarnings("deprecation")
    private void buildProgressNotification(int totalResourcesPending) {// TODO Pruebas notificacion
        Resources appResources = getResources();
        Intent notificationIntent = new Intent(this, EntryPointActivity.class);
        notificationIntent.setAction("android.intent.action.MAIN");
        notificationIntent.addCategory("android.intent.category.LAUNCHER");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.notification_sync_data);
        remoteView.setProgressBar(R.id.progressBar, totalResourcesPending, 0, false);
        remoteView.setTextViewText(R.id.syncTextView, getString(R.string.synchronizing_data));
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "Channel name",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(appResources.getString(R.string.synchronizing_data));
            channel.setVibrationPattern(new long[]{0});
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(this, "default");
        } else {
            builder = new NotificationCompat.Builder(
                    this);
        }
        builder// .setContent(remoteView)
                .setContentIntent(contentIntent)
                .setProgress(totalResourcesPending, 0, true)
                .setAutoCancel(false)
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(appResources, R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_refresh)
                .setTicker(appResources.getString(R.string.synchronizing_data))
                .setWhen(System.currentTimeMillis())
                .setVibrate(new long[]{0L});
//				.setContentTitle(appResources.getString(R.string.app_name))
//				.setContentText(appResources.getString(R.string.synchronizing_data));
        notification = builder.build();
        notification.contentView = remoteView;
        notificationManager.cancelAll();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @SuppressWarnings("ConstantConditions")
    private void buildFinishedNotification(ErrorReport error, boolean isFromAnotherApp) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        if (!isFromAnotherApp) {
            boolean result = error == null;
            Resources appResources = getResources();
            Intent notificationIntent = new Intent(this, EntryPointActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            String finishedResultText = result ? appResources
                    .getString(R.string.download_complete) : error.getType();
            String finishedTickerText = result ? appResources
                    .getString(R.string.download_complete) : appResources
                    .getString(R.string.error_download_incomplete);
            int iconResult = result ? R.drawable.ic_check : R.drawable.ic_ex;
            NotificationCompat.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new NotificationCompat.Builder(
                        this, "default");
            } else {
                builder = new NotificationCompat.Builder(
                        this);
            }
            builder.setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(appResources, R.mipmap.ic_launcher))
                    .setSmallIcon(iconResult).setTicker(finishedTickerText)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(appResources.getString(R.string.app_name))
                    .setContentText(finishedResultText)
                    .setVibrate(new long[]{1500, 250, 1000, 200, 500});
            notification = builder.build();
            notificationManager.cancelAll();
            notificationManager.notify(NOTIFICATION_ID, notification);
            if (!result && error != null) {
//            Tools.showSnack(this, error.getType());
                Toast.makeText(this, error.getType(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setDownloadError(SyncResult downloadResult) {
        this.downloadResult = downloadResult;
    }

    public void setDownloadResult(SyncResult downloadResult) {
        this.downloadResult = downloadResult;
    }

    public void setDatabaseError(SyncResult databaseResult) {
        this.databaseResult = databaseResult;
    }

    public static boolean isFinished() {
        return isFinished;
    }

    private static void setFinished(boolean isFinished) {
        SyncDataInService.isFinished = isFinished;
    }

    private class ResourceDownloader implements Runnable {
        private static final int TIMEOUT = 5 * 60 * 1000; // 5 minutes

        private SyncDataInService service;
        private ArrayList<SyncInResource> resourceList;
        private LinkedBlockingQueue<SyncInResource> readyToParseQueue;
        private boolean finished = false;
        private boolean isFromAnotherApp = false;

        ResourceDownloader(SyncDataInService service, ArrayList<SyncInResource> resourceList,
                           LinkedBlockingQueue<SyncInResource> readyToParseQueue, boolean isFromAnotherApp) {
            this.service = service;
            this.resourceList = resourceList;
            this.readyToParseQueue = readyToParseQueue;
            this.isFromAnotherApp = isFromAnotherApp;
        }

        @SuppressWarnings("UnusedAssignment")
        @Override
        public void run() {
            SyncResult downloadResult = null;
            for (SyncInResource resource : resourceList) {
                if (resource.getUrl().contains("telefonos")) {
                    Log.i("point", "");
                }
                if (service.hasError())
                    break;
                long startTime = System.currentTimeMillis();
                try {
                    downloadResult = downloadResource(resource);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
                long endTime = System.currentTimeMillis();
                Log.e(TAG, "Tiempo descarga " + (endTime - startTime) + "\n" + resource.getUrl());
                if (downloadResult == null || !downloadResult.isSuccessful() || downloadResult.getError() != null || resource.getFile() == null) {
                    service.setDownloadError(downloadResult);
                    break;
                } else {
                    if (!isFromAnotherApp)
                        service.incrementAndUpdateProgressNotification(resource);
                    else {
                        Intent resultIntent = new Intent();
                        String act = packageWhoCalled + SYNC_ADD_PROGRESS_TASK;
                        resultIntent.setAction(act);
                        sendBroadcast(resultIntent);
                        //TODO SERVICIO
                    }
                    try {
                        readyToParseQueue.put(resource);
                    } catch (InterruptedException ie) {
                        String userFriendlyMessage = (service
                                .getString(R.string.error_resource) + " / ResourceDownloader / ") +
                                resource;
                        Log.e(TAG, ie.getMessage(), ie);
                        ErrorReport error = new ErrorReport(ie);
                        error.setType(userFriendlyMessage);
                        downloadResult.setError(error);
                    }
                }
            }
            setFinished(true);
        }

        @SuppressWarnings({"UnusedAssignment", "ConstantConditions"})
        private SyncResult downloadResource(SyncInResource resource) {
            SyncResult result = new SyncResult();
            HttpURLConnection conn = null;
            int rcode;
            for (int tries = 0; tries < 3; tries++) {
                if (tries > 1)
                    SystemClock.sleep(5000);
                String url = resource.getUrl();
                if (url.contains("telefonos")) {
                    Log.e("point", "");
                }
                rcode = 0;
                conn = CnxUtils.makeHttpConnection(resource.getUrl(), null,
                        null, HttpConstants.GET);
                if (conn != null) {
                    Log.d(TAG, resource + " Requesting data.");
                    conn.setConnectTimeout(TIMEOUT);
                    rcode = CnxUtils.extractResponseCode(conn);
                    if (rcode == HttpURLConnection.HTTP_OK) {
                        try {
                            long startTime = System.currentTimeMillis();
                            File tempFile = File.createTempFile(".Olin", ".dat", new File(Properties.PHONE_FILES_DIR));
                            String json = null;
                            CnxUtils.downloadResponseDataToFile(conn, tempFile, json);
                            resource.setFile(tempFile);
                            resource.setJson(json);
                            result.setSuccessful(true);
//                            Log.d(TAG, "json-" + json);
                            Log.d(TAG, resource + " " + tempFile.length() / 1000.0 + " KB - Download Time: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s.");
                            break;
                        } catch (IOException ioe) {
                            String userFriendlyMessage = ioe.getMessage() + resource;
//                            Log.e(TAG, ioe.getMessage(), ioe);
                            ErrorReport error = new ErrorReport(ioe);
                            error.setType(userFriendlyMessage);
                            result.setError(error);
                        }
                    } else {
                        String userFriendlyMessage = (service.getString(R.string.error_http) + " / downloadResource / ") + // Este controla cuando se salio la RED WIFI HFR
                                rcode + resource;
                        Log.e(TAG, userFriendlyMessage);
                        ErrorReport error = new ErrorReport(new Exception());
                        error.setType(userFriendlyMessage);
                        result.setError(error);
                        return result;
                    }
                    CnxUtils.closeConnection(conn);
                    conn = null;
                } else {
                    String userFriendlyMessage = (service.getString(R.string.error_http)
                            + " / downloadResource / ") +
                            resource;
//                    Log.e(TAG, userFriendlyMessage);
                    ErrorReport error = new ErrorReport(new Exception());
                    error.setType(userFriendlyMessage);
                    result.setError(error);
                    return result;
                }
            }
            return result;
        }

        private boolean isFinished() {
            return finished;
        }

        private void setFinished(boolean finished) {
            this.finished = finished;
        }

    }

    private class ResourceParser implements Runnable {
        private SyncDataInService service;
        private ResourceDownloader resourceDownloader;
        private LinkedBlockingQueue<SyncInResource> readyToParseQueue;
        private boolean finished = false;
        private boolean isFromAnotherApp;
        private boolean isSubForms;
        private String packageWhoCalled;

        ResourceParser(SyncDataInService service, ResourceDownloader resourceDownloader,
                       LinkedBlockingQueue<SyncInResource> readyToParseQueue,
                       boolean isFromAnotherApp, boolean isSubForms, String packageWhoCalled) {
            this.service = service;
            this.resourceDownloader = resourceDownloader;
            this.readyToParseQueue = readyToParseQueue;
            this.isFromAnotherApp = isFromAnotherApp;
            this.isSubForms = isSubForms;
            this.packageWhoCalled = packageWhoCalled;
        }

        @Override
        public void run() {
            SyncResult databaseResult;
            int i = 0;
            try {
                while (!resourceDownloader.isFinished() || !readyToParseQueue.isEmpty()) {
                    i++;
                    if (service.hasError())
                        break;
                    if (readyToParseQueue.isEmpty())
                        Thread.sleep(250);
                    if (isSubForms) {
                        databaseResult = parseResourceSubForm(readyToParseQueue.take());
                        if (databaseResult == null || !databaseResult.isSuccessful()) {
                            service.setDatabaseError(databaseResult);
                            break;
                        } else {
                            service.setDownloadResult(databaseResult);
                        }
                    } else
                        databaseResult = parseResource(readyToParseQueue.take());
                    if (databaseResult == null || !databaseResult.isSuccessful()) {
                        service.setDatabaseError(databaseResult);
                        break;
                    }
                    if (!isFromAnotherApp)
                        service.incrementAndUpdateProgressNotification(null);
                    else {
                        Intent resultIntent = new Intent();
                        String act = packageWhoCalled + SYNC_ADD_PROGRESS_TASK;
                        resultIntent.setAction(act);
                        sendBroadcast(resultIntent);
                    }
                }
                setFinished(true);
                Intent resultIntent = new Intent();
                String act = packageWhoCalled + SYNC_ADD_PROGRESS_TASK_FINISH;
                resultIntent.setAction(act);
                sendBroadcast(resultIntent);
            } catch (InterruptedException ie) {
                Log.e(TAG, ie.getMessage(), ie);
            }
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored", "UnusedAssignment", "deprecation"})
        private SyncResult parseResource(SyncInResource resource) {
            long startTime = System.currentTimeMillis();
            SyncResult result = new SyncResult();
            File file = resource.getFile();
            Class<?> clz = resource.getClz();
            GenericDaoHelper helper = resource.getHelper();
            int numberOfEntitiesReceived = 0;
            Dao<?, Long> dao = null;
            JsonFactory factory = null;
            JsonParser jsonParser = null;
            if (file.length() > 0) {
                try {
                    dao = helper.getOlinDao(clz);
                    Log.d(TAG, resource + "-- " + clz.getSimpleName());
                    factory = new MappingJsonFactory().configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
                    jsonParser = factory.createJsonParser(file);
                    jsonParser.nextToken(); // Skip [ token
                    jsonParser.nextToken(); // Skip { token
                    ArrayList<Object> entities = new ArrayList<>();
                    Object entity = null;
                    boolean bufferLoad = file.length() > 1000000;
                    do {
                        /*if (entities.size() == 31)
                            Log.i("", "");
                        if (Tools.isDebug()) {
                            Log.e(TAG, "parsing " + resource.getUrl() + "  -  " + numberOfEntitiesReceived);
                        }*/
                        entity = jsonParser.readValueAs(clz);
                        if (entity == null) {
                            if (!entities.isEmpty())
                                SyncUtilities.writeToDatabase(entities, dao);
                            result.setSuccessful(true);
                            break;
                        } else {
                            entities.add(entity);
                            numberOfEntitiesReceived++;
                            if (bufferLoad && entities.size() > 500) {
                                SyncUtilities.writeToDatabase(entities, dao);
                                entities.clear();
                            }
                        }
                    } while (true);
                    Log.i(TAG,
                            resource + " " + numberOfEntitiesReceived
                                    + " Objects Parsed - Insert Time: "
                                    + (System.currentTimeMillis() - startTime)
                                    / 1000.0 + "s.");
                } catch (Exception jme) {
                    String userFriendlyMessage = service.getString(R.string.error_resource) + " / parser / " + resource;
                    Log.e(TAG, jme.getMessage(), jme);
                    ErrorReport error = new ErrorReport(jme);
                    error.setType(userFriendlyMessage);
                    result.setError(error);

                    return result;
                } finally {
                    if (jsonParser != null)
                        try {
                            jsonParser.close();
                        } catch (IOException ignored) {
                        }
                    if (file != null)
                        file.delete();
                    jsonParser = null;
                    file = null;
                    factory = null;
                    dao = null;
                }
            } else {// No data to parse.
                result.setSuccessful(true);
                Log.w(TAG, resource + " No data received.");
            }
            return result;
        }

        private SyncResult parseResourceSubForm(SyncInResource resource) {
            long startTime = System.currentTimeMillis();
            SyncResult result = new SyncResult();
            File file = resource.getFile();
            Class<?> clz = resource.getClz();
            GenericDaoHelper helper = resource.getHelper();
            int numberOfEntitiesReceived = 0;
            Dao<?, Long> dao = null;
            JsonFactory factory = null;
            JsonParser jsonParser = null;
            if (file.length() > 0) {
                try {
                    dao = helper.getOlinDao(clz);
                    Log.d(TAG, resource + "-- " + clz.getSimpleName());
                    factory = new MappingJsonFactory().configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
                    jsonParser = factory.createJsonParser(file);
                    jsonParser.nextToken(); // Skip [ token
                    jsonParser.nextToken(); // Skip { token
                    ArrayList<Object> entities = new ArrayList<>();
                    Object entity;
                    //boolean bufferLoad = file.length() > 1000000;
                    do {
                        entity = jsonParser.readValueAs(clz);
                        if (entity == null) {
                            if (!entities.isEmpty())
                                result.setSubForms(entities);
                            //SyncUtilities.writeToDatabase(entities, dao);
                            if (entities.size() > 1) {
                                Log.d("", "");
                            }
                            result.setSuccessful(true);
                            break;
                        } else {
                            entities.add(entity);
                            numberOfEntitiesReceived++;
                            // TODO BUFFER DE MEMORIA ?????
                            /*if (bufferLoad && entities.size() > 500) {
                                result.setSubForms(entities);
                                SyncUtilities.writeToDatabase(entities, dao);
                                entities.clear();
                            }*/
                        }
                    } while (true);
                    Log.i(TAG,
                            resource + " " + numberOfEntitiesReceived
                                    + " Objects Parsed - Insert Time: "
                                    + (System.currentTimeMillis() - startTime)
                                    / 1000.0 + "s.");
                } catch (Exception jme) {
                    String userFriendlyMessage = service.getString(R.string.error_resource) + " / parser / " + resource;
                    Log.e(TAG, jme.getMessage(), jme);
                    ErrorReport error = new ErrorReport(jme);
                    error.setType(userFriendlyMessage);
                    result.setError(error);
                    return result;
                } finally {
                    if (jsonParser != null)
                        try {
                            jsonParser.close();
                        } catch (IOException ignored) {
                        }
                    if (file != null)
                        file.delete();
                    jsonParser = null;
                    file = null;
                    factory = null;
                    dao = null;
                }
            } else {// No data to parse.
                result.setSuccessful(true);
                Log.w(TAG, resource + " No data received.");
            }
            return result;
        }

        boolean isFinished() {
            return finished;
        }

        private void setFinished(boolean finished) {
            this.finished = finished;
        }
    }

    String loginValidation(String username, String password, String packageWhoCalled) {
        String errorMsg = "";
        int rcode;
        String response;
        HttpURLConnection conn = null;
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("news");
            String token;
            String identifier = EquipmentProperties.getIdentifier(getApplicationContext());
            do {
                token = FirebaseInstanceId.getInstance().getToken();
                if (token != null)
                    Log.e("Tareas TOKEN", " <-- TOKEN -> " + token);
            } while (token == null);
            if (!Properties.isReleaseApp && (token == null || token.isEmpty())) {
                token = "tyipokjgkuygf";
            }
            if (!Properties.isReleaseApp && (identifier == null || identifier.isEmpty())) {
                identifier = "tyipokjgkuygf";
            }
            String appName = Properties.APP_NAME;
            switch (packageWhoCalled) {
                case Properties.pakage_olin:
                    appName = Properties.APP_NAME_OLIN;
                    break;
                case Properties.pakage_legal:
                    appName = Properties.APP_NAME_LEGAL;
                    break;
                case Properties.pakage_tareas:
                    appName = Properties.APP_NAME;
                    break;
                case Properties.pakage_ubicuo:
                    appName = Properties.APP_NAME_UBICUO;
                    break;
            }
            String versionApp = Util.getCurrentVerApks(getApplicationContext(), packageWhoCalled);
            String versionCode = String.valueOf(Util.getCurrentVerCodeApks(
                    getApplicationContext(), packageWhoCalled));
            StringBuilder url = new StringBuilder();
            url.append(IUrls.USER_LOGINLDAP)
                    .append('?')
                    .append("username=")
                    .append(URLEncoder.encode(username, "ISO-8859-1"))
                    .append("&password=")
                    .append(URLEncoder.encode(password, "ISO-8859-1"))
                    .append("&device_id=")
                    .append(URLEncoder.encode(identifier, "ISO-8859-1"))
                    .append("&reg_id=")
                    .append(URLEncoder.encode(token, "ISO-8859-1"))
                    .append("&appname=")
                    .append(URLEncoder.encode(appName, "ISO-8859-1"))
                    .append("&versionapp=")
                    .append(URLEncoder.encode(versionApp, "ISO-8859-1"))
                    .append("&versioncode=")
                    .append(URLEncoder.encode(versionCode, "ISO-8859-1"))
            ;
            Log.d("url", url.toString());
            conn = CnxUtils.makeHttpConnection(url.toString(), null, null, HttpConstants.GET);
            if (conn != null) {
                rcode = CnxUtils.extractResponseCode(conn);
                if (rcode == HttpURLConnection.HTTP_OK) {
                    response = new String(CnxUtils.extractResponseData(conn));
                    if (response.length() > 0) {
                        UserData user = new ObjectMapper().readValue(response, UserData.class);
                        if (user != null) {
                            String md5Password = Util.md5(password).toUpperCase();
                            user.setPassword(md5Password);
                            user.setUserLock(username);
                            user.getSession().setDevice_id(EquipmentProperties.getIdentifier(getBaseContext()));
                            user.getSession().setFecha_login(new Date());
                            if (user.getEstatus_contrasenia() != 3) {
                                if (user.getPermisos().getTipo_usuario_desc().equals(Properties.PERMISSION_INTERNAL)) {
                                    SharedPreferences sh = ContextApplication.getAppContext().getSharedPreferences("USER_AUTENTICHATION", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sh.edit();
                                    editor.putString("_USUARIO", username);
                                    editor.putString("_NOMBRE", user.getNombre());
                                    editor.putString("_CORREO", user.getCorreo());
                                    editor.apply();
                                    UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper.getHelper(getApplicationContext());
                                    userDatabaseHelper.getUserDataDao().create(user);
                                    userDatabaseHelper.close();
                                } else {
                                    CatalogDatabaseHelper catalogDatabaseHelper = CatalogDatabaseHelper.getHelper(getApplicationContext());
                                    catalogDatabaseHelper.clearTables();
                                    catalogDatabaseHelper.close();
                                    UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper.getHelper(getApplicationContext());
                                    userDatabaseHelper.getUserDataDao();
                                    userDatabaseHelper.clearTables();
                                    userDatabaseHelper.getUserDataDao().create(user);
                                    userDatabaseHelper.close();
                                }
                            } else
                                getString(R.string.error_user_blocked);
                        } else
                            getString(R.string.error_user_blocked);
                    } else
                        errorMsg = getString(R.string.error_auth_credentials);
                } else
                    errorMsg = getString(R.string.error_auth_credentials);
            } else
                errorMsg = CnxUtils.getLastError();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            errorMsg = "Error al procesar la solicitud";
        } finally {
            if (conn != null) {
                CnxUtils.closeConnection(conn);
            }
        }
        return errorMsg;
    }
}