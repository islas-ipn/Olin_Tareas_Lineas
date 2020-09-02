package mx.com.pendulum.olintareas.ui.activities.tareas;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.EpochDate;
import mx.com.pendulum.olintareas.dto.tareasV2.FileUploadDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NotaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.SeguimientoTarea;
import mx.com.pendulum.olintareas.dto.tareasV2.TareaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.TemporalForm;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.fragments.tareas.AddNotaV2Fragment;
import mx.com.pendulum.olintareas.ui.parents.AppCompatActivityParent;
import mx.com.pendulum.utilities.Tools;

public class AddNotaActivity extends AppCompatActivityParent {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nota);
        /*Bundle bundle = getArguments();
        bundle.putLong(AddOptionsQuestionsActivity.KEY_ID_TAREA, 0);
        bundle.putLong(AddOptionsQuestionsActivity.KEY_ID_NOTA, 0);*/
        AddNotaV2Fragment fragment = new AddNotaV2Fragment();
        fragment.setArguments(getArguments());
        Tools.fragmentChooser(R.id.containerDynamicForm, fragment, getFragmentManager(), AddNotaV2Fragment.class.getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        Tools.setupUI(getActivity(), getView().findViewById(android.R.id.content));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ok_cancel_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Tools.hideSoftKeyboard(this);
        AddNotaV2Fragment fragment = (AddNotaV2Fragment) getFragmentManager().findFragmentByTag(AddNotaV2Fragment.class.getSimpleName());
        switch (item.getItemId()) {
            case R.id.menu_ok:
                if (fragment != null) {
                    NotaDTO notaDTO = fragment.validateForm(false);
                    if (notaDTO != null)
                        saveData(getActivity(), notaDTO, false);
                }
                return true;
            case R.id.menu_cancel:
            case android.R.id.home: // TODO BOTON CANCELAR TOOLBAR
                saveParcial();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveParcial() {
        boolean showDialog = true;
        AddNotaV2Fragment fragment = (AddNotaV2Fragment) getFragmentManager().findFragmentByTag(AddNotaV2Fragment.class.getSimpleName());
        if (fragment != null) {
            NotaDTO notaDTO = fragment.validateForm(true);
            if (notaDTO != null) {
                int idActividad;
                String idNegocio = "";
                int idAsunto;
                if (fragment.isIsFromPencel()) {
                    TareaDTO tareaDTO = fragment.getTareaDTO();
                    idActividad = tareaDTO.getIdTipoTarea();
                    idNegocio = tareaDTO.getIdSeccion();
                    idAsunto = tareaDTO.getSubTipo();
                } else {
                    SeguimientoTarea seguimientoTarea = fragment.getSeguimientoTarea();
                    idActividad = seguimientoTarea.getIdTipo();
                    idNegocio = seguimientoTarea.getOrigen();
                    idAsunto = seguimientoTarea.getSubtipo();
                }
                /*if (idActividad == 1 &&  //1 = tarea //1 -> cita
                        idNegocio.equals("AB")
                        && (idAsunto == 34 ||  //Limpieza regular
                        idAsunto == 35 ||  //Limpieza mayor
                        idAsunto == 53 ||  //Verificación de completitud de datos
                        idAsunto == 284 ||  //Rondín
                        idAsunto == 324 ||  //Cotización limpieza mayor
                        idAsunto == 325 ||  //Cotización tapiado
                        idAsunto == 305 ||  //Tapiados
                        idAsunto == 306 ||  //Des-Tapiados
                        idAsunto == 307 ||  //Mantenimiento Extraordinario
                        idAsunto == 308 ||  //Lonas
                        idAsunto == 309     //Completitudes)
                )) {
                    saveData(getActivity(), notaDTO, true);
                    showDialog = false;
                }*/
                saveData(getActivity(), notaDTO, true);
                showDialog = false;
            }
        }
        if (showDialog)
            CustomDialog.salirSinGuardar(getActivity(), new Interfaces.OnResponse<Object>() {
                @Override
                public void onResponse(int handlerCode, Object o) {
                    if ((boolean) o) {
                        saveSharedPreference();
                        getActivity().finish();
                    }
                }
            });
    }

    @Override
    public void onBackPressed() {
        saveParcial();
    }

    public void saveData(Activity activity, NotaDTO notaDTO, boolean isParcialSave) {
        UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper.getHelper(activity);
        try {
            Dao<SeguimientoTarea, Long> seguimientoTareaDao = userDatabaseHelper.getDao(SeguimientoTarea.class);
            Dao<TareaDTO, Long> tareaDao = userDatabaseHelper.getDao(TareaDTO.class);
            Dao<NotaDTO, Long> notaDTODao = userDatabaseHelper.getDao(NotaDTO.class);
            Dao<AnswerDTO, Long> answerDao = userDatabaseHelper.getDao(AnswerDTO.class);
            Dao<FileUploadDTO, Long> fileUploadDao = userDatabaseHelper.getDao(FileUploadDTO.class);
            Dao<EpochDate, Long> epochDao = userDatabaseHelper.getDao(EpochDate.class);
            /*Dao<Lw, Long> lwDao = userDatabaseHelper.getLwDao();
            Dao<NotaDTO, Long> notaDao = userDatabaseHelper.getDao(NotaDTO.class);
            QueryBuilder<NotaDTO, Long> queryBuilderNota = notaDao.queryBuilder();
            queryBuilderNota.where().eq(NotaDTO.COL_ID_TAREA, notaDTO.getIdTarea()).and().in(NotaDTO.COL_PARCIAL_SAVE, true);
            NotaDTO pendingNota = queryBuilderNota.queryForFirst();
            if (pendingNota != null) {
                notaDTO.set_id(pendingNota.get_id());
                notaDTODao.delete(pendingNota);
            }*/
            DeleteBuilder<AnswerDTO, Long> deleteDao = answerDao.deleteBuilder();
            deleteDao.where().eq(AnswerDTO.NOTA_DTO_ID_FIELD_NAME, notaDTO.get_id());
            int l = deleteDao.delete();
            NotaDTO tmp = new NotaDTO();
            tmp.set_id(notaDTO.get_id());
            int j = notaDTODao.update(tmp);
            EpochDate fechaComp = notaDTO.getFechaComp();
            EpochDate fechaAlta = notaDTO.getFechaAlta();
            EpochDate horaIni = notaDTO.getHoraIni();
            EpochDate horaFin = notaDTO.getHoraFin();
            epochDao.createOrUpdate(fechaComp);
            epochDao.createOrUpdate(fechaAlta);
            epochDao.createOrUpdate(horaIni);
            epochDao.createOrUpdate(horaFin);
            notaDTO.setFechaComp(fechaComp);
            notaDTO.setFechaAlta(fechaAlta);
            notaDTO.setHoraIni(horaIni);
            notaDTO.setHoraFin(horaFin);
            int i = notaDTODao.update(notaDTO);
            /*if (i == 0) {
                notaDao.delete(notaDTO);
                notaDTO.set_id(null);
                notaDTODao.create(notaDTO);
            }*/
            if (notaDTO.getAnswers() != null)
                for (AnswerDTO answerDTO : notaDTO.getAnswers()) {
                    answerDTO.setNotaDTO(notaDTO);
                    answerDao.create(answerDTO);
                    if (answerDTO.getFiles() != null)
                        for (FileUploadDTO fileUploadDTO : answerDTO.getFiles()) {
                            fileUploadDTO.setAnswerDTO(answerDTO);
                            fileUploadDao.create(fileUploadDTO);
                            //se comenta para evitar duplicidadde imagenes en Lw
                        /*Lw lw = fileUploadDTO.getLw();
                        lw.setUpdated(true);
                        lwDao.create(lw);*/
                        }
                }
            NotaDTO notaDtoResult = notaDTODao.queryForId(notaDTO.get_id());
            /*if (notaDtoResult == null) {
                notaDtoResult = notaDTO;
            }*/
            if (isParcialSave) {
                notaDtoResult.setParcialSave(true);
            } else {
                notaDtoResult.setParcialSave(false);
            }
            int i2 = notaDTODao.update(notaDtoResult);
            int idTarea = notaDtoResult.getIdTarea();
            QueryBuilder<SeguimientoTarea, Long> queryBuilder = seguimientoTareaDao.queryBuilder();
            queryBuilder.where().eq(SeguimientoTarea.COL_ID_TAREA, idTarea);
            QueryBuilder<TareaDTO, Long> query = tareaDao.queryBuilder();
            query.where().eq(TareaDTO.COL_ID, idTarea);
            SeguimientoTarea seguimientoTarea = queryBuilder.queryForFirst();
            if (!isParcialSave) {
                if (seguimientoTarea != null) {
                    seguimientoTarea.setUpdated(true);
                    if (notaDtoResult.getIdEstado() == 2) {
                        seguimientoTarea.setEstado(2);
                        seguimientoTarea.setIdEstado(2L);
                        seguimientoTarea.setDescEstado("Rechazada");
                        seguimientoTarea.setEstadotarea("Rechazada");
                    } else if (notaDtoResult.getIdEstado() == 3) {
                        seguimientoTarea.setEstado(3);
                        seguimientoTarea.setIdEstado(3L);
                        seguimientoTarea.setDescEstado("Cerrada");
                        seguimientoTarea.setEstadotarea("Cerrada");
                    } else if (notaDtoResult.getIdEstado() == 4) {
                        seguimientoTarea.setEstado(4);
                        seguimientoTarea.setIdEstado(4L);
                        seguimientoTarea.setDescEstado("Proceso");
                        seguimientoTarea.setEstadotarea("Proceso");
                    } else if (notaDtoResult.getIdEstado() == 5) {
                        seguimientoTarea.setEstado(5);
                        seguimientoTarea.setIdEstado(5L);
                        seguimientoTarea.setDescEstado("Reasignada");
                        seguimientoTarea.setEstadotarea("Reasignada");
                        seguimientoTarea.setResNombre(notaDTO.getNvoResNombre());
                        seguimientoTarea.setResCorreo(notaDTO.getNvoResCorreo());
                        seguimientoTarea.setResQuasar(notaDTO.getNvoResQuasar());
                    }
                    seguimientoTareaDao.update(seguimientoTarea);
                } else {
                    TareaDTO tareaDTO = query.queryForFirst();
                    tareaDTO.setUpdated(true);
                    if (notaDtoResult.getIdEstado() == 2) {
                        tareaDTO.setEstado("Rechazada");
                        tareaDTO.setIdEstado(2);
                        /*tareaDTO.setDescEstado("Rechazada");
                        tareaDTO.setEstadotarea("Rechazada");*/
                    } else if (notaDtoResult.getIdEstado() == 3) {
                        tareaDTO.setEstado("Cerrada");
                        tareaDTO.setIdEstado(3);
                        /*tareaDTO.setDescEstado("Cerrada");
                        tareaDTO.setEstadotarea("Cerrada");*/
                    } else if (notaDtoResult.getIdEstado() == 4) {
                        tareaDTO.setEstado("Proceso");
                        tareaDTO.setIdEstado(4);
                        /*tareaDTO.setDescEstado("Proceso");
                        tareaDTO.setEstadotarea("Proceso");*/
                    } else if (notaDtoResult.getIdEstado() == 5) {
                        tareaDTO.setEstado("Reasignada");
                        tareaDTO.setIdEstado(5);
                        /*tareaDTO.setDescEstado("Reasignada");
                        tareaDTO.setEstadotarea("Reasignada");
                        tareaDTO.setResNombre(notaDTO.getNvoResNombre());*/
                    }
                    tareaDao.update(tareaDTO);
                }
                Dao<TemporalForm, Long> daoTemp = userDatabaseHelper.getDao(TemporalForm.class);
                DeleteBuilder<TemporalForm, Long> deleteTempBulder;
                if (notaDTO.getAnswers() != null) {
                    for (AnswerDTO ans : notaDTO.getAnswers()) {
                        int idOption = ans.getId_option();
                        deleteTempBulder = daoTemp.deleteBuilder();
                        deleteTempBulder.where().eq(TemporalForm.COL_ID_OPTION, idOption)
                                .and().eq(TemporalForm.COL_ID_NOTA, notaDTO.getTmpNotaId())
                                .and().eq(TemporalForm.COL_ID_TAREA, notaDTO.getTmpTareaId());
                        int resp = deleteTempBulder.delete();
                        Log.i("", "");
                    }
                }
                //userDatabaseHelper.clearTable(TemporalForm.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userDatabaseHelper.close();
        }
        saveSharedPreference();
        activity.finish();
    }

    private void saveSharedPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(Properties.SHARED_FROM_ANOTHER_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Properties.SHARED_IS_FINISHED_TASK, true);
        editor.apply();
    }
}