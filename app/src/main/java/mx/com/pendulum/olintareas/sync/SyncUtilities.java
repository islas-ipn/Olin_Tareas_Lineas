package mx.com.pendulum.olintareas.sync;

import android.content.Context;
import android.database.Cursor;
import android.text.Spanned;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.SyncDataOutObject;
import mx.com.pendulum.olintareas.dto.UserData;
import mx.com.pendulum.olintareas.dto.UserSession;
import mx.com.pendulum.olintareas.sync.constants.IUrls;
import mx.com.pendulum.utilities.ErrorReport;
import mx.com.pendulum.utilities.JsonUtils;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.Util;
import mx.com.pendulum.utilities.http.CnxUtils;
import mx.com.pendulum.utilities.http.HttpConstants;
import mx.com.pendulum.utilities.http.HttpHeaders;

public class SyncUtilities {

    private static final String TAG = SyncUtilities.class.getSimpleName();

    @SuppressWarnings({"unchecked", "rawtypes"})
    static public void writeToDatabase(final ArrayList<Object> entities,
                                       final Dao dao) {
        try {
            dao.callBatchTasks(new Callable<Void>() {
                @Override
                public Void call() throws SQLException {
                    for (Object entity : entities) {
                        dao.create(entity);

                    }
                    entities.clear();
                    return null;
                }
            });
        } catch (Exception e) {
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static public void writeToDatabase(final Object entities,
                                       final Dao dao) {
        try {
            dao.callBatchTasks(new Callable<Void>() {
                @Override
                public Void call() throws SQLException {
                    dao.create(entities);
                    //entities.clear();
                    return null;
                }
            });
        } catch (Exception e) {
        }
    }

    private static int obtainNum(Context context, String tableName, boolean obtainLW) {
        int num = 0;
        String query;
        UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper.getHelper(context);
        if (!obtainLW)
            query = "SELECT count(*) modificados from " + tableName + " WHERE updated= 1;";
        else
            query = "SELECT count(*) modificados from " + tableName + ";";
        Cursor c = userDatabaseHelper.getReadableDatabase().rawQuery(query, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            num = c.getInt(c.getColumnIndex("modificados"));
        }
        c.close();
        return num;
    }

    static public SyncBatchResults logout(Context context, UserDatabaseHelper userDatabaseHelper,
                                          UserData user, String packageWhoCalled) {
        SyncBatchResults result = new SyncBatchResults();
        UserSession session;
        Dao<UserSession, Long> dao;
        try {
            dao = userDatabaseHelper.getDao(UserSession.class);
            QueryBuilder<UserSession, Long> queryBuilder = dao.queryBuilder();
            PreparedQuery<UserSession> preparedQuery = queryBuilder.prepare();
            session = dao.queryForFirst(preparedQuery);
            if (session != null) {
                String username = user.getUsername();
                session.setUsername(username);
                // TODO AGREGAR NUMERO DE TASKS Y NOTAS
                session.setNum_task(obtainNum(context, TableNames.TAREA_DTO, false));
                session.setNum_notes(obtainNum(context, TableNames.SEGUIMIENTOTAREA, false));
                session.setNum_lw(obtainNum(context, TableNames.LW, true));
                // Purely for server logging purposes, no effect on DB logout
                // date(sysdate).
                session.setFecha_logout(new Date());
                switch (packageWhoCalled) {
                    case Properties.pakage_olin:
                        session.setCallFromApp(Properties.APP_NAME_OLIN);
                        break;
                    case Properties.pakage_legal:
                        session.setCallFromApp(Properties.APP_NAME_LEGAL);
                        break;
                    default:
                        session.setCallFromApp(Properties.APP_NAME);
                        break;
                }
                JSONObject sessionJson = JsonUtils.toJsonObject(session);
                sessionJson.remove(SyncDataOutObject.INVALID_ID);
                sessionJson.remove(SyncDataOutObject.COL_ACTIVITY_DATE);
                sessionJson.remove(SyncDataOutObject.COL_UPDATED);
                String sessionStr = sessionJson.toString();
                Log.i(TAG, sessionStr);
                result = SyncUtilities.postJson(context, IUrls.USER_LOGOUT,
                        sessionStr.getBytes());
            } else {
                String userFriendlyMessage = new StringBuilder()
                        .append(context.getString(R.string.error_user_data))
                        .append(UserSession.class.getName()).toString();
                Log.e(TAG, userFriendlyMessage);
                ErrorReport error = new ErrorReport(new Exception());
                error.setType(userFriendlyMessage);
                result.addError(error);
            }
        } catch (SQLException e) {
            String userFriendlyMessage = new StringBuilder()
                    .append(context.getString(R.string.error_database))
                    .append(UserSession.class.getName()).toString();
            Log.e(TAG, userFriendlyMessage);
            ErrorReport error = new ErrorReport(new Exception());
            error.setType(userFriendlyMessage);
            result.addError(error);
        }
        return result;
    }

    static public SyncBatchResults postJson(Context context, String url,
                                            byte[] postData) {
        SyncBatchResults result = new SyncBatchResults();
        long start;
        long end;
        start = System.currentTimeMillis();
        String resource = null;
        HttpURLConnection conn = null;
        int rcode = 0;
        try {
            resource = url.substring(url.lastIndexOf('/'));
            HttpHeaders headers = new HttpHeaders();
            headers.addProperty(HttpHeaders.HEADER_CONTENT_TYPE,
                    "application/json");
            conn = CnxUtils.makeHttpConnection(url, headers, postData,
                    HttpConstants.POST);
            if (conn != null) {
                rcode = CnxUtils.extractResponseCode(conn);
                if (rcode == HttpURLConnection.HTTP_OK)
                    result.setSuccessful(true);
                else {
                    String userFriendlyMessage = new StringBuilder()
                            .append(context.getString(R.string.error_http))
                            .append(rcode).append(resource).toString();
                    Log.e(TAG, userFriendlyMessage);
                    ErrorReport error = new ErrorReport(new Exception());
                    error.setType(userFriendlyMessage);
                    result.addError(error);
                }
            } else {
                String userFriendlyMessage = new StringBuilder()
                        .append(context.getString(R.string.error_http))
                        .append(resource).toString();
                Log.e(TAG, userFriendlyMessage);
                ErrorReport error = new ErrorReport(new Exception());
                error.setType(userFriendlyMessage);
                result.addError(error);
            }
        } catch (Exception e) {
            String userFriendlyMessage = new StringBuilder().append(e)
                    .append('-').append(resource).toString();
            Log.e(TAG, e.getMessage(), e);
            ErrorReport error = new ErrorReport(e);
            error.setType(userFriendlyMessage);
            result.addError(error);
        } finally {
            if (conn != null) {
                CnxUtils.closeConnection(conn);
                conn = null;
            }
        }
        end = System.currentTimeMillis();
        if (result.isSuccessful()
                && (result.getErrors() == null || result.getErrors().size() == 0))
            Log.i(TAG, resource + " Uploaded " + " - Time:" + (end - start)
                    + "ms. HTTP " + rcode);
        else
            Log.e(TAG, resource + " Not uploaded " + " - Time:" + (end - start)
                    + "ms. HTTP" + rcode);
        return result;
    }

    //Get Wifi status from system.
    static public boolean wifiValidation(Context context) {
        boolean isConnected;
        if (Tools.isDebug())
            return true;
        if (context == null)
            return false;
        if (Properties.EXECUTE_WIFI_TEST)
            // isConnected = Util.isConnected(context);
            isConnected = Util.isWiFiConnected(context);
        else
            isConnected = true;
        return isConnected;
    }
}
