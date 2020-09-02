package mx.com.pendulum.olintareas.ui.fragments.tareas;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.CatalogDatabaseHelper;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NotaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.OptionsQuestionsForm;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.dto.tareasV2.TemporalForm;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.activities.tareas.AddOptionsQuestionsActivity;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;
import mx.com.pendulum.olintareas.ui.parents.FragmentParent;
import mx.com.pendulum.utilities.Tools;

public class AddOptionsQuestionsFragment extends FragmentParent implements LoaderManager.LoaderCallbacks<Object> {

    //<editor-fold desc="Variables">
    private static final String TAG = AddOptionsQuestionsFragment.class.getSimpleName();
    private static final int LOADER_QUESTIONS = 8569820;
    private Options option;

    private DynamicFormAdapter adapterForm;
    private List<Questions> listForm;
    private ListView listView;
    private static NotaDTO pendingNotaDTO;
    private ArrayList<String> pathDocuments;

    //</editor-fold >


    //<editor-fold desc="Ciclo de vida">
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        option = (Options) getArguments().get("OPTION");
        return inflater.inflate(R.layout.fragment_dynamic_form, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Tools.setupUI(getActivity(), getView().findViewById(android.R.id.content));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(R.id.listview);
//        listView.addHeaderView(getHeader());
//        listView.addFooterView(getFooter());
        listView.setAdapter(null);
        Bundle args = getArguments();
        getLoaderManager().restartLoader(LOADER_QUESTIONS, args, AddOptionsQuestionsFragment.this).forceLoad();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapterForm = AddTareaV2Fragment.result(requestCode, resultCode, data, adapterForm);

    }

    public NotaDTO validateForm(boolean isParcialSave) {
        Boolean isFullData = true;
        boolean isAnyChange = false;
        NotaDTO notaDTO = new NotaDTO();
        notaDTO.setUpdated(true);
        Collection<AnswerDTO> answerList = new ArrayList<>();
        notaDTO.setAnswers(answerList);
        if (listForm != null)
            if (!listForm.isEmpty())
                for (int i = 0; i < listForm.size(); i++) {
                    View view = getViewByPosition(i, listView);
                    Object tag = AddTareaV2Fragment.isValid(getActivity(), view, listForm.get(i), true, notaDTO.getCredito() == null ? ("" + notaDTO.getJuicio()) : notaDTO.getCredito());
                    if (tag == null) {
                        Log.i("Valid", "No_Valid");
                        isFullData = false;
                        if (!isParcialSave) {
                            listForm.get(i).setError(true);
                            Tools.showSnack(getActivity(), "Por favor conteste " + listForm.get(i).getQuestion());
                            adapterForm.notifyDataSetChanged();
                            listView.smoothScrollToPosition(i);
                            return null;
                        }
                    } else {
                        Log.i("Valid", "Si_Valid");
                        if (tag instanceof AnswerDTO) {
                            AnswerDTO answerDTO = (AnswerDTO) tag;
                            answerList.add(answerDTO);
                            switch (answerDTO.getType()) {
                                case "FILE_UPLOAD":
                                case "RAW":
                                case "SIGN":
                                case "IMAGE":
                                case "VIDEO":
                                    if (!answerDTO.getFiles().isEmpty()) {
                                        isAnyChange = true;
                                    }
                                    break;
                                case "GEOLOCATION":
                                    if (!answerDTO.getResponse().isEmpty()) {
                                        isAnyChange = true;
                                    }
                                    Log.i("", "");
                                    break;
                                case "CHOICE":
                                case "LIST":
                                    int idOption = answerDTO.getId_option();
                                    Long idTarea = getArguments().getLong(AddOptionsQuestionsActivity.KEY_ID_TAREA);
                                    Long idNota = getArguments().getLong(AddOptionsQuestionsActivity.KEY_ID_NOTA);
                                    boolean isComplete = AddNotaV2Fragment.loadOptionAnswers(getActivity(), idOption, answerList, isParcialSave, idTarea, idNota);
                                    if (!isComplete) {
                                        isFullData = false;
                                        if (!isParcialSave) {
                                            listForm.get(i).setError(true);
                                            Tools.showSnack(getActivity(), "Por favor conteste " + listForm.get(i).getQuestion());
                                            adapterForm.notifyDataSetChanged();
                                            listView.smoothScrollToPosition(i + 1);
                                            return null;
                                        }
                                    } else {
                                        isAnyChange = true;
                                    }
                                    break;
                                default:
                                    isAnyChange = true;
                                    break;
                            }
                        }
                    }
                    Log.i("", "");
                }
        if (isParcialSave) {
            return notaDTO;
        } else {
            if (isFullData) {
                return notaDTO;
            } else {
                return null;
            }
        }
    }

