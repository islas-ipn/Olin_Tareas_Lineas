package mx.com.pendulum.olintareas.tareas.views;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Document;
import mx.com.pendulum.olintareas.dto.tareasV2.Obj;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.tareas.Validator;
import mx.com.pendulum.olintareas.ui.dialog.FileChooserDialog;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.views.CustomTextView;

public class ViewOptionRequiredDocument {
    private Context context;

    ViewOptionRequiredDocument(Context context) {
        this.context = context;
    }


    public View getView() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }

    public void configView(List<Obj> answerContainerDocsList, LinearLayout linearLayout, Options option, Questions question) {
        Document doc = option.getDocument();
        if (doc == null) {

            return;
        }


        long maxDocuments = doc.getMax();


        if (linearLayout.getChildCount() == maxDocuments) return;


        for (int i = 0; i < maxDocuments; i++) {

            if (linearLayout.findViewById(i) == null) {
                View v = View.inflate(context, R.layout.row_dynamic_form_option_required_document, null);
                v.setId(i);
                initView(answerContainerDocsList, v, option, question);
                linearLayout.addView(v);
            }

        }


    }


    private void initView(List<Obj> answerContainerDocsList, final View view, final Options option, final Questions question) {

        final TextView tvOptionDocumentExtension = (TextView) view.findViewById(R.id.tvOptionDocumentExtension);
        final CustomTextView tvOptionDocumentAnswer = (CustomTextView) view.findViewById(R.id.tvOptionDocumentAnswer);
        final View addedOptionDocument = view.findViewById(R.id.addedOptionDocument);
        final View addOptionDocument = view.findViewById(R.id.addOptionDocument);
        final View ivOptionDocumentDelete = view.findViewById(R.id.ivOptionDocumentDelete);
        final Obj obj = new Obj();
        answerContainerDocsList.add(obj);
        setAddVisible(addOptionDocument, addedOptionDocument, ivOptionDocumentDelete, false);
        if (ivOptionDocumentDelete != null)
            ivOptionDocumentDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvOptionDocumentExtension.setText("");
                    tvOptionDocumentAnswer.setText("");
                    tvOptionDocumentAnswer.setTag(null);
                    obj.setObj(null);
                    question.setAnswer(null);
                    setAddVisible(addOptionDocument, addedOptionDocument, ivOptionDocumentDelete, false);
                }
            });

        view.findViewById(R.id.rvOptionDocumentAnwser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new FileChooserDialog(context, option.getDocument().getExtension()).setFileListener(new FileChooserDialog.FileSelectedListener() {
                    @Override
                    public void fileSelected(File file) {

//                        view.findViewById(R.id.flError).setVisibility(View.GONE);
                        if (file != null) {
                            String fileName = file.getAbsolutePath();


                            if (!Validator.validate(context, true, fileName, option.getDocument())) {
                                //  Toast.makeText(getContext(), "Archivo inválido", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String extencion = Tools.getFileExt(fileName);

                            tvOptionDocumentExtension.setText(extencion);
                            tvOptionDocumentAnswer.setText(file.getName());
                            tvOptionDocumentAnswer.setTag(fileName);
                            obj.setObj(fileName);
                            question.setAnswer(fileName);
                            setAddVisible(addOptionDocument, addedOptionDocument, ivOptionDocumentDelete, true);

//                            LinearLayout ll = (LinearLayout) view.getParent();
//                            int numberOfChilds = ll.getChildCount();
//                            if (numberOfChilds <question.getDocumentList().getMax()){
//
//                                View v = View.inflate(context, R.layout.row_dynamic_form_question_required_document, null);
//                                initView(v, question);
//                                ll.addView(v);
//
//                            }
                        }


                    }
                }).

                        showDialog();
            }
        });

    }

    private void setAddVisible(View add, View added, View ivDelete, boolean isAdded) {
        if (add != null && added != null && ivDelete != null)
            if (isAdded) {
                add.setVisibility(View.GONE);
                added.setVisibility(View.VISIBLE);
                ivDelete.setVisibility(View.VISIBLE);
            } else {
                add.setVisibility(View.VISIBLE);
                added.setVisibility(View.GONE);
                ivDelete.setVisibility(View.GONE);
            }
    }

}
