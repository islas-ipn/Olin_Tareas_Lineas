package mx.com.pendulum.olintareas.ui.dialog;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.tareas.views.ViewFile_upload;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.views.SignatureView;

public class SignatureDialog {

    private Context context;
    private AlertDialog alert;
    private SignatureView signature;
    private int itemPosition;
    private int rowPosition;
    private String fileName;//ID_TA

    public SignatureDialog(Context context, final Interfaces.OnResponse<Object> response,
                           final int request, String prefijo, int itemPosition, int rowPosition) {
        this.context = context;
        this.fileName = prefijo;
        this.itemPosition = itemPosition;
        this.rowPosition = rowPosition;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_dynamic_form_signature, null);
        FrameLayout ll = view.findViewById(R.id.flSignature);
        signature = new SignatureView(context, null);
        signature.setBackgroundColor(Color.TRANSPARENT);
        ll.addView(signature, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean bool = signature.isSigned();
                if (!bool) {
                    return;
                }
                String filePath = captureSignature();
                if (response != null)
                    response.onResponse(request, filePath);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (response != null)
                    response.onResponse(request, null);
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Borrar", null);
        alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Typeface font = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.font_bold));
                AlertDialog alertDialog = (AlertDialog) dialog;
                Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setTypeface(font);
                button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                button.setTypeface(font);
                button = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                button.setTypeface(font);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signature.clear();
                    }
                });
            }
        });
    }

    private Context getContext() {
        return context;
    }

    public void showDialog() {
        alert.show();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String captureSignature() {
        FrameLayout ll = alert.findViewById(R.id.flSignature);
        ll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.White));
        ll.setDrawingCacheEnabled(true);
        Bitmap mBitmap = Bitmap.createBitmap(ll.getWidth(), ll.getHeight(), Bitmap.Config.RGB_565);
        String imageName = "signature" + ViewFile_upload.SEPATATOR + itemPosition +
                ViewFile_upload.SEPATATOR + rowPosition + ViewFile_upload.SEPATATOR +
                Tools.getDateFileStr() + ".jpg";
        Canvas canvas = new Canvas(mBitmap);
        try {
            File f = new File(Properties.SD_CARD_IMAGES_DIR);
            if (!f.exists()) {
                f.mkdirs();
            }
            File file = new File(f.getAbsoluteFile(), imageName);
            FileOutputStream mFileOutStream = new FileOutputStream(file);
            ll.draw(canvas);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, mFileOutStream);
            mFileOutStream.flush();
            mFileOutStream.close();
            imageName = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            imageName = null;
        }
        return imageName;
    }
}
