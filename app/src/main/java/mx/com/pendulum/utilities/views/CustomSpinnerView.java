package mx.com.pendulum.utilities.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import mx.com.pendulum.olintareas.R;

public class CustomSpinnerView extends androidx.appcompat.widget.AppCompatSpinner {

    private Typeface mTypeface;
    private Context context;
    private AttributeSet attrs;

    public CustomSpinnerView(Context context) {
        super(context);
        mTypeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font));
        setTypeF("", mTypeface);
    }

    public CustomSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomSpinnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        this.attrs = attrs;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTextView, 0, 0);
        try {
            String fileFont = a.getString(R.styleable.CustomTextView_typeFace);
            mTypeface = Typeface.createFromAsset(context.getAssets(), fileFont);
            setTypeF("", mTypeface);
        } catch (Exception e) {
            setTypeF(context.getString(R.string.font), null);
        } finally {
            a.recycle();
        }
    }

    public void setTypeF(String font, Typeface type) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTextView, 0, 0);
        try {
            if (!font.equalsIgnoreCase(""))
                mTypeface = Typeface.createFromAsset(context.getAssets(), font);
            else
                mTypeface = type;
            //setTypeface(mTypeface);
        } catch (Exception e) {
            //setTypeface("", Typeface.DEFAULT);
        } finally {
            a.recycle();
        }
    }
}