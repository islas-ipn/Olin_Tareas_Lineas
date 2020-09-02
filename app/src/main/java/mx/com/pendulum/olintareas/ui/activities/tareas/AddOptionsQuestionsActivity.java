package mx.com.pendulum.olintareas.ui.activities.tareas;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.tareasV2.NotaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.dto.tareasV2.TemporalForm;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.fragments.tareas.AddOptionsQuestionsFragment;
import mx.com.pendulum.olintareas.ui.parents.AppCompatActivityParent;
import mx.com.pendulum.utilities.Tools;

public class AddOptionsQuestionsActivity extends AppCompatActivityParent {

    public static final String KEY_ID_TAREA = "KEY_ID_TAREA";
    public static final String KEY_ID_NOTA = "KEY_ID_NOTA";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);


//        Long idTarea = getArguments().getLong(AddOptionsQuestionsActivity.KEY_ID_TAREA);
//        Long idNota = getArguments().getLong(AddOptionsQuestionsActivity.KEY_ID_NOTA);
//        Bundle bundle = getArguments();
//        bundle.putLong(AddOptionsQuestionsActivity.KEY_ID_TAREA, getArguments().getLong(AddOptionsQuestionsActivity.KEY_ID_TAREA));
//        bundle.putLong(AddOptionsQuestionsActivity.KEY_ID_NOTA, getArguments().);

        AddOptionsQuestionsFragment fragment = new AddOptionsQuestionsFragment();
        fragment.setArguments(getArguments());
        Tools.fragmentChooser(R.id.containerDynamicForm, fragment, getFragmentManager(), AddOptionsQuestionsFragment.class.getSimpleName());
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

        AddOptionsQuestionsFragment fragment = (AddOptionsQuestionsFragment) getFragmentManager().findFragmentByTag(AddOptionsQuestionsFragment.class.getSimpleName());
        switch (item.getItemId()) {
            case R.id.menu_ok:


                if (fragment != null) {
                    NotaDTO notaDTO = fragment.validateForm(false);
                    if (notaDTO != null) {


                        saveData(notaDTO, false);


                    }
                }

                return true;
            case R.id.menu_cancel:
            case android.R.id.home:

                onBackPressed();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
//        saveParcial();

        AddOptionsQuestionsFragment fragment = (AddOptionsQuestionsFragment) getFragmentManager().findFragmentByTag(AddOptionsQuestionsFragment.class.getSimpleName());
        if (fragment != null) {
            NotaDTO notaDTO = fragment.validateForm(true);


            saveData(notaDTO, true);


        }
    }


    @SuppressWarnings("ConstantConditions")
    public void saveData(NotaDTO notaDTO, boolean isParcialSave) {


        UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper.getHelper(getActivity());

        try {

            Long idTarea = getArguments().getLong(KEY_ID_TAREA);
            Long idNota = getArguments().getLong(KEY_ID_NOTA);

            Dao<TemporalForm, Long> dao = userDatabaseHelper.getDao(TemporalForm.class);
            Options option = (Options) getArguments().get("OPTION");
            String json = new Gson().toJson(notaDTO.getAnswers());
            Integer idOption = option.getId();

            Questions question = (Questions) getArguments().get("QUESTION");
            Long idQuestion = question.getId();


            if (isParcialSave) {
                QueryBuilder<TemporalForm, Long> queryBuilder = dao.queryBuilder();
                queryBuilder.where().eq(TemporalForm.COL_ID_QUESTION, idQuestion)
                        .and().eq(TemporalForm.COL_PARCIAL_SAVE, false)
                        .and().eq(TemporalForm.COL_ID_NOTA, idNota)
                        .and().eq(TemporalForm.COL_ID_TAREA, idTarea);
                TemporalForm t = queryBuilder.queryForFirst();
                if (t != null) {
                    CustomDialog.salirSinGuardar(getActivity(), new Interfaces.OnResponse<Object>() {
                        @Override
                        public void onResponse(int handlerCode, Object o) {
                            if ((boolean) o) {
                                setResult(RESULT_CANCELED);
                                getActivity().finish();
                                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                            }
                        }
                    });
                    return;
                }

                //si es falso entonces guardar
                //si es true entonces preguntar al usuario si quiere salir sin guardar los cambios efectuados no se guardaran
            }

            //elimina todas las posibles respuestas de la pregunta para que pueda quedarse con las respuestas de la ultima opcion''''¿
            DeleteBuilder<TemporalForm, Long> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq(TemporalForm.COL_ID_QUESTION, idQuestion)
                    .and().eq(TemporalForm.COL_ID_NOTA, idNota)
                    .and().eq(TemporalForm.COL_ID_TAREA, idTarea);
//            int response =
            deleteBuilder.delete();


            TemporalForm temporalForm = dao.queryBuilder().where().eq(TemporalForm.COL_ID_OPTION, idOption)
                    .and().eq(TemporalForm.COL_ID_NOTA, idNota)
                    .and().eq(TemporalForm.COL_ID_TAREA, idTarea).queryForFirst();

            Log.i("", "");


            if (temporalForm == null) {
                temporalForm = new TemporalForm();
                temporalForm.setIdOption(option.getId());
                temporalForm.setAnswers(json);
                temporalForm.setIdQuestion(idQuestion);
                temporalForm.setParcialSave(isParcialSave);
                temporalForm.setIdOption(option.getId());
                temporalForm.setAnswers(json);
                temporalForm.setIdTarea(idTarea);
                temporalForm.setIdNota(idNota);
                dao.create(temporalForm);
            } else {
                temporalForm.setAnswers(json);
                temporalForm.setParcialSave(isParcialSave);
                temporalForm.setIdOption(option.getId());
                temporalForm.setAnswers(json);
                temporalForm.setIdTarea(idTarea);
                temporalForm.setIdNota(idNota);
                dao.update(temporalForm);
            }


            Log.i("", "");


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userDatabaseHelper.close();
        }

        Intent intent = getIntent();
        intent.putExtra("IS_COMPLETE", isParcialSave);
        intent.putExtra("POSITION", getArguments().getInt("POSITION"));
        setResult(RESULT_OK, intent);

        getActivity().finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }


}
