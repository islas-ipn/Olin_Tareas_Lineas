package mx.com.pendulum.olintareas.sync;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.config.util.ContextApplication;
import mx.com.pendulum.olintareas.db.CatalogDatabaseHelper;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.HeaderApp;
import mx.com.pendulum.olintareas.dto.Lw;
import mx.com.pendulum.olintareas.dto.SyncDataOutObject;
import mx.com.pendulum.olintareas.dto.TransactionResult;
import mx.com.pendulum.olintareas.dto.UserData;
import mx.com.pendulum.olintareas.dto.UserSession;
import mx.com.pendulum.olintareas.dto.tareasV2.NotaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.SeguimientoTarea;
import mx.com.pendulum.olintareas.dto.tareasV2.TareaDTO;
import mx.com.pendulum.olintareas.sync.constants.IResources;
import mx.com.pendulum.olintareas.sync.constants.IUrls;
import mx.com.pendulum.olintareas.sync.resources.SyncOutResource;
import mx.com.pendulum.olintareas.sync.results.SyncResult;
import mx.com.pendulum.olintareas.ui.activities.EntryPointActivity;
import mx.com.pendulum.utilities.ErrorReport;
import mx.com.pendulum.utilities.JsonUtils;
import mx.com.pendulum.utilities.Tools;

import static mx.com.pendulum.olintareas.sync.constants.IUrls.CONTEXT_SEPERATOR;

@SuppressWarnings({"rawtypes", "unchecked", "UnusedParameters", "deprecation", "UnusedAssignment", "unused", "ResultOfMethodCallIgnored", "StringBufferReplaceableByString", "ConstantConditions"})
public class SyncDataOutService extends IntentService {
    private static final String TAG = SyncDataOutService.class.getSimpleName();
    private static int NOTIFICATION_ID = 23296487;
    private UserDatabaseHelper userDatabaseHelper = null;
    private CatalogDatabaseHelper catalogDatabaseHelper = null;
    public static final String SYNC_FINISHED_BROADCAST = "mx.com.pendulum.olintareas.sync.SyncDataOutService.SYNC_FINISHED_BROADCAST";
    public static final String SYNC_FAILED_BROADCAST = "mx.com.pendulum.olintareas.sync.SyncDataOutService.SYNC_FAILED_BROADCAST";
    public static final String SYNC_PROGRESS_BROADCAST = "mx.com.pendulum.olintareas.sync.SyncDataOutService.SYNC_PROGRESS_BROADCAST";
    public static final String SYNC_PROGRESS = "SYNC_PROGRESS";
    public static final String SYNC_DSCRIPTION = "SYNC_DSCRIPTION";
    public static final String SYNC_FINISHED_BROADCAST_FROM = ".sync.SyncDataOutService.SYNC_FINISHED_BROADCAST_TASKS";
    public static final String SYNC_FAILED_BROADCAST_FROM = ".sync.SyncDataOutService.SYNC_FAILED_BROADCAST_TASKS";
    public static final String SYNC_PROGRESS_TASK_ERROR_FROM = ".sync.SyncDataOutService.SYNC_PROGRESS_TASK_ERROR";
    public static final String SYNC_ADD_PROGRESS_TASK = ".sync.SyncDataOutService.SYNC_ADD_PROGRESS_TASK";
    public static final String SYNC_QUANTITY_BROADCAST_TASKS = ".sync.SyncDataOutService.SYNC_QUANTITY_BROADCAST_TASKS";
    private static boolean isFinished = false;
    private NotificationManager notificationManager = null;
    private Notification notification = null;
    private String user;
    String packageWhoCalled = "";

