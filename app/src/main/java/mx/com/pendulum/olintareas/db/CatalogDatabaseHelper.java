package mx.com.pendulum.olintareas.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.concurrent.atomic.AtomicInteger;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.SubClasifica.CatTskSubclasificaTareas;
import mx.com.pendulum.olintareas.dto.SubClasifica.RelOptionQuery;
import mx.com.pendulum.olintareas.dto.SubClasifica.RelSubClasificaQuestions;
import mx.com.pendulum.olintareas.dto.fromOptions.RelOptionQueryFO;
import mx.com.pendulum.olintareas.dto.fromOptions.RelOptionQuestionsFO;
import mx.com.pendulum.olintareas.dto.fromOptions.AutocompleteFO;
import mx.com.pendulum.olintareas.dto.fromOptions.DocumentsFO;
import mx.com.pendulum.olintareas.dto.fromOptions.OptionsFO;
import mx.com.pendulum.olintareas.dto.fromOptions.QuestionsFO;
import mx.com.pendulum.olintareas.dto.tareasV2.Autocomplete;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogActividad;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogCases;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogCuentas;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogEstadosNota;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogPrioridades;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogSubComandoNota;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogSubTipos;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogTiposTarea;
import mx.com.pendulum.olintareas.dto.tareasV2.ComandoSubcomandodto;
import mx.com.pendulum.olintareas.dto.tareasV2.Document;
import mx.com.pendulum.olintareas.dto.tareasV2.NotasV2Form;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.OptionsQuestionsForm;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.dto.tareasV2.ResponsablesDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.TareasV2Form;
import mx.com.pendulum.olintareas.interfaces.GenericDaoHelper;

public class CatalogDatabaseHelper extends OrmLiteSqliteOpenHelper implements GenericDaoHelper {

    private static final String TAG = CatalogDatabaseHelper.class.getSimpleName();
    public static final String DATABASE_NAME = Properties.pathDataBase + "catalog.db";
    public static final String DATABASE_FOREIGN_ATTACH_NAME = "catalog_db";
    private static CatalogDatabaseHelper helper;
    private static final AtomicInteger usageCounter = new AtomicInteger(0);

    private CatalogDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, context.getResources().getInteger(R.integer.verion_cat_actual));
    }

    public static synchronized CatalogDatabaseHelper getHelper(Context context) {
        if (helper == null)
            helper = new CatalogDatabaseHelper(context);
        usageCounter.incrementAndGet();
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        createTableIfNotExists(connectionSource, TareasV2Form.class);
        createTableIfNotExists(connectionSource, NotasV2Form.class);
        createTableIfNotExists(connectionSource, CatalogActividad.class);
        createTableIfNotExists(connectionSource, CatalogTiposTarea.class);
        createTableIfNotExists(connectionSource, CatalogPrioridades.class);
        createTableIfNotExists(connectionSource, CatalogSubTipos.class);
        createTableIfNotExists(connectionSource, CatalogEstadosNota.class);
        createTableIfNotExists(connectionSource, CatalogSubComandoNota.class);
        createTableIfNotExists(connectionSource, ResponsablesDTO.class);
        createTableIfNotExists(connectionSource, OptionsQuestionsForm.class);
        createTableIfNotExists(connectionSource, CatalogCuentas.class);
        createTableIfNotExists(connectionSource, CatalogCases.class);
        createTableIfNotExists(connectionSource, ComandoSubcomandodto.class);
        createTableIfNotExists(connectionSource, CatTskSubclasificaTareas.class);
        createTableIfNotExists(connectionSource, RelSubClasificaQuestions.class);
        createTableIfNotExists(connectionSource, Questions.class);
        createTableIfNotExists(connectionSource, Document.class);
        createTableIfNotExists(connectionSource, Options.class);
        createTableIfNotExists(connectionSource, RelOptionQuery.class);
        createTableIfNotExists(connectionSource, RelOptionQuestionsFO.class);
        createTableIfNotExists(connectionSource, Autocomplete.class);
        createTableIfNotExists(connectionSource, AutocompleteFO.class);
        createTableIfNotExists(connectionSource, DocumentsFO.class);
        createTableIfNotExists(connectionSource, OptionsFO.class);
        createTableIfNotExists(connectionSource, QuestionsFO.class);
        createTableIfNotExists(connectionSource, RelOptionQuestionsFO.class);
        createTableIfNotExists(connectionSource, RelOptionQueryFO.class);
        // Resolucion de preguntas
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        //TableUtils.createTableIfNotExists(connectionSource, CatalogoServiciosmotivo.class);
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

    public <T> void createTableIfNotExists(ConnectionSource connectionSource, Class<T> obj) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        if (usageCounter.decrementAndGet() == 0) {
            super.close();
            dao = null;
            //Dao = null;
            helper = null;
        }
    }

    public void clearTables() {
        clearTable(connectionSource, TareasV2Form.class);
        clearTable(connectionSource, NotasV2Form.class);
        clearTable(connectionSource, CatalogActividad.class);
        clearTable(connectionSource, CatalogTiposTarea.class);
        clearTable(connectionSource, CatalogPrioridades.class);
        clearTable(connectionSource, CatalogSubTipos.class);
        clearTable(connectionSource, CatalogEstadosNota.class);
        clearTable(connectionSource, CatalogSubComandoNota.class);
        clearTable(connectionSource, ResponsablesDTO.class);
        clearTable(connectionSource, OptionsQuestionsForm.class);
        clearTable(connectionSource, CatalogCuentas.class);
        clearTable(connectionSource, ComandoSubcomandodto.class);
        clearTable(connectionSource, CatTskSubclasificaTareas.class);
        clearTable(connectionSource, RelSubClasificaQuestions.class);
        clearTable(connectionSource, Questions.class);
        clearTable(connectionSource, Document.class);
        clearTable(connectionSource, Options.class);
        clearTable(connectionSource, RelOptionQuery.class);
        clearTable(connectionSource, RelOptionQuestionsFO.class);
        clearTable(connectionSource, Autocomplete.class);
        clearTable(connectionSource, AutocompleteFO.class);
        clearTable(connectionSource, DocumentsFO.class);
        clearTable(connectionSource, OptionsFO.class);
        clearTable(connectionSource, QuestionsFO.class);
        clearTable(connectionSource, RelOptionQuestionsFO.class);
        clearTable(connectionSource, RelOptionQueryFO.class);
    }

    private static <T> void clearTable(ConnectionSource connectionSource, Class<T> dataClass) {
        try {
            TableUtils.clearTable(connectionSource, dataClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Dao<? extends Object, Long> dao = null;

    /**
     * Returns the Database Access Object (DAO) for our classes. It will create
     * it or just give the cached value.
     */
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
}