package mx.com.pendulum.olintareas.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.IOException;
import java.util.Calendar;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.camera.VIdeoTextureView;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.utilities.Tools;

@SuppressWarnings("ALL")
public class CustomDialog {

    public static void dialogChoice(final Context context, final Interfaces.OnResponse<Object> mOnResponse, final int response, final String title, final String message) {
        dialogChoice(context, mOnResponse, response, title, message, context.getString(R.string.yes), context.getString(R.string.no), null);
    }

    public static void dialogChoice(final Context context, final Interfaces.OnResponse<Object> mOnResponse, final int response, final String title, final String message, String acceptBtn, String cancelBtn) {
        dialogChoice(context, mOnResponse, response, title, message, acceptBtn, cancelBtn, null);
    }

    public static void dialogChoice(final Context context, final Interfaces.OnResponse<Object> mOnResponse, final int response, final String title, final Spanned message, String acceptBtn, String cancelBtn) {
        dialogChoice(context, mOnResponse, response, title, message, acceptBtn, cancelBtn, null);
    }

    public static void dialogChoice(final Context context, final Interfaces.OnResponse<Object> mOnResponse, final int response, final String title, final String message, String acceptBtn, String cancelBtn, String neutralBtn) {
        Tools.hideSoftKeyboard((Activity) context);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setCancelable(false);
        if (title != null)
            builder.setTitle(title);
        if (message != null)
            builder.setMessage(message);
        if (acceptBtn != null)
            builder.setPositiveButton(acceptBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mOnResponse != null)
                        mOnResponse.onResponse(response, true);
                    dialog.dismiss();
                }
            });
        if (cancelBtn != null)
            builder.setNegativeButton(cancelBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mOnResponse != null)
                        mOnResponse.onResponse(response, false);
                    dialog.dismiss();
                }
            });
        if (neutralBtn != null) {
            builder.setNeutralButton(neutralBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.show();
    }

    public static void dialogChoice(final Context context, final Interfaces.OnResponse<Object> mOnResponse, final int response, final String title, final Spanned message, String acceptBtn, String cancelBtn, String neutralBtn) {
        Tools.hideSoftKeyboard((Activity) context);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setCancelable(false);
        if (title != null)
            builder.setTitle(title);
        if (message != null)
            builder.setMessage(message);
        if (acceptBtn != null)
            builder.setPositiveButton(acceptBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mOnResponse != null)
                        mOnResponse.onResponse(response, true);
                    dialog.dismiss();
                }
            });
        if (cancelBtn != null)
            builder.setNegativeButton(cancelBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mOnResponse != null)
                        mOnResponse.onResponse(response, false);
                    dialog.dismiss();
                }
            });
        if (neutralBtn != null) {
            builder.setNeutralButton(neutralBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.show();
    }

    public static void showDisclaimer(Context context, String message, final Interfaces.OnResponse mOnResponse) {
        Tools.hideSoftKeyboard((Activity) context);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setCancelable(false);
        if (message != null)
            builder.setMessage(message);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mOnResponse != null)
                    mOnResponse.onResponse(0, false);
                dialog.dismiss();
            }
        });

         AlertDialog dialog = builder.create();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
        wmlp.x = 100;   //x position
        wmlp.y = 100;   //y position

        dialog.show();
    }

    public static void showDisclaimer(Context context, String title, Spanned message, final Interfaces.OnResponse mOnResponse) {
        Tools.hideSoftKeyboard((Activity) context);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setCancelable(false);
        if (message != null)
            builder.setMessage(message);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mOnResponse != null)
                    mOnResponse.onResponse(0, false);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void showDisclaimer(Context context, String title, String message, final Interfaces.OnResponse mOnResponse) {
        Tools.hideSoftKeyboard((Activity) context);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setCancelable(false);
        if (message != null)
            builder.setMessage(message);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mOnResponse != null)
                    mOnResponse.onResponse(0, false);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void showMessage(Context context, String message) {
        Tools.hideSoftKeyboard((Activity) context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setCancelable(false);
        if (message != null)
            builder.setMessage(message);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void showMessagePosNeg(Context context, String message, final Interfaces.OnResponse<Object> mOnResponse) {
        Tools.hideSoftKeyboard((Activity) context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setCancelable(false);
        if (message != null)
            builder.setMessage(message);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mOnResponse != null)
                    mOnResponse.onResponse(778, true);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void showList(Context context, String title, Cursor cursor, String labelColumn, DialogInterface.OnClickListener clickListener) {
        Tools.hideSoftKeyboard((Activity) context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setSingleChoiceItems(cursor, 1, labelColumn, clickListener);
        builder.show();
    }

    public static void salirSinGuardar(Context context, final Interfaces.OnResponse<Object> mOnResponse) {
        Tools.hideSoftKeyboard((Activity) context);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setCancelable(false);
        builder.setTitle("¿Desea salir sin guardar?");
        builder.setMessage("La información cargada en ésta pantalla no se guardará.");
        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mOnResponse != null)
                    mOnResponse.onResponse(0, true);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private static ProgressDialog dialog;

    public static void showProgressDialog(final Context context, final boolean showDialog, String message) {
        try {
            Tools.hideSoftKeyboard((Activity) context);
            if (dialog == null) {
                dialog = new ProgressDialog(context, R.style.AlertDialogStyle);
                dialog.setCancelable(false);
            }
            if (message != null) {
                dialog.setMessage(message);
            }
            if (showDialog) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog = null;
            }
        } catch (Exception ignored) {
        }
    }

    public static void dialogChoiceImage(final Context context, final Interfaces.OnResponse<Object> mOnResponse, final int response, final String title, final String message, String acceptBtn, String cancelBtn, int imageResource) {
        Tools.hideSoftKeyboard((Activity) context);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setCancelable(false);
        if (title != null)
            builder.setTitle(title);
        if (message != null)
            builder.setMessage(message);
        if (acceptBtn != null)
            builder.setPositiveButton(acceptBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mOnResponse != null)
                        mOnResponse.onResponse(response, true);
                    dialog.dismiss();
                }
            });
        if (cancelBtn != null)
            builder.setNegativeButton(cancelBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mOnResponse != null)
                        mOnResponse.onResponse(response, false);
                    dialog.dismiss();
                }
            });
        builder.setIcon(imageResource);
        builder.show();
    }

    public static void getDate(final Context context, final Interfaces.OnResponse<Object> mOnResponse, final int response, String title, Calendar minDate) {
        Tools.hideSoftKeyboard((Activity) context);
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, final int year, final int monthOfYear,
                                  final int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mOnResponse.onResponse(response, newDate);
            }
        };
        Calendar todayCal;
        DatePickerDialog dialog;
        todayCal = Calendar.getInstance();
        dialog = new DatePickerDialog(context,
                dateSetListener, todayCal.get(Calendar.YEAR),
                todayCal.get(Calendar.MONTH), todayCal.get(Calendar.DAY_OF_MONTH));
        if (minDate != null) {
            dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        }
        if (title != null)
            dialog.setTitle(title);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOnResponse.onResponse(response, null);
                        dialog.dismiss();
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

    public static void getTime(final Context context, final Interfaces.OnResponse<Object> mOnResponse, final int response, String title, final Calendar startTime) {
        Tools.hideSoftKeyboard((Activity) context);
        final TimePickerDialog.OnTimeSetListener dateSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedTime.set(Calendar.MINUTE, minute);
                if (startTime != null) {
                    int _hourOfDay = startTime.get(Calendar.HOUR_OF_DAY);
                    int _minute = startTime.get(Calendar.MINUTE);
                    Calendar c1 = Calendar.getInstance();
                    c1.set(Calendar.HOUR_OF_DAY, _hourOfDay);
                    c1.set(Calendar.MINUTE, _minute);
                    if (c1.after(selectedTime)) {
                        Log.e("DIALOG_TIME", hourOfDay + ":" + minute);
                        Toast.makeText(context, "Hora incorrecta", Toast.LENGTH_SHORT).show();
                    }
                }
                mOnResponse.onResponse(response, selectedTime);
            }
        };
        Calendar todayCal;
        if (startTime != null) {
            todayCal = startTime;
        } else {
            todayCal = Calendar.getInstance();
        }
        final TimePickerDialog dialog = new TimePickerDialog(context, dateSetListener, todayCal.get(Calendar.HOUR_OF_DAY), todayCal.get(Calendar.MINUTE), true);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface mDialog) {
                Typeface font = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_bold));
                final AlertDialog alertDialog = (AlertDialog) mDialog;
                Button button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                button.setTypeface(font);
                button = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                button.setTypeface(font);
                button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setTypeface(font);
            }
        });
        if (title != null)
            dialog.setTitle(title);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOnResponse.onResponse(response, null);
                        dialog.dismiss();
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

    private static int mOrientation;

    public static void dialogImage(Context context, final Interfaces.OnResponse response, final int request, final String filePath) {
        final Dialog dialog = new Dialog(context);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        dialog.setContentView(R.layout.dialog_image_editor);
        dialog.findViewById(R.id.tvDialogYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response.onResponse(request, null);
                dialog.dismiss();

            }
        });
        dialog.findViewById(R.id.tvDialogNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExifInterface exif;
                try {
                    exif = new ExifInterface(filePath);
                    exif.setAttribute(ExifInterface.TAG_ORIENTATION, mOrientation + "");
                    exif.saveAttributes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response.onResponse(request, null);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.menu_turn_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                switch (orientation) {
                    case 0:
                    case 1:
                        orientation = 8;
                        break;
                    case 6:
                        orientation = 1;
                        break;
                    case 3:
                        orientation = 6;
                        break;
                    case 8:
                        orientation = 3;
                        break;
                }
                try {
                    exif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation + "");
                    exif.saveAttributes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageView mCropImageView = (ImageView) dialog.findViewById(R.id.cropIv);
                DisplayImageOptions ops = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.drawable.page)
                        .showImageForEmptyUri(R.drawable.page)
                        .showImageOnFail(R.drawable.page)
                        .considerExifParams(true)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .build();
                ImageLoader.getInstance().displayImage("file://" + filePath, mCropImageView, Tools.getImageLoaderOptions(0, R.drawable.page));
            }
        });
        dialog.findViewById(R.id.menu_turn_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                switch (orientation) {
                    case 1:
                    case 0:
                        orientation = 6;
                        break;
                    case 6:
                        orientation = 3;
                        break;
                    case 3:
                        orientation = 8;
                        break;
                    case 8:
                        orientation = 1;
                        break;
                }
                try {
                    exif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation + "");
                    exif.saveAttributes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageView mCropImageView = (ImageView) dialog.findViewById(R.id.cropIv);
                ImageLoader.getInstance().displayImage("file://" + filePath, mCropImageView, Tools.getImageLoaderOptions(0, R.drawable.page));
            }
        });
        final ImageView mCropImageView = (ImageView) dialog.findViewById(R.id.cropIv);
        ImageLoader.getInstance().displayImage("file://" + filePath, mCropImageView, Tools.getImageLoaderOptions(0, R.drawable.page));
        dialog.show();
    }

    public static void dialogVideo(Context context, final Interfaces.OnResponse response, final int request, final String filePath) {
        final Dialog dialog = new Dialog(context);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        dialog.setContentView(R.layout.video_preview);
        final VIdeoTextureView mVideoView = dialog.findViewById(R.id.mVideoView);
        dialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response.onResponse(request, null);
                dialog.dismiss();
            }
        });
        mVideoView.setSource(filePath);
        mVideoView.setLooping(false);
        FrameLayout frameView = dialog.findViewById(R.id.videoViewWrapper);
        mVideoView.SetFrameLaout(frameView);
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                return;
            }
        });
        dialog.onBackPressed();
        dialog.show();
    }

    public static void dialogThubtail(Context context, String filePath) {
        final Dialog dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_thumbtail);
        ImageView iv = (ImageView) dialog.findViewById(R.id.cropIv);
        ImageLoader.getInstance().displayImage("file://" + filePath, iv, Tools.getImageLoaderOptions(0, R.drawable.page));
        dialog.show();
    }

    public static void showNormalDialog(Context context, String message) {
        Tools.hideSoftKeyboard((Activity) context);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setCancelable(false);
        if (message != null)
            builder.setMessage(message);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}