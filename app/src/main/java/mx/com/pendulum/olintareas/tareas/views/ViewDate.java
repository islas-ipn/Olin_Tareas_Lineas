package mx.com.pendulum.olintareas.tareas.views;


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

public class ViewDate extends ParentViewMain {


    public ViewDate(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }

    public View getView(int position, View convertView, Questions question) {
        DynamicViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_date, null);
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
            convertView.setTag(holder);
        }
        super.configureDocsAndComments(question.getAnswerContainerDocsList(), question.getAnswerContainerCommList(), question.getQuestionContainerDocsList(), convertView, question, position);
        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);
        if (question.getOptions() != null) {
            super.addOptions(holder.tvAnswer, question);
        }
        setAddVisible(holder, super.setRespText(question.getAnswer(), holder.tvAnswer, false));
        super.setError(holder.flError, question.isError());
        if (question.getType().equalsIgnoreCase("DATE_READ")) {
            if (holder.tvAnswer != null) {
                String currentDate = Tools.getCurrentDate(" ");
                holder.tvAnswer.setText(currentDate);
                setClickAction(holder, question);
                setOnClickDelete(null, question.getOptions(), question);
                if (getPendingAnswer() != null && question.getAnswer() == null) {
                    Calendar cal = Tools.convertStringToCalendar(currentDate);
                    if (cal != null)
                        click(cal, holder, question);
                }
            }
        } else {
            setClickAction(holder, question);
            setOnClickDelete(holder, question.getOptions(), question);
            if (getPendingAnswer() != null && question.getAnswer() == null) {
                if (getPendingAnswer().getEpochDate() != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(getPendingAnswer().getEpochDate().getEpoch() * 1000);
                    click(cal, holder, question);
                }
            }
        }
        return convertView;
    }

    private void setClickAction(final DynamicViewHolder holder, final Questions question) {
        if (holder != null && holder.rlAnswer != null)
            if (question.getType().equalsIgnoreCase("DATE_READ"))
                holder.rlAnswer.setOnClickListener(null);
            else
                holder.rlAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        CustomDialog.getDate(getContext(), new Interfaces.OnResponse<Object>() {
                            @Override
                            public void onResponse(int handlerCode, Object o) {
                                if (o != null) {
                                    Calendar cal = (Calendar) o;
                                    click(cal, holder, question);
                                }
                            }
                        }, 0, "Seleccione una fecha", null);
                    }
                });
    }

    private void click(Calendar cal, DynamicViewHolder holder, Questions question) {
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        String date = Tools.getDate(cal, null);
        holder.tvAnswer.setText(date);
        holder.tvAnswer.setTag(cal);
        question.setError(false);
        question.setAnswer(cal);
        setAddVisible(holder, true);
        setError(holder.flError, question.isError());
    }

    private void setOnClickDelete(final DynamicViewHolder holder, final List<Options> options, final Questions question) {
        if (holder != null && holder.ivDelete != null)
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
