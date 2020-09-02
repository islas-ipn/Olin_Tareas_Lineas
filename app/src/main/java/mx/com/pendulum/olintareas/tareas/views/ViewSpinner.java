package mx.com.pendulum.olintareas.tareas.views;


import android.app.Fragment;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.config.util.SpinnerAdapter;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.dto.tareasV2.TemporalForm;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;

public class ViewSpinner extends ParentViewMain {

    public ViewSpinner(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }

    private Questions questionClear(Questions cleanQ) {
        if (cleanQ != null && cleanQ.getOptions() != null) {
            List<Options> optionsList = cleanQ.getOptions();
            for (int h = 0; h < optionsList.size(); h++) {
                for (int i = h + 1; i < optionsList.size(); i++) {
                    int idOriginal = optionsList.get(h).getId();
                    int idCopy = optionsList.get(i).getId();
                    if (idOriginal == idCopy) {
                        optionsList.remove(i);
                        i--;
                    }
                }
            }
        } else return cleanQ;
        return cleanQ;
    }

    public View getView(int position, View convertView, Questions question) {
        DynamicViewHolder holder = null;
        question = questionClear(question);
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_lists, null);
        } else {
            holder = (DynamicViewHolder) convertView.getTag();
        }
        if (holder == null) {
            holder = new DynamicViewHolder();
            holder.rlAnswer = convertView.findViewById(R.id.rlAnswer);
            holder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
            holder.spAnswer = convertView.findViewById(R.id.spAnswer);
            holder.flError = convertView.findViewById(R.id.flError);
            holder.ivDelete = convertView.findViewById(R.id.ivDelete);
            holder.moreQuestions = convertView.findViewById(R.id.moreQuestions);
            holder.moreQuestionsContainer = convertView.findViewById(R.id.moreQuestionsContainer);
            convertView.setTag(holder);
        }
        super.configureDocsAndComments(question.getAnswerContainerDocsList(), question.getAnswerContainerCommList(), question.getQuestionContainerDocsList(), convertView, question, position);
        if (getPendingAnswer() != null && question.getAnswer() == null) {
            for (Options option : question.getOptions()) {
                if (option.getId().equals(getPendingAnswer().getId_option())) {
                    setSelectedOption(convertView, holder, option, question, true, position);
                    break;
                }
            }
        }
        setAdapterSpinner(question, holder);
        if (question.getAnswer() != null) {
            Options option = (Options) question.getAnswer();
            setSelectedOption(convertView, holder, option, question, true, position);
        }
        setClickAction(convertView, holder, question, position);
        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);
        if (question.getOptions() != null) {
            super.addOptions(holder.spAnswer, question);
        }
        setOnClickDelete(convertView, holder, question.getOptions(), question, position);
        super.setError(holder.flError, question.isError());
        setAddVisible(holder, super.setRespText(question.getAnswer(), holder.spAnswer, null), question);
        setSelectedAction(convertView, holder, question, position);
        return convertView;
    }

    private void setAdapterSpinner(Questions questions, final DynamicViewHolder holder) {
        List<Options> options = questions.getOptions();
        String sIdQs = String.valueOf(questions.getId());
        int idQs = Integer.parseInt(sIdQs);
        if (options == null) return;
        if (options.size() == 0) return;
        if (!options.get(0).getOption().equalsIgnoreCase(Properties.SPINNER_OPTION_SELECT_STRING)) {
            Options opSelect = new Options();
            opSelect.setId(Properties.SPINNER_OPTION_SELECT_INT);
            opSelect.setOption(Properties.SPINNER_OPTION_SELECT_STRING);
            opSelect.setIdquestion(idQs);
            opSelect.setRequiredComment(false);
            opSelect.setRequiredDocument(false);
            options.add(opSelect);
            Collections.reverse(options);
        }
        for (Options opt : options) {
            opt.setIdquestion(idQs);
        }
        SpinnerAdapter adapter = new SpinnerAdapter(getContext(), options);
        if (holder.spAnswer != null)
            holder.spAnswer.setAdapter(adapter);
    }

    private void setSelectedAction(final View convertView, final DynamicViewHolder holder, final Questions questions, final int positionQ) {

    }

    private void setClickAction(final View convertView, final DynamicViewHolder holder, final Questions questions, final int positionQ) {
        if (holder.rlAnswer != null && holder.spAnswer != null) {
            holder.spAnswer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                    final Options option = (Options) holder.spAnswer.getAdapter().getItem(position);
                    boolean isAnswered = false;
                    if (!option.getOption().equalsIgnoreCase(Properties.SPINNER_OPTION_SELECT_STRING)) {
                        UserDatabaseHelper userHelper = UserDatabaseHelper.getHelper(getContext());
                        try {
                            Dao<TemporalForm, Long> daoTemp = userHelper.getDao(TemporalForm.class);
                            Integer idOption = option.getId();
                            Long idQuestion = questions.getId();
                            TemporalForm temporalForm = daoTemp.queryBuilder().where().eq(TemporalForm.COL_ID_QUESTION, idQuestion)
                                    .and().notIn(TemporalForm.COL_ID_OPTION, idOption)
                                    .and().eq(TemporalForm.COL_ID_NOTA, getIdNota())
                                    .and().eq(TemporalForm.COL_ID_TAREA, getIdTarea())
                                    .queryForFirst();
                            if (temporalForm != null) {
                                String ans = temporalForm.getAnswers();
                                if (ans != null && !ans.isEmpty()) {
                                    CustomDialog.dialogChoice(getContext(), new Interfaces.OnResponse<Object>() {
                                        @Override
                                        public void onResponse(int handlerCode, Object o) {
                                            boolean bool = (boolean) o;
                                            if (bool) {
                                                setSelectedOption(convertView, holder, option, questions, false, position);
                                            }
                                        }
                                    }, 0, "¡Atención!", "Se tiene un cuestionario contestado para la pregunta\n\n\"" +
                                            questions.getQuestion() + "\". \n\n" +
                                            "Sí deseas \"Continuar\" dichos cambios se perderán. \n\n" +
                                            "¿Desea continuar?", "Continuar", "Cancelar");
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            userHelper.close();
                        }
                        if (option.getContienesubpregunta() == 1 && questions.getAnswer() != null) { // Sub Calsificacion
                            Options optQNS = (Options) questions.getAnswer();
                            if (!optQNS.getOption().equalsIgnoreCase(option.getOption())) { // Diferentes respuestas
                                if (option.getIdquestion() == questions.getId()) { // Misma pregunta
                                    addMoreQuestionsBecauseOptionsHas(option, positionQ, 1);
                                    option.setAlreadyCharged(false);
                                    isAnswered = true;
                                }
                            } else {
                                if (!option.isAlreadyCharged()) {
                                    addMoreQuestionsBecauseOptionsHas(option, positionQ, 1);
                                    option.setAlreadyCharged(false);
                                    isAnswered = true;
                                }
                            }
                        } else {
                            if (questions.getAnswer() != null) { // Clasificacion
                                Options optQNS = (Options) questions.getAnswer();
                                if (!optQNS.getOption().equalsIgnoreCase(option.getOption())) { // Diferentes respuestas
                                    if (option.getIdquestion() == questions.getId()) { // Misma pregunta
                                        addMoreQuestionsBecauseOptionsHas(option, positionQ, 1);
                                        option.setAlreadyCharged(false);
                                        isAnswered = true;
                                    }
                                }
                            } else if (!option.isAlreadyCharged()) { // Aun no se asigna contestacion
                                addMoreQuestionsBecauseOptionsHas(option, positionQ, 1);
                                option.setAlreadyCharged(false);
                                isAnswered = true;
                            }
                        }
                    } else { // SELECCIONAR
                        if (questions.getAnswer() != null) {
                            Options optQNS = (Options) questions.getAnswer();
                            if (!optQNS.getOption().equalsIgnoreCase(option.getOption())) { // Diferentes respuestas
                                if (option.getIdquestion() == questions.getId()) { // Misma pregunta
                                    option.setAlreadyCharged(false);
                                    addMoreQuestionsBecauseOptionsHas(option, positionQ, 2);
                                }
                            }
                        }
                        isAnswered = true;
                    }
                    if (!option.isAlreadyCharged()) {
                        option.setAlreadyCharged(true);
                        setSelectedOption(convertView, holder, option, questions, isAnswered, position);
                    }
                    holder.moreQuestions.setTag(option);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        if (holder.moreQuestions != null) {
            holder.moreQuestions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Options option = (Options) questions.getAnswer();
                    // ABRE QUESTION DE PREGUNTA EN OTRA VENTANA
                    //loadQuestionOptions(questions, option, holder.moreQuestionsContainer, false, positionQ);
                }
            });
            Options option = (Options) holder.moreQuestions.getTag();
            if (option != null) {
                setSelectedOption(convertView, holder, option, questions, option.getRequiredComment(), positionQ);
            }
        }
    }

    private void setSelectedOption(View convertView, DynamicViewHolder holder, Options options, Questions question, boolean isAnswered, int position) {
        if (options == null) return;
        if (options.getRequiredComment() != null) {
            onOptionRequiredComment(question.getAnswerContainerCommList(), convertView, options, options.getRequiredComment(), question);
        } else {
            onOptionRequiredComment(question.getAnswerContainerCommList(), convertView, options, false, question);
        }
        if (options.getRequiredDocument() != null) {
            onOptionRequiredDocument(question.getAnswerContainerDocsList(), convertView, options, options.getRequiredDocument(), question);
        } else {
            onOptionRequiredDocument(question.getAnswerContainerDocsList(), convertView, options, false, question);
        }
        try {
            if (question.getAnswer() instanceof Options) {
                if (question.getId() == options.getIdquestion()) {
                    //if(((Options) question.getAnswer()).getOption() != null && !((Options) question.getAnswer()).getOption().equalsIgnoreCase("SELECCIONAR") ) {
                    setAddVisible(holder, isAnswered, question);
                    question.setAnswer(options);
                    holder.spAnswer.setTag(options);
                    setError(holder.flError, question.isError());
                    if (!options.getOption().equalsIgnoreCase("SELECCIONAR"))
                        question.setError(false);
                    loadQuestionOptionsAnswers(question, options, holder.moreQuestionsContainer, isAnswered);
                    loadQuestionSpinner(options, holder, isAnswered);
                    //}
                }
            } else {
                setAddVisible(holder, isAnswered, question);
                question.setAnswer(options);
                holder.spAnswer.setTag(options);
                setError(holder.flError, question.isError());
                if (!options.getOption().equalsIgnoreCase("SELECCIONAR"))
                    question.setError(false);
                loadQuestionOptionsAnswers(question, options, holder.moreQuestionsContainer, isAnswered);
                loadQuestionSpinner(options, holder, isAnswered);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadQuestionSpinner(Options option, DynamicViewHolder holder, boolean isAnswered) {
        int sizeAdapter = holder.spAnswer.getAdapter().getCount();
        List<Options> options = new ArrayList<>();
        for (int i = 0; i < sizeAdapter; i++) {
            Options op = (Options) holder.spAnswer.getAdapter().getItem(i);
            options.add(op);
        }
        if (isAnswered) {
            int i = 0;
            for (Options op : options) {
                if (op.equals(option)) {
                    holder.spAnswer.setSelection(i);
                }
                i++;
            }
        }
    }

    private void setOnClickDelete(final View convertView, final DynamicViewHolder holder, final List<Options> options, final Questions question, final int position) {
        if (holder.ivDelete != null)
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserDatabaseHelper userHelper = UserDatabaseHelper.getHelper(getContext());
                    try {
                        Dao<TemporalForm, Long> daoTemp = userHelper.getDao(TemporalForm.class);
                        final Long idQuestion = question.getId();
                        TemporalForm temporalForm = daoTemp.queryBuilder().where().eq(TemporalForm.COL_ID_QUESTION, idQuestion)
                                .and().eq(TemporalForm.COL_ID_NOTA, getIdNota())
                                .and().eq(TemporalForm.COL_ID_TAREA, getIdTarea())
                                .queryForFirst();
                        if (temporalForm != null) {
                            String ans = temporalForm.getAnswers();
                            if (ans != null && !ans.isEmpty()) {
                                CustomDialog.dialogChoice(getContext(), new Interfaces.OnResponse<Object>() {
                                    @Override
                                    public void onResponse(int handlerCode, Object o) {
                                        boolean bool = (boolean) o;
                                        if (bool) {
//                                setSelectedOption(convertView, holder, option, questions, false, position);
                                            Log.i("", "");
                                            deleteOptionsQuuestions(idQuestion);
                                            deleteAction(convertView, holder, question);
                                        }
                                    }
                                }, 0, "¡Atención!", "Se tiene un cuestionario contestado para la pregunta\n\n\"" +
                                        question.getQuestion() + "\". \n\n" +
                                        "Sí deseas \"Continuar\" dichos cambios se perderán. \n\n" +
                                        "¿Desea continuar?", "Continuar", "Cancelar");
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        userHelper.close();
                    }
                    if (question.getAnswer() != null) {
                        Options optQNS = (Options) question.getAnswer();
                        Options op = options.get(0);
                        if (!optQNS.getOption().equalsIgnoreCase(op.getOption())) {
                            addMoreQuestionsBecauseOptionsHas(op, position, 2);
                            op.setAlreadyCharged(false);
                        }
                    }
                    deleteAction(convertView, holder, question);
                }
            });
    }

    private void deleteAction(final View convertView, final DynamicViewHolder holder, final Questions question) {
        onOptionRequiredComment(question.getAnswerContainerCommList(), convertView, null, false, question);
        onOptionRequiredDocument(question.getAnswerContainerDocsList(), convertView, null, false, question);
        setAddVisible(holder, false, question);
        holder.spAnswer.setSelection(0);
        holder.spAnswer.setTag(null);
        question.setAnswer(null);
        holder.moreQuestionsContainer.setVisibility(View.GONE);
    }

    private void setAddVisible(DynamicViewHolder holder, boolean added, Questions question) {
        if (holder.ivDelete != null) {
            AnswerDTO answerDTO = getPendingAnswer();
            if (answerDTO != null && answerDTO.getResponse() != null && !answerDTO.getResponse().equalsIgnoreCase("Seleccionar")) {
                holder.ivDelete.setVisibility(View.VISIBLE);
            } else {
                holder.ivDelete.setVisibility(View.GONE);
                /*if (added) {
                    holder.ivDelete.setVisibility(View.VISIBLE);
                } else {
                    holder.ivDelete.setVisibility(View.GONE);
                }*/
            }
            if (question.getAnswer() != null) {
                Options optQNS = (Options) question.getAnswer();
                if (optQNS.getOption() != null && !optQNS.getOption().equalsIgnoreCase("Seleccionar")) {
                    holder.ivDelete.setVisibility(View.VISIBLE);
                } else {
                    holder.ivDelete.setVisibility(View.GONE);
                }

            }
        }
    }
}
