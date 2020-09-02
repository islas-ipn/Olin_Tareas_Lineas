package mx.com.pendulum.olintareas.ui.adapter.tareas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mx.com.pendulum.olintareas.camera.CameraActivity;
import mx.com.pendulum.olintareas.camera.CaptureVideoActivity;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Obj;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.tareas.views.ViewAddress;
import mx.com.pendulum.olintareas.tareas.views.ViewAutocomplete;
import mx.com.pendulum.olintareas.tareas.views.ViewDate;
import mx.com.pendulum.olintareas.tareas.views.ViewDate_stskt_end;
import mx.com.pendulum.olintareas.tareas.views.ViewDatetime;
import mx.com.pendulum.olintareas.tareas.views.ViewDtetime_stskt_end;
import mx.com.pendulum.olintareas.tareas.views.ViewFile_upload;
import mx.com.pendulum.olintareas.tareas.views.ViewGeolocation;
import mx.com.pendulum.olintareas.tareas.views.ViewLabel;
import mx.com.pendulum.olintareas.tareas.views.ViewLink;
import mx.com.pendulum.olintareas.tareas.views.ViewMultichoice;
import mx.com.pendulum.olintareas.tareas.views.ViewRating_bar;
import mx.com.pendulum.olintareas.tareas.views.ViewSign;
import mx.com.pendulum.olintareas.tareas.views.ViewSpinner;
import mx.com.pendulum.olintareas.tareas.views.ViewText;
import mx.com.pendulum.olintareas.tareas.views.ViewTime;
import mx.com.pendulum.olintareas.tareas.views.ViewTime_stskt_end;

public class DynamicFormAdapter extends BaseAdapter {

    private List<Questions> list;
    private static final int TEXT = 1;
    public static final int FILE_UPLOAD = 2;
    public static final int GEOLOCATION = 3;
    public static final int SIGN = 4;
    public static final int CHOICE = 5;
    public static final int LIST = 6;
    private static final int MULTICHOICE = 7;
    private static final int DATE = 8;
    private static final int DATE_STSKT_END = 9;
    private static final int DATETIME = 10;
    private static final int DTETIME_STSKT_END = 11;
    private static final int TIME = 12;
    private static final int TIME_STSKT_END = 13;
    private static final int RAW = 14;
    public static final int VIDEO = 15;
    private static final int TEXT_AREA = 17;
    private static final int IMAGE = 18;
    private static final int RICH_TEXT = 19;
    private static final int RATING_BAR = 20;
    private static final int AUTOCOMPLETE = 21;
    private static final int LABEL = 22;
    private static final int BOOLEAN = 23;
    private static final int LINK = 24;
    private static final int DATE_READ = 25;
    private static final int FILE_UPLOAD_DESC = 26;
    private static final int HOOK = 27;
    public static final int ADDRESS = 28;
    private static final int TYPE_MAX_COUNT = 29;
    public static final int OPTION_WITH_QUESTION_SELECTED = 7683;
    public static final int OPTION_CLEAN_QUESTION_SELECTED = 7684;
    private ViewText viewText;
    private ViewFile_upload viewFile_upload;
    private ViewAddress viewAddress;
    private ViewGeolocation viewGeolocation;
    private ViewSign viewSign;
    //private ViewList viewList;
    private ViewSpinner viewSpinner;
    private ViewMultichoice viewMultichoice;
    private ViewDate viewDate;
    private ViewDate_stskt_end viewDate_stskt_end;
    private ViewDatetime viewDatetime;
    private ViewDtetime_stskt_end viewDtetime_stskt_end;
    private ViewTime viewTime;
    private ViewTime_stskt_end viewTime_stskt_end;
    //    private ViewText_area viewText_area;
//    private ViewRich_text viewRich_text;
    private ViewRating_bar viewRating_bar;
    private ViewAutocomplete viewAutocomplete;
    private ViewLabel viewLabel;
    private ViewLink viewLink;
    private Collection<AnswerDTO> pendingAnswers;
    private boolean isImageResize;
    private Long idTarea;
    private Long idNota;

