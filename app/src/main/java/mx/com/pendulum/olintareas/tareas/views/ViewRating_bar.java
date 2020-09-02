package mx.com.pendulum.olintareas.tareas.views;


import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;

import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;

public class ViewRating_bar extends ParentViewMain {
    public ViewRating_bar(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }

    public View getView(int position, View convertView, Questions question) {
        DynamicViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_rating_bar, null);
        } else {
            holder = (DynamicViewHolder) convertView.getTag();
        }
        if (holder == null) {
            holder = new DynamicViewHolder();
            holder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
            holder.rbAnswer = convertView.findViewById(R.id.rbAnswer);
            holder.flError = convertView.findViewById(R.id.flError);
            holder.ivDelete = convertView.findViewById(R.id.ivDelete);
            convertView.setTag(holder);
        }
        super.configureDocsAndComments(question.getAnswerContainerDocsList(), question.getAnswerContainerCommList(), question.getQuestionContainerDocsList(), convertView, question, position);
        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);
        if (question.getOptions() != null) {
            super.addOptions(holder.tvAnswer, question);
        }
        setListner(holder, question);
        setOnClickDelete(holder, question.getOptions(), question);
        super.setError(holder.flError, question.isError());
        setAddVisible(holder, super.setRespText(question.getAnswer(), holder.tvAnswer, null));
        if (getPendingAnswer() != null && question.getAnswer() == null) {
            String response = getPendingAnswer().getResponse();
            float rating = 0;
            try {
                rating = Float.parseFloat(response);
            } catch (Exception ignored) {
            }
            holder.rbAnswer.setRating(rating);
            selectedRate(rating, question, holder);
        }
        return convertView;
    }

    private void setListner(final DynamicViewHolder holder, final Questions question) {
        if (holder.rbAnswer == null) return;
        holder.rbAnswer.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        int intRating = (int) rating;
                        if (intRating != rating) {
                            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
                                intRating++; // TODO Comprobar si funciona en diferentes a Nougat 7
                            holder.rbAnswer.setRating(intRating);
                            selectedRate(intRating, question, holder);
                        }
                    }
                }
        );
    }

    private void selectedRate(float rating, Questions question, DynamicViewHolder holder) {
        holder.rbAnswer.setTag(rating);
        question.setError(false);
        question.setAnswer(rating);
        setAddVisible(holder, true);
        setError(holder.flError, question.isError());
    }

    @SuppressWarnings("unused")
    private void setOnClickDelete(final DynamicViewHolder holder, final List<Options> options, final Questions question) {
        if (holder.ivDelete != null)
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.rbAnswer.setRating(0f);
                    holder.rbAnswer.setTag(null);
                    question.setAnswer(null);
                    setAddVisible(holder, false);
                }
            });
    }

    private void setAddVisible(DynamicViewHolder holder, boolean added) {
        if (holder.ivDelete != null)
            if (added) {
                holder.ivDelete.setVisibility(View.VISIBLE);
            } else {
                holder.ivDelete.setVisibility(View.INVISIBLE);
            }
    }
}
