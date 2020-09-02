package mx.com.pendulum.olintareas.ui.fragments.tareas;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.CatalogDatabaseHelper;
import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.SubClasifica.RelOptionQuery;
import mx.com.pendulum.olintareas.dto.SubClasifica.RelSubClasificaQuestions;
import mx.com.pendulum.olintareas.dto.UserData;
import mx.com.pendulum.olintareas.dto.fromOptions.AutocompleteFO;
import mx.com.pendulum.olintareas.dto.fromOptions.DocumentsFO;
import mx.com.pendulum.olintareas.dto.fromOptions.OptionsFO;
import mx.com.pendulum.olintareas.dto.fromOptions.QuestionsFO;
import mx.com.pendulum.olintareas.dto.fromOptions.RelOptionQueryFO;
import mx.com.pendulum.olintareas.dto.fromOptions.RelOptionQuestionsFO;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Autocomplete;
import mx.com.pendulum.olintareas.dto.tareasV2.Document;
import mx.com.pendulum.olintareas.dto.tareasV2.EpochDate;
import mx.com.pendulum.olintareas.dto.tareasV2.NotaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NotasV2Form;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.dto.tareasV2.ResponsablesDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.SeguimientoTarea;
import mx.com.pendulum.olintareas.dto.tareasV2.TareaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.TemporalForm;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.tareas.Validator;
import mx.com.pendulum.olintareas.tareas.views.ParentViewMain;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;
import mx.com.pendulum.olintareas.ui.parents.FragmentParent;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.views.CustomAutoCompleteTextView;
import mx.com.pendulum.utilities.views.CustomEditText;
import mx.com.pendulum.utilities.views.CustomTextView;

import static mx.com.pendulum.utilities.Tools.getDate;

@SuppressWarnings("ALL")
public class AddNotaV2Fragment extends FragmentParent implements LoaderManager.LoaderCallbacks<Object>, AdapterView.OnItemClickListener {

