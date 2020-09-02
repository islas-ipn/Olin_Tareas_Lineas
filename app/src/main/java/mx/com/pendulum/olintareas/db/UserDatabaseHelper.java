package mx.com.pendulum.olintareas.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.dao.UserDataDaoImpl;
import mx.com.pendulum.olintareas.dto.Lw;
import mx.com.pendulum.olintareas.dto.SyncDataOutObject;
import mx.com.pendulum.olintareas.dto.UserData;
import mx.com.pendulum.olintareas.dto.UserPermissions;
import mx.com.pendulum.olintareas.dto.UserSession;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogCases;
import mx.com.pendulum.olintareas.dto.tareasV2.EpochDate;
import mx.com.pendulum.olintareas.dto.tareasV2.FileUploadDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NotaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.TareaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogActividad;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogCuentas;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogEstadosNota;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogPrioridades;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogSubTipos;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogTiposTarea;
import mx.com.pendulum.olintareas.dto.tareasV2.NotasDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NotasV2Form;
import mx.com.pendulum.olintareas.dto.tareasV2.NoteResponseDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.OptionsQuestionsForm;
import mx.com.pendulum.olintareas.dto.tareasV2.ResponsablesDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.ResponseTask;
import mx.com.pendulum.olintareas.dto.tareasV2.SeguimientoTarea;
import mx.com.pendulum.olintareas.dto.tareasV2.TareasV2Form;
import mx.com.pendulum.olintareas.dto.tareasV2.TemporalForm;
import mx.com.pendulum.olintareas.interfaces.GenericDaoHelper;

public class UserDatabaseHelper extends OrmLiteSqliteOpenHelper implements GenericDaoHelper {
    private static final String TAG = UserDatabaseHelper.class.getSimpleName();

    public static final String DATABASE_NAME = Properties.pathDataBase
            + "user.db";

    private static UserDatabaseHelper helper;
    private static final AtomicInteger usageCounter = new AtomicInteger(0);

