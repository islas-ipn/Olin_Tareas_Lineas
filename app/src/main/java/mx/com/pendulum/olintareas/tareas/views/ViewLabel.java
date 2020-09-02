package mx.com.pendulum.olintareas.tareas.views;


import android.app.Fragment;
import android.content.Context;
import android.view.View;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;

public class ViewLabel extends ParentViewMain {

    public ViewLabel(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }

    public View getView(int position, View convertView, Questions question) {
        DynamicViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_label, null);
        } else {
            holder = (DynamicViewHolder) convertView.getTag();
        }
        if (holder == null) {
            holder = new DynamicViewHolder();
            holder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
            holder.flError = convertView.findViewById(R.id.flError);
            holder.tvAnswer = convertView.findViewById(R.id.tvAnswer);
            convertView.setTag(holder);
        }
        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);
        return convertView;
    }
}