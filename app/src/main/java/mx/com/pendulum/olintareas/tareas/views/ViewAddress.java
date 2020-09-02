package mx.com.pendulum.olintareas.tareas.views;

import android.app.Fragment;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;

public class ViewAddress extends ParentViewMain {

    public static final String SEPATATOR = "__";

    public ViewAddress(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }

    public View getView(int position, View convertView, Questions question) {
        if (getPendingAnswer() != null && question.getObject() == null) {
            question.setObject(getPendingAnswer());
        }
        DynamicViewHolder holder = null;
        if (convertView == null)
            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_address, null);
        else
            holder = (DynamicViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new DynamicViewHolder();
            holder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
            holder.tvAnswer = convertView.findViewById(R.id.tvAnswer);
            holder.flError = convertView.findViewById(R.id.flError);
            holder.row = convertView.findViewById(R.id.row);
            holder.ivDelete = convertView.findViewById(R.id.ivDelete);
            holder.ivAddAddress = convertView.findViewById(R.id.ivAddAddress);
            convertView.setTag(holder);
        }
        // TODO QUITAR HARDCODEO CUANDO SE HABILITE EL REQUERIDO DESDE LA BASE DE DAOTS
        question.setRequired(true);
        // TODO QUITAR HARDCODEO CUANDO SE HABILITE EL REQUERIDO DESDE LA BASE DE DAOTS
        super.configureDocsAndComments(question.getAnswerContainerDocsList(), question.getAnswerContainerCommList(), question.getQuestionContainerDocsList(), convertView, question, position);
        if (question.getObject() != null) {
            //super.updateQuestionRequiredDocument(question.getObject(), (LinearLayout) convertView.findViewById(ParentViewMain.ID_QUESTION_REQUIRED_DOCUMENT), question);
            if (question.getObject() instanceof String) {
                String idAddress = (String) question.getObject();
                setAddress(holder, question, idAddress);
            }
        }
        if (getPendingAnswer() != null && question.getAnswer() == null) {
            try {
                AnswerDTO answerDTO = getPendingAnswer();
                if(answerDTO != null && answerDTO.getValue() != null) {
                    String idAddress = getPendingAnswer().getValue();
                    setAddress(holder, question, idAddress);
                }
            } catch (Exception ignored) {
            }
        }
        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);
        if (question.getOptions() != null) {
            super.addOptions(holder.tvAnswer, question);
        }
        setOnClickDelete(holder, question);
        super.setError(holder.flError, question.isError());
        if (holder.row != null && holder.ivAddAddress != null) {
            holder.ivAddAddress.setOnClickListener(
                    getAddAddress(convertView, holder.ivAddAddress, question, position, 0));
        }
        setAddVisible(holder, super.setRespText(question.getAnswer(), holder.tvAnswer, null));
        return convertView;
    }

    private void setAddress(DynamicViewHolder holder, Questions question, String idAddress) {
        holder.tvAnswer.setText(idAddress);
        holder.tvAnswer.setTag(idAddress);
        question.setError(false);
        setError(holder.flError, question.isError());
        question.setAnswer(idAddress);
        setAddVisible(holder, true);
    }

    private void setAddVisible(DynamicViewHolder holder, boolean added) {
        if (holder.ivDelete != null)
            if (added) {
                holder.ivDelete.setVisibility(View.VISIBLE);
            } else {
                holder.ivDelete.setVisibility(View.GONE);
            }
    }

    private void setOnClickDelete(final DynamicViewHolder holder, final Questions question) {
        if (holder.ivDelete != null)
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.tvAnswer.setText("");
                    holder.tvAnswer.setTag(null);
                    question.setAnswer(null);
                    setAddVisible(holder, false);
                }
            });
    }
}