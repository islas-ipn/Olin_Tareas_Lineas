package mx.com.pendulum.olintareas.tareas.views;


import android.app.Fragment;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;
import mx.com.pendulum.utilities.Tools;

public class ViewTime_stskt_end extends ParentViewMain {

    public ViewTime_stskt_end(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }


    public View getView(int position, View convertView, Questions question) {

        DynamicViewHolder holder = null;
        if (convertView == null) {

            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_time_stskt_end, null);

        } else {
            holder = (DynamicViewHolder) convertView.getTag();
        }

        if (holder == null) {
            holder = new DynamicViewHolder();
            holder.rlAnswerStart = convertView.findViewById(R.id.rlAnswerStart);
            holder.rlAnswerEnd = convertView.findViewById(R.id.rlAnswerEnd);
            holder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
            holder.tvAnswer = convertView.findViewById(R.id.tvAnswer);
            holder.tvAnswer2 = convertView.findViewById(R.id.tvAnswer2);
            holder.flError = convertView.findViewById(R.id.flError);
            holder.ivDelete = convertView.findViewById(R.id.ivDelete);
            holder.ivDelete2 = convertView.findViewById(R.id.ivDelete2);
            convertView.setTag(holder);


        }

        super.configureDocsAndComments(question.getAnswerContainerDocsList(), question.getAnswerContainerCommList(), question.getQuestionContainerDocsList(), convertView, question, position);


        setClickAction(holder, question);

        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);


        if (question.getOptions() != null) {
            super.addOptions(holder.tvAnswer, question);
            super.addOptions(holder.tvAnswer2, question);
        }
        setAddVisibleStart(holder, false);
        setAddVisibleEnd(holder, false);
        setOnClickDelete(holder, question.getOptions(), question);
        super.setError(holder.flError, question.isError());


//      setAddVisible(holder, super.setRespText(question.getAnswer(), holder.tvAnswer,true));

        return convertView;
    }


    private void setClickAction(final DynamicViewHolder holder, final Questions question) {
        if (holder.rlAnswerStart != null)
            holder.rlAnswerStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    CustomDialog.getTime(getContext(), new Interfaces.OnResponse<Object>() {
                        @Override
                        public void onResponse(int handlerCode, Object o) {

                            if (o != null) {

                                //  String f = "^$|^(([0-9])|([0-2][0-9])|([3][0-1]))\\-(Ene|Feb|Mar|Abr|May|Jun|Jul|Ago|Sep|Oct|Nov|Dic)\\-\\d{4}$";
                                setAddVisibleStart(holder, true);
                                Calendar cal = (Calendar) o;
                                cal.set(Calendar.MONTH, 0);
                                cal.set(Calendar.DAY_OF_MONTH, 0);
                                cal.set(Calendar.YEAR, 0);
                                String time = Tools.getTime(cal);
                                holder.tvAnswer.setText(time + " hrs.");
                                holder.tvAnswer.setTag(cal);
                                question.setError(false);
                                question.setAnswer(cal);
                                setError(holder.flError, question.isError());
                                waitSeconds(new Interfaces.OnResponse<Boolean>() {
                                    @Override
                                    public void onResponse(int handlerCode, Boolean aBoolean) {
                                        holder.rlAnswerEnd.performClick();
                                    }
                                });
                            }
                        }
                    }, 0, "Seleccione una hora de inicio", null);
                }
            });

        if (holder.rlAnswerEnd != null)
            holder.rlAnswerEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View ve) {

                    if (holder.tvAnswer.getTag() == null) {

                        Toast.makeText(getContext(), "Primero selecciona la hora de inicio", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final Calendar strStartDate = (Calendar) holder.tvAnswer.getTag();
                    CustomDialog.getTime(getContext(), new Interfaces.OnResponse<Object>() {
                        @Override
                        public void onResponse(int handlerCode, Object o) {

                            if (o != null) {

                                //  String f = "^$|^(([0-9])|([0-2][0-9])|([3][0-1]))\\-(Ene|Feb|Mar|Abr|May|Jun|Jul|Ago|Sep|Oct|Nov|Dic)\\-\\d{4}$";
                                Calendar startDate = (Calendar) holder.tvAnswer.getTag();
                                Calendar cal = (Calendar) o;
                                cal.set(Calendar.MONTH, 0);
                                cal.set(Calendar.DAY_OF_MONTH, 0);
                                cal.set(Calendar.YEAR, 0);

                                if (startDate.before(cal)) {

                                    String time = Tools.getTime(cal);
                                    holder.tvAnswer2.setText(time + " hrs.");
                                    holder.tvAnswer2.setTag(cal);
                                    question.setError(false);
                                    question.setAnswer2(cal);
                                    setAddVisibleEnd(holder, true);
                                    setError(holder.flError, question.isError());
                                } else {
                                    holder.tvAnswer2.setText("");
                                    holder.tvAnswer2.setTag(null);
                                    question.setAnswer2(null);
                                    setAddVisibleEnd(holder, false);
                                }


                            }
                        }
                    }, 0, "Seleccione una hora de fin", strStartDate);
                }
            });
    }


    private void setOnClickDelete(final DynamicViewHolder holder, final List<Options> options, final Questions question) {
        if (holder.ivDelete != null)
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAddVisibleStart(holder, false);
                    holder.tvAnswer.setText("");
                    holder.tvAnswer.setTag(null);
                    question.setAnswer(null);
                    holder.ivDelete2.performClick();
                }
            });
        if (holder.ivDelete2 != null)
            holder.ivDelete2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAddVisibleEnd(holder, false);
                    holder.tvAnswer2.setText("");
                    holder.tvAnswer2.setTag(null);
                    question.setAnswer2(null);
                }
            });
    }

    private void setAddVisibleStart(DynamicViewHolder holder, boolean added) {
        if (holder.ivDelete != null)
            if (added) {
                holder.ivDelete.setVisibility(View.VISIBLE);
            } else {
                holder.ivDelete.setVisibility(View.GONE);
            }
    }

    private void setAddVisibleEnd(DynamicViewHolder holder, boolean added) {
        if (holder.ivDelete2 != null)
            if (added) {
                holder.ivDelete2.setVisibility(View.VISIBLE);
            } else {
                holder.ivDelete2.setVisibility(View.GONE);
            }
    }

//    public static class DynamicViewHolder {
//        CustomTextView tvQuestion;
//        public CustomTextView tvAnswer;
//        public CustomTextView tvAnswer2;
//        public FrameLayout flError;
//        ImageView ivDelete;
//        ImageView ivDelete2;
//        View rlAnswerStart;
//        View rlAnswerEnd;
//        public List<View> questionContainerDocsList;
//        public List<View> answerContainerDocsList;
//        public List<View> answerContainerCommList;
//    }
}
