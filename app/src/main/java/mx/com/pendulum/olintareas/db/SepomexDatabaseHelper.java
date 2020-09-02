package mx.com.pendulum.olintareas.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import mx.com.pendulum.olintareas.db.dao.SepoCP;
import mx.com.pendulum.olintareas.db.dao.SepoColony;
import mx.com.pendulum.olintareas.db.dao.SepoMuni;
import mx.com.pendulum.olintareas.db.dao.SepoStates;

public class SepomexDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = SepomexDatabaseHelper.class.getSimpleName();
    private static String DATABASE_PATH;// "/data/data/mx.com.pendulum.olin/databases/";
    public static final String DATABASE_NAME = "SEPOMEXSMB.db";
    public static final String ASSET_NAME = "SEPOMEXSMB.db";
    private static final int DATABASE_VERSION = 3;
    public static final String DATABASE_FOREIGN_ATTACH_NAME = "SEPOMEXSMB";
    private static SepomexDatabaseHelper helper;
    private static final AtomicInteger usageCounter = new AtomicInteger(0);
    private static Context mContext;

    private SepomexDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized SepomexDatabaseHelper getHelper(Context context) {
        mContext = context;
        DATABASE_PATH = "/data/data/" + mContext.getPackageName()
                + "/databases/";
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            DATABASE_PATH = context.getApplicationInfo().dataDir
                    + "/databases/";
        } else {
            DATABASE_PATH = "/data/data/" + context.getPackageName()
                    + "/databases/";
        }
        if (helper == null)
            helper = new SepomexDatabaseHelper(context);
        usageCounter.incrementAndGet();
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade");
    }

    @Override
    public void close() {
        if (usageCounter.decrementAndGet() == 0) {
            super.close();
            helper = null;
        }
    }

    public void prepareDataBase() {
        if (checkDataBase()) {
            Log.i(TAG, "Database exists.");
        } else {
            Log.w(TAG, "Copying database from assets...");
            InputStream inputStream = null;
            try {
                inputStream = mContext.getAssets().open(ASSET_NAME);
                copyDataBase(inputStream);
            } catch (IOException ioe) {
                Log.e(TAG, ioe.getMessage(), ioe);
            } finally {
                if (inputStream != null)
                    try {
                        inputStream.close();
                    } catch (IOException ioe) {
                        Log.e(TAG, ioe.getMessage(), ioe);
                    }
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String path = DATABASE_PATH + DATABASE_NAME;
            File file = new File(path);
            if (file.exists()) {
                checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
            }
        } catch (Exception sqle) {
            // database does't exist yet.
        }
        if (checkDB != null)
            if (checkDB.isOpen())
                checkDB.close();
        return checkDB != null;
    }

    private void copyDataBase(InputStream dbInputStream) {
        OutputStream dbOutputStream = null;
        try {
            String outFileName = DATABASE_PATH + DATABASE_NAME;
            dbOutputStream = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dbInputStream.read(buffer)) > 0)
                dbOutputStream.write(buffer, 0, length);
            dbOutputStream.flush();
        } catch (IOException ioe) {
            Log.i(TAG, ioe.getMessage(), ioe);
        } finally {
            if (dbOutputStream != null) {
                try {
                    dbOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getFullyQualifiedName(String tableName,
                                               String fieldName, boolean attached) {
        String comma = "`";
        String dot = ".";
        StringBuilder fullName = new StringBuilder();
        if (attached)
            fullName.append(SepomexDatabaseHelper.DATABASE_FOREIGN_ATTACH_NAME)
                    .append(dot);
        fullName.append(comma).append(tableName).append(comma).append(dot)
                .append(comma).append(fieldName).append(comma);
        return fullName.toString();
    }

    public List<SepoStates> obtainAllStates() {
        ArrayList<SepoStates> statesList = new ArrayList<>();
        String slc = "select description, name, _id FROM STATE ORDER BY _id ASC";
        Cursor cursor;
        cursor = getReadableDatabase().rawQuery(slc, null);
        if (cursor.moveToFirst()) {
            SepoStates state;
            do {
                state = new SepoStates();
                state.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                state.setName(cursor.getString(cursor.getColumnIndex("name")));
                state.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                statesList.add(state);
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed())
            cursor.close();
        return statesList;
    }

    public List<SepoCP> searchByCp(String sCp) {
        ArrayList<SepoCP> cpList = new ArrayList<>();
        String slc = "SELECT _id, id_state, id_municipality, name FROM SETTLEMENT WHERE zip_code = " + "'" + sCp + "'";
        Cursor cursor;
        cursor = getReadableDatabase().rawQuery(slc, null);
        if (cursor.moveToFirst()) {
            SepoCP cp;
            do {
                cp = new SepoCP();
                cp.setName(cursor.getString(cursor.getColumnIndex("name")));
                cp.setId_state(cursor.getInt(cursor.getColumnIndex("id_state")));
                cp.setId_muni(cursor.getInt(cursor.getColumnIndex("id_municipality")));
                cp.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                cpList.add(cp);
            } while (cursor.moveToNext());

        }
        if (!cursor.isClosed())
            cursor.close();
        return cpList;
    }

    public List<SepoColony> searchColonyByCp(String sCp) {
        ArrayList<SepoColony> colonyList = new ArrayList<>();
        String slc = "SELECT _id, id_state, id_municipality, name FROM SETTLEMENT WHERE zip_code = " + "'" + sCp + "'";
        Cursor cursor;
        cursor = getReadableDatabase().rawQuery(slc, null);
        SepoCP cp = new SepoCP();
        if (cursor.moveToFirst()) {
            cp.setName(cursor.getString(cursor.getColumnIndex("name")));
            cp.setId_state(cursor.getInt(cursor.getColumnIndex("id_state")));
            cp.setId_muni(cursor.getInt(cursor.getColumnIndex("id_municipality")));
            cp.set_id(cursor.getInt(cursor.getColumnIndex("_id")));

            String slc2 = "select name, _id, id_state, id_municipality FROM SETTLEMENT WHERE id_state = " + cp.getId_state() + " AND id_municipality = " + cp.getId_muni() + " AND zip_code = " + "'" + sCp + "' ORDER BY name ASC";
            cursor = getReadableDatabase().rawQuery(slc2, null);
            SepoColony colony = new SepoColony();
            colony.set_id(0);
            colony.setName("SELECCIONAR FRACC. / COLONIA / RANCHERIA / ETC.");
            colony.setId_state(0);
            colony.setId_municipality(0);
            colonyList.add(colony);
            if (cursor.moveToFirst()) {
                do {
                    colony = new SepoColony();
                    colony.setName(cursor.getString(cursor.getColumnIndex("name")));
                    colony.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                    colony.setId_state(cursor.getInt(cursor.getColumnIndex("id_state")));
                    colony.setId_municipality(cursor.getInt(cursor.getColumnIndex("id_municipality")));
                    colonyList.add(colony);
                } while (cursor.moveToNext());
            }
        }
        if (!cursor.isClosed())
            cursor.close();
        return colonyList;
    }

    public List<SepoMuni> obtainAllMuni(int id_state) {
        ArrayList<SepoMuni> muniList = new ArrayList<>();
        String slc = "select description, name, _id, id_state FROM MUNICIPALITY WHERE id_state = " + id_state + " ORDER BY name ASC";
        Cursor cursor;
        cursor = getReadableDatabase().rawQuery(slc, null);
        SepoMuni muni = new SepoMuni();
        muni.set_id(0);
        muni.setName("SELECCIONAR MUNICIPIO");
        muni.setDescription("SELECCIONAR MUNICIPIO");
        muni.setId_state(0);
        muniList.add(muni);
        if (cursor.moveToFirst()) {
            do {
                muni = new SepoMuni();
                muni.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                muni.setName(cursor.getString(cursor.getColumnIndex("name")));
                muni.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                muni.setId_state(cursor.getInt(cursor.getColumnIndex("id_state")));
                muniList.add(muni);
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed())
            cursor.close();
        return muniList;
    }

    public List<SepoColony> obtainAllColony(int idMuni, int idState) {
        ArrayList<SepoColony> colonyList = new ArrayList<>();
        String slc = "select name, _id, id_state, id_municipality FROM SETTLEMENT WHERE id_state = " + idState + " AND id_municipality = " + idMuni + " ORDER BY name ASC";
        Cursor cursor;
        cursor = getReadableDatabase().rawQuery(slc, null);
        SepoColony colony = new SepoColony();
        colony.set_id(0);
        colony.setName("SELECCIONAR FRACC. / COLONIA / RANCHERIA / ETC.");
        colony.setId_state(0);
        colony.setId_municipality(0);
        colonyList.add(colony);
        if (cursor.moveToFirst()) {
            do {
                colony = new SepoColony();
                colony.setName(cursor.getString(cursor.getColumnIndex("name")));
                colony.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                colony.setId_state(cursor.getInt(cursor.getColumnIndex("id_state")));
                colony.setId_municipality(cursor.getInt(cursor.getColumnIndex("id_municipality")));
                colonyList.add(colony);
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed())
            cursor.close();
        return colonyList;
    }
}