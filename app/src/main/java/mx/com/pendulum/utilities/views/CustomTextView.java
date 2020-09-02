package mx.com.pendulum.utilities.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import mx.com.pendulum.olintareas.R;


/**
 * Created by evaleriano on 6/21/2017.
 */

public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {


    private Typeface mTypeface;
    private Context context;
    private AttributeSet attrs;


    public CustomTextView(Context context) {
        super(context);


        mTypeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font));
        setTypeface(mTypeface);

    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);

    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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

            setTypeface(mTypeface);
        } catch (Exception e) {
//            setTypeface(Typeface.DEFAULT);
            setTypeface(context.getString(R.string.font));
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



    /*@Override
    public void setText(CharSequence text, BufferType type) {
        String str = text.toString();
        str = str.replaceAll("¿","");
        super.setText(str, type);
    }

*/
}