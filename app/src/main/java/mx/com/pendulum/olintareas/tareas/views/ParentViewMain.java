package mx.com.pendulum.olintareas.tareas.views;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;

import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.CatalogDatabaseHelper;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Autocomplete;
import mx.com.pendulum.olintareas.dto.tareasV2.Obj;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.OptionsQuestionsForm;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.dto.tareasV2.TemporalForm;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.activities.tareas.AddOptionsQuestionsActivity;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;
import mx.com.pendulum.olintareas.ui.dialog.tareas.AddressDialog;
import mx.com.pendulum.olintareas.ui.fragments.tareas.AddNotaV2Fragment;
import mx.com.pendulum.olintareas.ui.fragments.tareas.AddTareaV2Fragment;
import mx.com.pendulum.olintareas.ui.parents.FragmentParent;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.views.CustomAutoCompleteTextView;
import mx.com.pendulum.utilities.views.CustomEditText;
import mx.com.pendulum.utilities.views.CustomSpinnerView;
import mx.com.pendulum.utilities.views.CustomTextView;

import static android.app.Activity.RESULT_OK;

@SuppressWarnings({"WeakerAccess"})
public abstract class ParentViewMain {

    //    private static final String TAG = ParentViewMain.class.getSimpleName();
    public static final int HTTP = 1;
    public static final int FILE = 2;
    public static final int RESOURCE = 3;
    public static final int ASSET = 4;
    public static final int DRRAWABLE = 5;
    private Context context;
    private DynamicFormAdapter adapter;
    public final static int ID_QUESTION_REQUIRED_DOCUMENT = R.id.view_1;
    public final static int ID_OPTION_REQUIRED_DOCUMENT = R.id.view_2;
    public final static int ID_OPTION_REQUIRED_COMMENT = R.id.view_3;
    private boolean isImageResize = false;
    private Long idTarea;
    private Long idNota;
    private Fragment fragment;
    private AnswerDTO pendingAnswer;
    ViewQuestionRequiredDocument viewQuestionRequiredDocument;

    public void setPendingAnswer(AnswerDTO pendingAnswer) {
        this.pendingAnswer = pendingAnswer;
    }

    public AnswerDTO getPendingAnswer() {
        return pendingAnswer;
    }

    public boolean isImageResize() {
        return isImageResize;
    }

    public void setIsImageResize(boolean isImageResize, Long idTarea, Long idNota) {
        this.isImageResize = isImageResize;
        this.idTarea = idTarea;
        this.idNota = idNota;
    }

    private void onQuestionRequiredDocument(List<Obj> questionContainerDocsList, View convertView, Questions question, boolean isVisible, int itemPosition) {
        LinearLayout row = convertView.findViewById(ID_QUESTION_REQUIRED_DOCUMENT);
        if (row != null)
            if (isVisible) { // TODO HERE DISCRIMINAR
                viewQuestionRequiredDocument = new ViewQuestionRequiredDocument(getContext(), adapter);
                viewQuestionRequiredDocument.configView(questionContainerDocsList, convertView, row, question, fragment, itemPosition, pendingAnswer, isImageResize());
                row.setVisibility(View.GONE);
            } else {
                row.setVisibility(View.GONE);
            }
    }

    public void updateQuestionRequiredDocument(Object obj, LinearLayout row, Questions question) {
        question.setError(false);
        viewQuestionRequiredDocument.setUpdate(obj, row, question);
    }

    public void onOptionRequiredDocument(List<Obj> answerContainerDocsList, View convertView, Options option, boolean isVisible, Questions question) {
        LinearLayout row = convertView.findViewById(ID_OPTION_REQUIRED_DOCUMENT);
        if (row != null)
            if (isVisible) {
                row.setVisibility(View.VISIBLE);
                new ViewOptionRequiredDocument(getContext()).configView(answerContainerDocsList, row, option, question);
            } else {
                row.setVisibility(View.GONE);
            }
    }

