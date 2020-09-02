package mx.com.pendulum.olintareas.tareas.views;


import android.app.Fragment;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Autocomplete;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.ui.adapter.tareas.AutocompleteAdapter;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;
import mx.com.pendulum.utilities.views.CustomAutoCompleteTextView;

public class ViewAutocomplete extends ParentViewMain {

    public ViewAutocomplete(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }


    public View getView(int position, View convertView, Questions question) {


        DynamicViewHolder holder = null;
        if (convertView == null) {

            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_autocomplete, null);

        } else {
            holder = (DynamicViewHolder) convertView.getTag();
        }


        if (holder == null) {

            holder = new DynamicViewHolder();
            holder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
            holder.actvAnswer = convertView.findViewById(R.id.etAnswer);
            holder.flError = convertView.findViewById(R.id.flError);
            holder.ivDelete = convertView.findViewById(R.id.ivDelete);

            convertView.setTag(holder);

        }

        super.configureDocsAndComments(question.getAnswerContainerDocsList(), question.getAnswerContainerCommList(), question.getQuestionContainerDocsList(), convertView, question, position);



        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);


        if (question.getOptions() != null) {
            super.addOptions(holder.actvAnswer, question);
            if (!question.getOptions().isEmpty()) {
                loadAdapterAutocomplete(holder, holder.actvAnswer, question.getOptions().get(0).getAutocomplete(), question);
            }
        }

        setAction(holder, question);

        setOnClickDelete(holder, question);

        super.setError(holder.flError, question.isError());


        setAddVisible(holder, super.setRespText(question.getAnswer(), holder.actvAnswer, null));


        if (getPendingAnswer() != null && question.getAnswer() == null) {
            Log.i("", "");


            Autocomplete autocomplete = new Autocomplete();
            autocomplete.setLabel(getPendingAnswer().getResponse());
            autocomplete.setValue(getPendingAnswer().getValue());

            clcik(autocomplete, holder.actvAnswer, question);
        }

        return convertView;
    }

    private void loadAdapterAutocomplete(final DynamicViewHolder holder, final CustomAutoCompleteTextView tv, ArrayList<Autocomplete> autocompleteList, final Questions question) {
        if (tv == null) return;
        AutocompleteAdapter adapter = new AutocompleteAdapter(getContext(), autocompleteList);

        tv.setThreshold(1);//will start working from first character
        tv.setAdapter(adapter);
        tv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Autocomplete item = (Autocomplete) parent.getItemAtPosition(position);
                // Log.i("", "");
                clcik(item, tv, question);
                setError(holder.flError, question.isError());

            }
        });


    }

    private void clcik(Autocomplete item, CustomAutoCompleteTextView tv, Questions question) {
        tv.setTag(item);
        question.setError(false);
        question.setAnswer(item);
        tv.setText(item.getLabel());
    }

    private void setOnClickDelete(final DynamicViewHolder holder, final Questions question) {
        if (holder.ivDelete != null)
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.actvAnswer.setText("");
                    holder.actvAnswer.setTag(null);
                    question.setAnswer(null);
                }
            });
    }


    private void setAction(final DynamicViewHolder holder, final Questions question) {
        if (holder.actvAnswer == null) return;
        holder.actvAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("", "");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("", "");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {

                    setAddVisible(holder, false);
                } else {

                    setAddVisible(holder, true);
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
