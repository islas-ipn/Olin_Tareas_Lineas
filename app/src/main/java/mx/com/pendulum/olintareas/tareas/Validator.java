package mx.com.pendulum.olintareas.tareas;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.dto.Lw;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Autocomplete;
import mx.com.pendulum.olintareas.dto.tareasV2.Comment;
import mx.com.pendulum.olintareas.dto.tareasV2.Document;
import mx.com.pendulum.olintareas.dto.tareasV2.EpochDate;
import mx.com.pendulum.olintareas.dto.tareasV2.FileUploadDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Obj;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.dto.tareasV2.ResponsablesDTO;
import mx.com.pendulum.utilities.Tools;

import static mx.com.pendulum.olintareas.Properties.SPINNER_OPTION_SELECT_STRING;

public class Validator {

    private Object obj;
    private Object value;
    private Object params;
    private Location mLocation;
    private String creditOrJuicio;
    private Context context;

    public Validator(Context context, View view, Object params) {
        this.obj = view.getTag();
        this.params = params;
        this.context = context;
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    public boolean validate(boolean showSnack) {
        if (obj == null) {
            value = new Object();
            return !isRequired();
        }
        if (params == null) return false;
        String classType = obj.getClass().getSimpleName();
        boolean isValid = false;
        try {
            Method m = this.getClass().getDeclaredMethod(classType);
            isValid = (boolean) m.invoke(this);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    public Object getValue() {
        if (value instanceof AnswerDTO) {
            AnswerDTO dto = (AnswerDTO) value;
            if (dto.getResponse() != null) {
                if (dto.getResponse().endsWith(" - ")) {
                    dto.setResponse(dto.getResponse().replace(" - ", ""));
                }
                if (dto.getResponse().contains("null")) {
                    dto.setResponse(dto.getResponse().replace("null", ""));
                }
            }
        }
        return value;
    }

    private boolean isRequired() {
        if (params instanceof Boolean) {
            return (Boolean) params;
        }
        //more validations
        return true;
    }

    @SuppressWarnings("unused")
    private boolean DynamicViewHolder() {
        if (!(params instanceof Questions)) return false;
        Questions question = (Questions) params;
        boolean isValid;
        isValid = validType(validRequired(question.getRequired()), question, true);
        if (!isValid) return false;
        isValid = validRequiredDocument(question.getRequiredDocument(), question.getDocument(), question);
        return isValid;
    }

    @SuppressWarnings({"unchecked", "UnusedAssignment"})
    private boolean validType(boolean isRequired, Questions question, boolean showSnack) { //TODO GUARDAR EN RESPONSE
        String type = question.getType();
        ArrayList<Options> options = question.getOptions();
        Options option;
        if (options.size() > 0)
            option = options.get(0);
        else option = new Options();
        if (question.getRequired() == null) {
            question.setRequired(false);
        }
        if (type == null) return false;
        AnswerDTO answerDTO = new AnswerDTO();
        boolean isValid = false;
        answerDTO.setQuestion(question.getQuestion());
        switch (type.toUpperCase()) {
            default:
                isValid = isRequired;
                break;
            case "IMAGE":
            case "VIDEO":
            case "RAW":
            case "FILE_UPLOAD": {
                long maxDoc = question.getDocument().getMax();
                int takend = 0;
                File file;
                for (Obj o : question.getQuestionContainerDocsList()) {
                    if (o == null) continue;
                    if (o.getObj() == null) continue;
                    if (!(o.getObj() instanceof String)) continue;
                    file = new File((String) o.getObj());
                    if (file.exists())
                        takend++;
                }
                answerDTO.setId_question(question.getId());
                answerDTO.setId_option(options.get(0).getId());
                answerDTO.setType(question.getType());
                String pluralObj = "";
                String plural = "";
                String pluralPer = "";
                switch (type.toUpperCase()) {
                    case "IMAGE":
                        if (takend == 0)
                            break;
                        if (takend != 1) {
                            pluralObj = "es";
                            plural = "s";
                        }
                        if (maxDoc != 1)
                            pluralPer = "s";
                        answerDTO.setResponse(takend + " imagen" + pluralObj + " capturada" + plural + " de " + maxDoc + " permitida" + pluralPer);
                        break;
                    case "RAW":
                    case "VIDEO":
                        if (takend == 0)
                            break;
                        if (takend != 1) {
                            pluralObj = "s";
                            plural = "s";
                        }
                        if (maxDoc != 1)
                            pluralPer = "s";
                        answerDTO.setResponse(takend + " video" + pluralObj + " capturado" + plural + " de " + maxDoc + " permitido" + pluralPer);
                        break;
                    case "FILE_UPLOAD":
                        if (takend == 0)
                            break;
                        if (takend != 1) {
                            pluralObj = "s";
                            plural = "s";
                        }
                        if (maxDoc != 1)
                            pluralPer = "s";
                        answerDTO.setResponse(takend + " archivo" + pluralObj + " capturado" + plural + " de " + maxDoc + " permitido" + pluralPer);
                        break;
                }
                isValid = true;
            }
            break;
            case "TEXT": {
                String ans = (String) question.getAnswer();
                if (question.getRequired() && (ans == null || ans.isEmpty())) {
                    break;
                }
                if (ans != null) {
                    ans = ans.trim();
                    if (!validRegexp(option.getRegexp(), ans, showSnack, option.getKeyboard(), question)) {
                        Log.i("", "");
                        break;
                    }
                    if (!validLength(option.getLength(), ans, showSnack)) {
                        Log.i("", "");
                        break;
                    }
                } else {
                    ans = "";
                }
                answerDTO.setId_question(question.getId());
                answerDTO.setId_option(options.get(0).getId());
                answerDTO.setType(question.getType());
                answerDTO.setResponse(ans + " - ");
                answerDTO.setValue(ans);
                isValid = true;
            }
            break;
            case "GEOLOCATION": {
                Location location = (Location) question.getAnswer();
                if (question.getRequired() && location == null) {
                    break;
                }
                String value;
                if (location == null)
                    value = "0" + " , " + "0";
                else
                    value = location.getLatitude() + " , " + location.getLongitude();
                answerDTO.setId_question(question.getId());
                answerDTO.setId_option(options.get(0).getId());
                answerDTO.setType(question.getType());
                answerDTO.setResponse(value + " - ");
                answerDTO.setValue(value);
//                answerDTO.setLocationDTO(); //TODO poner este dto en cuanto este disponible en lugar de value
                isValid = true;
            }
            break;
            case "ADDRESS":
                String idAddress = (String) question.getAnswer();
                if (question.getRequired() && question.getAnswer() == null) {
                    break;
                }
                answerDTO.setId_question(question.getId());
                answerDTO.setId_option(options.get(0).getId());
                answerDTO.setType(question.getType());
                answerDTO.setResponse(idAddress);
                answerDTO.setValue(idAddress);
                isValid = true;
                break;
            case "SIGN": {
                int takend = 0;
                File file;
                for (Obj o : question.getQuestionContainerDocsList()) {
                    if (o == null) continue;
                    if (o.getObj() == null) continue;
                    if (!(o.getObj() instanceof String)) continue;
                    file = new File((String) o.getObj());
                    if (file.exists())
                        takend++;
                }
                if (takend > 0)
                    answerDTO.setResponse("Firma capturada");
                isValid = true;
                answerDTO.setId_question(question.getId());
                answerDTO.setId_option(options.get(0).getId());
                answerDTO.setType(question.getType());
            }
            break;
            case "CHOICE": {
                Options selectedOption = (Options) question.getAnswer();
                if (selectedOption == null && isRequired) {
                    return false;
                }
                if (selectedOption != null && selectedOption.getOption().equalsIgnoreCase(SPINNER_OPTION_SELECT_STRING)) {
                    if (isRequired) {
                        return false;
                    }
                    answerDTO.setId_option(0);
                    answerDTO.setResponse(null);
                    answerDTO.setValue(null);
                } else {
                    answerDTO.setId_option(selectedOption == null ? 0 : selectedOption.getId());
                    answerDTO.setResponse((selectedOption == null ? null : selectedOption.getOption()) + " - ");
                    answerDTO.setValue(selectedOption == null ? null : selectedOption.getOption());
                }
                isValid = true;
                answerDTO.setId_question(question.getId());
                answerDTO.setType(question.getType());
                value = answerDTO;
                if (selectedOption != null) {
                    isValid = validOptionRequiredComment(question.getAnswerContainerCommList(), selectedOption.getRequiredComment(), selectedOption.getComment());
                    if (!isValid) return false;
                    isValid = validOptionRequiredDocument(question.getAnswerContainerDocsList(), selectedOption.getRequiredDocument(), selectedOption.getDocument(), question);
                }
            }
            return isValid;
            case "BOOLEAN":
            case "LIST": {
                Options selectedOption = (Options) question.getAnswer();
                if (selectedOption == null) {
                    if (isRequired) {
                        return false;
                    }
                }
                if (selectedOption != null && selectedOption.getOption().equalsIgnoreCase(SPINNER_OPTION_SELECT_STRING)) {
                    if (isRequired) {
                        return false;
                    }
                    answerDTO.setId_option(0);
                    answerDTO.setResponse(null);
                    answerDTO.setValue(null);
                } else {
                    answerDTO.setId_option(selectedOption == null ? 0 : selectedOption.getId());
                    answerDTO.setResponse((selectedOption == null ? null : selectedOption.getOption()) + " - ");
                    answerDTO.setValue(selectedOption == null ? null : selectedOption.getOption());
                }
                isValid = true;
                answerDTO.setId_question(question.getId());
                answerDTO.setType(question.getType());
                value = answerDTO;
                if (selectedOption != null) {
                    isValid = validOptionRequiredComment(question.getAnswerContainerCommList(), selectedOption.getRequiredComment(), selectedOption.getComment());
                    if (!isValid) return false;
                    isValid = validOptionRequiredDocument(question.getAnswerContainerDocsList(), selectedOption.getRequiredDocument(), selectedOption.getDocument(), question);
                }
            }
            return isValid;
            case "MULTICHOICE": {
                ArrayList<Options> selectedOptions = (ArrayList<Options>) question.getAnswer();
                if (selectedOptions == null) {
                    selectedOptions = new ArrayList<>();
                    if (isRequired) {
                        return false;
                    }
                }
                boolean requiredComment = false;
                boolean requiredDocument = false;
                Comment comment = null;
                Document documen = null;
                String str = "";
                for (Options op : selectedOptions) {
                    if (!requiredComment)
                        requiredComment = op.getRequiredComment() == null ? false : op.getRequiredComment();
                    if (!requiredDocument)
                        requiredDocument = op.getRequiredDocument() == null ? false : op.getRequiredDocument();
                    if (comment == null)
                        comment = op.getComment() == null ? null : op.getComment();
                    if (documen == null)
                        documen = op.getDocument();
                    str += op.getId() + ",";
                }
                if (str.endsWith(","))
                    str = Tools.removeLastChar(str);
                isValid = true;
                answerDTO.setId_question(question.getId());
                answerDTO.setId_option(options.get(0).getId());
                answerDTO.setType(question.getType());
                answerDTO.setResponse((str.equals("") ? null : str) + " - ");
                answerDTO.setValue(str.equals("") ? null : str);
                value = answerDTO;
                Log.i("", "");
                if (!str.equals("")) {
                    isValid = validOptionRequiredComment(question.getAnswerContainerCommList(), requiredComment, comment);
                    if (!isValid) return false;
                    isValid = validOptionRequiredDocument(question.getAnswerContainerDocsList(), requiredDocument, documen, question);
                }
            }
            return isValid;
            case "DATE_READ":
            case "DATE": {
                Calendar cal = (Calendar) question.getAnswer();
                if (question.getRequired() && cal == null) {
                    break;
                }
                answerDTO.setId_question(question.getId());
                answerDTO.setId_option(options.get(0).getId());
                answerDTO.setType(question.getType());
                answerDTO.setResponse((cal == null ? "" : getEpochDateWithOutTime(cal).getDateStr()) + " - ");
                answerDTO.setValue(cal == null ? "" : Tools.getDate(cal, "-"));
                answerDTO.setEpochDate(getEpochDate(cal));
                isValid = true;
                Log.i("", "");
            }
            break;
            case "DATETIME": {
                Calendar cal = (Calendar) question.getAnswer();
                if (question.getRequired() && cal == null) {
                    break;
                }
                answerDTO.setId_question(question.getId());
                answerDTO.setId_option(options.get(0).getId());
                answerDTO.setType(question.getType());
                answerDTO.setResponse((cal != null ? getEpochDate(cal).getDateStr() : "") + " - ");
                answerDTO.setValue(cal == null ? "" : (Tools.getDate(cal, "-") + " " + Tools.getTime(cal) + " hrs."));
                answerDTO.setEpochDate(getEpochDate(cal));
                isValid = true;
                Log.i("", "");
            }
            break;
            case "DATE_STSKT_END":
            case "DTETIME_STSKT_END":
            case "TIME_STSKT_END": {
                Calendar cal1 = (Calendar) question.getAnswer();
                Calendar cal2 = (Calendar) question.getAnswer2();
                if (question.getRequired() && (cal1 == null || cal2 == null)) {
                    break;
                }
                if (cal1 == null || cal2 == null) {
                    cal1 = null;
                    cal2 = null;
                }
                answerDTO.setId_question(question.getId());
                answerDTO.setId_option(options.get(0).getId());
                answerDTO.setType(question.getType());
                String tmp = "";
                try {
                    tmp = getEpochDate(cal1).getDateStr() + ";;" + getEpochDate(cal2).getDateStr();
                } catch (Exception ignored) {
                }
                answerDTO.setResponse(tmp + " - ");
                answerDTO.setValue(tmp);
                isValid = true;
                Log.i("", "");
            }
            break;
            case "TIME": {
                Calendar cal = (Calendar) question.getAnswer();
                if (question.getRequired() && cal == null) {
                    break;
                }
                answerDTO.setId_question(question.getId());
                answerDTO.setId_option(options.get(0).getId());
                answerDTO.setType(question.getType());
                answerDTO.setResponse((cal == null ? "" : getEpochDateJustTime(cal).getDateStr()) + " - ");
                answerDTO.setValue(cal == null ? "" : (Tools.getTime(cal) + " hrs."));
                answerDTO.setEpochDate(getEpochDate(cal));
                isValid = true;
                Log.i("", "");
            }
            break;
            case "RICH_TEXT":
            case "TEXT_AREA": {
                String ans = (String) question.getAnswer();
                if (question.getRequired() && (ans == null || ans.isEmpty())) {
                    break;
                }
                if (ans != null) {
                    ans = ans.trim();
                    if (!validRegexp(option.getRegexp(), ans, showSnack, option.getKeyboard(), question)) {
                        Log.i("", "");
                        break;
                    }
                    if (!validLength(option.getLength(), ans, showSnack)) {
                        Log.i("", "");
                        break;
                    }
                } else {
                    ans = "";
                }
                answerDTO.setId_question(question.getId());
                answerDTO.setId_option(options.get(0).getId());
                answerDTO.setType(question.getType());
                answerDTO.setResponse(ans);
                answerDTO.setValue(ans);
                isValid = true;
            }
            break;
            case "RATING_BAR": {
                Float rating = (Float) question.getAnswer();
                if (question.getRequired() && rating == null) {
                    break;
                }
                answerDTO.setId_question(question.getId());
                answerDTO.setId_option(options.get(0).getId());
                answerDTO.setType(question.getType());
                answerDTO.setResponse((rating == null ? "" : rating) + " - ");
                answerDTO.setValue(rating + "");
                isValid = true;
                Log.i("", "");
            }
            break;
            case "AUTOCOMPLETE": {
                Autocomplete autocomplete = (Autocomplete) question.getAnswer();
                if (question.getRequired() && autocomplete == null) {
                    break;
                }
                answerDTO.setId_question(question.getId());
                answerDTO.setId_option(options.get(0).getId());
                answerDTO.setType(question.getType());
                answerDTO.setResponse((autocomplete == null ? "" : autocomplete.getLabel()) + " - ");
                answerDTO.setValue(autocomplete == null ? null : autocomplete.getValue());
                isValid = true;
                Log.i("", "");
            }
            break;
            case "HOOK":
            case "GRID":
            case "LINK":
            case "LABEL":
                answerDTO.setId_question(question.getId());
                if (options.size() > 0)
                    answerDTO.setId_option(options.get(0).getId());
                else
                    answerDTO.setId_option(null);
                answerDTO.setType(question.getType());
                answerDTO.setResponse(null);
                answerDTO.setValue(null);
                isValid = true;
                break;
        }
        value = answerDTO;
        if (!isValid) return false;
        isValid = validOptionRequiredComment(question.getAnswerContainerCommList(), option.getRequiredComment(), option.getComment());
        if (!isValid) return false;
        isValid = validOptionRequiredDocument(question.getAnswerContainerDocsList(), option.getRequiredDocument(), option.getDocument(), question);
        return isValid;
    }

    private boolean validRequired(Boolean required) {
        if (required == null) return false;
        return required;
    }

    public static boolean validate(Context context, boolean showSnack, String filePath, Document document) {
        if (document == null) return true;
        File file = new File(filePath);
        return file.exists()
                && validateExtension(context, showSnack, file.getName(), document.getExtension())
                && validateSize(context, showSnack, file, document.getSize());
    }

    private static boolean validateSize(Context context, boolean showSnack, File f, Long size) {
        if (size == null) return false;
        long fileSize = getFolderSize(f);//call function and convert bytes into Kb
        if (fileSize <= size) {
            return true;
        } else {
            String errorStr = "Error, archivo demasiado grande";
            if (showSnack) {
                Tools.showSnack(context, errorStr);
                //Toast.makeText(context, errorStr, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

    private static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size = f.length();
        }
        return size;
    }

    private static boolean validateExtension(Context context, boolean showSnack, String fileName, String extencion) {
        if (extencion == null) return true;
        if (extencion.equals("")) return true;
        if (extencion.equalsIgnoreCase("ALL")) return true;
        String[] ext = extencion.split(",");
        for (String suffix : ext) {
            if (fileName.toUpperCase().endsWith(suffix.toUpperCase())) {
                return true;
            }
        }
        String errorStr = "Error, tipo de archivo incorrecto";
        if (showSnack) {
            //Toast.makeText(context, errorStr, Toast.LENGTH_SHORT).show();
            Tools.showSnack(context, errorStr);
        }
        return false;
    }

    private boolean validRequiredDocument(Boolean requiredDocument, Document documentList, Questions question) {
        //si  requiredDocument es nulo entonces ni siquiera se ha pintado la vista de requerimiento de Documentos
        if (requiredDocument == null)
            return false;
        //si requiredDocument es falso entnces no se pinto la vista de documentp
        if (!requiredDocument)
            return true;
        boolean isReuiredDoc = false;
        if (question.getRequired()
                && (question.getType().equals("FILE_UPLOAD")
                || question.getType().equals("RAW")
                || question.getType().equals("SIGN")
                || question.getType().equals("IMAGE")
                || question.getType().equals("VIDEO"))) {
            isReuiredDoc = true;
        }
        //se verifica si por lo menos hay un documento capturado
        boolean isAnyTag = false;
        for (int i = 0; i < question.getQuestionContainerDocsList().size(); i++) {
            if (question.getQuestionContainerDocsList().get(i).getObj() != null) {
                isAnyTag = true;
                break;
            }
        }
        if (!isAnyTag && isReuiredDoc) {//si no hay doumentos capturados entnces se regresa falso ya que los documentos son necesarios
            return false;
        }
        Collection<FileUploadDTO> files = new ArrayList<>();
        //se recorre el arreglo docQuestionContainer para obtener las rutas de los archivos mediante su tag y asi poder vaidar
        for (int i = 0; i < question.getQuestionContainerDocsList().size(); i++) {
            String fileName = (String) question.getQuestionContainerDocsList().get(i).getObj();
            if (fileName != null)
                try {
                    if (!fileName.contains(Properties.SD_CARD_IMAGES_DIR) &&
                            !fileName.contains((Properties.SD_CARD_VIDEOS_DIR))) {
                        if (!validate(context, true, fileName, documentList)) {
                            //  Toast.makeText(getContext(), "Archivo inválido", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        fileName = copyFileToPrivateFolder(fileName);
                    }
                    File file = new File(fileName);
                    FileUploadDTO fileUploadDTO = new FileUploadDTO();
                    fileUploadDTO.setDate(Tools.getEpoch());
                    Lw lw = new Lw();
                    lw.setCategoria("Documento Tarea");//categoria
                    lw.setDescripcion(file.getName());
                    lw.setNombre_archivo(file.getName());
                    lw.setCredito(creditOrJuicio);
                    lw.setFecha_actividad(new SimpleDateFormat(Properties.DATE_TIME_FORMAT_DB).format(new Date()));
                    lw.setLatitud(mLocation == null ? 0 : mLocation.getLatitude());
                    lw.setLongitud(mLocation == null ? 0 : mLocation.getLongitude());
                    lw.setUpdated(true);
                    fileUploadDTO.setLw(lw);
                    files.add(fileUploadDTO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        AnswerDTO answerDTO;
        if (value instanceof AnswerDTO)
            answerDTO = (AnswerDTO) value;
        else
            answerDTO = new AnswerDTO();
        if (answerDTO.getFiles() == null) {
            answerDTO.setFiles(files);
        } else {
            answerDTO.getFiles().addAll(files);
        }
        if (answerDTO.getFiles().size() == 0 &&
                (answerDTO.getType().equals("FILE_UPLOAD") ||
                        answerDTO.getType().equals("RAW") ||
                        answerDTO.getType().equals("SIGN") ||
                        answerDTO.getType().equals("IMAGE") ||
                        answerDTO.getType().equals("VIDEO"))
                && isReuiredDoc) {
            answerDTO = null;
        }
        value = answerDTO;
        return true;
    }

    private boolean validOptionRequiredComment(List<Obj> answerContainerCommList, Boolean requiredComment, Comment comment) {
        //si requiredComment es nulo la vista para insertar comentario desde las opciones no se ha pntado porque ha sido ignorado
        if (requiredComment == null) return false;
        //si requiredDocument es falso no pinto la vista de inertar comentario desde las opciones por lo cual no se recolipara informacion
        if (!requiredComment) return true;
        String comentario = (String) answerContainerCommList.get(0).getObj();
        AnswerDTO answerDTO;
        if (value instanceof AnswerDTO)
            answerDTO = (AnswerDTO) value;
        else
            answerDTO = new AnswerDTO();
        answerDTO.setComment(comentario);
        answerDTO.setResponse(answerDTO.getResponse() + comentario);
        value = answerDTO;
        return true;
    }

    private boolean validOptionRequiredDocument(List<Obj> answerContainerDocsList, Boolean requiredDocument, Document document, Questions question) {
        //si requiredDocument es nulo la vista para insertar documentos desde las opciones no se ha pntado porque ha sido ignorado
        if (requiredDocument == null) return false;
        //si requiredDocument es falso no pinto la vista de inertar documetos desde las opciones por lo cual no se recolipara informacion
        if (!requiredDocument) return true;
        boolean isAnyTag = false;
        for (int i = 0; i < answerContainerDocsList.size(); i++) {
            if (answerContainerDocsList.get(i).getObj() != null) {
                isAnyTag = true;
                break;
            }
        }
        //en caso que no existan documentos captuados se regresra falso y se pondra la vista de error visible para notificarle al usuario si
        if (!isAnyTag) {
            return false;
        }
        Collection<FileUploadDTO> files = new ArrayList<>();
        //se recorre el arreglo docQuestionContainer para obtener las rutas de los archivos mediante su tag y asi poder vaidar
        for (int i = 0; i < answerContainerDocsList.size(); i++) {
            String fileName = (String) answerContainerDocsList.get(i).getObj();
            if (fileName != null)
                try {
                    if (!validate(context, false, fileName, document)) {
                        //  Toast.makeText(getContext(), "Archivo inválido", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    fileName = copyFileToPrivateFolder(fileName);
                    File file = new File(fileName);
                    FileUploadDTO fileUploadDTO = new FileUploadDTO();
                    fileUploadDTO.setDate(Tools.getEpoch());
                    Lw lw = new Lw();
                    lw.setCategoria("Documento Tarea");
                    lw.setDescripcion(file.getName());
                    lw.setNombre_archivo(file.getName());
                    lw.setCredito(creditOrJuicio);
                    lw.setFecha_actividad(new SimpleDateFormat(Properties.DATE_TIME_FORMAT_DB).format(new Date()));
                    lw.setLatitud(mLocation == null ? 0 : mLocation.getLatitude());
                    lw.setLongitud(mLocation == null ? 0 : mLocation.getLongitude());
                    lw.setUpdated(true);
                    fileUploadDTO.setLw(lw);
                    files.add(fileUploadDTO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        AnswerDTO answerDTO;
        if (value instanceof AnswerDTO)
            answerDTO = (AnswerDTO) value;
        else
            answerDTO = new AnswerDTO();
        answerDTO.setFiles(files);
        value = answerDTO;
        return true;
    }

    private boolean validRegexp(String regex, String text, boolean showSnack, String keyboard, Questions question) {
        if (regex == null) return true;
        if (regex.equals("[]?")) return true;
        if (keyboard != null)
            if (!keyboard.equals("PHONE")) {
                return true;
            }
        boolean bool;
        try {
            Pattern patt = Pattern.compile(regex);
            Matcher matcher = patt.matcher(text);
            bool = matcher.matches();
        } catch (RuntimeException e) {
            e.printStackTrace();
            bool = false;
        }
        String errorStr = "Error, El valor ingresado no tiene el formato correcto.";
        if (!bool && showSnack) {
            Toast.makeText(context, errorStr, Toast.LENGTH_SHORT).show();
        }
        return bool;
    }

    private boolean validLength(Long length, String ans, boolean showSnack) {
        if (length == null) return true;
        boolean bool = ans.length() <= length;
        String errorStr = "Error, número de caracteres inválido";
        if (!bool && showSnack) {
            Toast.makeText(context, errorStr, Toast.LENGTH_SHORT).show();
        }
        return bool;
    }

    @SuppressWarnings("unused")
    private boolean ResponsablesDTO() {
        ResponsablesDTO userQuasar = (ResponsablesDTO) obj;
        value = userQuasar;
        return !isRequired() || userQuasar != null;
    }

    @SuppressWarnings("unused")
    private boolean String() {
        String str = (String) obj;
        value = str;
        //noinspection SimplifiableIfStatement
        if (isRequired()) {
            return !str.isEmpty();
        }
        return true;
    }

    @SuppressWarnings("unused")
    private boolean Short() {
        Short s = (Short) obj;
        value = s;
        return !isRequired() || s != null;
    }

    @SuppressWarnings("unused")
    private boolean GregorianCalendar() {
        return Calendar();
    }

    private boolean Calendar() {
        Calendar cal = (Calendar) obj;
        value = cal;
        return !isRequired() || cal != null;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "TryFinallyCanBeTryWithResources"})
    private String copyFileToPrivateFolder(String sourcePath) throws IOException {
        String destPath = Properties.SD_CARD_IMAGES_DIR;
        File src = new File(sourcePath);
        String fileName = src.getName();
        String extencion = Tools.getFileExt(fileName);
        fileName = fileName.replace("." + extencion, "_") + Tools.getDateFileStr() + "." + extencion;
        File dst = new File(destPath, fileName);
        if (!dst.getParentFile().exists()) {
            dst.getParentFile().mkdirs();
        }
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
        return dst.getAbsolutePath();
    }

    public static EpochDate getEpochDate(Calendar cal) {
        if (cal == null) return null;
        EpochDate epochDate = new EpochDate();
        long epoch = cal.getTimeInMillis() / 1000;
        DateFormat date = new SimpleDateFormat("ZZZZ zzzz");
        String localTime = date.format(cal.getTime());
        String dateStr = Tools.getDate(cal, "-") + " " + Tools.getTime(cal);
        epochDate.setEpoch(epoch);
        epochDate.setDateStr(dateStr);
        epochDate.setTimeZone(localTime);
        return epochDate;
    }

    public static EpochDate getEpochDateJustTime(Calendar cal) {
        if (cal == null) return null;
        EpochDate epochDate = new EpochDate();
        long epoch = cal.getTimeInMillis() / 1000;
        DateFormat date = new SimpleDateFormat("ZZZZ zzzz");
        String localTime = date.format(cal.getTime());
        String dateStr = Tools.getTime(cal);
        epochDate.setEpoch(epoch);
        epochDate.setDateStr(dateStr);
        epochDate.setTimeZone(localTime);
        return epochDate;
    }

    public static EpochDate getEpochDateWithOutTime(Calendar cal) {
        if (cal == null) return null;
        EpochDate epochDate = new EpochDate();
        long epoch = cal.getTimeInMillis() / 1000;
        DateFormat date = new SimpleDateFormat("ZZZZ zzzz");
        String localTime = date.format(cal.getTime());
        String dateStr = Tools.getDate(cal, "-");
        epochDate.setEpoch(epoch);
        epochDate.setDateStr(dateStr);
        epochDate.setTimeZone(localTime);
        return epochDate;
    }

    public void setCreditOrJuicio(String creditOrJuicio) {
        this.creditOrJuicio = creditOrJuicio;
    }
}