    public SyncDataOutService() {
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

    @Override
    protected void onHandleIntent(Intent intent) {
        setFinished(false);
        ArrayList<ErrorReport> errors = new ArrayList<>();
        UserData user = null;
        boolean isFromAnotherApp = false;
        packageWhoCalled = "";
        int optionLogOut = 0;
        if (SyncUtilities.wifiValidation(this)) {
            userDatabaseHelper = UserDatabaseHelper.getHelper(this);
            catalogDatabaseHelper = CatalogDatabaseHelper.getHelper(this);
            Intent resultIntent = new Intent();
            ArrayList<ErrorReport> errorList = null;
            Bundle bun = intent.getExtras();
            if (bun != null) {
                isFromAnotherApp = bun.getBoolean(Properties.EXTRA_IS_FROM_ANOTHER_APP);
                optionLogOut = bun.getInt(Properties.EXTRA_LOG_OUT_OPTION);
                if (isFromAnotherApp) {
                    boolean needQuantityWS = bun.getBoolean(Properties.EXTRA_IS_QUANITY_NEEDED);
                    if (needQuantityWS) {
                        packageWhoCalled = bun.getString(Properties.CALL_FROM_PACKAGE_NAME);
                        String act = packageWhoCalled + SYNC_QUANTITY_BROADCAST_TASKS;
                        resultIntent.setAction(act);
                        resultIntent.putExtra(Properties.QUANITY_TASKS_WS, loadResourceListOnly().size());
                        sendBroadcast(resultIntent);
                        setFinished(true);
                        stopSelf();
                        return;
                    }
                    String errString = logoutValidation();
                    if (!errString.equals("")) {
                        String act = packageWhoCalled + SYNC_FAILED_BROADCAST_FROM;
                        resultIntent.setAction(act);
                        resultIntent.putExtra("Error", errString);
                        sendBroadcast(resultIntent);
                        Intent intentResult = new Intent();
                        String action = packageWhoCalled + SYNC_PROGRESS_TASK_ERROR_FROM;
                        intentResult.setAction(action);
                        sendBroadcast(intentResult);
                        setFinished(true);
                        stopSelf();
                        return;
                    }
                    packageWhoCalled = bun.getString(Properties.CALL_FROM_PACKAGE_NAME);
                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_name), MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Properties.EXTRA_IS_FROM_ANOTHER_APP, true);
                    editor.apply();
                }
            }
            try {
                user = userDatabaseHelper.getUserDataDao().getCurrentUser();
                if (user != null) {
                    this.user = user.getUsername();
                    ArrayList<SyncOutResource> resourceList = loadResourceList(user.getUsername());
                    SyncBatchResults syncBatchResults = processResourceList(isFromAnotherApp, resourceList, user.getSession(), userDatabaseHelper.getUserSessionDao());
                    if (syncBatchResults.isSuccessful() && errors.isEmpty()) {
                        if (syncBatchResults.getErrors() != null || !syncBatchResults.getErrors().isEmpty()) {
                            errors.addAll(syncBatchResults.getErrors());
                        }
                        syncBatchResults = SyncUtilities.logout(this, userDatabaseHelper, user, packageWhoCalled);
                        if (!syncBatchResults.isSuccessful() && (syncBatchResults.getErrors() != null || !syncBatchResults.getErrors().isEmpty()))
                            errors.addAll(syncBatchResults.getErrors());
                    } else
                        errors.addAll(syncBatchResults.getErrors());
                } else {
                    if (!isFromAnotherApp) {
                        ErrorReport error = new ErrorReport(new Exception());
                        error.setType(getString(R.string.error_user_data));
                        errors.add(error);
                    }
                }
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage(), e);
                ErrorReport error = new ErrorReport(e);
                error.setType(getString(R.string.error_user_data));
                errors.add(error);
            }
        } else {
            ErrorReport error = new ErrorReport(new Exception());
            error.setType(getString(R.string.error_no_wifi));
            errors.add(error);
        }
        Intent resultIntent = new Intent();
        // verificación de warnings
        boolean isError = false;
        for (ErrorReport error : errors) {
            if (error == null)
                continue;
            if (!error.isWarning()) {
                isError = true;
                errors = new ArrayList<>();
                errors.add(error);
                resultIntent.putExtras(error.toBundle());
                resultIntent.putExtra("Error", error.getType());
                break;
            }
        }
        if (!isError) {
            resultIntent.setAction(SYNC_FINISHED_BROADCAST);
            Log.i(TAG, "Cleaning up resources...");
            // Erase contents of LW folder in sdcard
            File lwDirectory = new File(Properties.SD_CARD_IMAGES_DIR);
            File lw;
            String[] children = lwDirectory.list();
            if (children != null) {
                for (String aChildren : children) {
                    lw = new File(lwDirectory, aChildren);
                    Log.d(TAG, lw.getName() + " deleted: " + lw.delete());
                }
            }
            userDatabaseHelper.clearTables();
            catalogDatabaseHelper.clearTables();
            if (isFromAnotherApp) {
                String act = packageWhoCalled + SYNC_FINISHED_BROADCAST_FROM;
                resultIntent.putExtra(Properties.EXTRA_LOG_OUT_OPTION, optionLogOut);
                resultIntent.setAction(act);
            } else {
                resultIntent.setAction(SYNC_FINISHED_BROADCAST);
            }
            errors = null;
        } else {
            if (isFromAnotherApp) {
                String act = packageWhoCalled + SYNC_FAILED_BROADCAST_FROM;
                resultIntent.setAction(act);
                Intent intentResult = new Intent();
                String action = packageWhoCalled + SYNC_PROGRESS_TASK_ERROR_FROM;
                intentResult.setAction(action);
                sendBroadcast(intentResult);
            } else
                resultIntent.setAction(SYNC_FAILED_BROADCAST);
        }
        sendBroadcast(resultIntent);
        buildFinishedNotification(errors, isFromAnotherApp);
        setFinished(true);
        stopSelf();
    }

    private ArrayList<SyncOutResource> loadResourceListOnly() {
        ArrayList<SyncOutResource> allResources = new ArrayList<>();
        // Send email de reumen
        allResources.add(new SyncOutResource(IUrls.I_EMAIL, HeaderApp.class, user, IResources.EMAIL));
        allResources.add(new SyncOutResource(IUrls.I_LW, Lw.class, user, SyncDataOutObject.COL_UPDATED));
        //envia tareas
        allResources.add(new SyncOutResource(IUrls.U_TAREA, TareaDTO.class, user, SyncDataOutObject.COL_UPDATED));
        //envia notas de seguimiento
        allResources.add(new SyncOutResource(IUrls.U_NOTA, NotaDTO.class, user, SyncDataOutObject.COL_UPDATED));
        return allResources;
    }

    private ArrayList<SyncOutResource> loadResourceList(String user) {
        ArrayList<SyncOutResource> resourcesWithData = new ArrayList<>();

        ArrayList<SyncOutResource> allResources = loadResourceListOnly();
        ArrayList<?> uploadDataList;
        for (SyncOutResource resource : allResources) {
            if (!resource.getClz().equals(HeaderApp.class)) {
                uploadDataList = userDatabaseHelper.getUpdatedResourceList(resource.getClz(), resource.getSelectionColumn(), resource.getUsername());
                if (uploadDataList != null && !uploadDataList.isEmpty()) {
                    if (resource.getType() == SyncOutResource.TYPE_JSON) {
                        JSONArray jsonArray = JsonUtils.toJsonArray(uploadDataList);
                        resource.setJson(jsonArray);
                        resourcesWithData.add(resource);
                    } else if (resource.getType() == SyncOutResource.TYPE_LW) {
                        SyncOutResource lwResource;
                        if (resource.getClz().equals(Lw.class))
                            for (Lw lw : (ArrayList<Lw>) uploadDataList) {
                                //if (!lw.getNombre_archivo().contains("video")) {
                                    /* TODO Descomentar cuando Multipart soporte video y
                                            quitar condicion de arriba
                                                if (!lw.getNombre_archivo().contains("video"))*/
                                    String path="";
                                    if (lw.getNombre_archivo().contains("video"))
                                        path = Properties.SD_CARD_VIDEOS_DIR + CONTEXT_SEPERATOR + lw.getNombre_archivo();
                                    else
                                     path = Properties.SD_CARD_IMAGES_DIR + CONTEXT_SEPERATOR + lw.getNombre_archivo();
                                    File file = new File(path);
                                    if (file.exists()) {
                                        lwResource = new SyncOutResource(IUrls.I_LW, Lw.class, user, SyncDataOutObject.COL_UPDATED);
                                        lw.setSource(Lw.TYPE_LW);
                                        lwResource.setLw(lw);
                                        resourcesWithData.add(lwResource);
                                    }
                                //}
                            }
                    }
                }
            } else {
                resource.setStrJson(getResumenCob(resource.getUsername()));
                resourcesWithData.add(resource);
            }
        }
        return resourcesWithData;
    }

    private static String getColorString(int resColor) {
        int greenColor = ContextCompat.getColor(ContextApplication.getAppContext(), resColor);
        String color = "#" + Integer.toHexString(greenColor).toUpperCase();
        return color.replace("#FF", "#");
    }

    public String getResumenCob(String user) {
        StringBuilder resumen = new StringBuilder();
        UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getBaseContext());
        String styleA = "style=\"background-color: " + getColorString(R.color.color_500) + "; color: white; padding: 8px; text-align: center\"";
        String styleB = "style=\"background-color: " + getColorString(R.color.color_100) + "; color: black; padding: 8px; text-align: center; white-space: nowrap \"";
        try {
            resumen.append("<p><h3>Resumen de actividades</h3></p>");
            resumen.append("<div id=\"sright\" style=\"float: right; cursor: pointer; margin-bottom: 5px; margin-right: 5px;\"><b> >>> </b></div>"
                    + "<div style=\"clear:both;width:100%;\">" + "<div class=\"outer\">" + "<div class=\"inner\">");
            resumen.append("<p><h4>Tareas Creadas</h4></p>");
            resumen.append("<table style=\"border-collapse: 1\"><tr>" + "<th ")
                    .append(styleA).append("> Credito/Juicio </th>").append("<th ")
                    .append(styleA).append("> Categoria </th>").append("<th ")
                    .append(styleA).append("> Sub Categoria </th>").append("<th ")
                    .append(styleA).append("> No. Seguimientos </th>").append("</tr>");
            String nextRoeBeg = "<tr>";
            String align = "<td " + styleB + ">";
            Dao<TareaDTO, Long> tareaDao = helper.getDao(TareaDTO.class);
            Dao<NotaDTO, Long> notDao = helper.getDao(NotaDTO.class);
            List<TareaDTO> allTsk = tareaDao.queryBuilder().where().eq(TareaDTO.COL_UPDATED, true).query();
            TareaDTO tarea;
            int delete = 0;
            for (int i = 0; i < allTsk.size(); i++) {
                tarea = allTsk.get(i);
                List<TareaDTO> creditTsk = tareaDao.queryBuilder().where().eq(TareaDTO.COL_CREDIT_NUMBER, tarea.getCredito()).query();
                for (TareaDTO tsk : creditTsk) {
                    resumen.append(nextRoeBeg);
                    List<NotaDTO> notasTsk = notDao.queryBuilder().where().eq(NotaDTO.COL_ID_TAREA, tarea.get_id()).and().eq(NotaDTO.COL_UPDATED, true).query();
                    resumen.append(align).append(tsk.getCredito() == null ? "n/a" : tsk.getCredito().isEmpty() ? "n/a" : tsk.getCredito()).append("</td>")
                            .append(align).append(tsk.getCredito() == null ? "n/a" : tsk.getAsunto().isEmpty() ? "n/a" : tsk.getAsunto()).append("</td>")
                            .append(align).append(tsk.getCredito() == null ? "n/a" : tsk.getSubClasifica().isEmpty() ? "n/a" : tsk.getSubClasifica()).append("</td>")
                            .append(align).append(notasTsk != null ? notasTsk.size() : 0).append("</td>").append("</tr>");
                }
                for (int j = 0; j < allTsk.size(); j++) {
                    if (allTsk.get(j).getCredito().equalsIgnoreCase(tarea.getCredito())) {
                        allTsk.remove(j);
                        j--;
                        delete++;
                    }
                }
                if (delete > 0) {
                    i--;
                    delete = 0;
                }
            }
            resumen.append("</table></div></div><br>");
            resumen.append("<div id=\"sright\" style=\"float: right; cursor: pointer; margin-bottom: 5px; margin-right: 5px;\"><b> >>> </b></div>"
                    + "<div style=\"clear:both;width:100%;\">" + "<div class=\"outer\">" + "<div class=\"inner\">");
            resumen.append("<p><h4>Tareas Asignadas</h4></p>");
            resumen.append("<table style=\"border-collapse: 1\"><tr>" + "<th ")
                    .append(styleA).append("> Credito/Juicio </th>").append("<th ")
                    .append(styleA).append("> Categoria </th>").append("<th ")
                    .append(styleA).append("> Sub Categoria </th>").append("<th ")
                    .append(styleA).append("> No. Seguimientos </th>").append("</tr>");
            align = "<td " + styleB + ">";
            String nextRowEnd = "</tr>";
            Dao<SeguimientoTarea, Long> segDao = helper.getDao(SeguimientoTarea.class);
            List<SeguimientoTarea> allTskInternet = segDao.queryBuilder().query();
            SeguimientoTarea tskInternet;
            delete = 0;
            for (int i = 0; i < allTskInternet.size(); i++) {
                tskInternet = allTskInternet.get(i);
                List<NotaDTO> notasTsk = notDao.queryBuilder().where().eq(NotaDTO.COL_ID_TAREA, tskInternet.getIdtarea()).and().eq(NotaDTO.COL_UPDATED, true).query();
                resumen.append(nextRoeBeg)
                        .append(align).append(tskInternet.getCredito() == null ? "n/a" : tskInternet.getCredito().isEmpty() ? "n/a" : tskInternet.getCredito()).append("</td>")
                        .append(align).append(tskInternet.getCredito() == null ? "n/a" : tskInternet.getAsunto().isEmpty() ? "n/a" : tskInternet.getAsunto()).append("</td>")
                        .append(align).append(tskInternet.getCredito() == null ? "n/a" : tskInternet.getSubClasifica() != null ? (tskInternet.getSubClasifica().isEmpty() ? "n/a" : tskInternet.getSubClasifica()) : "n/a").append("</td>")
                        .append(align).append(notasTsk != null ? notasTsk.size() : 0).append("</td>").append("</tr>");
                for (int j = 0; j < allTsk.size(); j++) {
                    if (allTsk.get(j).getCredito().equalsIgnoreCase(tskInternet.getCredito())) {
                        allTsk.remove(j);
                        j--;
                        delete++;
                    }
                }
                if (delete > 0) {
                    i--;
                    delete = 0;
                }
                //resumen.append(nextRowEnd);
            }
            resumen.append("</table></div></div><br>");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }
        HeaderApp h = new HeaderApp(resumen.toString(), user);
        switch (packageWhoCalled) {
            case Properties.pakage_olin:
                h.setCallFromApp(Properties.APP_NAME_OLIN);
                break;
            case Properties.pakage_legal:
                h.setCallFromApp(Properties.APP_NAME_LEGAL);
                break;
            default:
                h.setCallFromApp(Properties.APP_NAME);
                break;
        }
        return new Gson().toJson(h);
    }

    private SyncBatchResults processResourceList(boolean isFromAnotherApp, ArrayList<SyncOutResource> resourceList, UserSession userSession, Dao<UserSession, Long> userSessionDao) {
        SyncBatchResults syncBatchResults = new SyncBatchResults();
        ArrayList<SyncResult> uploadResults = new ArrayList<>();
        long end;
        long start = System.currentTimeMillis();
        float progress = 0;
        float totalResources = resourceList.size();
        if (!isFromAnotherApp)
            buildProgressNotification((int) totalResources);
        if (resourceList.isEmpty())
            syncBatchResults.setSuccessful(true);
        else {
            SyncResult uploadResult;
            for (SyncOutResource resource : resourceList) {
                if (resource.getType() == SyncOutResource.TYPE_JSON) {
                    uploadResult = uploadJson(resource);
                    if (uploadResult.isSuccessful() && uploadResult.getError() == null) {
                        syncBatchResults.setSuccessful(true);
                        if (!resource.getSelectionColumn().equals(IResources.EMAIL)) {
                            markResourceUploaded(resource.getClz(), resource.getSelectionColumn());
                        }
                    } else
                        syncBatchResults.addError(uploadResult.getError());
                } else if (resource.getType() == SyncOutResource.TYPE_LW) {
                    uploadResult = uploadLw(resource);
                    if (uploadResult.isSuccessful() && uploadResult.getError() == null) {
                        syncBatchResults.setSuccessful(true);
                    } else {
                        syncBatchResults.addError(uploadResult.getError());
                        return syncBatchResults;
                    }
                    uploadResults.add(uploadResult);
                }
                if (!isFromAnotherApp) {
                    int prog = (int) (progress / totalResources * 100);
                    notification.contentView.setProgressBar(R.id.progressBar, (int) totalResources, (int) ++progress, false);
                    notification.contentView.setTextViewText(R.id.resourceTextView, prog + "%");
                    notificationManager.notify(NOTIFICATION_ID, notification);
                    Intent resultIntent = new Intent();
                    resultIntent.setAction(SYNC_PROGRESS_BROADCAST);
                    resultIntent.putExtra(SYNC_PROGRESS, prog);
                    if (resource == null) {
                        resultIntent.putExtra(SYNC_DSCRIPTION, "");
                    } else {
                        resultIntent.putExtra(SYNC_DSCRIPTION, resource.getNotifName().replace(this.user, ""));
                    }
                    sendBroadcast(resultIntent);
                } else {
                    Intent resultIntent = new Intent();
                    String act = packageWhoCalled + SYNC_ADD_PROGRESS_TASK;
                    resultIntent.setAction(act);
                    sendBroadcast(resultIntent);
                }
            }
        }
        end = System.currentTimeMillis();
        Log.d(TAG, "Total Time:" + (end - start) / 1000 + "s.");
        for (SyncResult result : uploadResults) {
            if (!result.isSuccessful()) {
                syncBatchResults.addError(result.getError());
                break;
            }
        }
        return syncBatchResults;
    }

    HttpClient httpClient;
    HttpPost httpPost;
    HttpResponse response;

    public SyncResult uploadJson(SyncOutResource resource) {
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost(resource.getUrl());
        Log.i(TAG, "url: " + resource.getUrl());
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8");
        long start = System.currentTimeMillis();
        SyncResult uploadResult = new SyncResult();
        int rcode = 0;
        try {
            if (!resource.getSelectionColumn().equals(IResources.EMAIL)) {
                if (resource.getUrl().contains(IUrls.U_TAREA) || resource.getUrl().contains(IUrls.U_NOTA)) {
                    Log.i(TAG, "breakpoint");
                    ArrayList<?> uploadDataList = userDatabaseHelper.getUpdatedResourceList(resource.getClz(), resource.getSelectionColumn(), resource.getUsername());
                    JSONArray jsonArray = JsonUtils.toJsonArray(uploadDataList);
                    resource.setJson(jsonArray);
                    Log.i("obj", "obj1" + resource.getStrJson());
                }
                httpPost.setEntity(new StringEntity(resource.getJson().toString(), "utf-8"));
            } else {
                httpPost.setEntity(new StringEntity(resource.getStrJson(), "utf-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            response = httpClient.execute(httpPost);
            if (response != null) {
                rcode = response.getStatusLine().getStatusCode();
                if (rcode == HttpURLConnection.HTTP_OK) {
                    String responseString = entityToString(response.getEntity());
                    Log.i(TAG, responseString + "");
                    if (resource.getUrl().contains(IUrls.U_TAREA)) {
                        TransactionResult entity = new Gson().fromJson(responseString, TransactionResult.class);
                        String response = entity.getResponse();
                        String[] ids = response.split(",");
                        List<Double> ss = entity.getSuccessful();
                        for (int i = 0; i < ss.size(); i++) {
                            UserDatabaseHelper helper = UserDatabaseHelper.getHelper(ContextApplication.getAppContext());
                            try {
                                int id = ss.get(i).intValue();
                                Dao<NotaDTO, Long> dao = userDatabaseHelper.getDao(NotaDTO.class);
                                QueryBuilder query = dao.queryBuilder();
                                query.where().eq(NotaDTO.COL_ID_TAREA, id);
                                NotaDTO nota = (NotaDTO) query.queryForFirst();
                                String idStr = ids[i];
                                int idTarea = Integer.parseInt(idStr);
                                nota.setIdTarea(id);
                                dao.update(nota);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                helper.close();
                            }
                        }
                        Log.i("", "");
                    }
                    uploadResult.setSuccessful(true);
                    uploadResult.setHedaer_exito(response.getHeaders("phase_exito"));
                    uploadResult.setHedaer_fail(response.getHeaders("phase_error"));
                } else {
                    String userFriendlyMessage = getString(R.string.error_http) + " " + rcode + " " + resource;
                    Log.e(TAG, userFriendlyMessage);
                    ErrorReport error = new ErrorReport(new Exception());
                    error.setType(userFriendlyMessage);
                    uploadResult.setError(error);
                }
            } else {
                String userFriendlyMessage = getString(R.string.error_http_server) + resource;
                Log.e(TAG, userFriendlyMessage);
                ErrorReport error = new ErrorReport(new Exception());
                error.setType(userFriendlyMessage);
                uploadResult.setError(error);
            }
            if (uploadResult.isSuccessful())
                Log.d(TAG, resource + " Uploaded " + " - Time:" + (System.currentTimeMillis() - start) + "ms. HTTP " + rcode);
            else
                Log.e(TAG, resource + " Not uploaded " + " - Time:" + (System.currentTimeMillis() - start) + "ms. HTTP" + rcode);
        } catch (Exception e2) {
            e2.printStackTrace();
            String userFriendlyMessage = getString(R.string.error_http_server);
            Log.e(TAG, userFriendlyMessage);
            ErrorReport error = new ErrorReport(new Exception());
            error.setType(userFriendlyMessage);
            uploadResult.setError(error);
        }
        return uploadResult;
    }

    public SyncResult sendTareas(StringBuffer resource, String url, String[] file) {
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost(url);
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
        long start = System.currentTimeMillis();
        SyncResult uploadResult = new SyncResult();
        int rcode = 0;
//        Log.i(TAG, "URL SEND: " + url);
//        Log.i("json", "URL SEND: " + resource.toString());
        try {
            httpPost.setEntity(new StringEntity(resource.toString(), HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            response = httpClient.execute(httpPost);
            if (response != null) {
                rcode = response.getStatusLine().getStatusCode();
                String responseString = entityToString(response.getEntity());
//                if (Tools.isDebug()) {
//                    Log.d(TAG, "SEND:\t" + url + "\nRESP:\t" + responseString + "\n\n");
//                }
                if (rcode == HttpURLConnection.HTTP_OK) {
                    uploadResult.setSuccessful(true);
                    uploadResult.setHedaer_exito(response.getHeaders("phase_exito"));
                    uploadResult.setHedaer_fail(response.getHeaders("phase_error"));
                    for (String s : file) {
                        if (s != null && s.length() > 0) {
                            File f = new File(s);
                            if (f.exists()) {
                                f.delete();
                            }
                        }
                    }
                } else {
                    String userFriendlyMessage = getString(R.string.error_http) + rcode + resource;
                    Log.e(TAG, userFriendlyMessage);
                    ErrorReport error = new ErrorReport(new Exception());
                    error.setType(userFriendlyMessage);
                    uploadResult.setError(error);
                }
            } else {
                String userFriendlyMessage = getString(R.string.error_http_server) + resource;
                Log.e(TAG, userFriendlyMessage);
                ErrorReport error = new ErrorReport(new Exception());
                error.setType(userFriendlyMessage);
                uploadResult.setError(error);
            }
            if (uploadResult.isSuccessful())
                Log.d(TAG, resource + " Uploaded " + " - Time:" + (System.currentTimeMillis() - start) + "ms. HTTP " + rcode);
            else
                Log.e(TAG, resource + " Not uploaded " + " - Time:" + (System.currentTimeMillis() - start) + "ms. HTTP" + rcode);
        } catch (Exception e2) {
            String userFriendlyMessage = getString(R.string.error_http_server);
            Log.e(TAG, userFriendlyMessage);
            ErrorReport error = new ErrorReport(new Exception());
            error.setType(userFriendlyMessage);
            uploadResult.setError(error);
        }
        resource = null;
        Runtime garbage = Runtime.getRuntime();
        garbage.gc();
        return uploadResult;
    }

    public SyncResult uploadLw(SyncOutResource resource) {
        long start = System.currentTimeMillis();
        SyncResult uploadResult = new SyncResult();
        if (resource.getLw().lwFileExists()) {//se verifica que exista la imagen en el dispositivo
            Log.i(TAG, "url: " + resource.getUrl());
            Log.i(TAG, "Se inicializa la subida de " + resource.getLw().getNombre_archivo());
            HttpClient client = new DefaultHttpClient();
            HttpPost poster = new HttpPost(resource.getUrl());
            HttpResponse response = null;
            try {
                poster.setEntity(resource.getLw().getMultipartEntity());
                long st = System.currentTimeMillis();
                response = client.execute(poster);
                long time = (System.currentTimeMillis() - st);
                Log.e(TAG, "tiempo subida  imagen " + time + "ms, " + (time / 1000));
                if (response != null) {//hubo respuesta del servidor
                    int responseCode = response.getStatusLine().getStatusCode();
                    String userFriendlyMessage;
                    ErrorReport error;
                    TransactionResult entity;
                    Log.i(TAG, "response " + responseCode);
                    String responseString = entityToString(response.getEntity());
                    if (Tools.isDebug()) {
                        Log.i(TAG, "SEND:\t" + resource.getUrl() + "\nRESP:\t" + responseString + "\n\n");
                    }
                    switch (responseCode) {
                        case HttpURLConnection.HTTP_CREATED:
                            uploadResult.setSuccessful(true);
                            entity = new Gson().fromJson(responseString, TransactionResult.class);
                            String indiceFilegrid = entity.getResponse();
                            List<Long> ids = entity.getSuccessful();
                            if (resource.getLw().getSource() == Lw.TYPE_LW) {
                                try {
                                    Dao<Lw, Long> dao = userDatabaseHelper.getDao(Lw.class);
                                    Lw lw = dao.queryForId(ids.get(0));
                                    lw.setUpdated(false);
                                    lw.setIndiceFilegrid(indiceFilegrid);
                                    dao.update(lw);
                                    Log.i(TAG, resource.getLw().getNombre_archivo() + " deleted: " + resource.getLw().getImagen().delete());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    break;
                                }
                            }
                            Log.i(TAG, resource.getLw().getNombre_archivo() + " deleted: " + resource.getLw().getImagen().delete());
                            break;
                        case HttpURLConnection.HTTP_OK:
//                            String responseString = entityToString(response.getEntity());

                            entity = new Gson().fromJson(responseString, TransactionResult.class);
                            userFriendlyMessage = new StringBuilder()
                                    .append(getString(R.string.error_http))
                                    .append(" ")
                                    .append(responseCode)
                                    .append("\n")
                                    .append(getString(R.string.error_200))
                                    .append("\n")
                                    .append(resource.getClz().getSimpleName())
                                    .append("\n")
                                    .append(resource.getLw().getNombre_archivo())
                                    .toString();
                            Log.e(TAG, userFriendlyMessage);
                            Log.e(TAG, responseString);
                            error = new ErrorReport(new Exception());
                            error.setType(userFriendlyMessage);
                            uploadResult.setError(error);
                            break;
                        default:
                            userFriendlyMessage = new StringBuilder()
                                    .append(getString(R.string.error_http))
                                    .append(responseCode)
                                    .append("\n")
                                    .append(resource.getClz().getSimpleName())
                                    .append("\n")
                                    .append(resource.getLw().getNombre_archivo())
                                    .toString();
                            Log.e(TAG, userFriendlyMessage);
                            error = new ErrorReport(new Exception());
                            error.setType(userFriendlyMessage);
                            uploadResult.setError(error);
                            break;
                    }
                } else {
                    //no hubo respuesta del servidor
                    String userFriendlyMessage = getString(R.string.error_http_server);
                    Log.e(TAG, userFriendlyMessage);
                    ErrorReport error = new ErrorReport(new Exception());
                    error.setType(userFriendlyMessage);
                    uploadResult.setError(error);
                }
            } catch (Exception e) {
                e.printStackTrace();
                String userFriendlyMessage = getString(R.string.error_http_server) + "\n" + e.getMessage();
                Log.e(TAG, userFriendlyMessage);
                ErrorReport error = new ErrorReport(
                        new Exception());
                error.setType(userFriendlyMessage);
                uploadResult.setError(error);
            } finally {
                client = null;
                poster = null;
                response = null;
            }
        } else {//si la imagen no existe no hay manera de recuperarla asi que se pone como trasaccion exitosa
            String userFriendlyMessage = new StringBuilder()
                    .append(getString(R.string.error_missing_photo))
                    .append(" ").append(resource.getLw().getNombre_archivo())
                    .toString();
            Log.e(TAG, userFriendlyMessage);
            uploadResult.setSuccessful(true);
        }
        String message;
        if (uploadResult.isSuccessful()) {
            message = "Uploaded";
        } else {
            message = "Error";
        }
        Log.i(TAG, resource + message + resource.getLw().getNombre_archivo() + " - Time:" + (System.currentTimeMillis() - start) + "ms.");
        return uploadResult;
    }

    public static String entityToString(HttpEntity entity) {
        InputStream is = null;
        try {
            is = entity.getContent();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder str = new StringBuilder();
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                str.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str.toString();
    }

    private SyncResult markResourceUploaded(Class clazz, String updatedFieldName) {
        SyncResult databaseResult = new SyncResult();
        Dao<?, Integer> dao = null;
        try {
            dao = userDatabaseHelper.getDao(clazz);
            UpdateBuilder<?, Integer> updateBuilder = dao.updateBuilder();
            updateBuilder.updateColumnValue(updatedFieldName, false);
            updateBuilder.update();
            databaseResult.setSuccessful(true);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            ErrorReport error = new ErrorReport(e);
            error.setType(getString(R.string.error_database));
        }
        return databaseResult;
    }

    private void buildProgressNotification(int totalResourcesPending) {
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
            builder = new NotificationCompat.Builder(this);
        }
        builder
                .setContentIntent(contentIntent)
                .setProgress(totalResourcesPending, 0, true)
                .setAutoCancel(false)
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(appResources, R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_refresh)
                .setTicker(appResources.getString(R.string.synchronizing_data))
                .setWhen(System.currentTimeMillis());
        notification = builder.build();
        notification.contentView = remoteView;
        notificationManager.cancelAll();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void buildFinishedNotification(ArrayList<ErrorReport> errors, boolean isFromAnotherApp) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        if (!isFromAnotherApp) {
            boolean result = errors == null || errors.isEmpty();
            String errorStr = null;
            if (!result) {
                StringBuilder errorStrBuilder = new StringBuilder();
                for (ErrorReport error : errors) {
                    if (error != null)
                        errorStrBuilder.append(error.getType()).append(Properties.LINE_SEPARATOR);
                }
                errorStr = errorStrBuilder.toString();
            }
            Resources appResources = getResources();
            Intent notificationIntent = new Intent(this, EntryPointActivity.class);
            notificationIntent.setAction("android.intent.action.MAIN");
            notificationIntent.addCategory("android.intent.category.LAUNCHER");
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            String finishedResultText = result ? appResources.getString(R.string.upload_complete) : errorStr;
            String finishedTickerText = result ? appResources.getString(R.string.upload_complete) : appResources.getString(R.string.error_upload_incomplete);
            int iconResult = result ? R.drawable.ic_check : R.drawable.ic_ex;
            NotificationCompat.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new NotificationCompat.Builder(this, "default");
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
        }
    }

    public static boolean isFinished() {
        return isFinished;
    }

    private static void setFinished(boolean isFinished) {
        SyncDataOutService.isFinished = isFinished;
    }

    String logoutValidation() {
        ArrayList<String> list = new ArrayList<>();
        String str = "";
        UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getBaseContext());
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
            str = list.toString();
            str = str.substring(1);
            str = str.substring(0, str.length() - 1);
            return str;
        }
        return str;
    }
}
