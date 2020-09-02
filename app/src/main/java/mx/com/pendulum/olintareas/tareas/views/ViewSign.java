package mx.com.pendulum.olintareas.tareas.views;

import android.app.Fragment;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;

public class ViewSign extends ParentViewMain {

    public ViewSign(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }

    public View getView(int position, View convertView, Questions question) {
        if (getPendingAnswer() != null && question.getAnswer() == null) {
            question.setObject(getPendingAnswer());
        }
        if (question.getAnswer() != null)
            question.setObject(question.getAnswer());
        DynamicViewHolder holder = null;
        if (convertView == null)
            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_sign, null);
        else
            holder = (DynamicViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new DynamicViewHolder();
            holder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
            holder.flError = convertView.findViewById(R.id.flError);
            holder.tvAnswer = convertView.findViewById(R.id.tvAnswer);
            holder.ivAddSign = convertView.findViewById(R.id.ivAddSign);
            holder.row = convertView.findViewById(R.id.row);
            convertView.setTag(holder);
        }
        super.configureDocsAndComments(question.getAnswerContainerDocsList(), question.getAnswerContainerCommList(), question.getQuestionContainerDocsList(), convertView, question, position);
        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);
        if (question.getOptions() != null)
            super.addOptions(holder.tvAnswer, question);
        super.setError(holder.flError, question.isError());
        if (question.getObject() != null)
            super.updateQuestionRequiredDocument(question.getObject(), (LinearLayout) convertView.findViewById(ParentViewMain.ID_QUESTION_REQUIRED_DOCUMENT), question);
        if (holder.row != null && holder.ivAddSign != null)
            holder.ivAddSign.setOnClickListener(getSignatureClick(convertView, holder.ivAddSign, question, position));
        return convertView;
    }
}