    View.OnClickListener getExpandClick(final View convertView, final ImageView ivExpand, final Questions question) {
        LinearLayout row = convertView.findViewById(ID_QUESTION_REQUIRED_DOCUMENT);
        if (ivExpand != null) {
            if (question.isExpanded()) {
                ivExpand.setImageResource(R.drawable.expand_less);
                row.setVisibility(View.VISIBLE);
            } else {
                ivExpand.setImageResource(R.drawable.expand_more);
                row.setVisibility(View.GONE);
            }
        }
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout row = convertView.findViewById(ID_QUESTION_REQUIRED_DOCUMENT);
                if (question.isExpanded()) {
                    ivExpand.setImageResource(R.drawable.expand_more);
                    row.setVisibility(View.GONE);
                    question.setExpanded(false);
                } else {
                    ivExpand.setImageResource(R.drawable.expand_less);
                    row.setVisibility(View.VISIBLE);
                    question.setExpanded(true);
                }
            }
        };
    }

    View.OnClickListener getLinkClick(final Questions question,
                                      final int option) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment instanceof AddNotaV2Fragment) {
                    AddNotaV2Fragment notaFrg;
                    notaFrg = (AddNotaV2Fragment) fragment;
                    notaFrg.manageLinkButtons(option, question);
                } else if (fragment instanceof AddTareaV2Fragment) {
                    AddTareaV2Fragment tareaFrg;
                    tareaFrg = (AddTareaV2Fragment) fragment;
                    tareaFrg.manageLinkButtons(option, question);
                }
            }
        };
    }

    View.OnClickListener getAddClick(final View convertView, final ImageView ivAdd,
                                     final Questions question, final int itemPosition) {
        LinearLayout row = convertView.findViewById(ID_QUESTION_REQUIRED_DOCUMENT);
        row.setVisibility(View.VISIBLE);
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Questions questionClean = viewQuestionRequiredDocument.cleanQuestionContainerDocsList(question);
                final LinearLayout row = convertView.findViewById(ID_QUESTION_REQUIRED_DOCUMENT);
                int itemsTaked = viewQuestionRequiredDocument.obtainQuestionDocumentsSize(questionClean);
                Long maxDocuments = questionClean.getDocument().getMax();
                if (itemsTaked >= maxDocuments.intValue())
                    Toast.makeText(context,
                            "Solo se permite la captura de " + itemsTaked + " arvhivos.",
                            Toast.LENGTH_LONG).show();
                else {
                    int filesLefth = maxDocuments.intValue() - itemsTaked;
                    int rowPosition = filesLefth - 1;
                    if (rowPosition != 0)
                        rowPosition = filesLefth - rowPosition;
                    ArrayList<Integer> freeSpaces = viewQuestionRequiredDocument.findFreePosition(questionClean);
                    if (ivAdd.getId() == R.id.ivAddFile) {
                        viewQuestionRequiredDocument.invoqueActionFromParent(
                                2, questionClean, fragment, false, itemPosition,
                                filesLefth, itemsTaked, freeSpaces, convertView, rowPosition);
                    } else if (ivAdd.getId() == R.id.ivAddDocumento) {
                        viewQuestionRequiredDocument.invoqueActionFromParent(
                                1, questionClean, fragment, isImageResize(), itemPosition,
                                filesLefth, itemsTaked, freeSpaces, convertView, rowPosition);
                    }
                }
                row.setVisibility(View.VISIBLE);
            }
        };
    }

    View.OnClickListener getAddAddress(final View convertView, final ImageView ivAdd,
                                       final Questions questions, final int itemPosition, final int rowPosition) {
        LinearLayout row = convertView.findViewById(ID_QUESTION_REQUIRED_DOCUMENT);
        row.setVisibility(View.VISIBLE);
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AddressDialog(context, new Interfaces.OnResponse<Object>() {
                    @Override
                    public void onResponse(int handlerCode, Object requestString) {
                        if (requestString instanceof Integer) {
                            if (fragment instanceof AddNotaV2Fragment) {
                                // Guardar id domicilio
                                AddNotaV2Fragment notaFrg;
                                notaFrg = (AddNotaV2Fragment) fragment;
                                Intent intent = new Intent();
                                intent.putExtra("key_data", String.valueOf(requestString));
                                questions.setAnswer(requestString);
                                intent.putExtra("key_question", questions);
                                intent.putExtra("REULT_CODE", RESULT_OK);
                                intent.putExtra("POSITION", itemPosition);
                                notaFrg.addressTaken(intent);
                            } else if (fragment instanceof AddTareaV2Fragment) {
                                // Guardar id domicilio
                                AddTareaV2Fragment tareaFrg;
                                tareaFrg = (AddTareaV2Fragment) fragment;
                                Intent intent = new Intent();
                                intent.putExtra("key_data", String.valueOf(requestString));
                                questions.setAnswer(requestString);
                                intent.putExtra("key_question", questions);
                                intent.putExtra("REULT_CODE", RESULT_OK);
                                intent.putExtra("POSITION", itemPosition);
                                tareaFrg.addressTaken(intent);
                            }
                        }
                    }
                }, 0, itemPosition, rowPosition).showDialog();
            }
        };
    }

    View.OnClickListener getSignatureClick(final View convertView, final ImageView ivAdd,
                                           final Questions question, final int itemPosition) {
        LinearLayout row = convertView.findViewById(ID_QUESTION_REQUIRED_DOCUMENT);
        row.setVisibility(View.VISIBLE);
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Questions questionClean = viewQuestionRequiredDocument.cleanQuestionContainerDocsList(question);
                final LinearLayout row = convertView.findViewById(ID_QUESTION_REQUIRED_DOCUMENT);
                int itemsTaked = viewQuestionRequiredDocument.obtainQuestionDocumentsSize(questionClean);
                Long maxDocuments = questionClean.getDocument().getMax();
                if (itemsTaked >= maxDocuments.intValue()) {
                    String fir = "firma.";
                    if (itemsTaked > 1)
                        fir = "firmas.";
                    Toast.makeText(context,
                            "Solo se permite la captura de " + itemsTaked + " " + fir,
                            Toast.LENGTH_LONG).show();
                } else {
                    int filesLefth = maxDocuments.intValue() - itemsTaked;
                    int rowPosition = filesLefth - 1;
                    if (rowPosition != 0)
                        rowPosition = filesLefth - rowPosition;
                    ArrayList<Integer> freeSpaces = viewQuestionRequiredDocument.findFreePosition(questionClean);
                    if (ivAdd.getId() == R.id.ivAddSign) {
                        viewQuestionRequiredDocument.invoqueActionFromParent(
                                3, questionClean, fragment, isImageResize(), itemPosition,
                                filesLefth, itemsTaked, freeSpaces, convertView, rowPosition);
                    }
                }
                row.setVisibility(View.VISIBLE);
            }
        };
    }

    public void onOptionRequiredComment(List<Obj> answerContainerCommList, View convertView, Options option, boolean isVisible, Questions question) {
        LinearLayout row = convertView.findViewById(ID_OPTION_REQUIRED_COMMENT);
        if (row != null)
            if (isVisible) {
                new ViewOptionRequiredComment(getContext()).configView(answerContainerCommList, row, option, question, pendingAnswer);
                row.setVisibility(View.VISIBLE);
            } else {
                row.setVisibility(View.GONE);
            }
    }

    public ParentViewMain(Context context, @SuppressWarnings("unused") DynamicFormAdapter adapter, Fragment fragment) {
        this.context = context;
        this.adapter = adapter;
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public Context getContext() {
        return context;
    }

    public void configureDocsAndComments(List<Obj> answerContainerDocsList,
                                         List<Obj> answerContainerCommList,
                                         List<Obj> questionContainerDocsList,
                                         View convertView, Questions question, int itemPosition) {
        LinearLayout row = null;
        try {
            row = convertView.findViewById(R.id.row);
        } catch (Exception ignored) {
            //   e.printStackTrace();
        }
        View container;
        if (row != null) {
            if (row.findViewById(ID_QUESTION_REQUIRED_DOCUMENT) == null) {
                container = new ViewQuestionRequiredDocument(getContext(), adapter).getView();
                container.setId(ID_QUESTION_REQUIRED_DOCUMENT);
                row.addView(container);
            }
        }
        if (row != null) {
            if (row.findViewById(ID_OPTION_REQUIRED_COMMENT) == null) {
                container = new ViewOptionRequiredComment(getContext()).getView();
                container.setId(ID_OPTION_REQUIRED_COMMENT);
                row.addView(container);
            }
        }
        if (row != null) {
            if (row.findViewById(ID_OPTION_REQUIRED_DOCUMENT) == null) {
                container = new ViewOptionRequiredDocument(getContext()).getView();
                container.setId(ID_OPTION_REQUIRED_DOCUMENT);
                row.addView(container);
            }
        }
        if (row != null)
            row.setOnClickListener(null);
        Options options = null;
        if (question.getOptions() != null && question.getOptions().size() > 0)
            options = question.getOptions().get(0);
        if (question.getRequiredDocument() != null) {
            onQuestionRequiredDocument(questionContainerDocsList, convertView, question, question.getRequiredDocument(), itemPosition);
        } else {
            onQuestionRequiredDocument(questionContainerDocsList, convertView, question, false, itemPosition);
        }
        if (question.getOptions() == null) {
            onOptionRequiredDocument(answerContainerDocsList, convertView, null, false, question);
            onOptionRequiredComment(answerContainerCommList, convertView, null, false, question);
            return;
        }
        if (question.getOptions().isEmpty()) {
            onOptionRequiredDocument(answerContainerDocsList, convertView, null, false, question);
            onOptionRequiredComment(answerContainerCommList, convertView, null, false, question);
            return;
        }
        if (question.getOptions().size() == 1) {
            if (options.getRequiredDocument() != null) {
                onOptionRequiredDocument(answerContainerDocsList, convertView, options, options.getRequiredDocument(), question);
            } else {
                onOptionRequiredDocument(answerContainerDocsList, convertView, options, false, question);
            }
            if (options.getRequiredComment() != null) {
                onOptionRequiredComment(answerContainerCommList, convertView, options, options.getRequiredComment(), question);
            } else {
                onOptionRequiredComment(answerContainerCommList, convertView, options, false, question);
            }
        } else if (question.getOptions().size() > 1) {
            onOptionRequiredDocument(answerContainerDocsList, convertView, options, false, question);
            onOptionRequiredComment(answerContainerCommList, convertView, options, false, question);
        } else {
            onOptionRequiredDocument(answerContainerDocsList, convertView, options, false, question);
            onOptionRequiredComment(answerContainerCommList, convertView, options, false, question);
        }
    }

    @SuppressWarnings("unused")
    public void configureQuestion(List<Obj> questionContainerDocsList, View convertView, View view, Questions question, int itemPosition) {
        convertView.setTag(R.id.view_1, question);
        if (question == null) return;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (question.getRequired() != null) {
            if (question.getRequired())
                builder.append(getSpanRequired(getContext()));
        }
        if (question.getQuestion() != null) {
            builder.append(getText(getContext(), false, question.getQuestion()));
        }
        if (question.getRequiredDocument()) {
            if (question.getDocument() != null) {
                Long maxDocs = question.getDocument().getMax();
                if (maxDocs != null) {
                    builder.append(" (");
                    builder.append(maxDocs.toString());
                    builder.append(")");
                }
            }
        }
        if (builder.length() > 0 && view != null) {
            if (view instanceof CustomTextView) {
                CustomTextView tb = (CustomTextView) view;
                tb.setText(builder, TextView.BufferType.SPANNABLE);
                tb.setTypeface(getContext().getString(R.string.font_bold));
            } else if (view instanceof CustomEditText) {
                CustomEditText et = (CustomEditText) view;
                et.setText(builder, TextView.BufferType.SPANNABLE);
                et.setTypeface(getContext().getString(R.string.font_bold));
            } else if (view instanceof CustomAutoCompleteTextView) {
                CustomAutoCompleteTextView catv = (CustomAutoCompleteTextView) view;
                catv.setText(builder, TextView.BufferType.SPANNABLE);
                catv.setTypeface(getContext().getString(R.string.font_bold));
            }
        }
    }

    public void addOptions(View view, Questions question) {
        final Options options;
        if (question.getOptions() != null && question.getOptions().size() > 0) {
            options = question.getOptions().get(0);
            if (options.getPlaceHolder() != null) {
                if (view instanceof CustomTextView) {
                    ((CustomTextView) view).setHint(options.getPlaceHolder());
                } else if (view instanceof CustomEditText) {
                    ((CustomEditText) view).setHint(options.getPlaceHolder());
                } else if (view instanceof CustomAutoCompleteTextView) {
                    ((CustomAutoCompleteTextView) view).setHint(options.getPlaceHolder());
                } else if (view instanceof CustomSpinnerView) {
                }
            }
        }
    }

    public boolean setRespText(Object object, View view, Boolean isTime) {
        String text = "";
        boolean bool;
        if (object != null) {
            if (object instanceof Options)
                text = ((Options) object).getOption();
            else if (object instanceof String)
                text = (String) object;
            else if (object instanceof GregorianCalendar) {
                Calendar cal = (Calendar) object;
                if (isTime == null) {
                    text = Tools.getDate(cal, "-") + " " + Tools.getTime(cal) + " hrs.";
                } else if (isTime) {
                    text = Tools.getTime(cal) + " hrs.";
                } else {
                    text = Tools.getDate(cal, "-");
                }
            } else if (object instanceof Autocomplete) {
                Autocomplete a = (Autocomplete) object;
                text = a.getLabel();
            } else if (object instanceof Location) {
                Location location = (Location) object;
                text = location.getLatitude() + " , " + location.getLongitude();
            } else text = "";
            bool = true;
        } else {
            bool = false;
        }
        if (view instanceof CustomTextView) {
            ((CustomTextView) view).setText(text);
        } else if (view instanceof CustomEditText) {
            ((CustomEditText) view).setText(text);
        } else if (view instanceof CustomAutoCompleteTextView) {
            ((CustomAutoCompleteTextView) view).setText(text);
            ((CustomAutoCompleteTextView) view).dismissDropDown();
        }
        return bool;
    }

    public void setError(View errorView, boolean isVsible) {
        if (errorView == null) return;
        if (isVsible)
            errorView.setVisibility(View.VISIBLE);
        else
            errorView.setVisibility(View.GONE);
    }

    @SuppressWarnings("SameParameterValue")
    public Options getExtraOption(String defaultText) {
        if (defaultText == null)
            defaultText = context.getString(R.string.selecione);
        if (defaultText.isEmpty())
            defaultText = context.getString(R.string.selecione);
        Options options = new Options();
        options.setId(-1);
        options.setOrder((double) -1);
        options.setOption(defaultText);
        options.setRequiredComment(false);
        options.setRequiredDocument(false);
        return options;
    }

    public static void loadImageInImageView(ImageView iv, String filePath, int type) {
        String origin;
        switch (type) {
            case HTTP://  "http://site.com/image.png" // from Web
                origin = filePath;
                break;
            case FILE:
                // "file:///mnt/sdcard/image.png" // from SD card
                // "file:///mnt/sdcard/video.avi" // from SD card (video thumbnail)
                origin = "file:///" + filePath;
                break;
            case RESOURCE:
                //"content://media/external/images/media/13" // from content provider
                //"content://media/external/video/media/13" // from content provider (video thumbnail)
                origin = "content://" + filePath;
                break;
            case ASSET:
                //"assets://image.png" // from assets
                origin = "assets://" + filePath;
                break;
            case DRRAWABLE:
                //"drawable://" + R.drawable.img // from drawables (non-9patch images)
                origin = "drawable://" + filePath;
                break;
            default:
                return;
        }

        ImageLoader.getInstance().displayImage(origin, iv, Tools.getImageLoaderOptions(0, R.drawable.dynamic_file), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ImageView iv = (ImageView) view;
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                iv.setAdjustViewBounds(true);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Log.i("", "");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                try {
                    ImageView iv = (ImageView) view;
                    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    iv.setAdjustViewBounds(false);
                    iv.setImageBitmap(loadedImage);
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }

    public static void loadIconNotImage(ImageView iv, String filePath) {
        String origin = "assets://" + filePath;
        ImageLoader.getInstance().displayImage(origin, iv, Tools.getImageLoaderOptions(
                0, R.drawable.dynamic_file), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ImageView iv = (ImageView) view;
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                iv.setAdjustViewBounds(true);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Log.i("", "");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                try {
                    ImageView iv = (ImageView) view;
                    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    iv.setAdjustViewBounds(false);
                    iv.setImageBitmap(loadedImage);
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }

    void configureText(Options options, View view) {
        if (options.getLength() != null) {
            //pone el maximo caracteres para poder insertar
            int lenght = options.getLength().intValue();
            if (lenght > 0) {
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(lenght);
                if (view instanceof CustomTextView) {
                    ((CustomTextView) view).setFilters(filterArray);
                } else if (view instanceof CustomEditText) {
                    ((CustomEditText) view).setFilters(filterArray);
                } else if (view instanceof CustomAutoCompleteTextView) {
                    ((CustomAutoCompleteTextView) view).setFilters(filterArray);
                }
            }
        }


        if (options.getKeyboard() != null) {
            int inputType = Tools.getKeyboard(options.getKeyboard());
            if (view instanceof CustomTextView) {
                ((CustomTextView) view).setInputType(inputType);
            } else if (view instanceof CustomEditText) {
                ((CustomEditText) view).setInputType(inputType);
            } else if (view instanceof CustomAutoCompleteTextView) {
                ((CustomAutoCompleteTextView) view).setInputType(inputType);
            }

        }
    }

    private static SpannableString getSpanRequired(Context context) {
        SpannableString str1 = new SpannableString(" * ");
        str1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.Red)), 0, str1.length(), 0);
        return str1;
    }

    public static SpannableStringBuilder getText(Context context, boolean isRequired, String
            text) {
        SpannableStringBuilder builder = new SpannableStringBuilder();


        if (isRequired) {
            builder.append(ParentViewMain.getSpanRequired(context));
        }
        SpannableString str2 = new SpannableString(text);
        str2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_800)), 0, str2.length(), 0);
        builder.append(str2);

        return builder;
    }

    public static SpannableStringBuilder getText(Context context, boolean isRequired,
                                                 int textResource) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (isRequired) {
            builder.append(ParentViewMain.getSpanRequired(context));
        }
        SpannableString str2 = new SpannableString(context.getString(textResource));
        str2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_800)), 0, str2.length(), 0);
        builder.append(str2);

        return builder;
    }


    @SuppressLint("StaticFieldLeak")
    void waitSeconds(final Interfaces.OnResponse<Boolean> response) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                CustomDialog.showProgressDialog(getContext(), true, "Cargando...");
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                CustomDialog.showProgressDialog(getContext(), false, "Cargando...");
                response.onResponse(0, true);
            }
        }.execute();

    }

    void loadQuestionOptionsAnswers(Questions question, Options option, FrameLayout iv, boolean isAnswered) {
        UserDatabaseHelper userHelper = UserDatabaseHelper.getHelper(getContext());
        try {
            Dao<TemporalForm, Long> daoTemp = userHelper.getDao(TemporalForm.class);
            Integer idOption = option.getId();
            TemporalForm temporalForm = daoTemp.queryBuilder().where().eq(TemporalForm.COL_ID_OPTION, idOption)
                    .and().eq(TemporalForm.COL_ID_NOTA, idNota)
                    .and().eq(TemporalForm.COL_ID_TAREA, idTarea)
                    .queryForFirst();
            if (temporalForm != null) {
                String ans = temporalForm.getAnswers();
                if (ans != null && !ans.isEmpty()) {
                    iv.setVisibility(View.VISIBLE);
                } else {
                    iv.setVisibility(View.GONE);
                    if (!isAnswered)
                        deleteOptionsQuuestions(question.getId());
                }
            } else {
                iv.setVisibility(View.GONE);
                if (!isAnswered)
                    deleteOptionsQuuestions(question.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userHelper.close();
        }
    }

    void addMoreQuestionsBecauseOptionsHas(Options option, int positionQ, int opt) {
        Intent i = new Intent();
        i.putExtra("option", option);
        i.putExtra("positionQ", positionQ);
        if (opt == 1)
            getFragment().onActivityResult(DynamicFormAdapter.OPTION_WITH_QUESTION_SELECTED, 0, i);
        else
            getFragment().onActivityResult(DynamicFormAdapter.OPTION_CLEAN_QUESTION_SELECTED, 0, i);
    }

    public void deleteOptionsQuuestions(Long idQuestion) {
//        UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getContext());
//        try {
//            Dao<TemporalForm, Long> dao = helper.getDao(TemporalForm.class);
//            DeleteBuilder<TemporalForm, Long> deleteBuilder = dao.deleteBuilder();
//            deleteBuilder.where().eq(TemporalForm.COL_ID_QUESTION, idQuestion)
//                    .and().eq(TemporalForm.COL_ID_NOTA, idNota)
//                    .and().eq(TemporalForm.COL_ID_TAREA, idTarea);
//            deleteBuilder.delete();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            helper.close();
//        }
    }

    void loadQuestionOptions(Questions question, Options options, FrameLayout fl, boolean isAnswered, int position) {
        if (options == null)
            return;
        int idOption = options.getId() != null ? options.getId() : 0;
        CatalogDatabaseHelper helper = CatalogDatabaseHelper.getHelper(getContext());
        OptionsQuestionsForm optionsQuestionsForm = null;
        try {
            Dao<OptionsQuestionsForm, Long> dao = helper.getDao(OptionsQuestionsForm.class);
            QueryBuilder<OptionsQuestionsForm, Long> query = dao.queryBuilder();
            query.where().eq(OptionsQuestionsForm.COL_ID_OPTION, idOption);
            optionsQuestionsForm = query.queryForFirst();
        } catch (Exception ignored) {
        } finally {
            helper.close();
        }
        if (optionsQuestionsForm != null && !isAnswered) {
            //es de tipo cuestionario
            fl.setVisibility(View.VISIBLE);
            Intent intentDocs = new Intent(getContext(), AddOptionsQuestionsActivity.class);
            intentDocs.putExtras(getFragment().getArguments());
            intentDocs.putExtra(FragmentParent.IS_HOME_ENABLED, true);
            intentDocs.putExtra(FragmentParent.TITLE_FRAGMENT, question.getQuestion());
            intentDocs.putExtra(FragmentParent.SUB_TITLE_FRAGMENT, options.getOption());
            intentDocs.putExtra("isImageResize", isImageResize());
            intentDocs.putExtra("OPTION", options);
            intentDocs.putExtra("QUESTION", question);
            intentDocs.putExtra("POSITION", position);
            intentDocs.putExtra(AddOptionsQuestionsActivity.KEY_ID_TAREA, idTarea);
            intentDocs.putExtra(AddOptionsQuestionsActivity.KEY_ID_NOTA, idNota);
            getFragment().startActivityForResult(intentDocs, DynamicFormAdapter.LIST);
            ((Activity) getContext()).overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    public Long getIdTarea() {
        return idTarea;
    }

    public Long getIdNota() {
        return idNota;
    }

    public static class DynamicViewHolder {
        public FrameLayout flError;
        public CustomAutoCompleteTextView actvAnswer;
        public RadioGroup rgAnswer;
        public CustomEditText etAnswer;
        public RatingBar rbAnswer;
        public ImageView ivImageCaptures;
        public ImageView ivSigned;
        public CustomTextView tvAnswer;
        public CustomTextView tvAnswer2;
        public ImageView moreQuestions;
        public FrameLayout flExpand;
        public FrameLayout flAdd;
        public ImageView ivExpand;
        public ImageView ivAddDocumento;
        public ImageView ivAddSign;
        public ImageView ivAddFile;
        public ImageView ivAddAddress;
        public ImageView ivCopyLink;
        public ImageView ivGoLink;
        public FrameLayout moreQuestionsContainer;
        public List<View> questionContainerDocsList;
        public List<View> answerContainerDocsList;
        public List<View> answerContainerCommList;
        public LinearLayout row;
        View rlAnswerStart;
        View rlAnswerEnd;
        public CustomSpinnerView spAnswer;
        //        ImageView ivImage;
        View rlAnswer;
        ImageView ivDelete2;
        CustomTextView tvQuestion;
        ImageView ivDelete;
        ImageView ivSign;

    }


}
