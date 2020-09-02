package mx.com.pendulum.olintareas.ui.fragments.tareas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

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

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.CatalogDatabaseHelper;
import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.SubClasifica.CatTskSubclasificaTareas;
import mx.com.pendulum.olintareas.dto.SubClasifica.RelOptionQuery;
import mx.com.pendulum.olintareas.dto.SubClasifica.RelSubClasificaQuestions;
import mx.com.pendulum.olintareas.dto.fromOptions.AutocompleteFO;
import mx.com.pendulum.olintareas.dto.fromOptions.DocumentsFO;
import mx.com.pendulum.olintareas.dto.fromOptions.OptionsFO;
import mx.com.pendulum.olintareas.dto.fromOptions.QuestionsFO;
import mx.com.pendulum.olintareas.dto.fromOptions.RelOptionQueryFO;
import mx.com.pendulum.olintareas.dto.fromOptions.RelOptionQuestionsFO;
import mx.com.pendulum.olintareas.dto.SyncDataOutObject;
import mx.com.pendulum.olintareas.dto.UserData;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Autocomplete;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogCases;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogCuentas;
import mx.com.pendulum.olintareas.dto.tareasV2.CatalogPrioridades;
import mx.com.pendulum.olintareas.dto.tareasV2.Document;
import mx.com.pendulum.olintareas.dto.tareasV2.EpochDate;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.dto.tareasV2.ResponsablesDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.TareaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.TareasV2Form;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.tareas.Validator;
import mx.com.pendulum.olintareas.tareas.views.ParentViewMain;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;
import mx.com.pendulum.olintareas.ui.parents.FragmentParent;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.Util;
import mx.com.pendulum.utilities.views.CustomAutoCompleteTextView;
import mx.com.pendulum.utilities.views.CustomEditText;
import mx.com.pendulum.utilities.views.CustomTextView;

@SuppressWarnings({"ConstantConditions", "SpellCheckingInspection"})
public class AddTareaV2Fragment extends FragmentParent implements LoaderManager.LoaderCallbacks<Object>, Interfaces.OnResponse {