    private static final String TAG = AddNotaV2Fragment.class.getSimpleName();
    private static final int LOADER_SEGUIMIENTO = 8965438;
    private static final int LOADER_QUESTIONS = 8965439;
    private static final int LOADER_MOTIVO = 8965440;
    private static final int LOADER_CODIGO_ACCION = 8965441;
    private static final int LOADER_CODIGO_RESULTADO = 8965442;
    private static final int LOADER_QUESTION_FROM_OPTION = 85539265;
    private static final int LOADER_QUESTION_CLEAN_OPTION = 85539262;
    public static final String REULT_CODE = "REULT_CODE";
    private DynamicFormAdapter adapterForm;
    private List<Questions> listForm;
    private ListView listView;
    private SearchableCursorAdapter adapterTypeEstado;
    private SearchableCursorAdapter adapterComando;
    private SearchableCursorAdapter adapterSubComando;
    private SeguimientoTarea seguimientoTarea;
    private static TareaDTO tareaDTO;
    private static boolean isFromPencel = false;
    private static NotaDTO pendingNotaDTO;
    private NotaDTO notaDTO;
    private boolean isAnyChange = false;
    private boolean changeScroll = false;
    private int moveScroll = -1;
    int index = 0;
    int top = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dynamic_form, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Tools.setupUI(getActivity(), getView().findViewById(android.R.id.content));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().restartLoader(LOADER_SEGUIMIENTO, getArguments(), this).forceLoad();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DynamicFormAdapter.OPTION_WITH_QUESTION_SELECTED)
            addQuestionsFromOption(data, 1);
        else if (requestCode == DynamicFormAdapter.OPTION_CLEAN_QUESTION_SELECTED)
            addQuestionsFromOption(data, 2);
        else
            adapterForm = AddTareaV2Fragment.result(requestCode, resultCode, data, adapterForm);
    }

    public void signatureTaken(Intent data) {
        int code = data.getIntExtra(REULT_CODE, 0);
        adapterForm = AddTareaV2Fragment.result(DynamicFormAdapter.SIGN, code, data, adapterForm);
    }

    public void addressTaken(Intent data) {
        int code = data.getIntExtra(REULT_CODE, 0);
        adapterForm = AddTareaV2Fragment.result(DynamicFormAdapter.ADDRESS, code, data, adapterForm);
    }

    public void manageLinkButtons(int option, Questions question) {
        if (question.getOptions() != null && question.getOptions().size() > 0) {
            if (question.getOptions().get(0).getPlaceHolder() != null) {
                String url = question.getOptions().get(0).getPlaceHolder();
                if (option == 1) { // copy link
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                                getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(url);
                    } else {
                        android.content.ClipboardManager clipboard =
                                (android.content.ClipboardManager)
                                        getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        android.content.ClipData clip =
                                android.content.ClipData.newPlainText("url", url);
                        clipboard.setPrimaryClip(clip);
                    }
                    Toast.makeText(getContext(), "Link copiado al portapapeles --> " + url,
                            Toast.LENGTH_LONG).show();
                } else { // Open Link
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            } else Toast.makeText(getContext(), "Link no configurado correctamente.",
                    Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(getContext(), "Link no configurado correctamente.",
                    Toast.LENGTH_LONG).show();
    }

    private boolean validateHeader(NotaDTO notaDTO) {
        boolean isHeaderOk = true;
        UserData userData = getUserSecion();
        if (isFromPencel) {
            notaDTO.setIdTarea(tareaDTO.get_id().intValue());                //
            notaDTO.setTipoTarea(tareaDTO.getIdTipoTarea());              //cita o tarea
            notaDTO.setCredito(tareaDTO.getCredito());                //
            Long juicio;
            try {
                juicio = tareaDTO.getJuicio() == null ? null : Long.valueOf(tareaDTO.getJuicio());
            } catch (Exception e) {
                juicio = null;
            }
            notaDTO.setJuicio(juicio);
        } else {
            notaDTO.setIdTarea(seguimientoTarea.getIdtarea());                //
            notaDTO.setTipoTarea(seguimientoTarea.getTipotarea());              //cita o tarea
            notaDTO.setCredito(seguimientoTarea.getCredito());                //
            notaDTO.setJuicio(seguimientoTarea.getJuicio() == null ? null : Long.valueOf(seguimientoTarea.getJuicio()));                 //
        }
        notaDTO.setFechaAlta(Validator.getEpochDate(Calendar.getInstance()));               //fecha en que que se captura
        notaDTO.setQuasar(userData.getUsername());                  //usuario
        notaDTO.setCorreoQuasar(userData.getCorreo());            //correo usuarioi
        notaDTO.setNombreQuasar(userData.getNombre());            //nombre usuario
        Calendar calFechaCmpromiso = Calendar.getInstance();
        Object tag;
        if (getView().findViewById(R.id.containerEstado).getVisibility() == View.VISIBLE) {
            View spEstado = getView().findViewById(R.id.spEstado);
            tag = AddTareaV2Fragment.isValid(getActivity(), spEstado, true, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flEstadoError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor seleccione un Estado");
                listView.smoothScrollToPosition(0);
                isHeaderOk = false;
            } else {
                if (tag instanceof String) {
                    String selectedString = (String) tag;
                    String selectedStr = (String) spEstado.getTag(R.id.view_1);
                    Log.i("", "");
                    notaDTO.setEstado(selectedStr);
                    notaDTO.setIdEstado(Long.valueOf(selectedString));                  //
                }
            }
        }
        if (getView().findViewById(R.id.containerFechaFin).getVisibility() == View.VISIBLE) {
            View etFechaFinAnswer = getView().findViewById(R.id.etFechaFinAnswer);
            tag = AddTareaV2Fragment.isValid(getActivity(), etFechaFinAnswer, true, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flFechaFinError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor seleccione la fecha fin");
                listView.smoothScrollToPosition(0);
                isHeaderOk = false;
            } else {
                if (tag instanceof Calendar) {
                    Calendar cal = (Calendar) tag;
                    calFechaCmpromiso = cal;
                    EpochDate fechaFin = Validator.getEpochDate(cal);
                    notaDTO.setFechaComp(fechaFin);               //fecha fin
                    isAnyChange = true;
                }
            }
        }
        boolean isCodigoAccion = false;
        if (getView().findViewById(R.id.containerCodAccion).getVisibility() == View.VISIBLE) {
            View spCodAccion = getView().findViewById(R.id.spCodAccion);
            tag = AddTareaV2Fragment.isValid(getActivity(), spCodAccion, true, false, null);
            if (tag == null) {
//                View v = getView().findViewById(R.id.flCodAccionError);
//                v.setVisibility(View.VISIBLE);
//                Tools.showSnack(getActivity(), "Por favor seleccione un comando de acción");
//                listView.smoothScrollToPosition(0);
                //isHeaderOk = false;
            } else {
                if (tag instanceof String) {
                    String selectedString = (String) tag;
                    notaDTO.setComandoAccion(selectedString);          //
                    isCodigoAccion = true;
                    isAnyChange = true;
                }
            }
        }
        if (getView().findViewById(R.id.containerCodResultado).getVisibility() == View.VISIBLE) {
            View spCodResultado = getView().findViewById(R.id.spCodResultado);
            tag = AddTareaV2Fragment.isValid(getActivity(), spCodResultado, true, false, null);
            if (tag == null) {
                if (isCodigoAccion) {
                    View v = getView().findViewById(R.id.flCodResultadoError);
                    v.setVisibility(View.VISIBLE);
                    Tools.showSnack(getActivity(), "Por favor seleccione un comando de resultado");
                    listView.smoothScrollToPosition(0);
                    isHeaderOk = false;
                }
            } else {
                if (tag instanceof String) {
                    String selectedString = (String) tag;
                    notaDTO.setComandoResultado(selectedString);       //
                    isAnyChange = true;
                }
            }
        }
        if (getView().findViewById(R.id.containerHoras).getVisibility() == View.VISIBLE) {
            View etHoraInicioAnswer = getView().findViewById(R.id.etHoraInicioAnswer);
            tag = AddTareaV2Fragment.isValid(getActivity(), etHoraInicioAnswer, true, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flHoraInicioError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "por favor seleccione la hora de inicio");
                listView.smoothScrollToPosition(0);
                isHeaderOk = false;
            } else {
                if (tag instanceof Calendar) {
                    Calendar cal = (Calendar) tag;
                    cal.set(Calendar.YEAR, calFechaCmpromiso.get(Calendar.YEAR));
                    cal.set(Calendar.MONTH, calFechaCmpromiso.get(Calendar.MONTH));
                    cal.set(Calendar.DAY_OF_MONTH, calFechaCmpromiso.get(Calendar.DAY_OF_MONTH));
                    notaDTO.setHoraIni(Validator.getEpochDate(cal));                 //en caso cita hora inico
                    isAnyChange = true;
                }
            }
        }
        if (getView().findViewById(R.id.containerHoras).getVisibility() == View.VISIBLE) {
            View etHoraFinAnswer = getView().findViewById(R.id.etHoraFinAnswer);
            tag = AddTareaV2Fragment.isValid(getActivity(), etHoraFinAnswer, true, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flHoraFinError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor seleccione la hora fin");
                listView.smoothScrollToPosition(0);
                isHeaderOk = false;
            } else {
                if (tag instanceof Calendar) {
                    Calendar cal = (Calendar) tag;
                    cal.set(Calendar.YEAR, calFechaCmpromiso.get(Calendar.YEAR));
                    cal.set(Calendar.MONTH, calFechaCmpromiso.get(Calendar.MONTH));
                    cal.set(Calendar.DAY_OF_MONTH, calFechaCmpromiso.get(Calendar.DAY_OF_MONTH));
                    notaDTO.setHoraFin(Validator.getEpochDate(cal));                 //en caso cita hora fin
                    isAnyChange = true;
                }
            }
        }
        if (getView().findViewById(R.id.containerAsignar).getVisibility() == View.VISIBLE) {
            View etAsignarAnswer = getView().findViewById(R.id.etAsignarAnswer);
            tag = AddTareaV2Fragment.isValid(getActivity(), etAsignarAnswer, true, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flAsignarError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor escriba un responsable");
                listView.smoothScrollToPosition(0);
                isHeaderOk = false;
            } else {
                if (tag instanceof ResponsablesDTO) {
                    ResponsablesDTO userQuasar = (ResponsablesDTO) tag;
                    notaDTO.setNvoResNombre(userQuasar.getNombre());           //reasignadi
                    notaDTO.setNvoResQuasar(userQuasar.getId());           //reasignado quasar
                    notaDTO.setNvoResCorreo(userQuasar.getMail());           //reasignado correo
                    isAnyChange = true;
                }
            }
        }
        if (getView().findViewById(R.id.containerDescipcion).getVisibility() == View.VISIBLE) {
            View etDescipcionAnswer = getView().findViewById(R.id.etDescipcionAnswer);
            tag = AddTareaV2Fragment.isValid(getActivity(), etDescipcionAnswer, true, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flDescipcionError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor escriba una descripción");
                if (listForm != null)
                    listView.smoothScrollToPosition(listForm.size() + 1);
                else
                    listView.smoothScrollToPosition(0);
                isHeaderOk = false;
            } else {
                if (tag instanceof String) {
                    String str = (String) tag;
                    if (isFromPencel) {
                        tareaDTO.setDescripcion(str);
                        tareaDTO.setDescripcionHtml(str);
                    } else {
                        seguimientoTarea.setDescripcion(str);
                    }
                    int MAX_CHAR = 4000;
                    int maxLength = (str.length() < MAX_CHAR) ? str.length() : MAX_CHAR;
                    notaDTO.setDescripcion(str.substring(0, maxLength));            //max 4000 chars
                    notaDTO.setDescripcionHtml(str.substring(0, maxLength));        //igual a descripccion
                    if (isCodigoAccion) {
                        MAX_CHAR = 2000;
                        maxLength = (str.length() < MAX_CHAR) ? str.length() : MAX_CHAR;
                        notaDTO.setDescripcionBitacora(str.substring(0, maxLength));    //descripcion en caso de que sea codigo de accion y resultado a 2000 chars
                    }
                    isAnyChange = true;
                }
            }
        }
        return isHeaderOk;
    }

    private Object isValid(View view, Object params, boolean showSnack, String creditoOrJuicio) {
        if (view == null) return null;
        Validator validator = new Validator(getActivity(), view, params);
        validator.setCreditOrJuicio(creditoOrJuicio);
        if (!validator.validate(showSnack)) return null;
        return validator.getValue();
    }

    public NotaDTO validateForm(boolean isParcialSave) {
        Boolean isFullData = true;
        notaDTO.setUpdated(true);
        notaDTO.setTmpNotaId(notaDTO.get_id());
        notaDTO.setTmpTareaId(0L);
        boolean isValideader = validateHeader(notaDTO);
        if (!isValideader) {
            isFullData = false;
        }
        Collection<AnswerDTO> answerList = new ArrayList<>();
        notaDTO.setAnswers(answerList);
        if (listForm != null)
            if (!listForm.isEmpty())
                for (int i = 0; i < listForm.size(); i++) {
                    View view = getViewByPosition(i + 1, listView);
                    String type = listForm.get(i).getType().toUpperCase();
                    switch (type) {
                        case "FILE_UPLOAD_DESC":
                            //case "LABEL":
                            //case "LINK":
                            //case "GRID":
                            continue;
                    }
                    Object tag = AddTareaV2Fragment.isValid(getActivity(), view, listForm.get(i), true, notaDTO.getCredito() == null ? ("" + notaDTO.getJuicio()) : notaDTO.getCredito());
                    if (tag == null) {
                        Log.i("Valid", "No_Valid");
                        isFullData = false;
                        if (!isParcialSave) {
                            listForm.get(i).setError(true);
                            Tools.showSnack(getActivity(), "Por favor conteste " + listForm.get(i).getQuestion());
                            adapterForm.notifyDataSetChanged();
                            moveScroll = i;
                            listView.smoothScrollToPosition(i + 1);
                            return null;
                        }
                    } else {
                        Log.i("Valid", "Si_Valid");
                        if (tag instanceof AnswerDTO) {
                            AnswerDTO answerDTO = (AnswerDTO) tag;
                            answerList.add(answerDTO);
                            if (answerDTO.getType() != null) {
                                switch (answerDTO.getType()) {
                                    /*case "ADDRESS":
                                        if (!answerDTO.getResponse().isEmpty()) {
                                            isAnyChange = true;
                                        }
                                        break;*/
                                    case "FILE_UPLOAD":
                                    case "RAW":
                                    case "SIGN":
                                    case "IMAGE":
                                    case "VIDEO":
                                        if (!answerDTO.getFiles().isEmpty()) {
                                            isAnyChange = true;
                                        }
                                        break;
                                    case "GEOLOCATION":
                                        if (!answerDTO.getResponse().isEmpty()) {
                                            isAnyChange = true;
                                        }
                                        Log.i("", "");
                                        break;
                                    case "CHOICE":
                                    case "LIST":
                                        int idOption = answerDTO.getId_option();
                                        //TODO cambiar estos valores
                                        boolean isComplete = loadOptionAnswers(getActivity(), idOption, answerList, isParcialSave, 0L, notaDTO.get_id());
                                        if (!isComplete) {
                                            isFullData = false;
                                            if (!isParcialSave) {
                                                listForm.get(i).setError(true);
                                                Tools.showSnack(getActivity(), "Por favor conteste " + listForm.get(i).getQuestion());
                                                adapterForm.notifyDataSetChanged();
                                                listView.smoothScrollToPosition(i + 1);
                                                return null;
                                            }
                                        } else {
                                            isAnyChange = true;
                                        }
                                        break;
                                    default:
                                        isAnyChange = true;
                                        break;
                                }
                            }
                        }
                    }
                    Log.i("", "");
                }
//        notaDTO.setAnswers();
        if (isParcialSave) {
            if (!isAnyChange)
                return null;
            return notaDTO;
        } else {
            if (isFullData) {
                return notaDTO;
            } else {
                return null;
            }
        }
    }

    public static boolean loadOptionAnswers(Context context, int idOption, Collection<AnswerDTO> answerList, boolean isParcialSave, Long idTarea, Long idNota) {
        UserDatabaseHelper userHelper = UserDatabaseHelper.getHelper(context);
        try {
            Dao<TemporalForm, Long> daoTemp = userHelper.getDao(TemporalForm.class);
            TemporalForm temporalForm = daoTemp.queryBuilder().where().eq(TemporalForm.COL_ID_OPTION, idOption)
                    .and().eq(TemporalForm.COL_ID_NOTA, idNota)
                    .and().eq(TemporalForm.COL_ID_TAREA, idTarea).queryForFirst();
            if (temporalForm != null) {
                String ans = temporalForm.getAnswers();
                if (temporalForm.isParcialSave()) {
                    return false;
                }
                if (ans != null && !ans.isEmpty()) {
                    Type collectionType = new TypeToken<Collection<AnswerDTO>>() {
                    }.getType();
                    Collection<AnswerDTO> answerOptionsList = new Gson().fromJson(ans, collectionType);
                    answerList.addAll(answerOptionsList);
                }
            } else {
                if (isParcialSave) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userHelper.close();
        }
        return true;
    }

    public View getViewByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
        if (position < firstListItemPosition || position > lastListItemPosition) {
            return listView.getAdapter().getView(position, listView.getChildAt(position), listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private void loadDynamicForm(List<Questions> list) {
        if (list == null || list.isEmpty()) {
            listView.setAdapter(null);
            adapterForm = null;
            listForm = null;
            return;
        }
        boolean isImageResize = false;
        if (isFromPencel) {
            if (tareaDTO.getDe().equals("ABA")) {
                isImageResize = AddTareaV2Fragment.shouldResize(tareaDTO.getSubTipo());
            }
        } else {
            if (seguimientoTarea.getDe().equals("ABA")) {
                isImageResize = AddTareaV2Fragment.shouldResize(seguimientoTarea.getIdSubtipo());
            }
        }
//        Long idTarea = getArguments().getLong(AddOptionsQuestionsActivity.KEY_ID_TAREA);
//        Long idNota = getArguments().getLong(AddOptionsQuestionsActivity.KEY_ID_NOTA);
        // adapterForm = new DynamicFormAdapter(getActivity(), list, this, isImageResize, idTarea, idNota);
        adapterForm = new DynamicFormAdapter(getActivity(), list, this, isImageResize, 0l, notaDTO.get_id());
        if (pendingNotaDTO != null)
            if (pendingNotaDTO.getAnswers() != null) {
                adapterForm.setPendingAnswers(pendingNotaDTO.getAnswers());
            }
        //TODO aqui hacer el desmadre de los archivos
        /*if (pendingNotaDTO != null && pendingNotaDTO.getAnswers() != null && pendingNotaDTO.getAnswers().size() > 0) {
            long qsID;
            long asID;
            Collection<AnswerDTO> ansr = pendingNotaDTO.getAnswers();
            ArrayList<Obj> objs;
            for (Questions qe : list) {
                switch (qe.getType()) {
                    case "IMAGE":
                    case "VIDEO":
                    case "RAW":
                    case "FILE_UPLOAD":
                        for (AnswerDTO a : ansr) {
                            qsID = qe.getId();
                            asID = a.getId_question();
                            if (qsID == asID) {
                                qe.setAnswer(a);
                                objs = new ArrayList<>();
                                Obj obj = new Obj();
                                File fl;
                                for (FileUploadDTO file : a.getFiles()) {
                                    String path = Properties.SD_CARD_IMAGES_DIR + Properties.FILE_SEPERATOR + file.getLw().getNombre_archivo();
                                    fl = new File(path);
                                    obj.setObj(fl);
                                    objs.add(obj);
                                }
                                qe.setQuestionContainerDocsList(objs);
                                break;
                            }
                        }
                        break;
                }
            }
        }*/
        listForm = list;
        listView.setAdapter(adapterForm);
        if (moveScroll != -1) {
            listView.setSelectionFromTop(index, top);
            moveScroll = -1;
        }
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        String query;
        switch (id) {
            case LOADER_SEGUIMIENTO:
                return new SeguimientoLoader(getActivity(), args);
            case LOADER_QUESTIONS:
                CustomDialog.showProgressDialog(getActivity(), true, "Cargando...");
                if (isFromPencel) {
                    return new QuestionLoader(getActivity(), tareaDTO, getArguments());
                } else {
                    return new QuestionLoader(getActivity(), seguimientoTarea, getArguments());
                }
            case LOADER_MOTIVO:
                int idEstado = 0;
                if (isFromPencel) {
                    idEstado = tareaDTO.getIdEstado();
                } else {
                    idEstado = seguimientoTarea.getEstado();
                }
                query = new StringBuilder()
                        .append("select _id,value,id from (\n")
                        .append("		SELECT	0 _id, 'Selecciona' value, '' id\n")
                        .append("		UNION\n")
                        .append("		SELECT    _id,  value,	 id\n")
                        .append("	    FROM ").append(TableNames.CATALOG_GET_ESTADO_NOTA).append("\n")
                        .append("		WHERE\n")
                        .append("			( ( '").append(idEstado).append("'='7' AND id in ('3' , '4'))\n")
                        .append("			OR ( '").append(idEstado).append("' not in ('3','7') AND id in ('2','3','4','5')) )\n")
                        .append(")order by _id")
                        .toString();
                return new AddTareaV2Fragment.CatalogLoader(getActivity(), query);
            case LOADER_CODIGO_ACCION:
                //ESTO SOLO ES PARA VALIDAR SI TIENE LA VERSION PASADA
                query = "SELECT '' codigo, 'Selecciona'  value, 0 _id \n" +
                        "union\n" +
                        "SELECT comando codigo , descripcioncomando value,_id FROM comandoSubcomando \n" +
                        "group by codigo\n" +
                        "order by comando;";
                Cursor c = CatalogDatabaseHelper.getHelper(getActivity()).getReadableDatabase().rawQuery(query, null);
                if (c.getCount() < 1) {
                    query = " SELECT 	_id,	value,	codigo\n" +
                            " FROM\n" +
                            "  (\n" +
                            "SELECT	0 _id, 'Selecciona' value, '' codigo\n" +
                            "	UNION\n" +
                            "SELECT    _id, descripcion value,     codigo \n" +
                            "	FROM " + TableNames.CATALOG_COMANDO_NOTA + "\n" +
                            ")\n" +
                            "ORDER BY codigo ASC";
                }
                c.close();
                return new AddTareaV2Fragment.CatalogLoader(getActivity(), query);
            case LOADER_CODIGO_RESULTADO:
                String idComando = args.getString("LOADER_CODIGO_RESULTADO", "");
                query = "SELECT	0 _id, 'Selecciona' value, '' codigo\n" +
                        "	UNION\n" +
                        "SELECT  _id , descripcionsubcomando value,subcomando codigo " +
                        "FROM comandoSubcomando \n" +
                        "where comando = '" + idComando + "' and \n" +
                        "subcomando not in ('AZ', 'AY')\n" +
                        "order by subcomando asc";
                Cursor cs = CatalogDatabaseHelper.getHelper(getActivity()).getReadableDatabase().rawQuery(query, null);
                if (cs.getCount() < 1) {
                    query = " SELECT 	_id,	value,	codigo\n" +
                            " FROM\n" +
                            "  (\n" +
                            "SELECT	0 _id, 'Selecciona' value, '' codigo\n" +
                            "	UNION\n" +
                            "SELECT    _id, descripcion value,     codigo \n" +
                            "	FROM " + TableNames.CATALOG_SUB_COMANDO_NOTA + "\n" +
                            "	WHERE accionCode = '" + idComando + "'\n" +
                            ")\n" +
                            "ORDER BY codigo ASC";
                }
                return new AddTareaV2Fragment.CatalogLoader(getActivity(), query);
            case LOADER_QUESTION_CLEAN_OPTION:
                CustomDialog.showProgressDialog(getActivity(), true, "Cargando...");
                return new AddNotaV2Fragment.QuestionCleanLoader(getActivity(), args, listForm);
            case LOADER_QUESTION_FROM_OPTION:
                CustomDialog.showProgressDialog(getActivity(), true, "Cargando...");
                return new AddNotaV2Fragment.QuestionFromOptionLoader(getActivity(), args, listForm);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        switch (loader.getId()) {
            case LOADER_SEGUIMIENTO:
                if (notaDTO == null) {
                    notaDTO = new NotaDTO();
                    if (pendingNotaDTO == null) {
                        UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getActivity());
                        try {
                            Dao<NotaDTO, Long> dao = helper.getDao(NotaDTO.class);
                            dao.create(notaDTO);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            helper.close();
                        }
                    } else {
                        notaDTO.set_id(pendingNotaDTO.get_id());
                    }
                    Log.i(TAG, "onActivityCreated: ");
                }
                seguimientoTarea = (SeguimientoTarea) data;
                listView = (ListView) getView().findViewById(R.id.listview);
                listView.addHeaderView(getHeader());
                listView.addFooterView(getFooter());
                listView.setAdapter(null);
                listView.setOnItemClickListener(this);
                break;
            case LOADER_MOTIVO:
                loadMotivo(data);
                break;
            case LOADER_CODIGO_ACCION:
                loadComando(data);
                break;
            case LOADER_CODIGO_RESULTADO:
                loadSubComando(data);
                break;
            case LOADER_QUESTION_FROM_OPTION:
            case LOADER_QUESTION_CLEAN_OPTION:
            case LOADER_QUESTIONS:
                loadHeaderData();
                List<Questions> list = (List<Questions>) data;
                loadDynamicForm(list);
                CustomDialog.showProgressDialog(getActivity(), false, null);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
        switch (loader.getId()) {
            case LOADER_SEGUIMIENTO:
            case LOADER_QUESTIONS:
            case LOADER_MOTIVO:
            case LOADER_CODIGO_ACCION:
            case LOADER_CODIGO_RESULTADO:
                break;
        }
    }

    private void loadHeaderData() {
        loadHoraInicio();
        loadHoraFin();
        loadFechaFin();
        loadDescipcion();
        loadAsignado();
    }

    private void loadEstado() {
        if (pendingNotaDTO != null) {
            if (pendingNotaDTO.getIdEstado() != null) {
                Spinner spinner = (Spinner) getActivity().findViewById(R.id.spEstado);
                int pos = adapterTypeEstado.getPositionById(pendingNotaDTO.getIdEstado(), "id");
                spinner.setSelection(pos);
            }
        }
    }

    private void loadCodAccion() {
        if (pendingNotaDTO != null) {
            if (pendingNotaDTO.getComandoAccion() != null) {
                Spinner spinner = (Spinner) getActivity().findViewById(R.id.spCodAccion);
                int position = adapterComando.getPositionByName(pendingNotaDTO.getComandoAccion(), "codigo");
                spinner.setSelection(position, false);
                performCodigoActionClick(adapterComando.getCursor(), position);
            }
        }
    }

    private void loadCodResultado() {
        if (pendingNotaDTO != null) {
            if (pendingNotaDTO.getComandoResultado() != null) {
                Spinner spinner = (Spinner) getActivity().findViewById(R.id.spCodResultado);
                int position = adapterSubComando.getPositionByName(pendingNotaDTO.getComandoResultado(), "codigo");
                spinner.setSelection(position, false);
                performCodigoResultadoClick(adapterSubComando.getCursor(), position);
            }
        }
    }

    private void loadHoraInicio() {
        if (pendingNotaDTO != null) {
            if (pendingNotaDTO.getHoraIni() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(pendingNotaDTO.getHoraIni().getEpoch() * 1000);
                clickHoraInicio(cal);
            }
        }
    }

    private void loadHoraFin() {
        if (pendingNotaDTO != null) {
            if (pendingNotaDTO.getHoraFin() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(pendingNotaDTO.getHoraFin().getEpoch() * 1000);
                clickHoraFin(cal);
            }
        }
    }

    private void loadFechaFin() {
        if (pendingNotaDTO != null) {
            if (pendingNotaDTO.getFechaComp() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(pendingNotaDTO.getFechaComp().getEpoch() * 1000);
                clickFechaFin(cal);
            }
        }
    }

    private void loadDescipcion() {
        if (pendingNotaDTO != null) {
            if (pendingNotaDTO.getDescripcion() != null) {
                CustomEditText etDescipcionAnswer = (CustomEditText) getActivity().findViewById(R.id.etDescipcionAnswer);
                etDescipcionAnswer.setText(pendingNotaDTO.getDescripcion());
            }
        }
    }

    private void loadAsignado() {
        if (pendingNotaDTO != null) {
            if (pendingNotaDTO.getNvoResQuasar() != null) {
                ResponsablesDTO user = new ResponsablesDTO();
                user.setId(pendingNotaDTO.getNvoResQuasar());
                user.setNombre(pendingNotaDTO.getNvoResNombre());
                user.setMail(pendingNotaDTO.getNvoResCorreo());
                clickAsignado(user);
            }
        }
    }

    private View getHeader() {
        View view = View.inflate(getActivity(), R.layout.header_notas_v2, null);
        configureEstado(view);
        configureCodAccion(view);
        configureCodResultado(view);
        configureHoras(view);
        configureFechaFin(view);
        configureAsignado(view);
//        configureFake(view);
//        view.findViewById(R.id.containerEstado).setVisibility(View.GONE);
        view.findViewById(R.id.containerCodAccion).setVisibility(View.GONE);
        view.findViewById(R.id.containerCodResultado).setVisibility(View.GONE);
        view.findViewById(R.id.containerFechaFin).setVisibility(View.GONE);
        view.findViewById(R.id.containerHoras).setVisibility(View.GONE);
        view.findViewById(R.id.containerAsignar).setVisibility(View.GONE);
        return view;
    }

    private UserData getUserSecion() {
        UserData userData = new UserData();
        UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getActivity());
        try {
            userData = helper.getUserDataDao().getCurrentUser();
        } catch (Exception ignored) {

        } finally {
            helper.close();
        }
        return userData;
    }

    private SearchableCursorAdapter getAdapterGeneric() {
        String[] columnsEdo = new String[]{"value"};
        int[] to_edo = new int[]{R.id.spinnerRowDescription};
        SearchableCursorAdapter adapterGeneric = new SearchableCursorAdapter(getActivity(), R.layout.spinner_row_code_task, null, columnsEdo, to_edo, 0);
        adapterGeneric.setDropDownViewResource(R.layout.spinner_row_code_task);
        return adapterGeneric;
    }

    private View getFooter() {
        View view = View.inflate(getActivity(), R.layout.footer_notas_v2, null);
        configureDescipcion(view);
        view.findViewById(R.id.containerDescipcion).setVisibility(View.GONE);
        return view;
    }

    private void configureAsignado(final View view) {
        final CustomAutoCompleteTextView autoCompleteUser = (CustomAutoCompleteTextView) view.findViewById(R.id.etAsignarAnswer);
        final ImageView ivDelete = (ImageView) view.findViewById(R.id.ivAsignarDelete);
        CustomTextView tvAsignarQuestion = (CustomTextView) view.findViewById(R.id.tvAsignarQuestion);
        tvAsignarQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_asignar), TextView.BufferType.SPANNABLE);
        autoCompleteUser.setAdapter(new AutoCompleteFilter());
        ivDelete.setVisibility(View.GONE);
        autoCompleteUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                int indexOf = c.getColumnIndexOrThrow(ResponsablesDTO.NOMBRE);
                int indexOfQuasar = c.getColumnIndexOrThrow(ResponsablesDTO.COLID);
                int indexOfMail = c.getColumnIndexOrThrow(ResponsablesDTO.EMAIL);
                int indexOfSup = c.getColumnIndexOrThrow(ResponsablesDTO.SUPERVISOR);
                String nombre = c.getString(indexOf);
                String quasar = c.getString(indexOfQuasar);
                String mail = c.getString(indexOfMail);
                String supervisor = c.getString(indexOfSup);
                ResponsablesDTO user = new ResponsablesDTO();
                user.setId(quasar);
//                user.setSupervisor(supervisor);
                user.setNombre(nombre);
                user.setMail(mail);
                clickAsignado(user);
            }
        });
        autoCompleteUser.addTextChangedListener(tw);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteUser.setText("");
            }
        });
    }

    private TextWatcher tw = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            ImageView ivDelete = (ImageView) getActivity().findViewById(R.id.ivAsignarDelete);
            CustomAutoCompleteTextView autoCompleteUser = (CustomAutoCompleteTextView) getActivity().findViewById(R.id.etAsignarAnswer);
            if (s.toString().isEmpty()) {
                ivDelete.setVisibility(View.GONE);
            } else {
                ivDelete.setVisibility(View.VISIBLE);
            }
            autoCompleteUser.setTag(null);
            getActivity().findViewById(R.id.flAsignarError).setVisibility(View.GONE);
        }
    };

    private void clickAsignado(ResponsablesDTO user) {
        CustomAutoCompleteTextView autoCompleteUser = (CustomAutoCompleteTextView) getActivity().findViewById(R.id.etAsignarAnswer);
        autoCompleteUser.removeTextChangedListener(tw);
//                String str = quasar + " - " + nombre;
        autoCompleteUser.setText("");
        autoCompleteUser.append(user.getNombre());
        autoCompleteUser.setTag(user);
        autoCompleteUser.addTextChangedListener(tw);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.setVisibility(View.GONE);
    }

    private class AutoCompleteFilter extends BaseAdapter implements Filterable {
        Cursor cursor;
        LayoutInflater layoutInflater;

        private AutoCompleteFilter() {
            layoutInflater = LayoutInflater.from(getActivity());
        }

        public void setCursor(Cursor cursor) {
            this.cursor = cursor;
        }

        public Cursor getCursor() {
            return cursor;
        }

        @Override
        public int getCount() {
            return cursor == null ? 0 : cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            cursor.moveToPosition(position);
            return cursor;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            cursor.moveToPosition(position);
            int indexOf = cursor.getColumnIndexOrThrow(ResponsablesDTO.NOMBRE);
//            int indexOfQuasar = cursor.getColumnIndexOrThrow(UserQuasar.USERQUASAR);
//            int indexOfMail = cursor.getColumnIndexOrThrow(UserQuasar.EMAIL);
//            int indexOfSup = cursor.getColumnIndexOrThrow(UserQuasar.SUPERVISOR);
            CustomTextView txt;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.spinner_row_code_task, null);
                txt = (CustomTextView) view.findViewById(R.id.spinnerRowDescription);
                view.setTag(txt);
            } else {
                txt = (CustomTextView) view.getTag();
            }
            String nombre = cursor.getString(indexOf);
