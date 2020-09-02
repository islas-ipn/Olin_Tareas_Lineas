package mx.com.pendulum.olintareas.tareas.views;


import android.app.Fragment;
import android.content.Context;
import android.view.View;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;

public class ViewLink extends ParentViewMain {
    public ViewLink(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }


    public View getView(int position, View convertView, Questions question) {
        DynamicViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_link, null);
        } else {
            holder = (DynamicViewHolder) convertView.getTag();
        }
        if (holder == null) {
            holder = new DynamicViewHolder();
            holder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
            holder.ivCopyLink = convertView.findViewById(R.id.ivCopyLink);
            holder.ivGoLink = convertView.findViewById(R.id.ivGoLink);
            holder.row = convertView.findViewById(R.id.row);
            convertView.setTag(holder);
        }
        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);
        if (holder.row != null && holder.ivCopyLink != null && holder.ivGoLink != null) {
            holder.ivCopyLink.setOnClickListener(getLinkClick(question, 1));
            holder.ivGoLink.setOnClickListener(getLinkClick(question, 2));
        }
        return convertView;
    }
}