    public View getViewByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition) {
            return listView.getAdapter().getView(position, listView.getChildAt(position), listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
    //</editor-fold >


    //<editor-fold desc="LOADERS">
    private void loadDynamicForm(List<Questions> list) {
        if (list == null || list.isEmpty()) {
            listView.setAdapter(null);
            adapterForm = null;
            listForm = null;
            return;
        }

        boolean isImageResize = getArguments().getBoolean("isImageResize", false);

        Long idTarea = getArguments().getLong(AddOptionsQuestionsActivity.KEY_ID_TAREA);
        Long idNota = getArguments().getLong(AddOptionsQuestionsActivity.KEY_ID_NOTA);


        adapterForm = new DynamicFormAdapter(getActivity(), list, this, isImageResize, idTarea, idNota);
        if (pendingNotaDTO != null)
            if (pendingNotaDTO.getAnswers() != null) {
                adapterForm.setPendingAnswers(pendingNotaDTO.getAnswers());
            }
        listForm = list;
        listView.setAdapter(adapterForm);

    }


    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case LOADER_QUESTIONS:
                CustomDialog.showProgressDialog(getActivity(), true, "Cargando...");
                return new QuestionLoader(getActivity(), args);
            default:
                return null;
        }


    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        switch (loader.getId()) {
            case LOADER_QUESTIONS:
                List<Questions> list = (List<Questions>) data;
                loadDynamicForm(list);
                CustomDialog.showProgressDialog(getActivity(), false, null);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
        switch (loader.getId()) {
            case LOADER_QUESTIONS:

                break;
        }
    }


    private static class QuestionLoader extends AsyncTaskLoader<Object> {


        private Options option;
        private Long idTarea;
        private Long idNota;

        private QuestionLoader(Context context, Bundle args) {
            super(context);
            if (args != null) {

                option = (Options) args.get("OPTION");
                idTarea = args.getLong(AddOptionsQuestionsActivity.KEY_ID_TAREA);
                idNota = args.getLong(AddOptionsQuestionsActivity.KEY_ID_NOTA);
            }
        }

        @Override
        public List<Questions> loadInBackground() {
            List<Questions> list = new ArrayList<>();
            CatalogDatabaseHelper helper = CatalogDatabaseHelper.getHelper(getContext());
            UserDatabaseHelper userHelper = UserDatabaseHelper.getHelper(getContext());
            try {
                Integer idOption = option.getId();

                Dao<OptionsQuestionsForm, Long> daoQ = helper.getDao(OptionsQuestionsForm.class);
                OptionsQuestionsForm optionQuestionForm = daoQ.queryBuilder().where().eq(OptionsQuestionsForm.COL_ID_OPTION, idOption).queryForFirst();


                Log.i("", "");

                String questionsStr = optionQuestionForm.getQuestions();

                Type listType = new TypeToken<List<Questions>>() {
                }.getType();
                list = new Gson().fromJson(questionsStr, listType);

                Dao<TemporalForm, Long> daoTemp = userHelper.getDao(TemporalForm.class);


                TemporalForm temporalForm = daoTemp.queryBuilder().where().eq(TemporalForm.COL_ID_OPTION, idOption)
                        .and().eq(TemporalForm.COL_ID_NOTA, idNota)
                        .and().eq(TemporalForm.COL_ID_TAREA, idTarea)
                        .queryForFirst();
                if (temporalForm != null) {
                    String ans = temporalForm.getAnswers();

                    if (ans != null && !ans.isEmpty()) {

                        Type collectionType = new TypeToken<Collection<AnswerDTO>>() {
                        }.getType();
                        Collection<AnswerDTO> answerList = new Gson().fromJson(ans, collectionType);


                        pendingNotaDTO = new NotaDTO();
                        pendingNotaDTO.setAnswers(answerList);
                    } else {
                        pendingNotaDTO = null;
                    }
                } else {
                    pendingNotaDTO = null;
                }


                Log.i("", "");

            } catch (Exception ignored) {
                Log.i("", "");
            } finally {
                helper.close();
                userHelper.close();
            }


            return list;
        }
    }

    public void setPathDocuments(ArrayList<String> pathDocuments) {
        this.pathDocuments = pathDocuments;
    }

    public ArrayList<String> getPathDocuments() {
        if (pathDocuments != null) return this.pathDocuments;
        else return new ArrayList<>();
    }

    //</editor-fold >


}