    public DynamicFormAdapter(Context context, List<Questions> list, Fragment fragment, boolean isImageResize, Long idTarea, Long idNota) {
        this.list = list;
        viewText = new ViewText(context, fragment, this);
        viewFile_upload = new ViewFile_upload(context, fragment, this);
        viewAddress = new ViewAddress(context, fragment, this);
        viewGeolocation = new ViewGeolocation(context, fragment, this);
        viewSign = new ViewSign(context, fragment, this);
        viewSpinner = new ViewSpinner(context, fragment, this);
        viewMultichoice = new ViewMultichoice(context, fragment, this);
        viewDate = new ViewDate(context, fragment, this);
        viewDate_stskt_end = new ViewDate_stskt_end(context, fragment, this);
        viewDatetime = new ViewDatetime(context, fragment, this);
        viewDtetime_stskt_end = new ViewDtetime_stskt_end(context, fragment, this);
        viewTime = new ViewTime(context, fragment, this);
        viewTime_stskt_end = new ViewTime_stskt_end(context, fragment, this);
        viewRating_bar = new ViewRating_bar(context, fragment, this);
        viewAutocomplete = new ViewAutocomplete(context, fragment, this);
        viewLabel = new ViewLabel(context, fragment, this);
        viewLink = new ViewLink(context, fragment, this);
        this.isImageResize = isImageResize;
        this.idTarea = idTarea;
        this.idNota = idNota;
    }

    public void setPendingAnswers(Collection<AnswerDTO> pendingAnswers) {
        this.pendingAnswers = pendingAnswers;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Questions getItem(int position) {
        return list.get(position);
    }

    private void setItemsPosition(Questions questions, int position) {
        list.set(position, questions);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        String type = list.get(position).getType().toUpperCase();
        switch (type) {
            default:
            case "TEXT":
                return TEXT;
            case "ADDRESS":
                return ADDRESS;
            case "FILE_UPLOAD":
                return FILE_UPLOAD;
            case "FILE_UPLOAD_DESC":
                return FILE_UPLOAD_DESC;
            case "GEOLOCATION":
                return GEOLOCATION;
            case "SIGN":
                return SIGN;
            case "CHOICE":
                return CHOICE;
            case "LIST":
                return LIST;
            case "MULTICHOICE":
                return MULTICHOICE;
            case "DATE_READ":
                return DATE_READ;
            case "DATE":
                return DATE;
            case "DATE_STSKT_END":
                return DATE_STSKT_END;
            case "DATETIME":
                return DATETIME;
            case "DTETIME_STSKT_END":
                return DTETIME_STSKT_END;
            case "TIME":
                return TIME;
            case "TIME_STSKT_END":
                return TIME_STSKT_END;
            case "RAW":
                return RAW;
            case "VIDEO":
                return VIDEO;
            case "TEXT_AREA":
                return TEXT_AREA;
            case "IMAGE":
                return IMAGE;
            case "RICH_TEXT":
                return RICH_TEXT;
            case "RATING_BAR":
                return RATING_BAR;
            case "AUTOCOMPLETE":
                return AUTOCOMPLETE;
            case "LABEL":
                return LABEL;
            case "HOOK":
                return HOOK;
            case "BOOLEAN":
                return BOOLEAN;
            case "LINK":
                return LINK;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Questions question = getItem(position);
        long idQuestion = question.getId();
        AnswerDTO pendingAnswer = findAnswerById(idQuestion);
        switch (getItemViewType(position)) {
            default:
            case SIGN:
                viewSign.setPendingAnswer(pendingAnswer);
                viewSign.setIsImageResize(isImageResize, idTarea, idNota);
                return viewSign.getView(position, convertView, question);
            case ADDRESS:
                viewAddress.setPendingAnswer(pendingAnswer);
                viewAddress.setIsImageResize(isImageResize, idTarea, idNota);
                return viewAddress.getView(position, convertView, question);
            case FILE_UPLOAD_DESC:
            case IMAGE:
            case VIDEO:
            case RAW:
            case FILE_UPLOAD:
                viewFile_upload.setPendingAnswer(pendingAnswer);
                viewFile_upload.setIsImageResize(isImageResize, idTarea, idNota);
                return viewFile_upload.getView(position, convertView, question);
            case RICH_TEXT:
            case TEXT_AREA:
            case TEXT:
                viewText.setPendingAnswer(pendingAnswer);
                viewText.setIsImageResize(isImageResize, idTarea, idNota);
                return viewText.getView(position, convertView, question);
            case AUTOCOMPLETE:
                viewAutocomplete.setPendingAnswer(pendingAnswer);
                viewAutocomplete.setIsImageResize(isImageResize, idTarea, idNota);
                return viewAutocomplete.getView(position, convertView, question);
            case GEOLOCATION:
                viewGeolocation.setPendingAnswer(pendingAnswer);
                viewGeolocation.setIsImageResize(isImageResize, idTarea, idNota);
                return viewGeolocation.getView(position, convertView, question);
            case CHOICE:
            case LIST:
            case BOOLEAN:
                viewSpinner.setPendingAnswer(pendingAnswer);
                viewSpinner.setIsImageResize(isImageResize, idTarea, idNota);
                return viewSpinner.getView(position, convertView, question);
            /*case BOOLEAN:
                viewList.setPendingAnswer(pendingAnswer);
                viewList.setIsImageResize(isImageResize, idTarea, idNota);
                return viewList.getView(position, convertView, question);*/
            case DATE_READ:
            case DATE:
                viewDate.setPendingAnswer(pendingAnswer);
                viewDate.setIsImageResize(isImageResize, idTarea, idNota);
                return viewDate.getView(position, convertView, question);
            case TIME:
                viewTime.setPendingAnswer(pendingAnswer);
                viewTime.setIsImageResize(isImageResize, idTarea, idNota);
                return viewTime.getView(position, convertView, question);
            case RATING_BAR:
                viewRating_bar.setPendingAnswer(pendingAnswer);
                viewRating_bar.setIsImageResize(isImageResize, idTarea, idNota);
                return viewRating_bar.getView(position, convertView, question);
            case LABEL:
            case HOOK:
                return viewLabel.getView(position, convertView, question);
            case LINK:
                return viewLink.getView(position, convertView, question);
            case DATETIME:
                //TODO aun no es soportado por base de datos
                viewDatetime.setPendingAnswer(pendingAnswer);
                viewDatetime.setIsImageResize(isImageResize, idTarea, idNota);
                return viewDatetime.getView(position, convertView, question);
            case MULTICHOICE:
                //TODO guardado temporal
                //TODO no soportada en base de datos
                return viewMultichoice.getView(position, convertView, question);
            case DATE_STSKT_END://TODO no soportada en base de datos
                return viewDate_stskt_end.getView(position, convertView, question);
            case DTETIME_STSKT_END://TODO no soportada en base de datos
                return viewDtetime_stskt_end.getView(position, convertView, question);
            case TIME_STSKT_END://TODO no soportada en base de datos
                return viewTime_stskt_end.getView(position, convertView, question);
        }
    }

    public void setImagePathItem(int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            ArrayList<String> paths = data.getStringArrayListExtra(CameraActivity.KEY_DATA);
            //paths = data.getStringArrayExtra(CameraActivity.KEY_DATA);
            File file = new File(paths.get(0));
            String fileName = file.getName();
            String[] array = fileName.split(ViewFile_upload.SEPATATOR);
            int pos = Integer.parseInt(array[1]);
            getItem(pos).setObject(data);
            notifyDataSetChanged();
        }
    }

    public void setFilePathItem(int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            //String path = data.getStringExtra(CameraActivity.KEY_DATA);
            ArrayList<String> paths = data.getStringArrayListExtra(CameraActivity.KEY_DATA);
            File file = new File(paths.get(0));
            String fileName = file.getName();
            String[] array = fileName.split(ViewFile_upload.SEPATATOR);
            int pos = Integer.parseInt(array[array.length - 3]);
            getItem(pos).setObject(data);
            notifyDataSetChanged();
        }
    }

