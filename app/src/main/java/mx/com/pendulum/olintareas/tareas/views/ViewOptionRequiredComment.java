package mx.com.pendulum.olintareas.tareas.views;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Obj;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.utilities.views.CustomEditText;

public class ViewOptionRequiredComment {
    private Context context;

    ViewOptionRequiredComment(Context context) {
        this.context = context;
    }


    public View getView() {
        return View.inflate(context, R.layout.row_dynamic_form_option_required_comment, null);
    }


    public void configView(List<Obj> answerContainerCommList, LinearLayout linearLayout, final Options option, final Questions question, AnswerDTO pendingAnswer) {

        final CustomEditText etOptionCommentAnswer = linearLayout.findViewById(R.id.etOptionCommentAnswer);
        final ImageView ivOptionCommentDelete =  linearLayout.findViewById(R.id.ivOptionCommentDelete);
        final Obj obj = new Obj();
        answerContainerCommList.add(obj);



        etOptionCommentAnswer.addTextChangedListener(new TextWatcher() {
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
                    etOptionCommentAnswer.setTag(null);
                    obj.setObj(null);
//                    question.setAnswer(null);
                    setAddVisible(ivOptionCommentDelete, false);
                } else {
                    etOptionCommentAnswer.setTag(s.toString());
                    obj.setObj(s.toString());
//                    question.setAnswer(s.toString());
                    setAddVisible(ivOptionCommentDelete, true);
                }
            }
        });
        setAddVisible(ivOptionCommentDelete, false);

        if(pendingAnswer!=null){
            if(pendingAnswer.getComment()!=null){
                etOptionCommentAnswer.setText(pendingAnswer.getComment());
            }
        }

        if (ivOptionCommentDelete != null)
            ivOptionCommentDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAddVisible(ivOptionCommentDelete, false);
                    etOptionCommentAnswer.setText("");
                }
            });
    }

    private void setAddVisible(View view, boolean added) {
        if (view != null)
            if (added) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
    }
}
