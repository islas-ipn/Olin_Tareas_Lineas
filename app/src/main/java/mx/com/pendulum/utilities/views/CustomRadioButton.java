package mx.com.pendulum.utilities.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import mx.com.pendulum.olintareas.R;

/**
 * Created by evaleriano on 6/22/2017.
 */

public class CustomRadioButton extends androidx.appcompat.widget.AppCompatRadioButton {

    private Typeface mTypeface;
    private Context context;
    private AttributeSet attrs;


    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        setTypeface(context.getString(R.string.font));
    }

    public CustomRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
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

}