    private static final String TAG = AddTareaV2Fragment.class.getSimpleName();
    private static final int LOADER_QUESTIONS = 8569820;
    private static final int LOADER_CAT_ACTIVIDAD = 8569821;
    private static final int LOADER_CAT_DE = 8569822;
    private static final int LOADER_CAT_ASUNTO = 8569823;
    private static final int LOADER_CAT_PRIORIDAD = 8569824;
    private static final int LOADER_CAT_SUB_ASUNTO = 8569825;
    private static final int LOADER_SUB_QUESTIONS = 8569826;
    private static final int LOADER_QUESTION_FROM_OPTION = 85539265;
    private static final int LOADER_QUESTION_CLEAN_OPTION = 85539262;
    private static final String REULT_CODE = "REULT_CODE";
    private DynamicFormAdapter adapterForm;
    private List<Questions> listForm;
    private ListView listView;
    private SearchableCursorAdapter adapterTypeActivity;
    private SearchableCursorAdapter adapterTypeDe;
    private SearchableCursorAdapter adapterTypeAsunto;
    private SearchableCursorAdapter adapterTypeSubAsunto;
    private SearchableCursorAdapter adapterTypePrioridad;
    private String creditStr;
    private TareaDTO tareaDTO;
    private short posAsuntoSelected = 0;
    private boolean isCreORJuiShowing;
    private short idSubClasificaSelected = 0;
    private String subClasificaString = "";
    private boolean isCreditoJuicioFromAnotherApp;
    private String fromPackageName = null;
    public boolean showMessageCreditCase = false;
    public boolean accountFromLegal = false;
    public String accountName;
    private int moveScroll = -1;
    int index = 0;
    int top = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (getArguments().getBoolean(Properties.EXTRA_IS_FROM_ANOTHER_APP)) {
            creditStr = getArguments().getString(Properties.EXTRA_JSON_SEG);
            fromPackageName = getArguments().getString(Properties.CALL_FROM_PACKAGE_NAME);
        } else
            creditStr = getArguments().getString(SyncDataOutObject.COL_CREDIT_NUMBER, null);
        isCreditoJuicioFromAnotherApp = creditStr != null;
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
        if (tareaDTO == null) {
            UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getActivity());
            try {
                tareaDTO = new TareaDTO();
                Dao<TareaDTO, Long> dao = helper.getDao(TareaDTO.class);
                dao.create(tareaDTO);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                helper.close();
            }
        }
        listView = getView().findViewById(R.id.listview);
        listView.addHeaderView(getHeader());
        listView.addFooterView(getFooter());
        listView.setAdapter(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DynamicFormAdapter.OPTION_WITH_QUESTION_SELECTED)
            addQuestionsFromOption(data, 1);
        else if (requestCode == DynamicFormAdapter.OPTION_CLEAN_QUESTION_SELECTED)
            addQuestionsFromOption(data, 2);
        else
            adapterForm = result(requestCode, resultCode, data, adapterForm);
    }

    public void signatureTaken(Intent data) {
        int code = data.getIntExtra(REULT_CODE, 0);
        adapterForm = AddTareaV2Fragment.result(DynamicFormAdapter.SIGN, code, data, adapterForm);
    }

    public void addressTaken(Intent data) {
        int code = data.getIntExtra(REULT_CODE, 0);
        adapterForm = AddTareaV2Fragment.result(DynamicFormAdapter.ADDRESS, code, data, adapterForm);
    }

    public static DynamicFormAdapter result(int requestCode, int resultCode, Intent data, DynamicFormAdapter adapterForm) {
        switch (requestCode) {
            case DynamicFormAdapter.SIGN:
                if (adapterForm != null) {
                    adapterForm.setImagePathItemFromSignature(resultCode, data);
                }
                break;
            case DynamicFormAdapter.FILE_UPLOAD:
                if (adapterForm != null) {
                    adapterForm.setImagePathItemFromCamera(resultCode, data);
                }
                break;
            case DynamicFormAdapter.VIDEO:
                if (adapterForm != null) {
                    adapterForm.setVideoPathItemFromCamera(resultCode, data);
                }
                break;
            case DynamicFormAdapter.GEOLOCATION:
                if (adapterForm != null) {
                    adapterForm.setGeolocation(resultCode, data);
                }
                break;
            case DynamicFormAdapter.CHOICE:
            case DynamicFormAdapter.LIST:
                if (adapterForm != null) {
                    adapterForm.setFormAsAnswer(resultCode, data);
                }
                break;
            case DynamicFormAdapter.ADDRESS:
                if (adapterForm != null) {
                    adapterForm.setAddress(resultCode, data);
                }
                break;
        }
        return adapterForm;
    }

    private boolean validateHeader(TareaDTO tareaDTO) {
        Object tag;
        boolean isCita = false;
        boolean isLegal = false;
        showMessageCreditCase = false;
        Calendar calFechaCmpromiso = Calendar.getInstance();
        View etResponsableAnswer = getView().findViewById(R.id.etResponsableAnswer);
        {
            tag = isValid(getActivity(), etResponsableAnswer, true, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flResponsableError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor escriba un responsable");
                listView.smoothScrollToPosition(0);
                return false;
            } else {
                if (tag instanceof ResponsablesDTO) {
                    ResponsablesDTO userQuasar = (ResponsablesDTO) tag;
                    tareaDTO.setResponsable(userQuasar.getNombre());
                    tareaDTO.setQuasarResponsable(userQuasar.getId());
                    tareaDTO.setMailResponsable(userQuasar.getMail());
                    tareaDTO.setNombreResponsable(userQuasar.getNombre());
                }
            }
        }
        View spActividad = getView().findViewById(R.id.spActividad);
        {
            tag = isValid(getActivity(), spActividad, true, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flActividadError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor seleccione una actividad");
                listView.smoothScrollToPosition(0);
                return false;
            } else {
                if (tag instanceof Short) {
                    Short s = (Short) tag;
                    String selectedStr = (String) spActividad.getTag(R.id.view_1);
                    tareaDTO.setIdTipoTarea(s.intValue());
                    tareaDTO.setTipoTarea(s + "");
                    tareaDTO.setTipoTareaDesc(selectedStr);
                    if (s == 4) {
                        isCita = true;
                    }
                }
            }
        }
        View spDe = getView().findViewById(R.id.spDe);
        {
            tag = isValid(getActivity(), spDe, true, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flDeError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor seleccione un origen");
                listView.smoothScrollToPosition(0);
                return false;
            } else {
                if (tag instanceof String) {
                    String str = (String) tag;
                    String selectedStr = (String) spDe.getTag(R.id.view_1);
                    tareaDTO.setIdSeccion(str);
                    tareaDTO.setDe(selectedStr);
                    if (str.equals("QL")) {
                        isLegal = true;
                    }
                }
            }
        }
        View spAsunto = getView().findViewById(R.id.spAsunto);
        {
            tag = isValid(getActivity(), spAsunto, true, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flAsuntoError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor seleccione un asunto");
                listView.smoothScrollToPosition(0);
                return false;
            } else {
                if (tag instanceof String) {
                    String str = (String) tag;
                    String selectedStr = (String) spAsunto.getTag(R.id.view_1);
                    Integer idAsunto = Integer.valueOf(str);
                    tareaDTO.setSubTipo(idAsunto);
                    tareaDTO.setAsunto(selectedStr);
                }
            }
        }
        View etFechaStartAnswer = getView().findViewById(R.id.etFechaStartAnswer);
        {
            tag = isValid(getActivity(), etFechaStartAnswer, true, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flFechaStartError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor inserte una fecha");
                listView.smoothScrollToPosition(0);
                return false;
            } else {
                if (tag instanceof Calendar) {
                    Calendar cal = (Calendar) tag;
                    calFechaCmpromiso = cal;
                    EpochDate epochDate = Validator.getEpochDate(cal);
                    tareaDTO.setFechaInicio(epochDate);
                    tareaDTO.setFechaCompromiso(epochDate);
                }
            }
        }
//        View etFechaCompromisoAnswer = getView().findViewById(R.id.etFechaCompromisoAnswer);
//        {
//            tag = isValid(etFechaCompromisoAnswer, !isCita, false, null);
//            if (tag == null) {
//                View v = getView().findViewById(R.id.flFechaCompromisoError);
//                v.setVisibility(View.VISIBLE);
//                Tools.showSnack(getActivity(), "Por favor seleccione una fecha");
//                listView.smoothScrollToPosition(0);
//                return false;
//            } else {
//                if (tag instanceof Calendar) {
//                    Calendar cal = (Calendar) tag;
//                    EpochDate epochDate = Validator.getEpochDate(cal);
//                    tareaDTO.setFechaCompromiso(epochDate);
//                }
//            }
//        }
        View spPrioridad = getView().findViewById(R.id.spPrioridad);
        {
            tag = isValid(getActivity(), spPrioridad, true, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flPrioridadError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor seleccione la prioridad");
                listView.smoothScrollToPosition(0);
                return false;
            } else {
                if (tag instanceof String) {
                    String s = (String) tag;
                    tareaDTO.setIdPrioridad(Integer.valueOf(s));
                }
            }
        }
        View etHoraInicioAnswer = getView().findViewById(R.id.etHoraInicioAnswer);
        {
            tag = isValid(getActivity(), etHoraInicioAnswer, isCita, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flHoraInicioError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "por favor seleccione la hora de inicio");
                listView.smoothScrollToPosition(0);
                return false;
            } else {
                if (tag instanceof Calendar) {
                    Calendar cal = (Calendar) tag;
                    cal.set(Calendar.YEAR, calFechaCmpromiso.get(Calendar.YEAR));
                    cal.set(Calendar.MONTH, calFechaCmpromiso.get(Calendar.MONTH));
                    cal.set(Calendar.DAY_OF_MONTH, calFechaCmpromiso.get(Calendar.DAY_OF_MONTH));
                    tareaDTO.setHoraInicio(Validator.getEpochDate(cal));
                }
            }
        }
        View etHoraFinAnswer = getView().findViewById(R.id.etHoraFinAnswer);
        {
            tag = isValid(getActivity(), etHoraFinAnswer, isCita, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flHoraFinError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor seleccione la hora fin");
                listView.smoothScrollToPosition(0);
                return false;
            } else {
                if (tag instanceof Calendar) {
                    Calendar cal = (Calendar) tag;
                    cal.set(Calendar.YEAR, calFechaCmpromiso.get(Calendar.YEAR));
                    cal.set(Calendar.MONTH, calFechaCmpromiso.get(Calendar.MONTH));
                    cal.set(Calendar.DAY_OF_MONTH, calFechaCmpromiso.get(Calendar.DAY_OF_MONTH));
                    tareaDTO.setHoraFin(Validator.getEpochDate(cal));
                }
            }
        }
        View etLugarAnswer = getView().findViewById(R.id.etLugarAnswer);
        {
            tag = isValid(getActivity(), etLugarAnswer, isCita, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flLugarError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor escriba un lugar");
                listView.smoothScrollToPosition(0);
                return false;
            } else {
                if (tag instanceof String) {
                    String str = (String) tag;
                    tareaDTO.setLugar(str);
                }
            }
        }
        View etAnswer = getView().findViewById(R.id.etDescipcionAnswer);
        {
            tag = isValid(getActivity(), etAnswer, true, false, null);
            if (tag == null) {
                View v = getView().findViewById(R.id.flDescipcionError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor escriba una descripción");
                listView.smoothScrollToPosition(listForm.size() + 1);
                return false;
            } else {
                if (tag instanceof String) {
                    String str = (String) tag;
                    int MAX_CHAR = 4000;
                    int maxLength = (str.length() < MAX_CHAR) ? str.length() : MAX_CHAR;
                    tareaDTO.setDescripcion(str.substring(0, maxLength));            //max 4000 chars
                    tareaDTO.setDescripcionHtml(str.substring(0, maxLength));        //igual a descripccion
                }
            }
        }
        View etCreditoAnswer = getView().findViewById(R.id.etCreditoAnswer);
        {
            if (isCreORJuiShowing) {
                tag = isValid(getActivity(), etCreditoAnswer, true, false, null);
                if (tag == null) {
                    View v = getView().findViewById(R.id.flCreditoError);
                    v.setVisibility(View.VISIBLE);
                    Tools.showSnack(getActivity(), "Por favor escriba un " + (isLegal ? "juicio" : "crédito"));
                    listView.smoothScrollToPosition(0);
                    accountFromLegal = false;
                    return false;
                } else {
                    if (tag instanceof String) {
                        String cuenta = ((String) tag).trim();
                        String str = cuenta.toUpperCase();
                        // TODO HERE MOVER
                        if (isLegal) {
                            str = putCase(isLegal, str);
                        } else {
                            str = putCredit(isLegal, str);
                        }
                        if (str.equalsIgnoreCase("")) {
                            if (cuenta.trim().equalsIgnoreCase("")) {
                                return false;
                            } else {
                                showMessageCreditCase = true;
                                accountName = cuenta;
                                str = cuenta.toUpperCase();
                            }
                        }
                        //if (fromPackageName != null) { } else { }
                        if (isLegal) {
                            tareaDTO.setJuicio(str);
                            accountFromLegal = true;
                        } else {
                            tareaDTO.setCredito(str);
                            accountFromLegal = false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public TareaDTO getTareaDTO(){
        return tareaDTO;
    }

    private String putCredit(boolean isLegal, String str) {
        CatalogDatabaseHelper helper = CatalogDatabaseHelper.getHelper(getActivity());
        try {
            Dao<CatalogCuentas, Long> dao = helper.getDao(CatalogCuentas.class);
            QueryBuilder<CatalogCuentas, Long> query = dao.queryBuilder();
            query.where().eq(CatalogCuentas.COL_ACOUNT_NAME, str);
            CatalogCuentas cuenta = query.queryForFirst();
            if (cuenta == null) {
                View v = getView().findViewById(R.id.flCreditoError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor escriba un " + (isLegal ? "juicio" : "crédito") + " válido.");
                listView.smoothScrollToPosition(0);
                return "";
            }
            str = cuenta.getAccountName();
        } catch (Exception e) {
            View v = getView().findViewById(R.id.flCreditoError);
            v.setVisibility(View.VISIBLE);
            Tools.showSnack(getActivity(), "Por favor escriba un " + (isLegal ? "juicio" : "crédito") + " válido.");
            listView.smoothScrollToPosition(0);
            return "";
        } finally {
            helper.close();
        }
        return str;
    }

    private String putCase(boolean isLegal, String str) {
        CatalogDatabaseHelper helper = CatalogDatabaseHelper.getHelper(getActivity());
        try {
            Dao<CatalogCases, Long> dao = helper.getDao(CatalogCases.class);
            QueryBuilder<CatalogCases, Long> query = dao.queryBuilder();
            query.where().eq(CatalogCases.COL_ACOUNT_NAME, str);
            CatalogCases cuenta = query.queryForFirst();
            if (cuenta == null) {
                View v = getView().findViewById(R.id.flCreditoError);
                v.setVisibility(View.VISIBLE);
                Tools.showSnack(getActivity(), "Por favor escriba un " + (isLegal ? "juicio" : "crédito") + " válido.");
                listView.smoothScrollToPosition(0);
                return "";
            }
            str = cuenta.getAccountName();
        } catch (Exception e) {
            View v = getView().findViewById(R.id.flCreditoError);
            v.setVisibility(View.VISIBLE);
            Tools.showSnack(getActivity(), "Por favor escriba un " + (isLegal ? "juicio" : "crédito") + " válido.");
            listView.smoothScrollToPosition(0);
            return "";
        } finally {
            helper.close();
        }
        return str;
    }

    public static Object isValid(Activity activity, View view, Object params, boolean showSnack, String creditoOrJuicio) {
        if (view == null) return null;
        Validator validator = new Validator(activity, view, params);
        validator.setCreditOrJuicio(creditoOrJuicio);
        if (!validator.validate(showSnack)) return null;
        return validator.getValue();
    }

    public TareaDTO validateForm() {
        tareaDTO.setUpdated(true);
        UserData userData = getUserSecion();
        tareaDTO.setUsuAltaNombre(userData.getNombre());
        tareaDTO.setUsuAltaCorreo(userData.getCorreo());
        tareaDTO.setUsuAlta(userData.getUsername());
        tareaDTO.setEstado("Asignada");
        tareaDTO.setIdEstado(1);
        tareaDTO.setFechaAlta(Validator.getEpochDate(Calendar.getInstance()));
        tareaDTO.setJuicio("");
        tareaDTO.setCredito("");
        tareaDTO.setTmpNotaId(0L);
        tareaDTO.setTmpTareaId(tareaDTO.get_id());
        if (idSubClasificaSelected != 0) {
            tareaDTO.setHasSubClasifica(true);
            tareaDTO.setIdSubClasifica(idSubClasificaSelected);
            tareaDTO.setSubClasifica(subClasificaString);
        } else {
            tareaDTO.setHasSubClasifica(false);
            tareaDTO.setIdSubClasifica((short) 0);
            tareaDTO.setSubClasifica("");
        }
        boolean isValideader = validateHeader(tareaDTO);
        if (!isValideader) {
            return null;
        }
        Collection<AnswerDTO> answerList = new ArrayList<>();
        tareaDTO.setAnswers(answerList);
        if (listForm != null)
            if (!listForm.isEmpty())
                for (int i = 0; i < listForm.size(); i++) {
                    View view = getViewByPosition(i + 1, listView);
                    Object tag = null;
                    if (!listForm.get(i).getType().equalsIgnoreCase("FILE_UPLOAD_DESC"))
                        tag = isValid(getActivity(), view, listForm.get(i), true, isCreORJuiShowing ? (tareaDTO.getCredito() == null ? tareaDTO.getJuicio() : tareaDTO.getCredito()) : "");
                    if (tag == null) {
                        Log.i("No_Valid", "No_Valid");
                        listForm.get(i).setError(true);
                        adapterForm.notifyDataSetChanged();
                        moveScroll = i;
                        listView.smoothScrollToPosition(i + 1);
                        Tools.showSnack(getActivity(), "Por favor conteste " + listForm.get(i).getQuestion());
                        return null;
                    } else {
                        Log.i("", "");
                        if (tag instanceof AnswerDTO) {
                            AnswerDTO answerDTO = (AnswerDTO) tag;
                            answerList.add(answerDTO);
                            if (answerDTO != null && answerDTO.getType() != null) {
                                switch (answerDTO.getType()) {
                                    case "CHOICE":
                                    case "LIST":
                                        int idOption = answerDTO.getId_option();
                                        boolean isComplete = AddNotaV2Fragment.loadOptionAnswers(
                                                getActivity(), idOption, answerList,
                                                false, tareaDTO.get_id(), 0L);
                                        if (!isComplete) {
                                            return null;
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    Log.i("", "");
                }
        if (showMessageCreditCase) {
            return null;
        }
        return tareaDTO;
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
            listForm = null;
            return;
        }
        Spinner spDe = getActivity().findViewById(R.id.spDe);
        String de = (String) spDe.getTag();
        boolean isImageResize = false;
        if (de.equals("AB")) {
            String idTipo = (String) getActivity().findViewById(R.id.spAsunto).getTag();
            isImageResize = shouldResize(idTipo);
        }
        Long idTarea = tareaDTO.get_id();
        Long idNota = 0L;
        adapterForm = new DynamicFormAdapter(getActivity(), list, this, isImageResize, idTarea, idNota);
        listForm = list;
        listView.setAdapter(adapterForm);
        if (moveScroll != -1) {
            listView.setSelectionFromTop(index, top);
            moveScroll = -1;
        }
    }

    public static boolean shouldResize(String idTipo) {
        try {
            int id = Integer.parseInt(idTipo);
            return shouldResize(id);
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean shouldResize(int idTipo) {
        switch (idTipo) {
            case 34:    //Limpieza básica
            case 35:    //Limpieza mayor
            case 47:    //Colocación de lona de venta
            case 53:    //Completitud de datos
            case 284:    //Rondín
            case 367:    //Destapiado
            case 366:    //Inmueble invadido
            case 305:    //Tapiado
            case 306:    //Destapiado-tapiado
            case 490:    //Rondin especial
                return true;
            case 48:    //Visita apertura con cliente
            case 50:    //Visita apertura sin cliente
            case 51:    //Visita recepción de inmueble
            case 52:    //Visita avalúo por cliente
            case 56:    //Visita entrega de Inmueble
            case 102:    //Visita avalúo por unidad técnica
            default:
                return false;
        }
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        String query;
        switch (id) {
            case LOADER_QUESTIONS:
                CustomDialog.showProgressDialog(getActivity(), true, "Cargando...");
                return new QuestionLoader(getActivity(), args);
            case LOADER_SUB_QUESTIONS:
                CustomDialog.showProgressDialog(getActivity(), true, "Cargando...");
                return new SubQuestionLoader(getActivity(), args, listForm);
            case LOADER_QUESTION_FROM_OPTION:
                CustomDialog.showProgressDialog(getActivity(), true, "Cargando...");
                return new QuestionFromOptionLoader(getActivity(), args, listForm);
            case LOADER_QUESTION_CLEAN_OPTION:
                CustomDialog.showProgressDialog(getActivity(), true, "Cargando...");
                return new QuestionCleanLoader(getActivity(), args, listForm);
            case LOADER_CAT_ACTIVIDAD:
                query =
                        " SELECT 	_id,	value,	id\n" +
                                " FROM\n" +
                                "  (\n" +
                                "SELECT	0 _id, 'Selecciona' value, '0' id\n" +
                                "	UNION\n" +
                                "SELECT    _id,        			value,	 id\n" +
                                "	FROM " + TableNames.CATALOG_ACTIVIDAD + "\n" +
                                //      " 		WHERE id = '1' OR id = '4'\n" +
                                ")\n" +
                                "ORDER BY id ASC";
                return new CatalogLoader(getActivity(), query);
            case LOADER_CAT_DE:
                /*String where = "";
                if (creditStr != null) {
                    if (creditStr.startsWith("B")) {
                        where += "AND id == 'AB'";
                    }
                }*/
                query = " SELECT _id, value, id, requiereCredito, requiereJuicio\n" +
                        " FROM\n" +
                        "  (\n" +
                        "SELECT	0 _id, 'Selecciona' value, '' id, 0 requiereCredito, 0 requiereJuicio\n" +
                        "	UNION\n" +
                        "SELECT _id, value, id, requiereCredito, requiereJuicio\n" +
                        "	FROM " + TableNames.CATALOG_GET_TIPOS_TAREA + "\n" +
                        //" 		WHERE id in ('QC','AB') \n " + where +
                        ")\n" +
                        "ORDER BY id ASC";
                return new CatalogLoader(getActivity(), query);
            case LOADER_CAT_ASUNTO:
                String elected_de = args.getString("KEY_SELECTED_DE", "");
                /*query = " SELECT 	_id, value, id, code\n" +
                        " FROM\n" +
                        "  (\n" +
                        "SELECT	0 _id, 'Selecciona' value, '' id, '' code\n" +
                        "	UNION\n" +
                        "SELECT    _id, value, id, code\n" +
                        "	FROM " + TableNames.CATALOG_GET_SUBTIPOS + "\n" +
                        "where code = '" + elected_de + "'\n" +
                        "and id not in (60,61) \n" +
                        ")\n";// +*/
                query = " SELECT 	_id, value, id, code, despliegaOlinTareas\n" +
                        " FROM\n" +
                        "  (\n" +
                        "SELECT	0 _id, 'Selecciona' value, '' id, '' code, 1 despliegaOlinTareas\n" +
                        "	UNION\n" +
                        "SELECT    _id, value, id, code, despliegaOlinTareas\n" +
                        "	FROM " + TableNames.CATALOG_GET_SUBTIPOS + "\n" +
                        "where code = '" + elected_de + "'\n" +
                        "and id not in (60,61) \n" +
                        "and despliegaOlinTareas = 1 \n" +
                        ")\n";// +
                return new CatalogLoader(getActivity(), query);
            case LOADER_CAT_SUB_ASUNTO:
                String selected_asunto = args.getString("FORM_ID_SUB", "");
                query = " SELECT 	_id, id, description, sla, id_clasifica\n" +
                        " FROM\n" +
                        "  (\n" +
                        "SELECT	0 _id, '' id, 'Selecciona' description, '' sla, '' id_clasifica\n" +
                        "	UNION\n" +
                        "SELECT    _id, id, description, sla, id_clasifica\n" +
                        "	FROM " + TableNames.CATALOG_FORM_SUB + "\n" +
                        "where id_clasifica = '" + selected_asunto + "'\n" +
                        ")\n";// +
                return new CatalogLoader(getActivity(), query);
            case LOADER_CAT_PRIORIDAD:
                query = " SELECT 	_id,	value,	id\n" +
                        " FROM\n" +
                        "  (\n" +
                        "SELECT	0 _id, 'Selecciona' value, '0' id\n" +
                        "	UNION\n" +
                        "SELECT    _id,        			value,	 id\n" +
                        "	FROM " + TableNames.CATALOG_GET_PRIORIDADES + "\n" +
                        ")\n" +
                        "ORDER BY id ASC";
                return new CatalogLoader(getActivity(), query);
            default:
                return null;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
        switch (loader.getId()) {
            case LOADER_QUESTIONS:
            case LOADER_CAT_ACTIVIDAD:
            case LOADER_CAT_DE:
            case LOADER_CAT_ASUNTO:
            case LOADER_CAT_SUB_ASUNTO:
            case LOADER_CAT_PRIORIDAD:
            case LOADER_SUB_QUESTIONS:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        switch (loader.getId()) {
            case LOADER_QUESTIONS:
            case LOADER_SUB_QUESTIONS:
            case LOADER_QUESTION_FROM_OPTION:
            case LOADER_QUESTION_CLEAN_OPTION:
                List<Questions> list = (List<Questions>) data;
                loadDynamicForm(list);
                CustomDialog.showProgressDialog(getActivity(), false, null);
                break;
            case LOADER_CAT_ACTIVIDAD:
                loadCatActividad(data);
                break;
            case LOADER_CAT_DE:
                loadCatDe(data);
                break;
            case LOADER_CAT_ASUNTO:
                loadCatAsunto(data);
                break;
            case LOADER_CAT_SUB_ASUNTO:
                loadCatSubAsunto(data);
                break;
            case LOADER_CAT_PRIORIDAD:
                loadCatPrioridad(data);
                break;
        }
    }

    private View getHeader() {
        View view = View.inflate(getActivity(), R.layout.header_tareas_v2, null);
        configureResponsable(view);
        configureActividad(view);
        configureAsunto(view);
        configureSubAsunto(view);
        configureDe(view);
        configureCredito(view);
        configureFechaInicio(view);
        configurePrioridad(view);
        configureHoras(view);
        configureLugar(view);
        view.findViewById(R.id.containerCredito).setVisibility(View.GONE);
        view.findViewById(R.id.containerAsunto).setVisibility(View.GONE);
        view.findViewById(R.id.containerFechaInicio).setVisibility(View.GONE);
        view.findViewById(R.id.containerPrioridad).setVisibility(View.GONE);
        view.findViewById(R.id.containerHoras).setVisibility(View.GONE);
        view.findViewById(R.id.containerLugar).setVisibility(View.GONE);
        view.findViewById(R.id.containerSubAsunto).setVisibility(View.GONE);
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

    private SearchableCursorAdapter getSubAdapterGeneric() {
        String[] columnsEdo = new String[]{"description"};
        int[] to_edo = new int[]{R.id.spinnerRowDescription};
        SearchableCursorAdapter adapterGeneric = new SearchableCursorAdapter(getActivity(), R.layout.spinner_row_code_task, null, columnsEdo, to_edo, 0);
        adapterGeneric.setDropDownViewResource(R.layout.spinner_row_code_task);
        return adapterGeneric;
    }

    private View getFooter() {
        View view = View.inflate(getActivity(), R.layout.footer_tareas_v2, null);
        configureDescipcion(view);
        return view;
    }

    private void configureDescipcion(final View view) {
        final CustomEditText etDescipcionAnswer = view.findViewById(R.id.etDescipcionAnswer);
        final ImageView ivDescipcionDelete = view.findViewById(R.id.ivDescipcionDelete);
        final CustomTextView tvDescipcionQuestion = view.findViewById(R.id.tvDescipcionQuestion);
        tvDescipcionQuestion.setText(ParentViewMain.getText(getActivity(), true, "Descripción"), TextView.BufferType.SPANNABLE);
        ivDescipcionDelete.setVisibility(View.GONE);
        etDescipcionAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                if (hasFocus) {
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!v.hasFocus()) {
                                v.requestFocus();
                            }
                        }
                    }, 200);
                }
            }
        });
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

    private void configureLugar(final View view) {
        final CustomEditText etLugarAnswer = view.findViewById(R.id.etLugarAnswer);
        final ImageView ivLugarDelete = view.findViewById(R.id.ivLugarDelete);
        final CustomTextView tvLugarQuestion = view.findViewById(R.id.tvLugarQuestion);
        tvLugarQuestion.setText(ParentViewMain.getText(getActivity(), true, getString(R.string.tsk_en_d_nde_ser_la_cita)), TextView.BufferType.SPANNABLE);
        ivLugarDelete.setVisibility(View.GONE);
        etLugarAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                if (hasFocus) {
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!v.hasFocus()) {
                                v.requestFocus();
                            }
                        }
                    }, 200);
                }
            }
        });
        etLugarAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                view.findViewById(R.id.flLugarError).setVisibility(View.GONE);
                if (s.toString().isEmpty()) {
                    etLugarAnswer.setTag(null);
                    ivLugarDelete.setVisibility(View.GONE);
                } else {
                    etLugarAnswer.setTag(s.toString());
                    ivLugarDelete.setVisibility(View.VISIBLE);
                }
            }
        });
        ivLugarDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etLugarAnswer.setText("");
            }
        });
    }

    private void configureHoras(final View view) {
        RelativeLayout rlHoraInicio = view.findViewById(R.id.rlHoraInicio);
        final ImageView ivHoraInicioDelete = view.findViewById(R.id.ivHoraInicioDelete);
        final CustomTextView etHoraInicioAnswer = view.findViewById(R.id.etHoraInicioAnswer);
        final RelativeLayout rlHoraFin = view.findViewById(R.id.rlHoraFin);
        final ImageView ivHoraFinDelete = view.findViewById(R.id.ivHoraFinDelete);
        final CustomTextView etHoraFinAnswer = view.findViewById(R.id.etHoraFinAnswer);
        final CustomTextView tvHoraInicioQuestion = view.findViewById(R.id.tvHoraInicioQuestion);
        tvHoraInicioQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_hora_de_inicio), TextView.BufferType.SPANNABLE);
        final CustomTextView tvHoraFinQuestion = view.findViewById(R.id.tvHoraFinQuestion);
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
                            String time = Tools.getTime(cal);
                            String t = time + " hrs.";
                            etHoraInicioAnswer.setText(t);
                            etHoraInicioAnswer.setTag(cal);
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
                            Calendar startDate = (Calendar) etHoraInicioAnswer.getTag();
                            Calendar cal = (Calendar) o;
                            if (startDate.before(cal)) {
                                String time = Tools.getTime(cal);
                                String t = time + " hrs.";
                                etHoraFinAnswer.setText(t);
                                etHoraFinAnswer.setTag(cal);
                                ivHoraFinDelete.setVisibility(View.VISIBLE);
                            } else {
                                etHoraFinAnswer.setText("");
                                etHoraFinAnswer.setTag(null);
                                ivHoraFinDelete.setVisibility(View.GONE);
                            }
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

    private void configureFechaInicio(final View view) {
        RelativeLayout rlFechaInicio = view.findViewById(R.id.rlFechaInicio);
        final ImageView ivFechaStartDelete = view.findViewById(R.id.ivFechaStartDelete);
        final CustomTextView etFechaStartAnswer = view.findViewById(R.id.etFechaStartAnswer);
        CustomTextView tvFechaStartQuestion = view.findViewById(R.id.tvFechaStartQuestion);
        tvFechaStartQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_fecha_compromiso_original), TextView.BufferType.SPANNABLE);
        ivFechaStartDelete.setVisibility(View.GONE);
        ivFechaStartDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFechaStartAnswer.setText("");
                etFechaStartAnswer.setTag(null);
                ivFechaStartDelete.setVisibility(View.GONE);
            }
        });
        rlFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.getDate(getActivity(), new Interfaces.OnResponse<Object>() {
                    @Override
                    public void onResponse(int handlerCode, Object o) {
                        view.findViewById(R.id.flFechaStartError).setVisibility(View.GONE);
                        if (o != null) {
                            Calendar cal = (Calendar) o;
                            cal.set(Calendar.HOUR_OF_DAY, 12);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            String date = Tools.getDate(cal, null);
                            etFechaStartAnswer.setText(date);
                            etFechaStartAnswer.setTag(cal);
                            ivFechaStartDelete.setVisibility(View.VISIBLE);
                        }
                    }
                }, 0, "Seleccione una fecha", Calendar.getInstance());
            }
        });
    }

    private void configurePrioridad(final View view) {
        final Spinner spPrioridad = view.findViewById(R.id.spPrioridad);
        CustomTextView tvPrioridadQuestion = view.findViewById(R.id.tvPrioridadQuestion);
        tvPrioridadQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_prioridad), TextView.BufferType.SPANNABLE);
        adapterTypePrioridad = getAdapterGeneric();
        spPrioridad.setAdapter(adapterTypePrioridad);
        spPrioridad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                Object selected = parent.getItemAtPosition(position);
                if (selected instanceof Cursor) {
                    view.findViewById(R.id.flPrioridadError).setVisibility(View.GONE);
                    Cursor cursor = (Cursor) selected;
                    int indexOf = cursor.getColumnIndexOrThrow(CatalogPrioridades.COLID);
                    String selectedId = cursor.getString(indexOf);
                    if (selectedId.equals("0")) {
                        spPrioridad.setTag(null);
                    } else {
                        spPrioridad.setTag(selectedId);
                        spPrioridad.setTag(R.id.view_1, cursor.getString(cursor.getColumnIndex("value")));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getLoaderManager().restartLoader(LOADER_CAT_PRIORIDAD, getArguments(), this).forceLoad();
    }

    private void loadCatPrioridad(Object data) {
        if (data instanceof Cursor)
            adapterTypePrioridad.swapCursor((Cursor) data);
        else {
            adapterTypePrioridad.swapCursor(null);
        }
    }

    private void configureAsunto(final View view) {
        final Spinner spAsunto = view.findViewById(R.id.spAsunto);
        CustomTextView tvAsuntoQuestion = view.findViewById(R.id.tvAsuntoQuestion);
        tvAsuntoQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_asunto), TextView.BufferType.SPANNABLE);
        adapterTypeAsunto = getAdapterGeneric();
        spAsunto.setAdapter(adapterTypeAsunto);
        spAsunto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                Object selected = parent.getItemAtPosition(position);
                view.findViewById(R.id.flAsuntoError).setVisibility(View.GONE);
                if (selected instanceof Cursor) {
                    Cursor cursor = (Cursor) selected;
                    int indexOf = cursor.getColumnIndexOrThrow("id");
                    String selectedId = cursor.getString(indexOf);
                    posAsuntoSelected = Util.convertStringToShort(selectedId);
                    String selectedString = cursor.getString(cursor.getColumnIndex("value"));
                    if (selectedId.equals(""))
                        spAsunto.setTag(null);
                    else {
                        spAsunto.setTag(selectedId);
                        spAsunto.setTag(R.id.view_1, selectedString);
                    }
                    Bundle args = getArguments();
                    args.putString("FORM_ID_SUB", selectedId);
                    args.putString("FORM_ID", selectedId);
                    List<CatTskSubclasificaTareas> list = searchSubCategories(selectedId);
                    if (list.size() > 0) {
                        view.findViewById(R.id.containerSubAsunto).setVisibility(View.VISIBLE);
                        getLoaderManager().restartLoader(LOADER_CAT_SUB_ASUNTO, args, AddTareaV2Fragment.this).forceLoad();
                    } else
                        view.findViewById(R.id.containerSubAsunto).setVisibility(View.GONE);
                    getLoaderManager().restartLoader(LOADER_QUESTIONS, args, AddTareaV2Fragment.this).forceLoad();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void configureSubAsunto(final View view) {
        final Spinner spSubAsunto = view.findViewById(R.id.spSubAsunto);
        //CustomTextView tvSubAsuntoQuestion = view.findViewById(R.id.tvSubAsuntoQuestion);
        //tvSubAsuntoQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_sub_asunto), TextView.BufferType.EDITABLE);
        adapterTypeSubAsunto = getSubAdapterGeneric();
        spSubAsunto.setAdapter(adapterTypeSubAsunto);
        spSubAsunto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                Object selected = parent.getItemAtPosition(position);
                view.findViewById(R.id.flSubAsuntoError).setVisibility(View.GONE);
                if (selected instanceof Cursor) {
                    Cursor cursor = (Cursor) selected;
                    int indexOf = cursor.getColumnIndexOrThrow("id");
                    String selectedId = cursor.getString(indexOf);
                    String selectedString = cursor.getString(cursor.getColumnIndex("description"));
                    if (selectedId.equals("")) {
                        spSubAsunto.setTag(null);
                        idSubClasificaSelected = 0;
                        subClasificaString = selectedString;
                        Bundle args = getArguments();
                        String idAsunto = String.valueOf(posAsuntoSelected);
                        args.putString("FORM_ID_SUB", idAsunto);
                        args.putString("FORM_ID", idAsunto);
                        getLoaderManager().restartLoader(LOADER_QUESTIONS, args, AddTareaV2Fragment.this).forceLoad();
                    } else {
                        spSubAsunto.setTag(selectedId);
                        spSubAsunto.setTag(R.id.view_1, selectedString);
                        Bundle args = getArguments();
                        args.putString("FORM_ID_SUB", selectedId);
                        idSubClasificaSelected = Short.valueOf(selectedId);
                        subClasificaString = selectedString;
                        args.putShort("FORM_ID", posAsuntoSelected);
                        getLoaderManager().restartLoader(LOADER_SUB_QUESTIONS, args, AddTareaV2Fragment.this).forceLoad();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /// TODO agregar contante de estatus
    private void loadCatAsunto(Object data) {
        if (data instanceof Cursor) {
            adapterTypeAsunto.swapCursor((Cursor) data);
            Spinner spAsunto = getView().findViewById(R.id.spAsunto);
            View containerAsunto = getView().findViewById(R.id.containerAsunto);
            if (containerAsunto.getVisibility() == View.GONE) {
                loadDynamicForm(null);
            } else
                spAsunto.setSelection(0);
        } else {
            adapterTypeAsunto.swapCursor(null);
        }
    }

    private void loadCatSubAsunto(Object data) {
        if (data instanceof Cursor) {
            adapterTypeSubAsunto.swapCursor((Cursor) data);
            Spinner spAsunto = getView().findViewById(R.id.spSubAsunto);
            View containerSubAsunto = getView().findViewById(R.id.containerSubAsunto);
            if (containerSubAsunto.getVisibility() == View.GONE) {
                containerSubAsunto.setVisibility(View.VISIBLE);
            } else
                spAsunto.setSelection(0);
        } else {
            adapterTypeSubAsunto.swapCursor(null);
        }
    }

    private void configureDe(final View view) {
        final Spinner spDe = view.findViewById(R.id.spDe);
        CustomTextView tvDeQuestion = view.findViewById(R.id.tvDeQuestion);
        tvDeQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_negocio), TextView.BufferType.SPANNABLE);
        adapterTypeDe = getAdapterGeneric();
        spDe.setAdapter(adapterTypeDe);
        spDe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                Object selected = parent.getItemAtPosition(position);
                view.findViewById(R.id.flDeError).setVisibility(View.GONE);
                if (selected instanceof Cursor) {
                    Cursor cursor = (Cursor) selected;
                    int indexOf = cursor.getColumnIndexOrThrow("id");
                    int indexRequiereCredito = cursor.getColumnIndexOrThrow("requiereCredito");
                    int indexRequiereJuicio = cursor.getColumnIndexOrThrow("requiereJuicio");
                    String selectedId = cursor.getString(indexOf);
                    String value = cursor.getString(cursor.getColumnIndex("value"));
                    short requiereCred = cursor.getShort(indexRequiereCredito);
                    short requiereJuic = cursor.getShort(indexRequiereJuicio);
                    Bundle bundle = new Bundle();
                    bundle.putString("KEY_SELECTED_DE", selectedId);
                    getLoaderManager().restartLoader(LOADER_CAT_ASUNTO, bundle, AddTareaV2Fragment.this).forceLoad();
                    spDe.setTag(selectedId);
                    spDe.setTag(R.id.view_1, value);
                    isCreORJuiShowing = true;
                    CustomAutoCompleteTextView edt = view.findViewById(R.id.etCreditoAnswer);
                    if (requiereCred == 1) {
                        ((CustomTextView) view.findViewById(R.id.tvCreditoQuestion)).setText(ParentViewMain.getText(getActivity(), true, "Crédito"), TextView.BufferType.SPANNABLE);
                        view.findViewById(R.id.containerCredito).setVisibility(View.VISIBLE);
                        edt.setAdapter(new AutoCompleteCreditoFilter());
                        if (creditStr != null) {
                            edt.setText(creditStr);
                            edt.setTag(creditStr);
                        }
                    } else if (requiereJuic == 1) {
                        ((CustomTextView) view.findViewById(R.id.tvCreditoQuestion)).setText(ParentViewMain.getText(getActivity(), true, "Juicio"), TextView.BufferType.SPANNABLE);
                        view.findViewById(R.id.containerCredito).setVisibility(View.VISIBLE);
                        edt.setAdapter(new AutoCompleteCasesFilter());
                        if (creditStr != null) {
                            edt.setText(creditStr);
                            edt.setTag(creditStr);
                        }
                    } else {
                        isCreORJuiShowing = false;
                        view.findViewById(R.id.containerCredito).setVisibility(View.GONE);
                        edt = view.findViewById(R.id.etCreditoAnswer);
                        edt.setTag(null);
                        edt.setText("");
                    }
                    if (value.equalsIgnoreCase("Selecciona")) {
                        view.findViewById(R.id.containerAsunto).setVisibility(View.GONE);
                        edt = view.findViewById(R.id.etCreditoAnswer);
                        edt.setTag(null);
                        edt.setText("");
                    } else
                        view.findViewById(R.id.containerAsunto).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getLoaderManager().restartLoader(LOADER_CAT_DE, getArguments(), this).forceLoad();
    }

    private void loadCatDe(Object data) {
        if (data instanceof Cursor)
            adapterTypeDe.swapCursor((Cursor) data);
        else {
            adapterTypeDe.swapCursor(null);
        }
    }

    private void configureActividad(final View view) {
        final Spinner spActividad = view.findViewById(R.id.spActividad);
        CustomTextView tvActividadQuestion = view.findViewById(R.id.tvActividadQuestion);
        tvActividadQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_actividad), TextView.BufferType.SPANNABLE);
        adapterTypeActivity = getAdapterGeneric();
        spActividad.setAdapter(adapterTypeActivity);
        spActividad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                Object selected = parent.getItemAtPosition(position);
                if (selected instanceof Cursor) {
                    Cursor cursor = (Cursor) selected;
                    int indexOf = cursor.getColumnIndexOrThrow("id");
                    view.findViewById(R.id.flActividadError).setVisibility(View.GONE);
                    short selectedId = cursor.getShort(indexOf);
                    spActividad.setTag(selectedId);
                    spActividad.setTag(R.id.view_1, cursor.getString(cursor.getColumnIndex("value")));
                    ImageView ivHoraInicioDelete = view.findViewById(R.id.ivHoraInicioDelete);
                    ImageView ivHoraFinDelete = view.findViewById(R.id.ivHoraFinDelete);
                    ImageView ivFechaStartDelete = view.findViewById(R.id.ivFechaStartDelete);
                    CustomEditText etLugarAnswer = view.findViewById(R.id.etLugarAnswer);
                    switch (selectedId) {
                        default:
                        case 0://sin opcion
                            ((CustomTextView) view.findViewById(R.id.tvFechaStartQuestion)).setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_fecha_compromiso_original), TextView.BufferType.SPANNABLE);
                            view.findViewById(R.id.containerAsunto).setVisibility(View.GONE);
                            view.findViewById(R.id.containerFechaInicio).setVisibility(View.GONE);
                            view.findViewById(R.id.containerPrioridad).setVisibility(View.GONE);
                            view.findViewById(R.id.containerHoras).setVisibility(View.GONE);
                            view.findViewById(R.id.containerLugar).setVisibility(View.GONE);
                            ivHoraInicioDelete.performClick();
                            ivHoraFinDelete.performClick();
                            ivFechaStartDelete.performClick();
                            spActividad.setTag(null);
                            etLugarAnswer.setText("");
                            break;
                        case 1:
                        case 2:
                        case 3:
                            view.findViewById(R.id.containerFechaInicio).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.containerPrioridad).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.containerHoras).setVisibility(View.GONE);
                            view.findViewById(R.id.containerLugar).setVisibility(View.GONE);
                            ivHoraInicioDelete.performClick();
                            ivHoraFinDelete.performClick();
                            etLugarAnswer.setText("");
                            break;
                        case 4://cita
                            ((CustomTextView) view.findViewById(R.id.tvFechaStartQuestion)).setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_fecha_compromiso_original), TextView.BufferType.SPANNABLE);
                            view.findViewById(R.id.containerFechaInicio).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.containerPrioridad).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.containerHoras).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.containerLugar).setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getLoaderManager().restartLoader(LOADER_CAT_ACTIVIDAD, getArguments(), this).forceLoad();
    }

    private void loadCatActividad(Object data) {
        if (data instanceof Cursor)
            adapterTypeActivity.swapCursor((Cursor) data);
        else {
            adapterTypeActivity.swapCursor(null);
        }
    }

    private void configureCredito(final View view) {
        final CustomAutoCompleteTextView autocompleteCreditoAnswer = view.findViewById(R.id.etCreditoAnswer);
        final ImageView ivCreditoDelete = view.findViewById(R.id.ivCreditoDelete);
        if (!isCreditoJuicioFromAnotherApp) {
            CustomTextView tvCredito = view.findViewById(R.id.tvCreditoQuestion);
            tvCredito.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_cr_dito), TextView.BufferType.SPANNABLE);
            ivCreditoDelete.setVisibility(View.GONE);
            autocompleteCreditoAnswer.setAdapter(new AutoCompleteCreditoFilter());
            autocompleteCreditoAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(final View v, boolean hasFocus) {
                    if (hasFocus) {
                        v.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!v.hasFocus()) {
                                    v.requestFocus();
                                }
                            }
                        }, 200);
                    }
                }
            });

            final TextWatcher tw = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    view.findViewById(R.id.flCreditoError).setVisibility(View.GONE);
                    if (s.toString().isEmpty()) {
                        autocompleteCreditoAnswer.setTag(null);
                        ivCreditoDelete.setVisibility(View.GONE);
                    } else {
                        autocompleteCreditoAnswer.setTag(s.toString());
                        if (creditStr == null) {
                            ivCreditoDelete.setVisibility(View.VISIBLE);
                        }
                    }
                }
            };
            autocompleteCreditoAnswer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor c = (Cursor) parent.getAdapter().getItem(position);
                    int indexOf = c.getColumnIndexOrThrow(CatalogCuentas.COL_ACOUNT_NAME);
                    String nombre = c.getString(indexOf);
                    autocompleteCreditoAnswer.removeTextChangedListener(tw);
                    autocompleteCreditoAnswer.setText("");
                    autocompleteCreditoAnswer.append(nombre);
                    autocompleteCreditoAnswer.setTag(nombre);
                    autocompleteCreditoAnswer.addTextChangedListener(tw);
                }
            });
            autocompleteCreditoAnswer.addTextChangedListener(tw);
            ivCreditoDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autocompleteCreditoAnswer.setText("");
                }
            });
            if (creditStr != null) {
                autocompleteCreditoAnswer.removeTextChangedListener(tw);
                autocompleteCreditoAnswer.setText(creditStr);
                autocompleteCreditoAnswer.setTag(creditStr);
                autocompleteCreditoAnswer.setEnabled(false);
            }
        } else {
            autocompleteCreditoAnswer.setEnabled(false);
            ivCreditoDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResponse(int handlerCode, Object o) {
        Log.d("", "");
    }

    private class AutoCompleteCreditoFilter extends BaseAdapter implements Filterable {

        Cursor cursor;
        LayoutInflater layoutInflater;

        private AutoCompleteCreditoFilter() {
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

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            cursor.moveToPosition(position);
            int indexOf = cursor.getColumnIndexOrThrow(CatalogCuentas.COL_ACOUNT_NAME);
            CustomTextView txt;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.spinner_row_code_task, null);
                txt = view.findViewById(R.id.spinnerRowDescription);
                view.setTag(txt);
            } else {
                txt = (CustomTextView) view.getTag();
            }
            String nombre = cursor.getString(indexOf);
            txt.setText(nombre);
            return view;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected void publishResults(CharSequence constraint, FilterResults rer) {
                    cursor = (Cursor) rer.values;
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults ret = new FilterResults();
                    if ((constraint == null || constraint.length() == 0) && cursor != null) {
                        ret.count = cursor.getCount(); // TOdO validar null
                        ret.values = cursor;
                        return ret;
                    } else {
                        if (constraint != null) {
                            String filt = constraint.toString().toUpperCase();
                            Cursor cursor_aux = getCredito(filt);
                            ret.values = cursor_aux;
                            ret.count = cursor_aux.getCount();
                        }
                    }
                    return ret;
                }
            };
        }
    }

    private class AutoCompleteCasesFilter extends BaseAdapter implements Filterable {

        Cursor cursor;
        LayoutInflater layoutInflater;

        private AutoCompleteCasesFilter() {
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

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            cursor.moveToPosition(position);
            int indexOf = cursor.getColumnIndexOrThrow(CatalogCases.COL_ACOUNT_NAME);
            CustomTextView txt;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.spinner_row_code_task, null);
                txt = view.findViewById(R.id.spinnerRowDescription);
                view.setTag(txt);
            } else {
                txt = (CustomTextView) view.getTag();
            }
            String nombre = cursor.getString(indexOf);
            txt.setText(nombre);
            return view;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected void publishResults(CharSequence constraint, FilterResults rer) {
                    cursor = (Cursor) rer.values;
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults ret = new FilterResults();
                    if ((constraint == null || constraint.length() == 0) && cursor != null) {
                        ret.count = cursor.getCount(); // TOdO validar null
                        ret.values = cursor;
                        return ret;
                    } else {
                        if (constraint != null) {
                            String filt = constraint.toString().toUpperCase();
                            Cursor cursor_aux = getJuicio(filt);
                            ret.values = cursor_aux;
                            ret.count = cursor_aux.getCount();
                        }
                    }
                    return ret;
                }
            };
        }
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
                        Toast.makeText(getContext(), "Link copiado al portapapeles --> " + url,
                                Toast.LENGTH_LONG).show();
                    }
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

    private Cursor getCredito(String filter) {
        Cursor cursorReturn = null;
        CatalogDatabaseHelper catalogDat = CatalogDatabaseHelper.getHelper(getActivity());
        CloseableIterator<CatalogCuentas> iterator;
        try {
            Dao<CatalogCuentas, Long> actionCodeCatalogDao = catalogDat.getDao(CatalogCuentas.class);
            QueryBuilder<CatalogCuentas, Long> qbActionCode = actionCodeCatalogDao.queryBuilder();
            qbActionCode.where().like(CatalogCuentas.COL_ACOUNT_NAME, "%" + filter + "%");
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

    private Cursor getJuicio(String filter) {
        Cursor cursorReturn = null;
        CatalogDatabaseHelper catalogDat = CatalogDatabaseHelper.getHelper(getActivity());
        CloseableIterator<CatalogCases> iterator;
        try {
            Dao<CatalogCases, Long> actionCodeCatalogDao = catalogDat.getDao(CatalogCases.class);
            QueryBuilder<CatalogCases, Long> qbActionCode = actionCodeCatalogDao.queryBuilder();
            qbActionCode.where().like(CatalogCases.COL_ACOUNT_NAME, "%" + filter + "%");
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

    private void configureResponsable(final View view) {
        final CustomAutoCompleteTextView autoCompleteUser = view.findViewById(R.id.etResponsableAnswer);
        final ImageView ivDelete = view.findViewById(R.id.ivResponsableDelete);
        final CustomTextView tvResponsableQuestion = view.findViewById(R.id.tvResponsableQuestion);
        tvResponsableQuestion.setText(ParentViewMain.getText(getActivity(), true, R.string.tsk_responsable), TextView.BufferType.SPANNABLE);
        autoCompleteUser.setAdapter(new AutoCompleteFilter());
        ivDelete.setVisibility(View.GONE);
        final TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    autoCompleteUser.setTag(null);
                    ivDelete.setVisibility(View.GONE);
                } else {
                    ivDelete.setVisibility(View.VISIBLE);
                }
                view.findViewById(R.id.flResponsableError).setVisibility(View.GONE);
            }
        };
        autoCompleteUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                int indexOf = c.getColumnIndexOrThrow(ResponsablesDTO.NOMBRE);
                int indexOfQuasar = c.getColumnIndexOrThrow(ResponsablesDTO.COLID);
                int indexOfMail = c.getColumnIndexOrThrow(ResponsablesDTO.EMAIL);
                String nombre = c.getString(indexOf);
                String quasar = c.getString(indexOfQuasar);
                String mail = c.getString(indexOfMail);
                ResponsablesDTO user = new ResponsablesDTO();
                user.setId(quasar);
                user.setNombre(nombre);
                user.setMail(mail);
                autoCompleteUser.removeTextChangedListener(tw);
                autoCompleteUser.setText("");
                autoCompleteUser.append(nombre);
                autoCompleteUser.setTag(user);
                autoCompleteUser.addTextChangedListener(tw);
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

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            cursor.moveToPosition(position);
            int indexOf = cursor.getColumnIndexOrThrow(ResponsablesDTO.NOMBRE);
            CustomTextView txt;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.spinner_row_code_task, null);
                txt = view.findViewById(R.id.spinnerRowDescription);
                view.setTag(txt);
            } else {
                txt = (CustomTextView) view.getTag();
            }
            String nombre = cursor.getString(indexOf);
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

    public static class CatalogLoader extends AsyncTaskLoader<Object> {

        private String query;

        CatalogLoader(Context context, String query) {
            super(context);
            this.query = query;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = null;
            CatalogDatabaseHelper helper = CatalogDatabaseHelper.getHelper(getContext());
            try {
                cursor = helper
                        .getReadableDatabase()
                        .rawQuery(query, null);
            } catch (Exception sqle) {
                Log.e(TAG, sqle.getMessage(), sqle);
            }
            return cursor;
        }
    }

    private static class QuestionLoader extends AsyncTaskLoader<Object> {

        private String tipoForm;

        private QuestionLoader(Context context, Bundle args) {
            super(context);
            if (args != null)
                tipoForm = args.getString("FORM_ID", "");
        }

        @Override
        public List<Questions> loadInBackground() {
            List<Questions> list = new ArrayList<>();
            CatalogDatabaseHelper helper = CatalogDatabaseHelper.getHelper(getContext());
            try {
                Dao<TareasV2Form, Long> dao = helper.getDao(TareasV2Form.class);
                QueryBuilder<TareasV2Form, Long> qb = dao.queryBuilder();
                qb.where().eq(TareasV2Form.COLID, tipoForm);
                TareasV2Form form = qb.queryForFirst();
                Type listType = new TypeToken<List<Questions>>() {
                }.getType();
                list = new Gson().fromJson(form.getForm(), listType);
                for (int i = 1; i < list.size(); i++) {
                    list.get(i - 1).setFromClasifica(true);
                    list.get(i - 1).setIdPropio(i);
                    list.get(i - 1).setIdPadre(99999);
                }
            } catch (Exception ignored) {
            } finally {
                helper.close();
            }
            return list;
        }
    }

    private List<CatTskSubclasificaTareas> searchSubCategories(String selectedId) {
        long value = Util.convertStringToLong(selectedId);
        List<CatTskSubclasificaTareas> list = null;
        CatalogDatabaseHelper helper = CatalogDatabaseHelper.getHelper(getContext());
        try {
            Dao<CatTskSubclasificaTareas, Long> daoQ = helper.getDao(CatTskSubclasificaTareas.class);
            list = daoQ.queryBuilder().where().eq(CatTskSubclasificaTareas.COLCAT, value).query();
        } catch (Exception ignored) {
            Log.i("", "");
        } finally {
            helper.close();
            helper.close();
        }
        return list;
    }

    private static class SubQuestionLoader extends AsyncTaskLoader<Object> {
        private String tipoForm;
        private List<Questions> listForm;

        private SubQuestionLoader(Context context, Bundle args, List<Questions> listForm) {
            super(context);
            if (args != null) {
                tipoForm = args.getString("FORM_ID_SUB", "");
                this.listForm = listForm;
            }
        }

        @Override
        public List<Questions> loadInBackground() {
            long value = Util.convertStringToLong(tipoForm);
            List<Questions> list = new ArrayList<>();
            Questions subQNS;
            List<RelSubClasificaQuestions> listRSCQ;
            CatalogDatabaseHelper helper = CatalogDatabaseHelper.getHelper(getContext());
            ArrayList<Options> listOp;
            ArrayList<Autocomplete> listAut;
            RelOptionQuery relOpcion;
            Document questDoc;
            Dao<Options, Long> daoOp;
            Dao<Document, Long> daoDoc;
            Dao<Autocomplete, Long> daoAut;
            Dao<RelOptionQuery, Long> daoRelOp;
            try {
                Dao<RelSubClasificaQuestions, Long> daoRSCQ = helper.getDao(RelSubClasificaQuestions.class);
                listRSCQ = daoRSCQ.queryBuilder()
                        .where().eq(RelSubClasificaQuestions.COL_SUB_CLA, value)
                        .and().eq(RelSubClasificaQuestions.COL_STATUS, 1).query();
                for (RelSubClasificaQuestions rscq : listRSCQ) {
                    Dao<Questions, Long> daoQ = helper.getDao(Questions.class);
                    subQNS = daoQ.queryBuilder().where().eq(Questions.COLID, rscq.getIdQuestion()).queryForFirst();
                    if (subQNS == null) continue;
                    daoOp = helper.getDao(Options.class);
                    daoDoc = helper.getDao(Document.class);
                    daoAut = helper.getDao(Autocomplete.class);
                    daoRelOp = helper.getDao(RelOptionQuery.class);
                    listOp = (ArrayList<Options>) daoOp.queryBuilder().where().eq(Options.COLID_QNS, subQNS.getId()).query();
                    for (Options op : listOp) {
                        relOpcion = daoRelOp.queryBuilder().where().eq(RelOptionQuery.COL_INDICE, op.getId()).queryForFirst();
                        if (relOpcion != null) {
                            listAut = (ArrayList<Autocomplete>) daoAut.queryBuilder().where().eq(Autocomplete.COL_REL, relOpcion.getIndice()).query();
                            op.setAutocomplete(listAut);
                        }
                    }
                    subQNS.setOptions(listOp);
                    //questDoc = daoDoc.queryBuilder().where().eq(Document.COLID, value).queryForFirst();
                    questDoc = daoDoc.queryBuilder().where().eq(Document.COLID, subQNS.getId()).queryForFirst();
                    subQNS.setDocument(questDoc);
                    list.add(subQNS);
                }
            } catch (Exception ignored) {
                Log.i("", "");
            } finally {
                helper.close();
                helper.close();
            }
            return partialInitTempData(list);
        }

        private List<Questions> partialInitTempData(List<Questions> list) {
            for (int i = 1; i < list.size(); i++) {
                list.get(i - 1).setFromSubClasifica(true);
                list.get(i - 1).setIdPropio(i);
                list.get(i - 1).setIdPadre(88888);
            }
            boolean isEmpty = false;
            if (listForm != null) {
                if (!listForm.isEmpty()) {
                    for (int i = 0; i < listForm.size(); i++) {
                        if (!listForm.get(i).isFromClasifica()) {
                            listForm.remove(i);
                            i--;
                        }
                    }
                }
            } else isEmpty = true;
            if (listForm != null && list != null && list.size() > 0)
                listForm.addAll(list);
            if (isEmpty && list != null && list.size() > 0)
                listForm = list;
            return listForm;
        }
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
            list = sortList(list, true);
            return partialInitTempData(list);
        }

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
            getLoaderManager().restartLoader(LOADER_QUESTION_FROM_OPTION, args, AddTareaV2Fragment.this).forceLoad();
        else
            getLoaderManager().restartLoader(LOADER_QUESTION_CLEAN_OPTION, args, AddTareaV2Fragment.this).forceLoad();
    }
}