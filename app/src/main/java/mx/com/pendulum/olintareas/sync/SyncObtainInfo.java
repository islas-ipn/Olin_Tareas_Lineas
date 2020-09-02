package mx.com.pendulum.olintareas.sync;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

import mx.com.pendulum.olintareas.config.util.ContextApplication;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;

import static mx.com.pendulum.olintareas.Properties.TAG_DEVELOP;

public class SyncObtainInfo extends ContentProvider {

    private static final int TASKS = 1;
    private static final int CREDITS = 2;
    private static final int RESP_SEG = 3;
    private static final int RESP = 4;
    private static final int COUNT_TASKS = 5;
    private static final int COUNT_CREDITS = 6;
    private static final int USER_DB = 7;
    private static final int PENDING_DB = 8;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.mx.pendulum.olin.ObtainInfo.contentproviders", "tasks", TASKS);
        uriMatcher.addURI("com.mx.pendulum.olin.ObtainInfo.contentproviders", "credit", CREDITS);
        uriMatcher.addURI("com.mx.pendulum.olin.ObtainInfo.contentproviders", "responsable/seg", RESP_SEG);
        uriMatcher.addURI("com.mx.pendulum.olin.ObtainInfo.contentproviders", "responsable", RESP);
        uriMatcher.addURI("com.mx.pendulum.olin.ObtainInfo.contentproviders", "count/tasks", COUNT_TASKS);
        uriMatcher.addURI("com.mx.pendulum.olin.ObtainInfo.contentproviders", "count/credits", COUNT_CREDITS);
        uriMatcher.addURI("com.mx.pendulum.olin.ObtainInfo.contentproviders", "db/user", USER_DB);
        uriMatcher.addURI("com.mx.pendulum.olin.ObtainInfo.contentproviders", "db/pending", PENDING_DB);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper.getHelper(ContextApplication.getAppContext());
        Cursor c = null;
        String query;
        switch (uriMatcher.match(uri)) {
            case COUNT_TASKS:
                query = "select \n" +
                        "(SELECT count(\"updated\") from Tsk_TareaDTO WHERE \"updated\"= 1) + \n" +
                        "(SELECT count(\"updated\") from Tsk_seguimientoTarea WHERE \"updated\"= 1) \n" +
                        " modificados ,\n" +
                        " (SELECT count(\"updated\") from Tsk_TareaDTO) +\n" +
                        "(SELECT count(\"updated\") from Tsk_seguimientoTarea) " +
                        "todos;";
                c = userDatabaseHelper.getReadableDatabase().rawQuery(query, null);
                if (c.moveToFirst())
                    Log.d(TAG_DEVELOP, "Datos Encontrados en ContentProvider");
                break;
            case COUNT_CREDITS:
                query = "select (SELECT count(\"updated\") from Credit  \n" +
                        "WHERE \"updated\"= 1) modificados , (SELECT count(\"updated\") from Credit) todos";
                c = userDatabaseHelper.getReadableDatabase().rawQuery(query, null);
                if (c.moveToFirst())
                    Log.d(TAG_DEVELOP, "Datos Encontrados en ContentProvider");
                break;
            case USER_DB:
                query = "SELECT username FROM User_Data";
                c = userDatabaseHelper.getReadableDatabase().rawQuery(query, null);
                if (c.moveToFirst())
                    Log.d(TAG_DEVELOP, "Datos Encontrados en ContentProvider");
                break;
            case PENDING_DB:
                query = "SELECT idTarea FROM Tsk_NotaDTO where parcialSave = 1";
                c = userDatabaseHelper.getReadableDatabase().rawQuery(query, null);
                break;
            case CREDITS:
                // TODO HACER COSAS PARA LOS CREDITOS
                break;
            case RESP_SEG:
                query = "SELECT  question ," +
                        " response  ,idtarea " +
                        "FROM " +
                        "Tsk_responseTask " +
                        "where idtarea=" + selectionArgs[0];
                c = userDatabaseHelper.getReadableDatabase().rawQuery(query, null);
                if (c.moveToFirst())
                    Log.d(TAG_DEVELOP, "Datos Encontrados en ContentProvider");
                break;
            case RESP:
                query = "SELECT  question , " +
                        "response  ,_id " +
                        "FROM Tsk_AnswerDTO " +
                        "where tareaDTO_id = " + selectionArgs[0];
                c = userDatabaseHelper.getReadableDatabase().rawQuery(query, null);
                if (c.moveToFirst())
                    Log.d(TAG_DEVELOP, "Datos Encontrados en ContentProvider");
                break;
            case TASKS:
                //Cursor c = db.query(TABLA_CLIENTES, projection, where, selectionArgs, null, null, sortOrder);
                if (selectionArgs != null && selectionArgs.length > 0) {
                    long fecha = Long.parseLong(selectionArgs[0]);
                    int opc = Integer.parseInt(selectionArgs[1]);
                    String credit = selectionArgs[2];
                    String search_string = selectionArgs[3];
                    String where = " and ";
                    if (opc == 1) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, 23);
                        cal.set(Calendar.MINUTE, 59);
                        cal.set(Calendar.SECOND, 59);
                        cal.set(Calendar.MILLISECOND, 999);
                        long day = cal.getTimeInMillis() / 1000;
                        where += " FechaCompromiso >= " + fecha + " AND FechaCompromiso <= " + day + " ";
                    } else if (opc == 2) {
                        where += " FechaCompromiso < " + fecha + " ";
                    } else if (opc == 3) {
                        where = "";
                    } else if (opc == 4) {
                        where += " FechaCompromiso = " + fecha + " ";
                    } else if (opc == 5) {
                        where += " estado = 3 ";
                    } else if (opc == 6) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        long monday = cal.getTimeInMillis() / 1000;
                        cal.set(Calendar.HOUR_OF_DAY, 23);
                        cal.set(Calendar.MINUTE, 59);
                        cal.set(Calendar.SECOND, 59);
                        cal.set(Calendar.MILLISECOND, 999);
                        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                        cal.add(Calendar.DAY_OF_YEAR, 7);
                        long sunday = cal.getTimeInMillis() / 1000;
                        where += " FechaCompromiso >= " + monday + " AND FechaCompromiso <= " + sunday + " ";
                    }
                    try {
                        query = "select \n" +
                                " case WHEN estado = 3 then 'cerrada'\n" +
                                "      WHEN estado = 7 THEN 'incorrecto'\n" +
                                "      WHEN estado in (1,2,4,5) THEN \n" +
                                "            CASE WHEN date(FechaCompromiso, 'unixepoch', 'localtime')  >= (strftime('%Y-%m-%d', 'now'))\n" +
                                "                 THEN 'tiempo'\n" +
                                "                 ELSE 'vencida' \n" +
                                "            END\n" +
                                " END AS 'estatus',\n" +
                                "* from \n" +
                                "(SELECT \n" +
                                "			ResNombre,\n" +
                                "			descEstado,\n" +
                                "			De,\n" +
                                "           FechaOriginal,\n" +
                                "			Credito,\n" +
                                "			SolNombre,\n" +
                                "			TipotareaDesc,\n" +
                                "			Asunto,\n" +
                                "           FechaCompromiso,\n" +
                                "			Descripcion,\n" +
                                "			Estado,\n" +
                                "			idSubtipo,\n" +
                                "			id,\n" +
                                "			juicio,\n" +
                                "			tipoTarea,\n" +
                                "			updated,\n" +
                                "			1   isSeguimiento,\n" +
                                " CASE WHEN (SELECT sum(T.parcialSave) FROM Tsk_NotaDTO T where T.idTarea = Tsk_seguimientoTarea.id)  is null\n" +
                                "                 THEN 0\n" +
                                "                 ELSE (SELECT sum(T.parcialSave) FROM Tsk_NotaDTO T where T.idTarea = Tsk_seguimientoTarea.id)\n" +
                                "            END  as notaParcialSave,\n" +
                                "            deudorNombre\n" +
                                "FROM Tsk_seguimientoTarea\n" +
                                "UNION\n" +
                                "SELECT \n" +
                                "	   responsable			 ResNombre,\n" +
                                "      estado                descEstado,\n" +
                                "	   de					 De,\n" +
                                "	   (select epoch from Tsk_EpochDate where _id = horaInicio_id)				FechaOriginal,\n" +
                                "      credito				 Credito,\n" +
                                "      usuAltaNombre		 SolNombre,\n" +
                                "      tipoTareaDesc		 TipotareaDesc,\n" +
                                "      asunto				 Asunto,\n" +
                                "	   (select epoch from Tsk_EpochDate where _id = fechaCompromiso_id)			FechaCompromiso,\n" +
                                "      descripcion			 Descripcion,\n" +
                                "  	   idEstado				 Estado,\n" +
                                "	   subTipo               idSubtipo,\n" +
                                "	   _id                   id, \n" +
                                "	   juicio                juicio,\n" +
                                "	   tipoTarea             tipoTarea,\n" +
                                "			updated,\n" +
                                "	   0                     isSeguimiento,\n" +
                                " CASE WHEN (SELECT sum(T.parcialSave) FROM Tsk_NotaDTO T where T.idTarea = Tsk_TareaDTO._id)  is null\n" +
                                "                 THEN 0\n" +
                                "                 ELSE (SELECT sum(T.parcialSave) FROM Tsk_NotaDTO T where T.idTarea = Tsk_TareaDTO._id)\n" +
                                "            END  as notaParcialSave,\n" +
                                "      ''              deudorNombre\n" +
                                " FROM Tsk_TareaDTO \n" +
                                ")" +
                                "where  not (estado = 2 and updated = 0 and notaParcialSave = 0)  " + where;
                        if (credit != null) {
                            query += " AND (credito = '" + credit + "' OR juicio = '" + credit + "') ";
                        }
                        if (search_string != null && !search_string.isEmpty()) {
                            query += " and \n" +
                                    "(\n" +
                                    "       ResNombre           like '%" + search_string + "%' OR\n" +
                                    "       descEstado          like '%" + search_string + "%' OR\n" +
                                    "       De                  like '%" + search_string + "%' OR\n" +
                                    "       FechaOriginal       like '%" + search_string + "%' OR\n" +
                                    "       Credito             like '%" + search_string + "%' OR\n" +
                                    "       SolNombre           like '%" + search_string + "%' OR\n" +
                                    "       TipotareaDesc       like '%" + search_string + "%' OR\n" +
                                    "       Asunto              like '%" + search_string + "%' OR\n" +
                                    "       FechaCompromiso     like '%" + search_string + "%' OR\n" +
                                    "       Descripcion         like '%" + search_string + "%' OR\n" +
                                    "       Estado              like '%" + search_string + "%' OR\n" +
                                    "       idSubtipo           like '%" + search_string + "%' OR\n" +
                                    "       id                  like '%" + search_string + "%' OR\n" +
                                    "       juicio              like '%" + search_string + "%' OR\n" +
                                    "       tipoTarea           like '%" + search_string + "%' OR\n" +
                                    "       updated             like '%" + search_string + "%' OR\n" +
                                    "       isSeguimiento       like '%" + search_string + "%' OR\n" +
                                    "       estatus             like '%" + search_string + "%' \n" +
                                    ") ";
                        }
                        query += " order by notaParcialSave desc ,updated desc , fechaCompromiso ASC,id ASC";
                        c = userDatabaseHelper.getReadableDatabase().rawQuery(query, null);
                        if (c.moveToFirst())
                            Log.d(TAG_DEVELOP, "Datos Encontrados en ContentProvider");
                    } catch (Exception sqle) {
                        Log.e(TAG_DEVELOP, sqle.getMessage(), sqle);
                    } finally {
                        userDatabaseHelper.close();
                    }
                }
                break;
        }
        return c;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "tasks";
            case CREDITS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "credit";
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}