package mx.com.pendulum.olintareas.tareas.views;


import android.app.Fragment;
import android.content.Context;
import android.view.View;

import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;
import mx.com.pendulum.olintareas.ui.dialog.tareas.MultichoiceDialog;

public class ViewMultichoice extends ParentViewMain {


    public ViewMultichoice(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }


    public View getView(int position, View convertView, Questions question) {
        DynamicViewHolder holder = null;
        if (convertView == null) {

            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_multichoice, null);


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



        super.configureDocsAndComments(question.getAnswerContainerDocsList(), question.
                getAnswerContainerCommList(), question.
                getQuestionContainerDocsList(), convertView, question, position);


        setClickAction(convertView, holder, question);
        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);
        if (question.getOptions() != null)

        {
            super.addOptions(holder.tvAnswer, question);
        }

        setOnClickDelete(convertView, holder, question.getOptions(), question);

        super.

                setError(holder.flError, question.isError());

        setAddVisible(holder, super.setRespText(question.getAnswer(), holder.tvAnswer, null));

        return convertView;
    }


    private void setClickAction(final View convertView, final DynamicViewHolder holder, final Questions questions) {
        if (holder.rlAnswer != null)
            holder.rlAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    List<Options> options = questions.getOptions();
                    if (options == null) return;


//                Collections.sort(options, new Comparator<Options>() {
//                    @Override
//                    public int compare(Options o1, Options o2) {
//                        return o1.getOrder().compareTo(o2.getOrder());
//                    }
//                });

                    //  options.add(0, ViewList.super.getExtraOption(null));


                    new MultichoiceDialog(getContext(), new Interfaces.OnResponse<List<Options>>() {

                        @Override
                        public void onResponse(int handlerCode, List<Options> options) {
                            verifySelectedOption(convertView, holder, options, questions);

                        }
                    }, 0, options).showDialog();

                }
            });
    }


    private void verifySelectedOption(View convertView, DynamicViewHolder holder, List<Options> options, Questions question) {

        if (options == null) return;

        if (options.isEmpty()) {
            return;
        }


        Options requiredCommentOp = null;
        Options reqiredDocumentOP = null;
        String answerText = "";

        for (Options option : options) {
            if (option.getRequiredComment() != null) {
                if (option.getRequiredComment()) {
                    requiredCommentOp = option;
                }
            }
            if (option.getRequiredDocument() != null) {
                if (option.getRequiredDocument()) {
                    reqiredDocumentOP = option;
                }
            }
            answerText += " " + option.getOption() + ";\n";

        }

        answerText = answerText.substring(0, answerText.length() - 2);

        if (requiredCommentOp != null) {
            onOptionRequiredComment(question.getAnswerContainerCommList(), convertView, requiredCommentOp, requiredCommentOp.getRequiredComment(), question);
        } else {
            onOptionRequiredComment(question.getAnswerContainerCommList(), convertView, null, false, question);
        }
        if (reqiredDocumentOP != null) {
            onOptionRequiredDocument(question.getAnswerContainerDocsList(), convertView, reqiredDocumentOP, reqiredDocumentOP.getRequiredDocument(), question);
        } else {
            onOptionRequiredDocument(question.getAnswerContainerDocsList(), convertView, null, false, question);
        }
        setAddVisible(holder, true);
        holder.tvAnswer.setText(answerText);
        holder.tvAnswer.setTag(options);
        question.setError(false);
        question.setAnswer(options);
        setError(holder.flError, question.isError());

    }

    private void setOnClickDelete(final View convertView, final DynamicViewHolder holder, final List<Options> options, final Questions question) {
        if (holder.ivDelete != null)
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionRequiredComment(question.getAnswerContainerCommList(), convertView, null, false, question);
                    onOptionRequiredDocument(question.getAnswerContainerDocsList(), convertView, null, false, question);
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