//            String quasar = cursor.getString(indexOfquasar);
//            String mail = cursor.getString(indexOfMail);
//            String supervisor = cursor.getString(indexOfSup);
//            UserQuasar user = new UserQuasar();
//            user.setUserquasar(quasar);
//            user.setSupervisor(supervisor);
//            user.setNombre(nombre);
//            user.setEmail(mail);
//            txt.setTag(1, user);
            txt.setText(nombre);
            return view;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                private String getStr(String original) {
                    original = original.replace("á", "a");
                    original = original.replace("Á", "A");
                    original = original.replace("é", "e");
                    original = original.replace("É", "E");
                    original = original.replace("í", "i");
                    original = original.replace("Í", "I");
                    original = original.replace("ó", "o");
                    original = original.replace("Ó", "O");
                    original = original.replace("ú", "u");
                    original = original.replace("Ú", "U");
                    return original;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults rer) {
                    cursor = (Cursor) rer.values;
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults ret = new FilterResults();
                    if (constraint == null || constraint.length() == 0) {
                        ret.count = cursor.getCount();
                        ret.values = cursor;
                        return ret;
                    } else {
                        String filt = getStr(constraint.toString().toUpperCase());
                        Cursor cursor_aux = getUsuarioQuasar(filt);
                        ret.values = cursor_aux;
                        ret.count = cursor_aux.getCount();
                    }
                    return ret;
                }
            };
        }
    }

    private Cursor getUsuarioQuasar(String filter) {
        Cursor cursorReturn = null;
        CatalogDatabaseHelper catalogDat = CatalogDatabaseHelper.getHelper(getActivity());
        CloseableIterator<ResponsablesDTO> iterator;
        try {
            Dao<ResponsablesDTO, Long> actionCodeCatalogDao = catalogDat.getDao(ResponsablesDTO.class);
            QueryBuilder<ResponsablesDTO, Long> qbActionCode = actionCodeCatalogDao.queryBuilder();
            qbActionCode.where().like(ResponsablesDTO.CVE_BUSQUEDA, "%" + filter + "%");
            iterator = actionCodeCatalogDao.iterator(qbActionCode.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) iterator.getRawResults();
            cursorReturn = results.getRawCursor();
        } catch (SQLException sqle) {
            Log.e(TAG, sqle.getMessage(), sqle);
        } finally {
            catalogDat.close();
        }
        return cursorReturn;
    }

    private void configureEstado(final View view) {
        final Spinner spEstado = (Spinner) view.findViewById(R.id.spEstado);
        CustomTextView tvEstadoQuestion = (CustomTextView) view.findViewById(R.id.tvEstadoQuestion);
        tvEstadoQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_estado), TextView.BufferType.SPANNABLE);
        adapterTypeEstado = getAdapterGeneric();
        spEstado.setAdapter(adapterTypeEstado);
        spEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                Object selected = parent.getItemAtPosition(position);
                view.findViewById(R.id.flEstadoError).setVisibility(View.GONE);
                if (selected instanceof Cursor) {
                    Cursor cursor = (Cursor) selected;
                    int indexOf = cursor.getColumnIndexOrThrow("id");
                    String selectedId = cursor.getString(indexOf);
                    String selectedString = cursor.getString(cursor.getColumnIndex("value"));
                    spEstado.setTag(selectedId);
                    spEstado.setTag(R.id.view_1, selectedString);
                    switch (selectedId) {
                        case "2"://rechazada
                            view.findViewById(R.id.containerCodAccion).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.containerCodResultado).setVisibility(View.GONE);
                            view.findViewById(R.id.containerFechaFin).setVisibility(View.GONE);
                            view.findViewById(R.id.containerAsignar).setVisibility(View.GONE);
                            getView().findViewById(R.id.containerDescipcion).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.containerHoras).setVisibility(View.GONE);
                            getLoaderManager().restartLoader(LOADER_CODIGO_ACCION, getArguments(), AddNotaV2Fragment.this).forceLoad();
                            break;
                        case "4"://proceso
                        case "3": //cerrado
                            //mostrar fecha fin, comando accion y comando resultado
                            view.findViewById(R.id.containerCodAccion).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.containerCodResultado).setVisibility(View.GONE);
                            view.findViewById(R.id.containerFechaFin).setVisibility(View.VISIBLE);
                            Calendar minDate = Calendar.getInstance();
                            clickFechaFin(minDate);
                            view.findViewById(R.id.containerAsignar).setVisibility(View.GONE);
                            getView().findViewById(R.id.containerDescipcion).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.containerHoras).setVisibility(View.GONE);
                            if (isFromPencel) {
                                if (tareaDTO.getTipoTarea().equals("4") /*cita*/ && selectedId.equals("4")) {
                                    view.findViewById(R.id.containerHoras).setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (seguimientoTarea.getTipotarea() == 4 /*cita*/ && selectedId.equals("4")) {
                                    view.findViewById(R.id.containerHoras).setVisibility(View.VISIBLE);
                                }
                            }
                            getLoaderManager().restartLoader(LOADER_CODIGO_ACCION, getArguments(), AddNotaV2Fragment.this).forceLoad();
                            break;
                        case "5": //reasignada
                            //mostrar asignado a
                            view.findViewById(R.id.containerCodAccion).setVisibility(View.GONE);
                            view.findViewById(R.id.containerCodResultado).setVisibility(View.GONE);
                            view.findViewById(R.id.containerFechaFin).setVisibility(View.GONE);
                            view.findViewById(R.id.containerAsignar).setVisibility(View.VISIBLE);
                            getView().findViewById(R.id.containerDescipcion).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.containerHoras).setVisibility(View.GONE);
                            break;
                        case "":
                            spEstado.setTag(null);
                            view.findViewById(R.id.containerCodAccion).setVisibility(View.GONE);
                            view.findViewById(R.id.containerCodResultado).setVisibility(View.GONE);
                            view.findViewById(R.id.containerFechaFin).setVisibility(View.GONE);
                            view.findViewById(R.id.containerAsignar).setVisibility(View.GONE);
                            getView().findViewById(R.id.containerDescipcion).setVisibility(View.GONE);
                            view.findViewById(R.id.containerHoras).setVisibility(View.GONE);
                            break;
                        default:
                            view.findViewById(R.id.containerCodAccion).setVisibility(View.GONE);
                            view.findViewById(R.id.containerCodResultado).setVisibility(View.GONE);
                            view.findViewById(R.id.containerFechaFin).setVisibility(View.GONE);
                            view.findViewById(R.id.containerAsignar).setVisibility(View.GONE);
                            getView().findViewById(R.id.containerDescipcion).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.containerHoras).setVisibility(View.GONE);
                            break;
                    }
                    Bundle args = getArguments();
                    args.putString("idEstado", selectedId);
                    // TODO CAMBIAR POR VALORES
                    getLoaderManager().restartLoader(LOADER_QUESTIONS, getArguments(), AddNotaV2Fragment.this).forceLoad();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        getLoaderManager().restartLoader(LOADER_MOTIVO, getArguments(), AddNotaV2Fragment.this).forceLoad();
    }

    private void loadMotivo(Object data) {
        if (data instanceof Cursor) {
            Cursor cursor = (Cursor) data;
            adapterTypeEstado.swapCursor(cursor);
            loadEstado();
        } else {
            adapterTypeEstado.swapCursor(null);
        }
    }

