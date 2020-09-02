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

public class ViewDtetime_stskt_end extends ParentViewMain {

    public ViewDtetime_stskt_end(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }


    public View getView(int position, View convertView, Questions question) {
        DynamicViewHolder holder = null;

        if (convertView == null) {

            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_dtetime_stskt_end, null);

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


//        setAddVisible(holder, super.setRespText(question.getAnswer(), holder.tvAnswer, null));

        return convertView;
    }

    private void setClickAction(final DynamicViewHolder holder, final Questions questions) {
        if (holder.rlAnswerStart != null)
            holder.rlAnswerStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    CustomDialog.getDate(getContext(), new Interfaces.OnResponse<Object>() {
                        @Override
                        public void onResponse(int handlerCode, Object o) {

                            if (o != null) {
                                final Calendar cal = (Calendar) o;
                                waitSeconds(new Interfaces.OnResponse<Boolean>() {
                                    @Override
                                    public void onResponse(int handlerCode, Boolean aBoolean) {
                                        selectTimeStart(holder, questions, cal);
                                    }
                                });


                            }
                        }
                    }, 0, "Seleccione una fecha de inicio", null);
                }
            });

        if (holder.rlAnswerEnd != null)
            holder.rlAnswerEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {


                    if (holder.tvAnswer.getTag() == null) {

                        Toast.makeText(getContext(), "Primero selecciona la fecha y hora de inicio", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Calendar strStartDate = (Calendar) holder.tvAnswer.getTag();

                    CustomDialog.getDate(getContext(), new Interfaces.OnResponse<Object>() {
                        @Override
                        public void onResponse(int handlerCode, Object o) {

                            if (o != null) {
                                final Calendar cal = (Calendar) o;
                                waitSeconds(new Interfaces.OnResponse<Boolean>() {
                                    @Override
                                    public void onResponse(int handlerCode, Boolean aBoolean) {
                                        selectTimeFin(holder, questions, cal);
                                    }
                                });

                            }
                        }
                    }, 0, "Seleccione una fecha de fin", strStartDate);
                }
            });
    }

    private void selectTimeStart(final DynamicViewHolder holder, final Questions question, final Calendar calendar) {
        CustomDialog.getTime(getContext(), new Interfaces.OnResponse<Object>() {
            @Override
            public void onResponse(int handlerCode, Object o) {
                if (o != null) {

                    String date = Tools.getDate(calendar, null);

//                    CustomTextView btn = (CustomTextView) holder.tvAnswer;
                    Calendar cal = (Calendar) o;


                    calendar.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));


                    String time = Tools.getTime(calendar);
                    holder.tvAnswer.setText(date + " " + time + " hrs.");
                    holder.tvAnswer.setTag(calendar);
                    question.setError(false);
                    question.setAnswer(calendar);
                    setError(holder.flError, question.isError());
                    waitSeconds(new Interfaces.OnResponse<Boolean>() {
                        @Override
                        public void onResponse(int handlerCode, Boolean aBoolean) {
                            holder.rlAnswerEnd.performClick();
                        }
                    });

                    setAddVisibleStart(holder, true);
                }
            }
        }, 0, "Seleccione una hora de inicio", null);
    }

    private void selectTimeFin(final DynamicViewHolder holder, final Questions question, final Calendar selectedDate) {


        Calendar strStartDateTime = (Calendar) holder.tvAnswer.getTag();

        if (strStartDateTime.get(Calendar.YEAR) != selectedDate.get(Calendar.YEAR) ||  //YEAR
                strStartDateTime.get(Calendar.MONTH) != selectedDate.get(Calendar.MONTH) || //MONTH
                strStartDateTime.get(Calendar.DAY_OF_MONTH) != selectedDate.get(Calendar.DAY_OF_MONTH) //DAY
                ) {
            strStartDateTime = null;
        }


        CustomDialog.getTime(getContext(), new Interfaces.OnResponse<Object>() {
            @Override
            public void onResponse(int handlerCode, Object o) {
                if (o != null) {
                    Calendar startDate = (Calendar) holder.tvAnswer.getTag();
                    Calendar cal = (Calendar) o;

                    selectedDate.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
                    selectedDate.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));

                    if (startDate.before(selectedDate)) {
                        String date = Tools.getDate(selectedDate, null);

                        String time = Tools.getTime(selectedDate);
                        holder.tvAnswer2.setText(date + " " + time + " hrs.");
                        holder.tvAnswer2.setTag(cal);
                        question.setError(false);
                        setError(holder.flError, question.isError());
                        question.setAnswer2(cal);
                        setAddVisibleEnd(holder, true);
                    } else {
                        holder.tvAnswer2.setText("");
                        holder.tvAnswer2.setTag(null);
                        question.setAnswer2(null);
                        setAddVisibleEnd(holder, false);
                    }

                }
            }
        }, 0, "Seleccione una hora de fin", strStartDateTime);
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
//        public FrameLayout flError;
//        public  CustomTextView tvAnswer;
//        public CustomTextView tvAnswer2;
//        ImageView ivDelete;
//        ImageView ivDelete2;
//        View rlAnswerStart;
//        View rlAnswerEnd;
//        public List<View> questionContainerDocsList;
//        public List<View> answerContainerDocsList;
//        public List<View> answerContainerCommList;
//    }
}