    private UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, context.getResources().getInteger(R.integer.verion_user_actual));
    }

    public static synchronized UserDatabaseHelper getHelper(Context context) {
        if (helper == null)
            helper = new UserDatabaseHelper(context);

        usageCounter.incrementAndGet();
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        createTableIfNotExists(connectionSource, UserData.class);
        createTableIfNotExists(connectionSource, UserSession.class);
        createTableIfNotExists(connectionSource, UserPermissions.class);
        createTableIfNotExists(connectionSource, TareaDTO.class);
        createTableIfNotExists(connectionSource, NotaDTO.class);
        createTableIfNotExists(connectionSource, TareasV2Form.class);
        createTableIfNotExists(connectionSource, NotasV2Form.class);
        createTableIfNotExists(connectionSource, CatalogActividad.class);
        createTableIfNotExists(connectionSource, CatalogTiposTarea.class);
        createTableIfNotExists(connectionSource, CatalogPrioridades.class);
        createTableIfNotExists(connectionSource, CatalogSubTipos.class);
        createTableIfNotExists(connectionSource, CatalogCuentas.class);
        createTableIfNotExists(connectionSource, CatalogEstadosNota.class);
        createTableIfNotExists(connectionSource, SeguimientoTarea.class);
        createTableIfNotExists(connectionSource, ResponseTask.class);
        createTableIfNotExists(connectionSource, NoteResponseDTO.class);
        createTableIfNotExists(connectionSource, NotasDTO.class);
        createTableIfNotExists(connectionSource, ResponsablesDTO.class);
        createTableIfNotExists(connectionSource, OptionsQuestionsForm.class);
        createTableIfNotExists(connectionSource, EpochDate.class);
        createTableIfNotExists(connectionSource, FileUploadDTO.class);
        createTableIfNotExists(connectionSource, AnswerDTO.class);
        createTableIfNotExists(connectionSource, Lw.class);
        createTableIfNotExists(connectionSource, TemporalForm.class);
        createTableIfNotExists(connectionSource, CatalogCases.class);
        createTriggers(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                createTableIfNotExists(connectionSource, CatalogCases.class);
                break;
            case 2:
        }
    }

    private void execSQL(SQLiteDatabase db, String query) {
        try {
            db.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private <T> void createTableIfNotExists(ConnectionSource connectionSource, Class<T> obj) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> void clearTable(ConnectionSource connectionSource, Class<T> dataClass) {
        try {
            TableUtils.clearTable(connectionSource, dataClass);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createTriggers(SQLiteDatabase database) {
    }

    private void createTrigger(SQLiteDatabase database, String tableName, String columnCount, String columnToValidate) {
        String triggerName = tableName + "_" + columnCount + "_" + "InTrigger";
        String trigger =
                "CREATE TRIGGER " + triggerName + " AFTER INSERT ON " + tableName + " \n" +
                        "    BEGIN \n" +
                        "        UPDATE " + TableNames.USER_SESSION + " SET " + columnCount + " = \n" +
                        "            CASE \n" +
                        "                 WHEN (new." + columnToValidate + " = 1) \n" +
                        "                 then (SELECT " + columnCount + " + 1 FROM " + TableNames.USER_SESSION + " LIMIT 1)  \n" +
                        "                 ELSE (SELECT " + columnCount + "  FROM " + TableNames.USER_SESSION + " LIMIT 1)\n" +
                        "            END;\n" +
                        "    END;";
        //  Log.i(TAG, "Create trigger \n" + trigger);
        try {
            database.execSQL("DROP TRIGGER " + triggerName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            database.execSQL(trigger);
        } catch (Exception ignored) {
        }
        triggerName = tableName + "_" + columnCount + "_" + "UpTrigger";
        trigger =
                "    CREATE TRIGGER " + triggerName + " AFTER UPDATE ON " + tableName + " \n" +
                        "    BEGIN \n" +
                        "        UPDATE " + TableNames.USER_SESSION + " SET " + columnCount + " = \n" +
                        "            CASE \n" +
                        "                 WHEN \n" +
                        "                    (new." + columnToValidate + " = 1) \n" +
                        "                 THEN \n" +
                        "                 (\n" +
                        "                     CASE \n" +
                        "                      when (new." + columnToValidate + " = old." + columnToValidate + ")\n" +
                        "                      then (SELECT " + columnCount + "  FROM " + TableNames.USER_SESSION + " LIMIT 1)\n" +
                        "                      ELSE (SELECT " + columnCount + " + 1 FROM " + TableNames.USER_SESSION + " LIMIT 1)\n" +
                        "                      END\n" +
                        "                 )\n" +
                        "                 ELSE\n" +
                        "                    (SELECT " + columnCount + "  FROM " + TableNames.USER_SESSION + " LIMIT 1)\n" +
                        "                 END;\n" +
                        "    END;";
        try {
            database.execSQL("DROP TRIGGER " + triggerName);
        } catch (Exception ignored) {
            // e.printStackTrace();
        }
        try {
            database.execSQL(trigger);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void close() {
        if (usageCounter.decrementAndGet() == 0) {
            super.close();
            userDataDao = null;
            userSessionDao = null;
            dao = null;
            helper = null;

        }
    }

    public void clearTables() {
        clearTable(getConnectionSource(), UserData.class);
        clearTable(getConnectionSource(), UserSession.class);
        clearTable(getConnectionSource(), UserPermissions.class);
        clearTable(getConnectionSource(), TareaDTO.class);
        clearTable(getConnectionSource(), NotaDTO.class);
        clearTable(getConnectionSource(), TareasV2Form.class);
        clearTable(getConnectionSource(), NotasV2Form.class);
        clearTable(getConnectionSource(), CatalogActividad.class);
        clearTable(getConnectionSource(), CatalogTiposTarea.class);
        clearTable(getConnectionSource(), CatalogPrioridades.class);
        clearTable(getConnectionSource(), CatalogSubTipos.class);
        clearTable(getConnectionSource(), CatalogCuentas.class);
        clearTable(getConnectionSource(), CatalogEstadosNota.class);
        clearTable(getConnectionSource(), SeguimientoTarea.class);
        clearTable(getConnectionSource(), ResponseTask.class);
        clearTable(getConnectionSource(), NoteResponseDTO.class);
        clearTable(getConnectionSource(), NotasDTO.class);
        clearTable(getConnectionSource(), ResponsablesDTO.class);
        clearTable(getConnectionSource(), OptionsQuestionsForm.class);
        clearTable(getConnectionSource(), TemporalForm.class);
    }

    public ArrayList<?> getUpdatedResourceList(Class<?> clazz,
                                               String updatedFieldName, String username) {
        Dao<?, Integer> dao = null;
        ArrayList<SyncDataOutObject> updatedObjects = null;
        try {
            dao = (Dao<?, Integer>) getDao(clazz);
            QueryBuilder<?, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(updatedFieldName, true);
            PreparedQuery preparedQuery = queryBuilder.prepare();
            updatedObjects = (ArrayList<SyncDataOutObject>) dao
                    .query(preparedQuery);
            for (SyncDataOutObject object : updatedObjects)
                object.setUsername(username);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }


        return updatedObjects;
    }

    private Dao<? extends Object, Long> dao = null;

    @SuppressWarnings("unchecked")
    @Override
    public Dao<? extends Object, Long> getOlinDao(
            Class<? extends Object> objectClass) {

        try {
            dao = (Dao<? extends Object, Long>) getDao(objectClass);
        } catch (java.sql.SQLException e) {
            Log.e(TAG, "Can't get DAO.", e);
            throw new RuntimeException(e);
        }

        return dao;
    }

    private Dao<UserSession, Long> userSessionDao = null;
    private UserDataDaoImpl userDataDao = null;

    public Dao<UserSession, Long> getUserSessionDao() throws SQLException {
        if (userSessionDao == null) {
            try {
                userSessionDao = getDao(UserSession.class);
            } catch (java.sql.SQLException e) {
                Log.e(TAG, "Can't get DAO.", e);
                throw new RuntimeException(e);
            }
        }
        return userSessionDao;
    }

    public UserDataDaoImpl getUserDataDao() throws SQLException {
        if (userDataDao == null) {
            try {
                userDataDao = getDao(UserData.class);
            } catch (java.sql.SQLException e) {
                Log.e(TAG, "Can't get DAO.", e);
                throw new RuntimeException(e);
            }
        }
        return userDataDao;
    }

}