//    public void setSpinnerItemById(Spinner spinner, long idValue, String column) {
//        int spinnerCount = spinner.getCount();
//        for (int i = 0; i < spinnerCount; i++) {
//            Cursor value = (Cursor) spinner.getItemAtPosition(i);
//            long id = value.getLong(value.getColumnIndex(column));
//            if (id == idValue) {
//                spinner.setSelection(i);
//                break;
//            }
//        }
//    }

    private void configureCodAccion(final View view) {
        final Spinner spCodAccion = (Spinner) view.findViewById(R.id.spCodAccion);
        CustomTextView tvCodAccion = (CustomTextView) view.findViewById(R.id.tvCodAccionQuestion);
        tvCodAccion.setText(ParentViewMain.getText(getActivity(), false, R.string.tsk_comando_accion), TextView.BufferType.SPANNABLE);
        String[] columnsEdo = new String[]{"value"};
        int[] to_edo = new int[]{R.id.spinnerRowDescription};
        adapterComando = new SearchableCursorAdapter(getActivity(), R.layout.spinner_row_code_task, null, columnsEdo, to_edo, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                return getMyView(position, v);
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                return getMyView(position, v);
            }

            private View getMyView(int position, View v) {
                Cursor cursor = (Cursor) getItem(position);
                String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                String value = cursor.getString(cursor.getColumnIndex("value"));
                if (codigo.isEmpty()) {
                    codigo += " ";
                } else {
                    codigo += " - ";
                }
                CustomTextView tv = (CustomTextView) v.findViewById(R.id.spinnerRowDescription);
                tv.setText(codigo + value);
                return v;
            }
        };
        adapterComando.setDropDownViewResource(R.layout.spinner_row_code_task);
        spCodAccion.setAdapter(adapterComando);
        spCodAccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                Object selected = parent.getItemAtPosition(position);
                if (selected instanceof Cursor)
                    performCodigoActionClick((Cursor) selected, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void performCodigoActionClick(Cursor cursor, int position) {
        getActivity().findViewById(R.id.flCodAccionError).setVisibility(View.GONE);
        Spinner spCodAccion = (Spinner) getActivity().findViewById(R.id.spCodAccion);
        int indexOf = cursor.getColumnIndexOrThrow("codigo");
        String selectedId = cursor.getString(indexOf);
        String selectedString = cursor.getString(cursor.getColumnIndex("value"));
        if (selectedId.equals(""))
            spCodAccion.setTag(null);
        else {
            spCodAccion.setTag(selectedId);
            spCodAccion.setTag(R.id.view_1, selectedString);
        }
        Bundle bundle = getArguments();
        bundle.putString("LOADER_CODIGO_RESULTADO", selectedId);
        getLoaderManager().restartLoader(LOADER_CODIGO_RESULTADO, bundle, AddNotaV2Fragment.this).forceLoad();
    }

    private void loadComando(Object data) {
        if (data instanceof Cursor) {
            Cursor cursor = (Cursor) data;
            if (!cursor.isClosed()) {
                adapterComando.swapCursor(cursor);
                loadCodAccion();
            } else {
                View view = View.inflate(getActivity(), R.layout.header_notas_v2, null);
                configureCodAccion(view);
                adapterComando.swapCursor(cursor);
                loadCodAccion();
            }
        } else {
            adapterComando.swapCursor(null);
        }
    }

    private View getViewFromAdapterByPosition(int position, Spinner spinner) {
        View view;
        int firstVisiblePos = spinner.getFirstVisiblePosition();
        int lastVisiblePos = spinner.getLastVisiblePosition();
        if (position < firstVisiblePos || position > lastVisiblePos) {
            view = spinner.getAdapter().getView(position, null, spinner);
        } else {
            view = spinner.getChildAt(position - firstVisiblePos);
        }
        return view;
    }

//    private void configureFake(View view) {
//
//        Spinner sp = (Spinner) view.findViewById(R.id.fake);
////get the spinner from the xml.
////create a list of items for the spinner.
//        String[] items = new String[]{"Selecciona", "2", "three",
//                "pregunta 1",
//                "pregunta 2",
//                "pregunta 3",
//                "pregunta 4",
//                "pregunta 5",
//                "pregunta 6",
//                "pregunta 7",
//                "pregunta 8",
//                "pregunta 9",
//                "pregunta 10",
//                "pregunta 11",
//                "pregunta 12",
//                "pregunta 13",
//                "pregunta 14",
//                "pregunta 15",
//                "pregunta 16",
//                "pregunta 17",
//                "pregunta 18"
//        };
////create an adapter to describe how the items are displayed, adapters are used in several places in android.
////There are multiple variations of this, but this is the basic variant.
//        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
////set the spinners adapter to the previously created one.
//        sp.setAdapter(adapter);
//        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position != 0) {
//                    Intent intentDocs = new Intent(getContext(), AddOptionsQuestionsActivity.class);
//                    intentDocs.putExtras(getArguments());
//                    intentDocs.putExtra(FragmentParent.IS_HOME_ENABLED, true);
////                    intentDocs.putExtra(FragmentParent.TITLE_FRAGMENT, question.getQuestion());
////                    intentDocs.putExtra(FragmentParent.SUB_TITLE_FRAGMENT, options.getOption());
////            intentDocs.putExtra("TAREA_ID", );
////                    intentDocs.putExtra("OPTION", options);
//
//                    getContext().startActivity(intentDocs);
//
//                    ((Activity) getContext()).overridePendingTransition(R.anim.enter, R.anim.exit);
//                }
//            }

//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }

    private void configureCodResultado(final View view) {
        final Spinner spCodResultado = (Spinner) view.findViewById(R.id.spCodResultado);
        CustomTextView tvCodResultadoQuestion = (CustomTextView) view.findViewById(R.id.tvCodResultadoQuestion);
        tvCodResultadoQuestion.setText(ParentViewMain.getText(getActivity(), false, R.string.tsk_comando_resultado), TextView.BufferType.SPANNABLE);
        String[] columnsEdo = new String[]{"value"};
        int[] to_edo = new int[]{R.id.spinnerRowDescription};
        adapterSubComando = new SearchableCursorAdapter(getActivity(), R.layout.spinner_row_code_task, null, columnsEdo, to_edo, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                return getMyView(position, v);
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                return getMyView(position, v);
            }

            private View getMyView(int position, View v) {
                Cursor cursor = (Cursor) getItem(position);
                String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                String value = cursor.getString(cursor.getColumnIndex("value"));
                if (codigo.isEmpty()) {
                    codigo += " ";
                } else {
                    codigo += " - ";
                }
                CustomTextView tv = (CustomTextView) v.findViewById(R.id.spinnerRowDescription);
                tv.setText(codigo + value);
                return v;
            }
        };
        adapterSubComando.setDropDownViewResource(R.layout.spinner_row_code_task);
        spCodResultado.setAdapter(adapterSubComando);
        spCodResultado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                Object selected = parent.getItemAtPosition(position);
                view.findViewById(R.id.flCodResultadoError).setVisibility(View.GONE);
                if (selected instanceof Cursor) {
                    performCodigoResultadoClick((Cursor) selected, position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void performCodigoResultadoClick(Cursor cursor, int position) {
        Spinner spCodResultado = (Spinner) getActivity().findViewById(R.id.spCodResultado);
        int indexOf = cursor.getColumnIndexOrThrow("codigo");
        String selectedId = cursor.getString(indexOf);
        String selectedString = cursor.getString(cursor.getColumnIndex("value"));
        if (selectedId.equals(""))
            spCodResultado.setTag(null);
        else {
            spCodResultado.setTag(selectedId);
            spCodResultado.setTag(R.id.view_1, selectedString);
        }
    }

    private void loadSubComando(Object data) {
        if (data instanceof Cursor) {
            Cursor c = (Cursor) data;
            if (c.getCount() == 1) {
                adapterSubComando.swapCursor(null);
                getView().findViewById(R.id.containerCodResultado).setVisibility(View.GONE);
            } else {
                adapterSubComando.swapCursor(c);
                getView().findViewById(R.id.containerCodResultado).setVisibility(View.VISIBLE);
                loadCodResultado();
            }
        } else {
            adapterSubComando.swapCursor(null);
            getView().findViewById(R.id.containerCodResultado).setVisibility(View.GONE);
        }
    }

    private void configureHoras(final View view) {
        RelativeLayout rlHoraInicio = (RelativeLayout) view.findViewById(R.id.rlHoraInicio);
        final ImageView ivHoraInicioDelete = (ImageView) view.findViewById(R.id.ivHoraInicioDelete);
        final CustomTextView etHoraInicioAnswer = (CustomTextView) view.findViewById(R.id.etHoraInicioAnswer);
        final RelativeLayout rlHoraFin = (RelativeLayout) view.findViewById(R.id.rlHoraFin);
        final ImageView ivHoraFinDelete = (ImageView) view.findViewById(R.id.ivHoraFinDelete);
        final CustomTextView etHoraFinAnswer = (CustomTextView) view.findViewById(R.id.etHoraFinAnswer);
        final CustomTextView tvHoraInicioQuestion = (CustomTextView) view.findViewById(R.id.tvHoraInicioQuestion);
        tvHoraInicioQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_hora_de_inicio), TextView.BufferType.SPANNABLE);
        final CustomTextView tvHoraFinQuestion = (CustomTextView) view.findViewById(R.id.tvHoraFinQuestion);
        tvHoraFinQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_hora_fin), TextView.BufferType.SPANNABLE);
        ivHoraFinDelete.setVisibility(View.GONE);
        ivHoraInicioDelete.setVisibility(View.GONE);
        rlHoraInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                CustomDialog.getTime(getActivity(), new Interfaces.OnResponse<Object>() {
                    @Override
                    public void onResponse(int handlerCode, Object o) {
                        view.findViewById(R.id.flHoraInicioError).setVisibility(View.GONE);
                        if (o != null) {
                            ivHoraInicioDelete.setVisibility(View.VISIBLE);
                            Calendar cal = (Calendar) o;
                            clickHoraInicio(cal);
                            rlHoraFin.performClick();
                        }
                    }
                }, 0, "Seleccione una hora de inicio", null);
            }
        });
        rlHoraFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View ve) {
                if (etHoraInicioAnswer.getTag() == null) {
                    Toast.makeText(getActivity(), "Primero selecciona la hora de inicio", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Calendar strStartDate = (Calendar) etHoraInicioAnswer.getTag();
                CustomDialog.getTime(getActivity(), new Interfaces.OnResponse<Object>() {
                    @Override
                    public void onResponse(int handlerCode, Object o) {
                        view.findViewById(R.id.flHoraFinError).setVisibility(View.GONE);
                        if (o != null) {
                            Calendar cal = (Calendar) o;
                            clickHoraFin(cal);
                        }
                    }
                }, 0, "Seleccione una hora de fin", strStartDate);
            }
        });
        ivHoraInicioDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivHoraInicioDelete.setVisibility(View.GONE);
                etHoraInicioAnswer.setText("");
                etHoraInicioAnswer.setTag(null);
                ivHoraFinDelete.performClick();
            }
        });
        ivHoraFinDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivHoraFinDelete.setVisibility(View.GONE);
                etHoraFinAnswer.setText("");
                etHoraFinAnswer.setTag(null);
            }
        });
    }

    private void clickHoraInicio(Calendar cal) {
        CustomTextView etHoraInicioAnswer = (CustomTextView) getActivity().findViewById(R.id.etHoraInicioAnswer);
        //  String f = "^$|^(([0-9])|([0-2][0-9])|([3][0-1]))\\-(Ene|Feb|Mar|Abr|May|Jun|Jul|Ago|Sep|Oct|Nov|Dic)\\-\\d{4}$";
//                            cal.set(Calendar.YEAR, 0);
//                            cal.set(Calendar.MONTH, 0);
//                            cal.set(Calendar.DAY_OF_MONTH, 0);
        String time = Tools.getTime(cal);
        etHoraInicioAnswer.setText(time + " hrs.");
        etHoraInicioAnswer.setTag(cal);
    }

    private void clickHoraFin(Calendar cal) {
        CustomTextView etHoraInicioAnswer = (CustomTextView) getActivity().findViewById(R.id.etHoraInicioAnswer);
        CustomTextView etHoraFinAnswer = (CustomTextView) getActivity().findViewById(R.id.etHoraFinAnswer);
        ImageView ivHoraFinDelete = (ImageView) getActivity().findViewById(R.id.ivHoraFinDelete);
        //  String f = "^$|^(([0-9])|([0-2][0-9])|([3][0-1]))\\-(Ene|Feb|Mar|Abr|May|Jun|Jul|Ago|Sep|Oct|Nov|Dic)\\-\\d{4}$";
        Calendar startDate = (Calendar) etHoraInicioAnswer.getTag();
//                            cal.set(Calendar.YEAR, 0);
//                            cal.set(Calendar.MONTH, 0);
//                            cal.set(Calendar.DAY_OF_MONTH, 0);
        if (startDate.before(cal)) {
            String time = Tools.getTime(cal);
            etHoraFinAnswer.setText(time + " hrs.");
            etHoraFinAnswer.setTag(cal);
            ivHoraFinDelete.setVisibility(View.VISIBLE);
        } else {
            etHoraFinAnswer.setText("");
            etHoraFinAnswer.setTag(null);
            ivHoraFinDelete.setVisibility(View.GONE);
        }
    }

    private void configureFechaFin(final View view) {
        RelativeLayout rlFechaFin = (RelativeLayout) view.findViewById(R.id.rlFechaFin);
        final ImageView ivFechaFinDelete = (ImageView) view.findViewById(R.id.ivFechaFinDelete);
        final CustomTextView etFechaFinAnswer = (CustomTextView) view.findViewById(R.id.etFechaFinAnswer);
        CustomTextView tvFechaFinQuestion = (CustomTextView) view.findViewById(R.id.tvFechaFinQuestion);
        tvFechaFinQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_fecha_compromiso_actual), TextView.BufferType.SPANNABLE);
        ivFechaFinDelete.setVisibility(View.GONE);
        ivFechaFinDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFechaFinAnswer.setText("");
                etFechaFinAnswer.setTag(null);
                ivFechaFinDelete.setVisibility(View.GONE);
            }
        });
        Log.i("", "");
        rlFechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar minDate = Calendar.getInstance();
                Long epochFechaAlta = 0L;
                if (isFromPencel) {
                    epochFechaAlta = tareaDTO.getFechaAlta().getEpoch();
                } else {
                    epochFechaAlta = Long.valueOf(seguimientoTarea.getFechaAlta());
                }
                minDate.setTimeInMillis(epochFechaAlta * 1000);
                CustomDialog.getDate(getActivity(), new Interfaces.OnResponse<Object>() {
                    @Override
                    public void onResponse(int handlerCode, Object o) {
                        view.findViewById(R.id.flFechaFinError).setVisibility(View.GONE);
                        if (o != null) {
                            Calendar cal = (Calendar) o;
                            clickFechaFin(cal);
                        }
                    }
                }, 0, "Seleccione una fecha", minDate);
            }
        });
    }

    private void clickFechaFin(Calendar cal) {
        CustomTextView etFechaFinAnswer = (CustomTextView) getActivity().findViewById(R.id.etFechaFinAnswer);
        ImageView ivFechaFinDelete = (ImageView) getActivity().findViewById(R.id.ivFechaFinDelete);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        String date = getDate(cal, null);
        etFechaFinAnswer.setText(date);
        etFechaFinAnswer.setTag(cal);
        ivFechaFinDelete.setVisibility(View.VISIBLE);
    }

    private void configureDescipcion(final View view) {
        final CustomEditText etDescipcionAnswer = (CustomEditText) view.findViewById(R.id.etDescipcionAnswer);
        final ImageView ivDescipcionDelete = (ImageView) view.findViewById(R.id.ivDescipcionDelete);
        final CustomTextView tvDescipcionQuestion = (CustomTextView) view.findViewById(R.id.tvDescipcionQuestion);
        tvDescipcionQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_descripci_n), TextView.BufferType.SPANNABLE);
        ivDescipcionDelete.setVisibility(View.GONE);
        etDescipcionAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                view.findViewById(R.id.flDescipcionError).setVisibility(View.GONE);
                if (s.toString().isEmpty()) {
                    etDescipcionAnswer.setTag(null);
                    ivDescipcionDelete.setVisibility(View.GONE);
                } else {
                    etDescipcionAnswer.setTag(s.toString());
                    ivDescipcionDelete.setVisibility(View.VISIBLE);
                }
            }
        });
        ivDescipcionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etDescipcionAnswer.setText("");
            }
        });
    }

    private static class QuestionLoader extends AsyncTaskLoader<Object> {
        String idEstado = "";//del estado de esta clase
        Integer idSubtipo;//de la clasificacion de la tarea
        Integer idTarea;//id de la tarea a la que se va a asignar la nota
        short idSubClasifica; // de la subclasficacion
        boolean hasSubclasifica; // Tiene Subclasificación;

        private QuestionLoader(Context context, SeguimientoTarea seguimientoTarea, Bundle args) {
            super(context);
            if (seguimientoTarea != null) {
                idSubtipo = seguimientoTarea.getIdSubtipo();
                idTarea = seguimientoTarea.getId();
            }
            if (args != null) {
                idEstado = args.getString("idEstado", "");
            }
        }

        private QuestionLoader(Context context, TareaDTO tareaDTO, Bundle args) {
            super(context);
            if (tareaDTO != null) {
                idSubtipo = tareaDTO.getSubTipo();
                idTarea = tareaDTO.get_id().intValue();
                hasSubclasifica = tareaDTO.isHasSubClasifica();
                idSubClasifica = tareaDTO.getIdSubClasifica();
            }
            if (args != null) {
                idEstado = args.getString("idEstado", "");
            }
        }

        @Override
        public List<Questions> loadInBackground() {
            if (idEstado.isEmpty() || idSubtipo == 0) {
                return new ArrayList<>();
                // PRIMER FORMA NORMAL SEGUN Y TERCERA
            }
            List<Questions> list = new ArrayList<>();
            CatalogDatabaseHelper helper = CatalogDatabaseHelper.getHelper(getContext());
            if (hasSubclasifica) {
                Questions qs;
                ArrayList<Options> listOp;
                ArrayList<OptionsFO> listOpFO;
                ArrayList<Autocomplete> listAut;
                ArrayList<Autocomplete> listAutFO;
                List<RelSubClasificaQuestions> qsOp;
                RelOptionQuery relOpcion;
                Document questDoc;
                Dao<Options, Long> daoOp;
                Dao<Document, Long> daoDoc;
                Dao<Autocomplete, Long> daoAut;
                Dao<RelOptionQuery, Long> daoRelOp;
                try {
                    Dao<RelSubClasificaQuestions, Long> daoRQ = helper.getDao(RelSubClasificaQuestions.class);
                    qsOp = daoRQ.queryBuilder().where().eq(RelSubClasificaQuestions.COL_SUB_CLA, idSubClasifica)
                            .and().eq(RelSubClasificaQuestions.COL_STATUS, idEstado).query();
                    Dao<Questions, Long> daoQ = helper.getDao(Questions.class);
                    for (RelSubClasificaQuestions ro : qsOp) {
                        long idQs = ro.getIdQuestion();
                        qs = daoQ.queryBuilder().where().eq(Questions.COLID, idQs).queryForFirst();
                        if (qs == null) continue;
                        daoOp = helper.getDao(Options.class);
                        daoDoc = helper.getDao(Document.class);
                        daoAut = helper.getDao(Autocomplete.class);
                        daoRelOp = helper.getDao(RelOptionQuery.class);
                        listOp = (ArrayList<Options>) daoOp.queryBuilder().where().eq(Options.COLID_QNS, idQs).query();
                        //listOp = new ArrayList<Options>(listOpFO);
                        //if (listOpFO.size() > 0) {
                        for (Options op : listOp) {
                            relOpcion = daoRelOp.queryBuilder().where().eq(RelOptionQuery.COL_INDICE, op.getId()).queryForFirst();
                            if (relOpcion != null) {
                                listAutFO = (ArrayList<Autocomplete>) daoAut.queryBuilder().where().eq(Autocomplete.COL_REL, relOpcion.getIndice()).query();
                                listAut = new ArrayList<Autocomplete>(listAutFO);
                                op.setAutocomplete(listAut);
                            }
                        }
                        //}
                        qs.setOptions(listOp);
                        questDoc = daoDoc.queryBuilder().where().eq(Document.COLID, idQs).queryForFirst();
                        qs.setDocument(questDoc);
                        list.add(qs);
                    }
                    for (int i = 1; i < list.size(); i++) {
                        list.get(i - 1).setFromClasifica(true);
                        list.get(i - 1).setIdPropio(i);
                        list.get(i - 1).setIdPadre(99999);
                    }
                } catch (Exception ignored) {
                    Log.i("", "");
                } finally {
                    helper.close();
                    helper.close();
                }
            } else {
                try {
                    String query = "select " + NotasV2Form.COLQUESTIOND + " from " + TableNames.CATALOG_NOTE_FORM + "\n" +
                            " where \n"
                            + "		" + NotasV2Form.COL_ID_ESTADO + " = " + idEstado + " and \n" +
                            "		" + NotasV2Form.COL_ID_SUBCLASIFICA + " = " + idSubtipo + " and \n" +
                            "		(" + NotasV2Form.COL_ID_TAREA + " is null or " + NotasV2Form.COL_ID_TAREA + " = " + idTarea + " ) " +
                            "order by " + NotasV2Form.COL_ID_TAREA + " asc ";
                    Cursor cursor = helper.getReadableDatabase().rawQuery(query, null);
                    cursor.moveToFirst();
                    String data = "[";
                    do {
                        String question = cursor.getString(cursor.getColumnIndex(NotasV2Form.COLQUESTIOND));
                        if (question == null || question.length() < 2)
                            continue;
                        question = question.substring(1, question.length() - 1) + ",";
                        data += question;
                        //fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                    } while (cursor.moveToNext());
                    if (data.endsWith(",")) {
                        data = data.substring(0, data.length() - 1);
                    }
                    data += "]";
                    cursor.close();
                    Type listType = new TypeToken<List<Questions>>() {
                    }.getType();
                    list = new Gson().fromJson(data, listType);
                    for (int i = 1; i < list.size(); i++) {
                        list.get(i - 1).setFromClasifica(true);
                        list.get(i - 1).setIdPropio(i);
                        list.get(i - 1).setIdPadre(99999);
                    }
                } catch (Exception e) {
                    Log.i("", "");
                } finally {
                    helper.close();
                }
            }
            return list;
        }
    }

    private static class SeguimientoLoader extends AsyncTaskLoader<Object> {

        private Integer tareaId;

        private SeguimientoLoader(Context context, Bundle args) {
            super(context);
            if (args != null) {
                tareaId = args.getInt(SeguimientoTarea.class.getSimpleName(), 0);
            }
        }

        @Override
        public SeguimientoTarea loadInBackground() {
            UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getContext());
            SeguimientoTarea seguimientoTarea = null;
            try {
                Dao<SeguimientoTarea, Long> dao = helper.getDao(SeguimientoTarea.class);
                seguimientoTarea = dao.queryBuilder().where().eq(SeguimientoTarea.ID, tareaId).queryForFirst();
                isFromPencel = false;
                if (seguimientoTarea == null) {
                    Dao<TareaDTO, Long> daoTArea = helper.getDao(TareaDTO.class);
                    tareaDTO = daoTArea.queryBuilder().where().eq(TareaDTO.COL_ID, tareaId).queryForFirst();
                    isFromPencel = true;
                }
                Dao<NotaDTO, Long> notaDao = helper.getDao(NotaDTO.class);
                QueryBuilder<NotaDTO, Long> queryBuilderNota = notaDao.queryBuilder();
                queryBuilderNota.where().eq(NotaDTO.COL_ID_TAREA, tareaId).and().in(NotaDTO.COL_PARCIAL_SAVE, true);
                queryBuilderNota.orderBy("_id", false);
                pendingNotaDTO = queryBuilderNota.queryForFirst();
                Log.i("", "");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                helper.close();
            }
            return seguimientoTarea;
        }
    }

    public SeguimientoTarea getSeguimientoTarea() {
        return seguimientoTarea;
    }

    public TareaDTO getTareaDTO() {
        return tareaDTO;
    }

    public boolean isIsFromPencel() {
        return isFromPencel;
    }

    public static class QuestionFromOptionLoader extends AsyncTaskLoader<Object> {
        private int positionQ;
        private int optionID;
        private List<Questions> listForm;

        QuestionFromOptionLoader(Context context, Bundle args, List<Questions> listForm) {
            super(context);
            if (args != null) {
                positionQ = args.getInt("FORM_POSITION_SUB_OPTION", 0);
                optionID = args.getInt("FORM_ID_SUB_OPTION", 0);
                this.listForm = listForm;
            }
        }

        @Override
        public List<Questions> loadInBackground() {
            List<Questions> list = new ArrayList<>();
            Questions qs;
            CatalogDatabaseHelper helper = CatalogDatabaseHelper.getHelper(getContext());
            ArrayList<Options> listOp;
            ArrayList<OptionsFO> listOpFO;
            ArrayList<Autocomplete> listAut;
            ArrayList<AutocompleteFO> listAutFO;
            List<RelOptionQuestionsFO> qsOp;
            RelOptionQuery relOpcion;
            Document questDoc;
            Dao<OptionsFO, Long> daoOp;
            Dao<DocumentsFO, Long> daoDoc;
            Dao<AutocompleteFO, Long> daoAut;
            Dao<RelOptionQueryFO, Long> daoRelOp;
            try {
                Dao<RelOptionQuestionsFO, Long> daoRQ = helper.getDao(RelOptionQuestionsFO.class);
                qsOp = daoRQ.queryBuilder().where().eq(RelOptionQuestionsFO.COL_INDICE, optionID).query();
                Dao<QuestionsFO, Long> daoQ = helper.getDao(QuestionsFO.class);
                for (RelOptionQuestionsFO ro : qsOp) {
                    qs = daoQ.queryBuilder().where().eq(QuestionsFO.COLID, ro.getIdquestion()).queryForFirst();
                    daoOp = helper.getDao(OptionsFO.class);
                    daoDoc = helper.getDao(DocumentsFO.class);
                    daoAut = helper.getDao(AutocompleteFO.class);
                    daoRelOp = helper.getDao(RelOptionQueryFO.class);
                    listOpFO = (ArrayList<OptionsFO>) daoOp.queryBuilder()
                            .orderBy(OptionsFO.COL_ORDER, false)
                            .where().eq(OptionsFO.COLID_QNS, qs.getId())
                            .query();
                    listOp = new ArrayList<Options>(listOpFO);
                    if (listOpFO.size() > 0) {
                        for (Options op : listOp) {
                            relOpcion = daoRelOp.queryBuilder().where()
                                    .eq(RelOptionQueryFO.COL_INDICE, op.getId()).queryForFirst();
                            if (relOpcion != null) {
                                listAutFO = (ArrayList<AutocompleteFO>) daoAut.queryBuilder().where().eq(AutocompleteFO.COL_REL, relOpcion.getIndice()).query();
                                listAut = new ArrayList<Autocomplete>(listAutFO);
                                op.setAutocomplete(listAut);
                            }
                        }
                    }
                    qs.setOptions(listOp);
                    questDoc = daoDoc.queryBuilder().where().eq(DocumentsFO.COLID, qs.getId()).queryForFirst();
                    qs.setDocument(questDoc);
                    list.add(qs);
                }
            } catch (Exception ignored) {
                Log.i("", "");
            } finally {
                helper.close();
                helper.close();
            }
            //list = sortList(list, true);
            return partialInitTempData(list);
        }
/*AQUI VAMOS A REALIZAR EL CODIGO PARA ORDENAR LA LISTA CON FORME EL NUMERO DE ORDEN */
        private ArrayList<Questions> orderList(ArrayList<Questions> listToOrder) {
            Questions qsF;
            Questions qsS;
            for (int i = 0; i < listToOrder.size(); i++) {
                qsF = listToOrder.get(i);
                for (int j = i + 1; j < listToOrder.size(); j++) {
                    qsS = listToOrder.get(j);
                    if (qsF.getOrder() > qsS.getOrder()) {
                        listToOrder.remove(i);
                        listToOrder.add(i + 1, qsF);
                    } else break;
                }
            }
            return listToOrder;
        }
//
//         private List<Questions> shortorderlist(final List<Questions> listToOrder, final boolean firstSort){
//            Collections.sort(listToOrder, );
//
//            return listToOrder;
//         }
//        Comparator<T> t =  new Comparator<Questions>() {
//            @Override
//            public int compare(Questions o1, Questions o2) {
//                if (firstSort) {
//                    sortList(listToOrder, false);
//                    return o1.getQuestion().compareTo(o2.getQuestion());
//                }
//                return o1.getOrder().compareTo(o2.getOrder());
//            }
//        };
//
        private List<Questions> sortList(final List<Questions> listToOrder, final boolean firstSort) {
            Collections.sort(listToOrder, new Comparator<Questions>() {
                @Override
                public int compare(Questions o1, Questions o2) {
                    if (firstSort) {
                        sortList(listToOrder, false);
                        return o1.getQuestion().compareTo(o2.getQuestion());
                    }
                    return o1.getOrder().compareTo(o2.getOrder());
                }
            });
            return listToOrder;
        }

        private List<Questions> partialInitTempData(List<Questions> list) {
            Questions qSelected = listForm.get(positionQ);
            boolean isFromClasifica = qSelected.isFromClasifica();
            int idPadre = qSelected.getIdPropio();
            for (int i = 1; i <= list.size(); i++) {
                if (isFromClasifica)
                    list.get(i - 1).setFromClasifica(true);
                else
                    list.get(i - 1).setFromSubClasifica(true);
                list.get(i - 1).setIdPropio(i);
                list.get(i - 1).setIdPadre(idPadre);
            }
            ArrayList<Integer> padresList = new ArrayList<>();
            for (int i = positionQ + 1; i < listForm.size(); i++) {
                if (idPadre == listForm.get(i).getIdPadre()) {
                    padresList.add(i);
                    int newPadreID = listForm.get(i).getIdPropio();
                    padresList = recursiveClean(newPadreID, listForm, i, padresList);
                    i = padresList.get(padresList.size() - 1);
                } else break;
            }
            /*for (int i = 1; i <= padresList.size(); i++) {
                int deletePosition = padresList.get(i);
                listForm.remove(deletePosition);
            }*/
            if (padresList.size() > 0) {
                for (int i = padresList.get(0); padresList.size() > 0; i++) {
                    listForm.remove(i);
                    padresList.remove(0);
                    i--;
                }
            }
            listForm.addAll(positionQ + 1, list);
            return listForm;
        }

        private ArrayList<Integer> recursiveClean(int idPadre, List<Questions> listForm, int i, ArrayList<Integer> padresList) {
            for (int j = i + 1; j < listForm.size(); j++) {
                if (idPadre == listForm.get(j).getIdPadre()) {
                    padresList.add(j);
                    int newPadreID = listForm.get(j).getIdPropio();
                    padresList = recursiveClean(newPadreID, listForm, j, padresList);
                    j = padresList.get(padresList.size() - 1);
                } else break;
            }
            return padresList;
        }
    }

    static class QuestionCleanLoader extends AsyncTaskLoader<Object> {
        private int positionQ;
        private List<Questions> listForm;

        public QuestionCleanLoader(Context context, Bundle args, List<Questions> listForm) {
            super(context);
            if (args != null) {
                positionQ = args.getInt("FORM_POSITION_SUB_OPTION", 0);
                this.listForm = listForm;
            }
        }

        @Override
        public List<Questions> loadInBackground() {
            return partialInitTempData();
        }

        private List<Questions> partialInitTempData() {
            Questions qSelected = listForm.get(positionQ);
            int idPadre = qSelected.getIdPropio();
            ArrayList<Integer> padresList = new ArrayList<>();
            for (int i = positionQ + 1; i < listForm.size(); i++) {
                if (idPadre == listForm.get(i).getIdPadre()) {
                    padresList.add(i);
                    int newPadreID = listForm.get(i).getIdPropio();
                    padresList = recursiveClean(newPadreID, listForm, i, padresList);
                    i = padresList.get(padresList.size() - 1);
                } else break;
            }
            if (padresList.size() > 0) {
                for (int i = padresList.get(0); padresList.size() > 0; i++) {
                    listForm.remove(i);
                    padresList.remove(0);
                    i--;
                }
            }
            return listForm;
        }

        private ArrayList<Integer> recursiveClean(int idPadre, List<Questions> listForm, int i, ArrayList<Integer> padresList) {
            for (int j = i + 1; j < listForm.size(); j++) {
                if (idPadre == listForm.get(j).getIdPadre()) {
                    padresList.add(j);
                    int newPadreID = listForm.get(j).getIdPropio();
                    padresList = recursiveClean(newPadreID, listForm, j, padresList);
                    j = padresList.get(padresList.size() - 1);
                } else break;
            }
            return padresList;
        }
    }

    private void addQuestionsFromOption(Intent data, int opt) {
        int positionQuestion = data.getIntExtra("positionQ", 0);
        moveScroll = listView.getFirstVisiblePosition() - 1;
        index = listView.getFirstVisiblePosition();
        View v = listView.getChildAt(0);
        top = (v == null) ? 0 : v.getTop();
        Options optionSelected = (Options) data.getSerializableExtra("option");
        Bundle args = getArguments();
        args.putInt("FORM_POSITION_SUB_OPTION", positionQuestion);
        args.putInt("FORM_ID_SUB_OPTION", optionSelected.getId());
        if (opt == 1)
            getLoaderManager().restartLoader(LOADER_QUESTION_FROM_OPTION, args, AddNotaV2Fragment.this).forceLoad();
        else
            getLoaderManager().restartLoader(LOADER_QUESTION_CLEAN_OPTION, args, AddNotaV2Fragment.this).forceLoad();
    }
}
