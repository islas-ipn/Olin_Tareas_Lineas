package mx.com.pendulum.utilities.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;

import java.util.ArrayList;

import mx.com.pendulum.olintareas.R;

/**
 * Created by evaleriano on 6/21/2017.
 */

public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {

    private Typeface mTypeface;
    private Context context;
    private AttributeSet attrs;
    private KeyboardListener listener;


    public CustomEditText(Context context) {
        super(context);
        mTypeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font));
        setTypeface(mTypeface);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        setTypeface(context.getString(R.string.font));
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        setTypeface(context.getString(R.string.font));
    }


    private void init(Context context, AttributeSet attrs) {

        this.context = context;
        this.attrs = attrs;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTextView, 0, 0);

        try {
            String fileFont = a.getString(R.styleable.CustomTextView_typeFace);
            mTypeface = Typeface.createFromAsset(context.getAssets(), fileFont);

            setTypeface(mTypeface);
        } catch (Exception e) {
            setTypeface(Typeface.DEFAULT);
        } finally {
            a.recycle();
        }
//        this.setFilters(new InputFilter[] { filter });

    }

//    private String blockCharacterSet = "'";
//    private InputFilter filter = new InputFilter() {
//
//        @Override
//        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//
//            if (source != null && blockCharacterSet.contains(source)) {
//                return "";
//            }
//            return null;
//        }
//    };

    /**
     * Metodo para modifica la fuente desde la carpeta assets/
     *
     * @param font String Ej. "fonts/Champagne_Limousines.ttf"
     */
    public void setTypeface(String font) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CustomTextView, 0, 0);
        try {

            mTypeface = Typeface.createFromAsset(context.getAssets(), font);
            setTypeface(mTypeface);
        } catch (Exception e) {
            setTypeface(Typeface.DEFAULT);
        } finally {
            a.recycle();
        }


    }


    @Override
    public boolean onKeyPreIme(int keyCode, @NonNull KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (listener != null)
                listener.onStateChanged(this, false);
        }
        return super.onKeyPreIme(keyCode, event);
    }


    public void setOnKeyboardListener(KeyboardListener listener) {
        this.listener = listener;
    }


    public interface KeyboardListener {
        void onStateChanged(CustomEditText customEditText, boolean showing);
    }


//    @Override
//    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
//
//        String str = text.toString();
//        if (str.contains("'")) {
//            setText("");
//            append(str.replaceAll("'", ""));
//        }
//
//        super.onTextChanged(text, start, lengthBefore, lengthAfter);
//    }


    public void setHint(String hint) {
        super.setHint(hint != null ? hint : "");
    }

//
//    public  void setText(String text) {
//        setText(text, mBufferType);
//    }

    public void setText(String text) {
        super.setText(text != null ? text : "");
    }


    private ArrayList<TextWatcher> mListeners = null;

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(watcher);

        super.addTextChangedListener(watcher);
    }

    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        if (mListeners != null) {
            int i = mListeners.indexOf(watcher);
            if (i >= 0) {
                mListeners.remove(i);
            }
        }

        super.removeTextChangedListener(watcher);
    }

    public void clearTextChangedListeners() {
        if (mListeners != null) {
            for (TextWatcher watcher : mListeners) {
                super.removeTextChangedListener(watcher);
            }

            mListeners.clear();
            mListeners = null;
        }
    }
}
