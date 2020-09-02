package mx.com.pendulum.olintareas.tareas.views;


import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.dto.tareasV2.TemporalForm;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;
import mx.com.pendulum.olintareas.ui.dialog.tareas.ListDialog;

public class ViewList extends ParentViewMain {

    public ViewList(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }

    public View getView(int position, View convertView, Questions question) {
        DynamicViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_list, null);
        } else {
            holder = (DynamicViewHolder) convertView.getTag();
        }
        if (holder == null) {
            holder = new DynamicViewHolder();
            holder.rlAnswer = convertView.findViewById(R.id.rlAnswer);
            holder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
            holder.tvAnswer = convertView.findViewById(R.id.tvAnswer);
            holder.flError = convertView.findViewById(R.id.flError);
            holder.ivDelete = convertView.findViewById(R.id.ivDelete);
            holder.moreQuestions = convertView.findViewById(R.id.moreQuestions);
            holder.moreQuestionsContainer = convertView.findViewById(R.id.moreQuestionsContainer);
            convertView.setTag(holder);
        }
        //  holder.moreQuestionsContainer.setVisibility(View.GONE);
        super.configureDocsAndComments(question.getAnswerContainerDocsList(), question.getAnswerContainerCommList(), question.getQuestionContainerDocsList(), convertView, question, position);
        if (getPendingAnswer() != null && question.getAnswer() == null) {
            for (Options option : question.getOptions()) {
                if (option.getId().equals(getPendingAnswer().getId_option())) {
                    setSelectedOption(convertView, holder, option, question, true, position);
                    break;
                }
            }
        }
        if (question.getAnswer() != null) {
//            LinearLayout row = convertView.findViewById(ID_OPTION_REQUIRED_COMMENT);
//            if (row.getVisibility() != View.VISIBLE) {
            Options option = (Options) question.getAnswer();
//                row.setVisibility(View.VISIBLE);
            setSelectedOption(convertView, holder, option, question, true, position);
//            }
        }
        setClickAction(convertView, holder, question, position);
        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);
        if (question.getOptions() != null) {
            super.addOptions(holder.tvAnswer, question);
        }
        setOnClickDelete(convertView, holder, question.getOptions(), question);
        super.setError(holder.flError, question.isError());
        setAddVisible(holder, super.setRespText(question.getAnswer(), holder.tvAnswer, null));
        return convertView;
    }

    private void setClickAction(final View convertView, final DynamicViewHolder holder, final Questions questions, final int position) {
        if (holder.rlAnswer != null)
            holder.rlAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    List<Options> options = questions.getOptions();
                    if (options == null) return;
                    new ListDialog(getContext(), new Interfaces.OnResponse<Options>() {
                        @Override
                        public void onResponse(int handlerCode, final Options option) {
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
                            setSelectedOption(convertView, holder, option, questions, false, position);
                            holder.moreQuestions.setTag(option);
                        }
                    }, 0, options).showDialog();
                }
            });
        if (holder.moreQuestions != null) {
            holder.moreQuestions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Options option = (Options) questions.getAnswer();
                    // ABRE QUESTION DE PREGUNTA EN OTRA VENTANA
                    //loadQuestionOptions(questions, option, holder.moreQuestionsContainer, false, position);
                }
            });
            Options option = (Options) holder.moreQuestions.getTag();
            if (option != null) {
                //setSelectedOption(convertView, holder, option, questions, option.getRequiredComment(), position);
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
            setAddVisible(holder, true);
            question.setError(false);
            question.setAnswer(options);
            holder.tvAnswer.setText(options.getOption());
            holder.tvAnswer.setTag(options);
            setError(holder.flError, question.isError());
            loadQuestionOptionsAnswers(question, options, holder.moreQuestionsContainer, isAnswered);
            // ABRE QUESTION DE PREGUNTA EN OTRA VENTANA
            //loadQuestionOptions(question, options, holder.moreQuestionsContainer, isAnswered, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOnClickDelete(final View convertView, final DynamicViewHolder holder, final List<Options> options, final Questions question) {
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
                    deleteAction(convertView, holder, question);
                }
            });
    }

    private void deleteAction(final View convertView, final DynamicViewHolder holder, final Questions question) {
        onOptionRequiredComment(question.getAnswerContainerCommList(), convertView, null, false, question);
        onOptionRequiredDocument(question.getAnswerContainerDocsList(), convertView, null, false, question);
        setAddVisible(holder, false);
        holder.tvAnswer.setText("");
        holder.tvAnswer.setTag(null);
        question.setAnswer(null);
        holder.moreQuestionsContainer.setVisibility(View.GONE);
    }

    private void setAddVisible(DynamicViewHolder holder, boolean added) {
        if (holder.ivDelete != null)
            if (added) {
                holder.ivDelete.setVisibility(View.VISIBLE);
            } else {
                holder.ivDelete.setVisibility(View.GONE);
            }
    }

//    public static class DynamicViewHolder {
//        CustomTextView tvQuestion;
//        public CustomTextView tvAnswer;
//        public FrameLayout flError;
//        ImageView ivDelete;
//        View rlAnswer;
//        public List<View> questionContainerDocsList;
//        public List<View> answerContainerDocsList;
//        public List<View> answerContainerCommList;
//    }
}
