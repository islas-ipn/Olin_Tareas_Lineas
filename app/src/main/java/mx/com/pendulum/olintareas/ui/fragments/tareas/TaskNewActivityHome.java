package mx.com.pendulum.olintareas.ui.fragments.tareas;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.config.util.ContextApplication;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.SyncDataOutObject;
import mx.com.pendulum.olintareas.dto.tareasV2.NotaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NotasDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.ResponseTask;
import mx.com.pendulum.olintareas.dto.tareasV2.SeguimientoTarea;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.activities.tareas.AddNotaActivity;
import mx.com.pendulum.olintareas.ui.activities.tareas.AddTareaActivity;
import mx.com.pendulum.olintareas.ui.activities.tareas.ShowNotasActivity;
import mx.com.pendulum.olintareas.ui.adapter.tareas.AdapterSeguimientoTarea;
import mx.com.pendulum.olintareas.ui.parents.FragmentParent;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.Util;

import static mx.com.pendulum.olintareas.Properties.TAG_DEVELOP;

public class TaskNewActivityHome extends FragmentParent implements //DatePickerListener,
        LoaderManager.LoaderCallbacks<List<SeguimientoTarea>>,
        Interfaces.OnResponse<Object>, SearchView.OnQueryTextListener {

    private static final String TAG = TaskNewActivityHome.class.getSimpleName();
    private static final String OPC_QUERY = "OPC_1";
    private static final String OPC_TIME = "OPC_TIME";
    private static final String OPC_TITLE = "OPC_TITLE";
    private static final String SEARCH_TEXT = "SEARCH_TEXT";

    private static final int LOADER_ID = 255114403;
    private RecyclerView listView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.task_new_activity, null);
        listView = v.findViewById(R.id.lv_tsk_tareas);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(clicFlotan);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle b = getArguments();
        boolean isFromAnotherApp = b.getBoolean(Properties.EXTRA_IS_FROM_ANOTHER_APP, false);
        if (isFromAnotherApp) {
            SharedPreferences sharedPreferences = getActivity()
                    .getSharedPreferences(Properties.SHARED_FROM_ANOTHER_APP, Context.MODE_PRIVATE);
            boolean isFinishedTask = sharedPreferences.getBoolean(Properties.SHARED_IS_FINISHED_TASK, false);
            if (!isFinishedTask) {
                SeguimientoTarea seg = (SeguimientoTarea) b.getSerializable(Properties.EXTRA_SERIAL_SEG);
                this.onResponse(0, seg);
                return;
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Properties.SHARED_IS_FINISHED_TASK, false);
                editor.apply();
                getActivity().finish();
            }
        }
        int op = b.getInt(OPC_QUERY, 6);
        Calendar cal = Calendar.getInstance();
        long time = b.getLong(OPC_TIME, cal.getTimeInMillis() / 1000);
        String title = b.getString(OPC_TITLE, "# Tareas de la semana");
        if (op == 6) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            time = cal.getTimeInMillis() / 1000;
        }
        searchTsk(time, op, title);
    }

    View.OnClickListener clicFlotan = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intentDocs = new Intent(getActivity(), AddTareaActivity.class);
            if (getArguments() != null) {
                intentDocs.putExtras(getArguments());
            }
            intentDocs.putExtra(FragmentParent.IS_HOME_ENABLED, true);
            intentDocs.putExtra(FragmentParent.TITLE_FRAGMENT, "Agregar tarea");
            startActivity(intentDocs);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @Override
    public Loader<List<SeguimientoTarea>> onCreateLoader(int id, Bundle args) {
        return new ListLoader(ContextApplication.getAppContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<SeguimientoTarea>> loader, List<SeguimientoTarea> data) {
        ActionBar a = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (a != null && a.getTitle() != null) {
            String title = a.getTitle().toString();
            titleActionBar(title.replace("#", "" + data.size()));
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ContextApplication.getAppContext());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(new AdapterSeguimientoTarea(getActivity(), TaskNewActivityHome.this, 0, data));
    }

    @Override
    public void onLoaderReset(Loader<List<SeguimientoTarea>> loader) {
    }

    @Override
    public void onResponse(int handlerCode, Object o) {
        if (handlerCode == 0) {
            SeguimientoTarea seguimientoTarea = (SeguimientoTarea) o;
            UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getActivity());
            Integer size = null;
            List<NotasDTO> notasList;
            List<NotaDTO> notaList;
            try {
                Dao<NotasDTO, Long> dao = helper.getDao(NotasDTO.class);
                Dao<NotaDTO, Long> ndao = helper.getDao(NotaDTO.class);
                QueryBuilder<NotasDTO, Long> qb = dao.queryBuilder();
                QueryBuilder<NotaDTO, Long> nqb = ndao.queryBuilder();
                qb.where().eq(NotasDTO.COL_ID_TAREA, seguimientoTarea.getId());
                nqb.where().eq(NotaDTO.COL_ID_TAREA, seguimientoTarea.getId());
                notasList = qb.query();
                notaList = nqb.query();
                size = notasList.size() + notaList.size();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                helper.close();
            }
            Intent intentDocs;
            if (size == null || size == 0) {
                UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper.getHelper(getActivity());
                try {
                    Dao<SeguimientoTarea, Long> seguimientoTareaDao = userDatabaseHelper.getDao(SeguimientoTarea.class);
                    QueryBuilder<SeguimientoTarea, Long> queryBuilder = seguimientoTareaDao.queryBuilder();
                    queryBuilder.where().eq(SeguimientoTarea.COL_ID_TAREA, seguimientoTarea.getIdtarea());
                    SeguimientoTarea tarea = queryBuilder.queryForFirst();
                    if (tarea != null && tarea.getIdEstado() != null) {
                        if (tarea.getIdEstado() == 3) {
                            CustomDialog.showDisclaimer(getActivity(), "No se puede dar seguimiento a una tarea que ya ha sido cerrada", null);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    userDatabaseHelper.close();
                }
                if (!ShowNotasActivity.isAbleToSeguimiento(getActivity(), seguimientoTarea.getIdtarea())) {
                    return;
                }
                intentDocs = new Intent(getActivity(), AddNotaActivity.class);
                intentDocs.putExtra(FragmentParent.TITLE_FRAGMENT, "Agregar Seguimiento");
                intentDocs.putExtra(SeguimientoTarea.class.getSimpleName(), seguimientoTarea.getId());
                intentDocs.putExtra(SyncDataOutObject.COL_CREDIT_NUMBER, getArguments().getString(SyncDataOutObject.COL_CREDIT_NUMBER, null));
            } else {
                intentDocs = new Intent(getActivity(), ShowNotasActivity.class);
                intentDocs.putExtra(FragmentParent.TITLE_FRAGMENT, "Seguimiento");
                intentDocs.putExtra(SeguimientoTarea.class.getSimpleName(), seguimientoTarea.getId());
                intentDocs.putExtra(SyncDataOutObject.COL_CREDIT_NUMBER, getArguments().getString(SyncDataOutObject.COL_CREDIT_NUMBER, null));
            }
            intentDocs.putExtra(FragmentParent.IS_HOME_ENABLED, true);
            startActivity(intentDocs);
        }
    }

    private static class ListLoader extends AsyncTaskLoader<List<SeguimientoTarea>> {
        Long fecha;
        String credit;
        String search_string;
        int opc;

        ListLoader(Context context, Bundle args) {
            super(context);
            if (args != null) {
                this.fecha = args.getLong(SeguimientoTarea.FECHA_COMPPROMISO);
                this.credit = args.getString(SyncDataOutObject.COL_CREDIT_NUMBER, null);
                this.opc = args.getInt(OPC_QUERY, 0);
                this.search_string = args.getString(SEARCH_TEXT, "");
            }
        }

        @Override
        public List<SeguimientoTarea> loadInBackground() {
            List<SeguimientoTarea> listseguimiento = new ArrayList<>();
            UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper.getHelper(ContextApplication.getAppContext());
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
                String query = "select \n" +
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

                        "	   subClasifica                subClasifica,\n" +

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

                        "	   subClasifica                subClasifica,\n" +

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
                        "where " +
                        //"de in ('COBRANZA','ABA')     and " +
                        //" not (de = 'ABA'  and estado = 2 and updated = 0 and notaParcialSave = 0)  "
                        //" not (estado = 2 and updated = 0 and notaParcialSave = 0)  "
                        " not (estado = 2)  "
                        + where;//FechaCompromiso = " + fecha + " and ";
                if (credit != null) {
                    query += " AND (credito = '" + credit + "' OR juicio = '" + credit + "') ";
                }
                if (!search_string.isEmpty()) {
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
                Cursor c = userDatabaseHelper.getReadableDatabase().rawQuery(query, null);
                SeguimientoTarea dto;
                if (c.moveToFirst())
                    do {
                        dto = new SeguimientoTarea(
                                c.getString(c.getColumnIndex("ResNombre")),
                                c.getString(c.getColumnIndex("descEstado")),
                                c.getString(c.getColumnIndex("De")),
                                c.getString(c.getColumnIndex("FechaOriginal")),
                                c.getString(c.getColumnIndex("Credito")),
                                c.getString(c.getColumnIndex("SolNombre")),
                                c.getString(c.getColumnIndex("TipotareaDesc")),
                                c.getString(c.getColumnIndex("Asunto")),
                                c.getString(c.getColumnIndex("FechaCompromiso")),
                                c.getString(c.getColumnIndex("Descripcion")),
                                c.getInt(c.getColumnIndex("Estado")),
                                c.getInt(c.getColumnIndex("idSubtipo")),
                                c.getInt(c.getColumnIndex("id")),
                                c.getString(c.getColumnIndex("juicio")),
                                c.getInt(c.getColumnIndex("tipoTarea")),
                                c.getString(c.getColumnIndex("subClasifica")));
                        dto.setSeguimiento(c.getInt(c.getColumnIndex("isSeguimiento")) != 0);
                        dto.setUpdated(c.getInt(c.getColumnIndex("updated")) != 0);
                        dto.setEstatus(c.getString(c.getColumnIndex("estatus")));
                        dto.setNotaParcialSave(c.getInt(c.getColumnIndex("notaParcialSave")) != 0);
                        dto.setDeudorNombre(c.getString(c.getColumnIndex("deudorNombre")));
                        if (dto.isSeguimiento())
                            dto.setResponseTasks(getResponsetsk(dto.getId(), userDatabaseHelper));
                        else
                            dto.setResponseTasks(getResponsetskTAREAS(dto.getId(), userDatabaseHelper));
                        listseguimiento.add(dto);
                    } while (c.moveToNext());
                c.close();
            } catch (Exception sqle) {
                Log.e(TAG, sqle.getMessage(), sqle);
            } finally {
                userDatabaseHelper.close();
            }
            return listseguimiento;
        }

        private ArrayList<ResponseTask> getResponsetskTAREAS(Integer id, UserDatabaseHelper userDatabaseHelper) {
            ArrayList<ResponseTask> array = null;
            String query = "SELECT  question , response  ,_id FROM Tsk_AnswerDTO where tareaDTO_id = " + id;
            Cursor cs = userDatabaseHelper.getReadableDatabase().rawQuery(query, null);
            if (cs.getCount() > 0) {
                int attquestion = cs.getColumnIndexOrThrow("question");
                int attresponse = cs.getColumnIndexOrThrow("response");
                if (cs.moveToFirst())
                    array = new ArrayList<>();
                do {
                    if (array != null)
                        array.add(new ResponseTask(cs.getString(attquestion), cs.getString(attresponse)));
                } while (cs.moveToNext());
            }
            cs.close();
            return array;
        }

        private ArrayList<ResponseTask> getResponsetsk(Integer id, UserDatabaseHelper userDatabaseHelper) {
            ArrayList<ResponseTask> array = null;
            String query = "SELECT  question ," +
                    " response  ,idtarea " +
                    "FROM " +
                    "Tsk_responseTask " +
                    "where idtarea=" + id;
            Cursor cs = userDatabaseHelper.getReadableDatabase().rawQuery(query, null);
            if (cs.getCount() > 0) {
                int attquestion = cs.getColumnIndexOrThrow("question");
                int attresponse = cs.getColumnIndexOrThrow("response");
                if (cs.moveToFirst())
                    array = new ArrayList<>();
                do {
                    if (array != null)
                        array.add(new ResponseTask(cs.getString(attquestion), cs.getString(attresponse)));
                } while (cs.moveToNext());
            }
            cs.close();
            return array;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        setMenuVisibility(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_tskr, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        ActionBar a = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (a != null) {
            final SearchView searchView = new SearchView(a.getThemedContext());
            MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
            MenuItemCompat.setActionView(item, searchView);
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        switch (item.getItemId()) {
            case R.id.tsk_day:
                searchTsk(cal.getTimeInMillis() / 1000, 1, "# Tareas del día");
                break;
            case R.id.tsk_pending:
                searchTsk(cal.getTimeInMillis() / 1000, 2, getString(R.string.tareas_pendientes));
                break;
            case R.id.tsk_all:
                searchTsk(0L, 3, "# Tareas");
                break;
            case R.id.tsk_date:
                seachrDate();
                break;
            case R.id.tsk_close:
                searchTsk(0L, 5, "# Tareas Cerradas");
                break;
            case R.id.tsk_week:
                searchTsk(0L, 6, "# Tareas de la semana");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchTsk(Long date, int opc, String s) {
        Bundle b = getArguments();
        b.putLong(SeguimientoTarea.FECHA_COMPPROMISO, date);
        b.putInt(OPC_QUERY, opc);
        b.putLong(OPC_TIME, date);
        b.putString(OPC_TITLE, s);
        getLoaderManager().restartLoader(LOADER_ID, b, this).forceLoad();
        titleActionBar(s);
    }

    private void titleActionBar(String s) {
        ActionBar a = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (a != null) {
            a.setTitle(s);
        }
    }

    private void seachrDate() {
        CustomDialog.getDate(this.getActivity(), new Interfaces.OnResponse<Object>() {
            @Override
            public void onResponse(int handlerCode, Object o) {
                if (o != null) {
                    Calendar cal = (Calendar) o;
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    String date = Tools.getDatenumber(cal, "-");
                    searchTsk(cal.getTimeInMillis() / 1000, 4, "# Tareas " + date);
                }
            }
        }, 0, "Seleccione una fecha", null);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.e(TAG, "query:  " + query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Bundle b = getArguments();
        int op = b.getInt(OPC_QUERY, 2);
        Calendar cal = Calendar.getInstance();
        long time = b.getLong(OPC_TIME, cal.getTimeInMillis() / 1000);
        String title = b.getString(OPC_TITLE, getString(R.string.tareas_pendientes));
        b.putString(SEARCH_TEXT, newText);
        if (op == 2) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            time = cal.getTimeInMillis() / 1000;
        }
        searchTsk(time, op, title);
        return true;
    }
}