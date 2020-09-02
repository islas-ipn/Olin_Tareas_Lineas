package mx.com.pendulum.utilities.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

public class CustomTextWatcher implements TextWatcher {

    private CustomEditText view;
    private Context context;
    private OnResponse onResponse;

    public interface OnResponse {
        void onTextChanged(CustomEditText view, CustomTextWatcher tw, CharSequence s, int start, int before, int count);
    }

    public CustomTextWatcher(Context context, CustomEditText view, OnResponse onResponse) {
        this.view = view;
        this.context = context;
        this.onResponse = onResponse;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        beforeTextChanged(view, s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        onTextChanged(view, s, start, before, count);
        onResponse.onTextChanged(view, this, s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
//        afterTextChanged(view, s);
    }

//    public abstract void beforeTextChanged(View view, CharSequence s, int start, int count, int after);
//
//    public abstract void onTextChanged(View view, CharSequence s, int start, int before, int count);
//
//    public abstract void afterTextChanged(View view, Editable s);
}
