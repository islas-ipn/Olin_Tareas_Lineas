package mx.com.pendulum.olintareas.tareas.views;


import android.app.Fragment;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;

public class ViewText extends ParentViewMain {

    public ViewText(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }

    public View getView(int position, View convertView, Questions question) {
        if (getPendingAnswer() != null && question.getAnswer() == null) {
            question.setAnswer(getPendingAnswer().getResponse());
        }
        DynamicViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_text, null);
        } else {
            holder = (DynamicViewHolder) convertView.getTag();
        }
        if (holder == null) {
            holder = new DynamicViewHolder();
            holder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
            holder.etAnswer = convertView.findViewById(R.id.etAnswer);
            holder.flError = convertView.findViewById(R.id.flError);
            holder.ivDelete = convertView.findViewById(R.id.ivDelete);
            convertView.setTag(holder);
        }
        if (holder.etAnswer != null) {
            holder.etAnswer.clearTextChangedListeners();
        }
        super.configureDocsAndComments(question.getAnswerContainerDocsList(), question.getAnswerContainerCommList(), question.getQuestionContainerDocsList(), convertView, question, position);
        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);
        if (question.getOptions() != null && question.getOptions().size() > 0) {
            super.addOptions(holder.etAnswer, question);
            super.configureText(question.getOptions().get(0), holder.etAnswer);
        }
        setAction(holder, question);
        setAddVisible(holder, super.setRespText(question.getAnswer(), holder.etAnswer, null));
        setOnClick(holder);
        super.setError(holder.flError, question.isError());
        return convertView;
    }

    private void setOnClick(final DynamicViewHolder holder) {
        if (holder.ivDelete != null)
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAddVisible(holder, false);
                    holder.etAnswer.setText("");
                }
            });
    }

    private void setAction(final DynamicViewHolder holder, final Questions question) {
        if (holder.etAnswer == null) return;
        holder.etAnswer.addTextChangedListener(new TextWatcher() {
            private int start;
            private int end;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                this.start = s.toString().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.end = s.toString().length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    holder.etAnswer.setTag(null);
                    question.setAnswer(null);
                    setAddVisible(holder, false);
                } else {
                    holder.etAnswer.setTag(s.toString());
                    question.setAnswer(s.toString());
                    setAddVisible(holder, true);
                    if (this.start != this.end) {
                        question.setError(false);
                    }
                    setError(holder.flError, question.isError());
                }
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
