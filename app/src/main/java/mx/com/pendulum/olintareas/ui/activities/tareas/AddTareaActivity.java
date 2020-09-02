package mx.com.pendulum.olintareas.ui.activities.tareas;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.List;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.EpochDate;
import mx.com.pendulum.olintareas.dto.tareasV2.FileUploadDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.TareaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.TemporalForm;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.fragments.tareas.AddTareaV2Fragment;
import mx.com.pendulum.olintareas.ui.parents.AppCompatActivityParent;
import mx.com.pendulum.utilities.Tools;

public class AddTareaActivity extends AppCompatActivityParent {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        AddTareaV2Fragment fragment = new AddTareaV2Fragment();
        fragment.setArguments(getArguments());
        Tools.fragmentChooser(R.id.containerDynamicForm, fragment, getFragmentManager(), AddTareaV2Fragment.class.getSimpleName());
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
        switch (item.getItemId()) {
            case R.id.menu_ok:
                final AddTareaV2Fragment fragment = (AddTareaV2Fragment) getFragmentManager().findFragmentByTag(AddTareaV2Fragment.class.getSimpleName());
                if (fragment != null) {
                    TareaDTO tareaDTO = fragment.validateForm();
                    if (tareaDTO != null)
                        saveData(tareaDTO);
                    else {
                        if (fragment.showMessageCreditCase) {
                            String msg = "El " + (fragment.accountFromLegal ? "juicio " : "credito ") + fragment.accountName.toUpperCase() + " " + getString(R.string.unknown_account);
                            CustomDialog.showMessagePosNeg(getActivity(), msg, new Interfaces.OnResponse<Object>() {
                                @Override
                                public void onResponse(int handlerCode, Object o) {
                                    if (handlerCode == 778) {
                                        TareaDTO tareaDTO = fragment.getTareaDTO();
                                        saveData(tareaDTO);
                                    }
                                }
                            });
                        }
                    }
                }
                return true;
            case R.id.menu_cancel:
            case android.R.id.home:
                CustomDialog.salirSinGuardar(getActivity(), new Interfaces.OnResponse<Object>() {
                    @Override
                    public void onResponse(int handlerCode, Object o) {
                        if ((boolean) o)
                            saveSharedPreference();
                        deleteEmptyTarea();
                        getActivity().finish();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        CustomDialog.salirSinGuardar(getActivity(), new Interfaces.OnResponse<Object>() {
            @Override
            public void onResponse(int handlerCode, Object o) {
                if ((boolean) o) {
                    deleteEmptyTarea();
                    saveSharedPreference();
                    getActivity().finish();
                }
            }
        });
    }

    private void deleteEmptyTarea() {
        UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getActivity());
        Dao<TareaDTO, Long> dao;
        List<TareaDTO> tskList;
        try {
            dao = helper.getDao(TareaDTO.class);
            tskList = dao.queryBuilder().query();
            for (TareaDTO tsk : tskList) {
                if (tsk.getUsuAlta() == null) {
                    dao.delete(tsk);
                } else if (tsk.getUsuAlta().equalsIgnoreCase("")) {
                    dao.delete(tsk);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveSharedPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(Properties.SHARED_FROM_ANOTHER_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Properties.SHARED_IS_FINISHED_TASK, true);
        editor.apply();
    }

    private void saveData(TareaDTO tareaDTO) {
        UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper.getHelper(getActivity());
        try {
            Dao<TareaDTO, Long> tareaDTODao = userDatabaseHelper.getDao(TareaDTO.class);
            Dao<AnswerDTO, Long> answerDao = userDatabaseHelper.getDao(AnswerDTO.class);
            Dao<FileUploadDTO, Long> fileUploadDao = userDatabaseHelper.getDao(FileUploadDTO.class);
            Dao<EpochDate, Long> epochDao = userDatabaseHelper.getDao(EpochDate.class);
            EpochDate fechaInicio = tareaDTO.getFechaInicio();
            EpochDate fechaCompromiso = tareaDTO.getFechaCompromiso();
            EpochDate horaInicio = tareaDTO.getHoraInicio();
            EpochDate horaFin = tareaDTO.getHoraFin();
            EpochDate fechaAlta = tareaDTO.getFechaAlta();
            epochDao.create(fechaInicio);
            epochDao.create(fechaCompromiso);
            epochDao.create(horaInicio);
            epochDao.create(horaFin);
            epochDao.create(fechaAlta);
            tareaDTO.setFechaInicio(fechaInicio);
            tareaDTO.setFechaCompromiso(fechaCompromiso);
            tareaDTO.setHoraInicio(horaInicio);
            tareaDTO.setHoraFin(horaFin);
            tareaDTO.setFechaAlta(fechaAlta);
            tareaDTODao.create(tareaDTO);
            for (AnswerDTO answerDTO : tareaDTO.getAnswers()) {
                answerDTO.setTareaDTO(tareaDTO);
                answerDao.create(answerDTO);
                if (answerDTO.getFiles() != null)
                    for (FileUploadDTO fileUploadDTO : answerDTO.getFiles()) {
                        fileUploadDTO.setAnswerDTO(answerDTO);
                        fileUploadDao.create(fileUploadDTO);
                    }
            }
            Dao<TemporalForm, Long> daoTemp = userDatabaseHelper.getDao(TemporalForm.class);
            DeleteBuilder<TemporalForm, Long> deleteTempBulder;
            if (tareaDTO.getAnswers() != null) {
                for (AnswerDTO ans : tareaDTO.getAnswers()) {
                    int idOption = ans.getId_option();
                    deleteTempBulder = daoTemp.deleteBuilder();
                    deleteTempBulder.where().eq(TemporalForm.COL_ID_OPTION, idOption)
                            .and().eq(TemporalForm.COL_ID_NOTA, tareaDTO.getTmpNotaId())
                            .and().eq(TemporalForm.COL_ID_TAREA, tareaDTO.getTmpTareaId());
                    int resp = deleteTempBulder.delete();
                    Log.i("", resp + "");
                }
            }
            Log.i("", "");
            TareaDTO tareaDtoResult = tareaDTODao.queryForId(tareaDTO.get_id());
            Log.i("", tareaDtoResult != null ? tareaDtoResult.getDescripcion() : "Vacio TareaDto");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userDatabaseHelper.close();
        }
        saveSharedPreference();
        finish();
    }

    public boolean saveDataParcial(TareaDTO tareaDTO) {
        UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper.getHelper(getActivity());
        try {
            Dao<TareaDTO, Long> tareaDTODao = userDatabaseHelper.getDao(TareaDTO.class);
            Dao<AnswerDTO, Long> answerDao = userDatabaseHelper.getDao(AnswerDTO.class);
            Dao<FileUploadDTO, Long> fileUploadDao = userDatabaseHelper.getDao(FileUploadDTO.class);
            Dao<EpochDate, Long> epochDao = userDatabaseHelper.getDao(EpochDate.class);
            EpochDate fechaInicio = tareaDTO.getFechaInicio();
            EpochDate fechaCompromiso = tareaDTO.getFechaCompromiso();
            EpochDate horaInicio = tareaDTO.getHoraInicio();
            EpochDate horaFin = tareaDTO.getHoraFin();
            EpochDate fechaAlta = tareaDTO.getFechaAlta();
            epochDao.create(fechaInicio);
            epochDao.create(fechaCompromiso);
            epochDao.create(horaInicio);
            epochDao.create(horaFin);
            epochDao.create(fechaAlta);
            tareaDTO.setFechaInicio(fechaInicio);
            tareaDTO.setFechaCompromiso(fechaCompromiso);
            tareaDTO.setHoraInicio(horaInicio);
            tareaDTO.setHoraFin(horaFin);
            tareaDTO.setFechaAlta(fechaAlta);
            tareaDTODao.create(tareaDTO);
            for (AnswerDTO answerDTO : tareaDTO.getAnswers()) {
                answerDTO.setTareaDTO(tareaDTO);
                answerDao.create(answerDTO);
                if (answerDTO.getFiles() != null)
                    for (FileUploadDTO fileUploadDTO : answerDTO.getFiles()) {
                        fileUploadDTO.setAnswerDTO(answerDTO);
                        fileUploadDao.create(fileUploadDTO);
                    }
            }
            Dao<TemporalForm, Long> daoTemp = userDatabaseHelper.getDao(TemporalForm.class);
            DeleteBuilder<TemporalForm, Long> deleteTempBulder;
            if (tareaDTO.getAnswers() != null) {
                for (AnswerDTO ans : tareaDTO.getAnswers()) {
                    int idOption = ans.getId_option();
                    deleteTempBulder = daoTemp.deleteBuilder();
                    deleteTempBulder.where().eq(TemporalForm.COL_ID_OPTION, idOption)
                            .and().eq(TemporalForm.COL_ID_NOTA, tareaDTO.getTmpNotaId())
                            .and().eq(TemporalForm.COL_ID_TAREA, tareaDTO.getTmpTareaId());
                    int resp = deleteTempBulder.delete();
                    Log.i("", resp + "");
                }
            }
            Log.i("", "");
            TareaDTO tareaDtoResult = tareaDTODao.queryForId(tareaDTO.get_id());
            userDatabaseHelper.close();
            Log.i("", tareaDtoResult != null ? (tareaDtoResult.getDescripcion() != null ? tareaDtoResult.getDescripcion() : "Vacio TareaDto") : "Vacio TareaDto");
        } catch (Exception e) {
            e.printStackTrace();
            userDatabaseHelper.close();
            return false;
        }
        //saveSharedPreference();
        return true;
    }
}