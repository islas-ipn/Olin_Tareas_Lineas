package mx.com.pendulum.olintareas.ui.activities.tareas;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NotaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NotasDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NoteResponseDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.SeguimientoTarea;
import mx.com.pendulum.olintareas.dto.tareasV2.TareaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.TemporalForm;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.adapter.tareas.NotasAdapter;
import mx.com.pendulum.olintareas.ui.parents.AppCompatActivityParent;
import mx.com.pendulum.olintareas.ui.parents.FragmentParent;
import mx.com.pendulum.utilities.Tools;

@SuppressWarnings("unchecked")
public class ShowNotasActivity extends AppCompatActivityParent implements LoaderManager.LoaderCallbacks<Object>, Interfaces.OnResponse<Object> {

    private static final int LOADER_NOTES = 841358474;
    private static final int RESPONSE_LIST = 841358475;
    private List<NotasDTO> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notas);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(clicFlotan);
    }

    private void reloadData() {
        getLoaderManager().restartLoader(LOADER_NOTES, getArguments(), this).forceLoad();
    }

    View.OnClickListener clicFlotan = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int idTarea = getArguments().getInt(SeguimientoTarea.class.getSimpleName(), 0);
            if (!isAbleToSeguimiento(getActivity(), idTarea)) {
                return;
            }
            Intent intentDocs = new Intent(getActivity(), AddNotaActivity.class);
            intentDocs.putExtras(getArguments());
            intentDocs.putExtra(FragmentParent.IS_HOME_ENABLED, true);
            intentDocs.putExtra(FragmentParent.TITLE_FRAGMENT, "Agregar Seguimiento");
            startActivity(intentDocs);
        }
    };

    public static boolean isAbleToSeguimiento(Context context, int idTarea) {
        UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper.getHelper(context);
        try {
            //TODO verificar si hay alguna nota en salvado parcial, si existe no dejar agregar otra
            Dao<SeguimientoTarea, Long> seguimientoTareaDao = userDatabaseHelper.getDao(SeguimientoTarea.class);
            QueryBuilder<SeguimientoTarea, Long> queryBuilder = seguimientoTareaDao.queryBuilder();
            queryBuilder.where().eq(SeguimientoTarea.COL_ID_TAREA, idTarea);
            SeguimientoTarea tarea = queryBuilder.queryForFirst();
            TareaDTO tareaDTO = null;
            if (tarea == null) {
                Dao<TareaDTO, Long> tareaDTOLongDao = userDatabaseHelper.getDao(TareaDTO.class);
                QueryBuilder<TareaDTO, Long> query = tareaDTOLongDao.queryBuilder();
                query.where().eq(TareaDTO.COL_ID, idTarea);
                tareaDTO = query.queryForFirst();
            }
            Dao<NotaDTO, Long> notaDao = userDatabaseHelper.getDao(NotaDTO.class);
            QueryBuilder<NotaDTO, Long> queryBuilderNota = notaDao.queryBuilder();
            int idEstado = tarea != null ? tarea.getIdEstado().intValue() : tareaDTO.getIdEstado();
            switch (idEstado) {
                case 1: //Asignad   a
                    break;
                case 2: //	Rechazada
                    if (tarea != null) {
                        if (tarea.getDe().equals("ABA")) {
                            CustomDialog.showDisclaimer(context, "No se puede dar seguimiento a una tarea que ha sido rechazada", null);
                            return false;
                        }
                    } else {
                        if (tareaDTO.getDe().equals("ABA")) {
                            CustomDialog.showDisclaimer(context, "No se puede dar seguimiento a una tarea que ha sido rechazada", null);
                            return false;
                        }
                    }
                    return true;
                case 3: //	Cerrada
                    CustomDialog.showDisclaimer(context, "No se puede dar seguimiento a una tarea que ha sido cerrada", null);
                    return false;
                case 4: //	Proceso
                    break;
                case 5: //	Reasignada
                    if (tarea != null) {
                        if (tarea.getResQuasar().equals(Tools.getUserSecion(context).getUsername()))
                            return true;
                    } else {
                        if (tareaDTO.getQuasarResponsable().equals(Tools.getUserSecion(context).getUsername()))
                            return true;
                    }
                    CustomDialog.showDisclaimer(context, "No se puede dar seguimiento a una tarea que ha sido reasignada", null);
                    return false;
                case 7: //	Incorrecto
                    break;
                case 8: //	Correcto
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userDatabaseHelper.close();
        }
        return true;
    }

    private void drawNotes(Object data) {
        list = (List<NotasDTO>) data;
        if (list.isEmpty()) {
            finish();
        }
        RecyclerView recyclerView = getView().findViewById(R.id.rv);
        NotasAdapter adapter = new NotasAdapter(this, list, this, RESPONSE_LIST);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Tools.setupUI(getActivity(), getView().findViewById(android.R.id.content));
        reloadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Tools.hideSoftKeyboard(this);
        if (item.getItemId() == android.R.id.home) {
            saveSharedPreference();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveSharedPreference();
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_NOTES) {
            return new LoaderNotes(getActivity(), args);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        if (loader.getId() == LOADER_NOTES) {
            drawNotes(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
        if (loader.getId() == LOADER_NOTES) {
        }
    }

    @Override
    public void onResponse(int handlerCode, Object object) {
        if (handlerCode == RESPONSE_LIST) {
            final NotasDTO notasDTO = (NotasDTO) object;
            if (notasDTO.isParcialSave()) {
                CustomDialog.dialogChoice(getActivity(), new Interfaces.OnResponse<Object>() {
                    @Override
                    public void onResponse(int handlerCode, Object o) {
                        if ((boolean) o) {
                            UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getActivity());
                            try {
                                Dao<NotaDTO, Long> notaDao = helper.getDao(NotaDTO.class);
                                Dao<AnswerDTO, Long> answerDao = helper.getDao(AnswerDTO.class);
                                Dao<TemporalForm, Long> dao = helper.getDao(TemporalForm.class);
                                DeleteBuilder<AnswerDTO, Long> deleteBuilder = answerDao.deleteBuilder();
                                deleteBuilder.where().eq(AnswerDTO.NOTA_DTO_ID_FIELD_NAME, notasDTO.getId());
                                Collection<AnswerDTO> ansList = notasDTO.getAnswerDTOList();
                                int response;
                                response = deleteBuilder.delete();
                                response = notaDao.deleteById(notasDTO.getId());
                                reloadData();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 0, null, "¿Deseas borrar ésta nota de seguimiento?");
            }
        }
    }

    private static class LoaderNotes extends AsyncTaskLoader<Object> {
        Integer tareaId;

        LoaderNotes(Context context, Bundle args) {
            super(context);
            if (args != null) {
                tareaId = args.getInt(SeguimientoTarea.class.getSimpleName());
            }
        }

        @Override
        public List<NotasDTO> loadInBackground() {
            UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getContext());
            List<NotasDTO> list = new ArrayList<>();
            try {
                String query = "SELECT * from (SELECT\n" +
                        "			_id             id,\n" +
                        "			idTarea  ,\n" +
                        "			comandoAccion		accion		,\n" +
                        "			descripcion			comentario,\n" +
                        "			estado				estado		,\n" +
                        "			(select epoch from Tsk_EpochDate where _id = id_fechaAlta)		fechaAlta	,\n" +
                        "			(select epoch from Tsk_EpochDate where _id = id_fechaComp)		fechaComp	,\n" +
                        "			(select epoch from Tsk_EpochDate where _id = id_horaFin)			horaFin		,\n" +
                        "			(select epoch from Tsk_EpochDate where _id = id_horaIni)			horaIni	,\n" +
                        "			comandoResultado	respuesta,\n" +
                        "			updated			     updated,\n" +
                        "			nombreQuasar resNombre,\n" +
                        "			1 isFromPencel,\n" +
                        "           parcialSave\n" +
                        "			FROM\n" +
                        "			Tsk_NotaDTO\n" +
                        "UNION\n" +
                        "select \n" +
                        "			id,\n" +
                        "			idTarea,\n" +
                        "			accion,\n" +
                        "			comentario,\n" +
                        "			estado,\n" +
                        "			fechaAlta,\n" +
                        "			fechaComp,\n" +
                        "			horaFin,\n" +
                        "			horaIni,\n" +
                        "			respuesta,\n" +
                        "			updated,\n" +
                        "			resNombre,\n" +
                        "			0 isFromPencel,\n" +
                        "           0 parcialSave\n" +
                        "FROM Tsk_NotasDTO) " +
                        "where idTarea = " + tareaId + "" +
                        " order by trim(fechaAlta) desc;";
                Cursor c = helper.getReadableDatabase().rawQuery(query, null);
                Dao<NoteResponseDTO, Long> noteResponseDao = helper.getDao(NoteResponseDTO.class);
                Dao<AnswerDTO, Long> answerDao = helper.getDao(AnswerDTO.class);
                if (c.moveToFirst()) {
                    do {
                        NotasDTO notasDTO = new NotasDTO();
                        int updated = c.getInt(c.getColumnIndex("updated"));
                        notasDTO.setId(c.getLong(c.getColumnIndex("id")));
                        notasDTO.setAccion(c.getString(c.getColumnIndex("accion")));
                        notasDTO.setComentario(c.getString(c.getColumnIndex("comentario")));
                        notasDTO.setEstado(c.getString(c.getColumnIndex("estado")));
                        notasDTO.setFechaAlta(c.getString(c.getColumnIndex("fechaAlta")));
                        notasDTO.setFechaComp(c.getString(c.getColumnIndex("fechaComp")));
                        notasDTO.setHoraFin(c.getString(c.getColumnIndex("horaFin")));
                        notasDTO.setHoraIni(c.getString(c.getColumnIndex("horaIni")));
                        notasDTO.setRespuesta(c.getString(c.getColumnIndex("respuesta")));
                        notasDTO.setUpdated(updated != 0);
                        notasDTO.setIdTarea(c.getLong(c.getColumnIndex("idTarea")));
                        notasDTO.setResNombre(c.getString(c.getColumnIndex("resNombre")));
                        notasDTO.setFromPencel(c.getInt(c.getColumnIndex("updated")) != 0);
                        notasDTO.setParcialSave(c.getInt(c.getColumnIndex("parcialSave")) != 0);
                        if (notasDTO.isFromPencel()) {
                            QueryBuilder<AnswerDTO, Long> nQb = answerDao.queryBuilder();
                            nQb.where().eq(AnswerDTO.NOTA_DTO_ID_FIELD_NAME, notasDTO.getId());
                            List<AnswerDTO> listAnswer = nQb.query();
                            notasDTO.setAnswerDTOList(listAnswer);
                        } else {
                            QueryBuilder<NoteResponseDTO, Long> qb = noteResponseDao.queryBuilder();
                            qb.where().eq(NoteResponseDTO.COL_ID_NOTA, notasDTO.getId());
                            List<NoteResponseDTO> resp = qb.query();
                            notasDTO.setNoteResponseDTO(resp);
                        }
                        list.add(notasDTO);
                    } while (c.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                helper.close();
            }
            return list;
        }
    }

    private void saveSharedPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(Properties.SHARED_FROM_ANOTHER_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Properties.SHARED_IS_FINISHED_TASK, true);
        editor.apply();
    }
}