package mx.com.pendulum.olintareas.tareas.views;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.view.View;

import java.util.Calendar;
import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;
import mx.com.pendulum.utilities.Tools;

public class ViewDatetime extends ParentViewMain {

    public ViewDatetime(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }

    public View getView(int position, View convertView, Questions question) {
        DynamicViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_datetime, null);
        } else {
            holder = (DynamicViewHolder) convertView.getTag();
        }
        if (holder == null) {
            holder = new DynamicViewHolder();
            holder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
            holder.tvAnswer = convertView.findViewById(R.id.tvAnswer);
            holder.flError = convertView.findViewById(R.id.flError);
            holder.ivDelete = convertView.findViewById(R.id.ivDelete);
            convertView.setTag(holder);
        }
        super.configureDocsAndComments(question.getAnswerContainerDocsList(), question.getAnswerContainerCommList(), question.getQuestionContainerDocsList(), convertView, question, position);
        setCliclDATETIME(holder, question);
        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);
        if (question.getOptions() != null) {
            super.addOptions(holder.tvAnswer, question);
        }
        setOnClickDelete(holder, question.getOptions(), question);
        super.setError(holder.flError, question.isError());
        setAddVisible(holder, super.setRespText(question.getAnswer(), holder.tvAnswer, null));
        if (getPendingAnswer() != null && question.getAnswer() == null) {
            if (getPendingAnswer().getEpochDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(getPendingAnswer().getEpochDate().getEpoch() * 1000);
                click(cal, holder, question);
            }
        }
        return convertView;
    }

    @SuppressLint("SetTextI18n")
    private void click(Calendar cal, DynamicViewHolder holder, Questions question) {
        String date = Tools.getDate(cal, null);
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        String time = Tools.getTime(cal);
        holder.tvAnswer.setText(date + " " + time + " hrs.");
        holder.tvAnswer.setTag(cal);
        question.setError(false);
        question.setAnswer(cal);
        setAddVisible(holder, true);
        setError(holder.flError, question.isError());
    }

    private void setCliclDATETIME(final DynamicViewHolder holder, final Questions questions) {
        if (holder.tvAnswer != null)
            holder.tvAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    CustomDialog.getDate(getContext(), new Interfaces.OnResponse<Object>() {
                        @Override
                        public void onResponse(int handlerCode, Object o) {
                            if (o != null) {
                                //  String f = "^$|^(([0-9])|([0-2][0-9])|([3][0-1]))\\-(Ene|Feb|Mar|Abr|May|Jun|Jul|Ago|Sep|Oct|Nov|Dic)\\-\\d{4}$";
                                final Calendar cal = (Calendar) o;
                                waitSeconds(new Interfaces.OnResponse<Boolean>() {
                                    @Override
                                    public void onResponse(int handlerCode, Boolean aBoolean) {
                                        selectTime(holder, questions, cal);
                                    }
                                });


                            }
                        }
                    }, 0, "Seleccione una fecha", null);
                }
            });
    }

    private void selectTime(final DynamicViewHolder holder, final Questions question, final Calendar calendar) {
        CustomDialog.getTime(getContext(), new Interfaces.OnResponse<Object>() {
            @Override
            public void onResponse(int handlerCode, Object o) {
                if (o != null) {
                    String date = Tools.getDate(calendar, null);
                    Calendar cal = (Calendar) o;
                    calendar.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
                    String time = Tools.getTime(calendar);
                    holder.tvAnswer.setText(date + " " + time + " hrs.");
                    holder.tvAnswer.setTag(calendar);
                    question.setError(false);
                    question.setAnswer(calendar);
                    setAddVisible(holder, true);
                    setError(holder.flError, question.isError());
                }
            }
        }, 0, "Seleccione una hora", null);
    }

    private void setOnClickDelete(final DynamicViewHolder holder, final List<Options> options, final Questions question) {
        if (holder.ivDelete != null)
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAddVisible(holder, false);
                    holder.tvAnswer.setText("");
                    holder.tvAnswer.setTag(null);
                    question.setAnswer(null);
                }
            });
    }

    private void setAddVisible(DynamicViewHolder holder, boolean added) {
        if (holder.ivDelete != null)
            if (added) {
                holder.ivDelete.setVisibility(View.VISIBLE);
            } else {
                holder.ivDelete.setVisibility(View.GONE);
            }
    }
}