    public void setImagePathItemFromCamera(int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            ArrayList<String> paths = data.getStringArrayListExtra(CaptureVideoActivity.KEY_DATA);
            Bundle arguments = data.getExtras();
            Questions questions;
            if (arguments != null) {
                questions = (Questions) arguments.get(CaptureVideoActivity.KEY_QUESTION);
                if (questions != null) {
                    if (questions.getObject() == null)
                        questions.setObject(arguments.get(CaptureVideoActivity.KEY_INTENT_OBJECT));
                    if (questions.getAnswer() == null)
                        questions.setAnswer(arguments.get(CaptureVideoActivity.KEY_INTENT_ANSWER));
                    if (questions.getAnswer2() == null)
                        questions.setAnswer2(arguments.get(CaptureVideoActivity.KEY_INTENT_ANSWER));
                    for (String path : paths) {
                        File file = new File(path);
                        String fileName = file.getName();
                        String[] array = fileName.split(ViewFile_upload.SEPATATOR);
                        int pos = Integer.parseInt(array[1]);
                        int rowPosition = Integer.parseInt(array[2]);
                        getItem(pos).setObject(data);
                        questions.setObject(data);
                        int diff = rowPosition - questions.getQuestionContainerDocsList().size();
                        if (diff >= 0) {
                            for (int i = 0; i <= diff; i++) {
                                final Obj obj = new Obj();
                                questions.getQuestionContainerDocsList().add(obj);
                            }
                        }
                        Obj obj = questions.getQuestionContainerDocsList().get(rowPosition);
                        obj.setObj(path);
                        setItemsPosition(questions, pos);
                    }
                }
                notifyDataSetChanged();
            }
        }
    }

    public void setImagePathItemFromSignature(int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            ArrayList<String> paths = data.getStringArrayListExtra(CaptureVideoActivity.KEY_DATA);
            for (String path : paths) {
                Bundle arguments = data.getExtras();
                Questions questions;
                if (arguments != null) {
                    questions = (Questions) arguments.get(CaptureVideoActivity.KEY_QUESTION);
                    if (questions != null) {
                        if (questions.getObject() == null)
                            questions.setObject(arguments.get(CaptureVideoActivity.KEY_INTENT_OBJECT));
                        if (questions.getAnswer() == null)
                            questions.setAnswer(arguments.get(CaptureVideoActivity.KEY_INTENT_ANSWER));
                        if (questions.getAnswer2() == null)
                            questions.setAnswer2(arguments.get(CaptureVideoActivity.KEY_INTENT_ANSWER));
                        File file = new File(path);
                        String fileName = file.getName();
                        String[] array = fileName.split(ViewFile_upload.SEPATATOR);
                        int pos = Integer.parseInt(array[1]);
                        int rowPosition = Integer.parseInt(array[2]);
                        getItem(pos).setObject(data);
                        questions.setObject(data);
                        if (rowPosition >= questions.getQuestionContainerDocsList().size()) {
                            final Obj obj = new Obj();
                            questions.getQuestionContainerDocsList().add(obj);
                        }
                        Obj obj = questions.getQuestionContainerDocsList().get(rowPosition);
                        obj.setObj(path);
                        setItemsPosition(questions, pos);
                    }
                    notifyDataSetChanged();
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void setVideoPathItemFromCamera(int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            ArrayList<String> paths = data.getStringArrayListExtra(CaptureVideoActivity.KEY_DATA);
            Questions questions;
            Bundle arguments;
            for (String path : paths) {
                arguments = data.getExtras();
                if (arguments != null) {
                    questions = (Questions) arguments.get(CaptureVideoActivity.KEY_QUESTION);
                    if (questions != null) {
                        if (questions.getObject() == null)
                            questions.setObject(arguments.get(CaptureVideoActivity.KEY_INTENT_OBJECT));
                        if (questions.getAnswer() == null)
                            questions.setAnswer(arguments.get(CaptureVideoActivity.KEY_INTENT_ANSWER));
                        if (questions.getAnswer2() == null)
                            questions.setAnswer2(arguments.get(CaptureVideoActivity.KEY_INTENT_ANSWER));
                        File file = new File(path);
                        String fileName = file.getName();
                        String[] array = fileName.split(ViewFile_upload.SEPATATOR);
                        int pos = Integer.parseInt(array[1]);
                        int rowPosition = Integer.parseInt(array[2]);
                        getItem(pos).setObject(data);
                        if (rowPosition >= questions.getQuestionContainerDocsList().size()) {
                            final Obj obj = new Obj();
                            questions.getQuestionContainerDocsList().add(obj);
                        }
                        Obj obj = questions.getQuestionContainerDocsList().get(rowPosition);
                        obj.setObj(path);
                        questions.setObject(data);
                        setItemsPosition(questions, pos);
                        notifyDataSetChanged();
                    }
                }
            }
        }
    }

    public void setGeolocation(int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            int pos = data.getIntExtra("POSITION", 0);
            double lat = data.getDoubleExtra("LATITUD", 0d);
            double lng = data.getDoubleExtra("LONGITUD", 0d);
            LatLng latLng = new LatLng(lat, lng);
            getItem(pos).setObject(latLng);
            notifyDataSetChanged();
        }
    }

    public void setAddress(int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            int pos = data.getIntExtra("POSITION", 0);
            getItem(pos).setError(false);
            String idAddress = data.getStringExtra("key_data");
            getItem(pos).setObject(idAddress);
            notifyDataSetChanged();
        }
    }

    public void setFormAsAnswer(int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            int pos = data.getIntExtra("POSITION", 0);
            getItem(pos).setError(false);
            notifyDataSetChanged();
        }
    }

    private AnswerDTO findAnswerById(long idQuestion) {
        try {
            for (AnswerDTO item : pendingAnswers) {
                long id = item.getId_question();
                if (id == idQuestion) {
                    return item;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}