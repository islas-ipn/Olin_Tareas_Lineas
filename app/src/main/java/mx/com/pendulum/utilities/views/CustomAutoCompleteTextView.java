package mx.com.pendulum.utilities.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextWatcher;
import android.util.AttributeSet;

import java.util.ArrayList;

import mx.com.pendulum.olintareas.R;

/**
 * Created by evaleriano on 6/22/2017.
 */

public class CustomAutoCompleteTextView extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {
    private Typeface mTypeface;
    private Context context;
    private AttributeSet attrs;
    private boolean isObligatory;

    private ArrayList<TextWatcher> mListeners = null;

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        setTypeface(context.getString(R.string.font));
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        setTypeface(context.getString(R.string.font));
    }


    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        this.attrs = attrs;
        isObligatory = false;
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

    }


    /**
     * Metodo para modifica la fuente desde la carpeta assets/
     *
     * @param font String Ej. "fonts/Champagne_Limousines.ttf"
     */
    public void setTypeface(String font) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTextView, 0, 0);
        try {

            mTypeface = Typeface.createFromAsset(context.getAssets(), font);
            setTypeface(mTypeface);
        } catch (Exception e) {
            setTypeface(Typeface.DEFAULT);
        } finally {
            a.recycle();
        }
    }

    // this is how to disable AutoCompleteTextView filter
    @Override
    protected void performFiltering(CharSequence text, final int keyCode) {
        // text = "";
        super.performFiltering(text, keyCode);
    }


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

    public boolean isObligatory() {
        return isObligatory;
    }

    public void setObligatory(boolean obligatory) {
        isObligatory = obligatory;
    }
}
