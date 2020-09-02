package mx.com.pendulum.olintareas.ui.dialog.tareas;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.dao.SepoCP;
import mx.com.pendulum.olintareas.db.dao.SepoColony;
import mx.com.pendulum.olintareas.db.dao.SepoStates;
import mx.com.pendulum.olintareas.db.SepomexDatabaseHelper;
import mx.com.pendulum.olintareas.db.dao.SepoMuni;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.adapter.tareas.SepoColonyAdapter;
import mx.com.pendulum.olintareas.ui.adapter.tareas.SepoMuniAdapter;
import mx.com.pendulum.olintareas.ui.adapter.tareas.SepoStateAdapter;
import mx.com.pendulum.utilities.views.CustomEditText;

public class AddressDialog implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Context context;
    private AlertDialog alert;
    private List<SepoStates> sepoStatesList;
    private List<SepoMuni> sepoMuniList;
    private List<SepoColony> sepoColonyList;
    private Spinner spState;
    private Spinner spMuni;
    private Spinner spColony;
    private int idAddressSelected = 0;
    private Interfaces.OnResponse<Object> response;
    private int request;
    private FrameLayout flErrorState;
    private FrameLayout flErrorColony;
    private FrameLayout flErrorMuni;
    private ImageView ivDeleteState;
    private ImageView ivDeleteMuni;
    private ImageView ivDeleteColony;
    private ImageView ivDeleteCp;
    private ImageView ivSearchCp;
    private int itemPosition;
    private int rowPosition;
    private boolean isCpUsed = false;
    private CustomEditText edtCp;
    private boolean putMuunicipality = false;
    private boolean moreThanOneCp = false;
    private boolean putColony = false;
    private List<SepoCP> sepoCP = null;

    public AddressDialog(Context context, final Interfaces.OnResponse<Object> response,
                         final int request, int itemPosition, int rowPosition) {
        this.context = context;
        this.itemPosition = itemPosition;
        this.rowPosition = rowPosition;
        this.response = response;
        this.request = request;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_dynamic_form_address, null);
        spState = view.findViewById(R.id.spState);
        view.findViewById(R.id.btnCancel).setOnClickListener(this);
        view.findViewById(R.id.btnAccept).setOnClickListener(this);
        spMuni = view.findViewById(R.id.spMuni);
        spColony = view.findViewById(R.id.spColony);
        flErrorState = view.findViewById(R.id.flErrorState);
        flErrorColony = view.findViewById(R.id.flErrorColony);
        flErrorMuni = view.findViewById(R.id.flErrorMuni);
        flErrorState.setVisibility(View.GONE);
        flErrorColony.setVisibility(View.GONE);
        flErrorMuni.setVisibility(View.GONE);
        ivDeleteState = view.findViewById(R.id.ivDeleteState);
        ivDeleteState.setVisibility(View.GONE);
        ivDeleteState.setOnClickListener(this);
        ivDeleteMuni = view.findViewById(R.id.ivDeleteMuni);
        ivDeleteMuni.setVisibility(View.GONE);
        ivDeleteMuni.setOnClickListener(this);
        ivDeleteColony = view.findViewById(R.id.ivDeleteColony);
        ivDeleteColony.setVisibility(View.GONE);
        ivDeleteColony.setOnClickListener(this);

        ivDeleteCp = view.findViewById(R.id.ivDeleteCp);
        ivDeleteCp.setVisibility(View.GONE);
        ivDeleteCp.setOnClickListener(this);
        ivSearchCp = view.findViewById(R.id.ivSearchCp);
        ivSearchCp.setOnClickListener(this);
        edtCp = view.findViewById(R.id.edtCp);
        edtCp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    ivDeleteCp.setVisibility(View.VISIBLE);
            }
        });

        SepomexDatabaseHelper sepomexDatabaseHelper = SepomexDatabaseHelper.getHelper(getContext());
        sepoStatesList = sepomexDatabaseHelper.obtainAllStates();
        ArrayAdapter<SepoStates> adapter = new SepoStateAdapter(context, sepoStatesList);

        spState.setAdapter(adapter);
        spState.setSelection(0);
        spState.setOnItemSelectedListener(this);
        spMuni.setEnabled(false);
        spMuni.setOnItemSelectedListener(this);
        spColony.setEnabled(false);
        spColony.setOnItemSelectedListener(this);
        builder.setView(view);
        builder.setCancelable(false);
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
            }
        });
    }

    private Context getContext() {
        return context;
    }

    public void showDialog() {
        alert.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spState:
                if (position == 0) {
                    ivDeleteState.setVisibility(View.GONE);
                    if (sepoMuniList != null && sepoMuniList.size() > 0) {
                        spMuni.setEnabled(false);
                        spMuni.setSelection(0);
                    }
                    if (sepoColonyList != null && sepoColonyList.size() > 0) {
                        spColony.setEnabled(false);
                        spColony.setSelection(0);
                    }
                } else {
                    ivDeleteState.setVisibility(View.VISIBLE);
                    SepoStates stateSelected = sepoStatesList.get(position);
                    fillMuniSpinner(stateSelected.get_id());
                    flErrorState.setVisibility(View.GONE);
                }
                if (putMuunicipality) {
                    putMuunicipalityByCp();
                    putMuunicipality = false;
                }
                break;
            case R.id.spMuni:
                if (position == 0) {
                    ivDeleteMuni.setVisibility(View.GONE);
                    if (sepoColonyList != null && sepoColonyList.size() > 0) {
                        spColony.setEnabled(false);
                        spColony.setSelection(0);
                    }
                } else {
                    ivDeleteMuni.setVisibility(View.VISIBLE);
                    SepoMuni muniSelected = sepoMuniList.get(position);
                    fillColonySpinner(muniSelected.get_id(), muniSelected.getId_state());
                    flErrorMuni.setVisibility(View.GONE);
                }
                if (putColony) {
                    putColonyByCp();
                    putColony = false;
                }
                break;
            case R.id.spColony:
                if (position == 0) {
                    ivDeleteColony.setVisibility(View.GONE);
                    idAddressSelected = 0;
                } else {
                    ivDeleteColony.setVisibility(View.VISIBLE);
                    SepoColony colonySelected = sepoColonyList.get(position);
                    idAddressSelected = colonySelected.get_id();
                    flErrorColony.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void fillMuniSpinner(int idState) {
        SepomexDatabaseHelper sepomexDatabaseHelper = SepomexDatabaseHelper.getHelper(getContext());
        sepoMuniList = sepomexDatabaseHelper.obtainAllMuni(idState);
        ArrayAdapter<SepoMuni> adapter = new SepoMuniAdapter(context, sepoMuniList);
        spMuni.setAdapter(adapter);
        spMuni.setSelection(0);
        spMuni.setEnabled(true);
    }

    private void fillColonySpinner(int idMuni, int idState) {
        SepomexDatabaseHelper sepomexDatabaseHelper = SepomexDatabaseHelper.getHelper(getContext());
        if (moreThanOneCp) {
            if (edtCp != null && edtCp.getText() != null)
                sepoColonyList = sepomexDatabaseHelper.searchColonyByCp(edtCp.getText().toString());
            moreThanOneCp = false;
            putColony = false;
        } else {
            sepoColonyList = sepomexDatabaseHelper.obtainAllColony(idMuni, idState);
        }
        ArrayAdapter<SepoColony> adapter = new SepoColonyAdapter(context, sepoColonyList);
        spColony.setAdapter(adapter);
        spColony.setSelection(0);
        spColony.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        String msg = "";
        switch (v.getId()) {
            case R.id.btnCancel:
                alert.dismiss();
                break;
            case R.id.btnAccept:
                if (idAddressSelected != 0) {
                    if (response != null)
                        response.onResponse(request, idAddressSelected);
                    alert.dismiss();
                } else {
                    if (sepoStatesList != null && sepoStatesList.size() > 0) {
                        SepoStates sepoStates = (SepoStates) spState.getSelectedItem();
                        if (sepoStates.get_id() == 0) {
                            flErrorState.setVisibility(View.VISIBLE);
                            msg = "Es necesario selecionar un estado.";
                        }
                    }
                    if (sepoMuniList != null && sepoMuniList.size() > 0) {
                        SepoMuni muni = (SepoMuni) spMuni.getSelectedItem();
                        if (muni.get_id() == 0) {
                            flErrorMuni.setVisibility(View.VISIBLE);
                            msg = "Es necesario selecionar un municipio.";
                        }
                    }
                    if (sepoColonyList != null && sepoColonyList.size() > 0) {
                        SepoColony colo = (SepoColony) spColony.getSelectedItem();
                        if (colo.get_id() == 0) {
                            flErrorColony.setVisibility(View.VISIBLE);
                            msg = "Es necesario selecionar un Fracc. / Cplonia / Rancheria / Etc.";
                        }
                    }
                    if (!msg.equalsIgnoreCase("")) {
                        CustomDialog.showMessage(getContext(), msg);
                    }
                }
                break;
            case R.id.ivDeleteState:
                ivDeleteState.setVisibility(View.GONE);
                spState.setSelection(0);
                if (sepoMuniList != null && sepoMuniList.size() > 0) {
                    spMuni.setEnabled(false);
                    spMuni.setSelection(0);
                }
                if (sepoColonyList != null && sepoColonyList.size() > 0) {
                    spColony.setEnabled(false);
                    spColony.setSelection(0);
                }
                break;
            case R.id.ivDeleteMuni:
                ivDeleteMuni.setVisibility(View.GONE);
                spMuni.setSelection(0);
                if (sepoColonyList != null && sepoColonyList.size() > 0) {
                    spColony.setEnabled(false);
                    spColony.setSelection(0);
                }
                break;
            case R.id.ivDeleteColony:
                ivDeleteColony.setVisibility(View.GONE);
                spColony.setSelection(0);
                idAddressSelected = 0;
                break;
            case R.id.ivDeleteCp:
                ivDeleteCp.setVisibility(View.GONE);
                if (isCpUsed) {
                    ivDeleteState.setVisibility(View.GONE);
                    if (sepoStatesList != null && sepoStatesList.size() > 0)
                        spState.setSelection(0);
                    if (sepoMuniList != null && sepoMuniList.size() > 0) {
                        spMuni.setEnabled(false);
                        spMuni.setSelection(0);
                    }
                    if (sepoColonyList != null && sepoColonyList.size() > 0) {
                        spColony.setEnabled(false);
                        spColony.setSelection(0);
                    }
                    idAddressSelected = 0;
                }
                isCpUsed = false;
                edtCp.setText("");
                break;
            case R.id.ivSearchCp:
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                isCpUsed = true;
                SepomexDatabaseHelper sepomexDatabaseHelper = SepomexDatabaseHelper.getHelper(getContext());
                if (edtCp != null && edtCp.getText() != null && edtCp.getText().toString().trim().length() > 0) {
                    sepoCP = sepomexDatabaseHelper.searchByCp(edtCp.getText().toString());
                    if (sepoCP != null && sepoCP.size() > 0) {
                        SepoCP sCpData = sepoCP.get(0);
                        if (sepoCP.size() == 1) {
                            moreThanOneCp = false;
                            idAddressSelected = sCpData.get_id();
                        } else {
                            idAddressSelected = 0;
                            moreThanOneCp = true;
                            msg = "Selecionar un Fracc. / Cplonia / Rancheria / Etc.";
                            CustomDialog.showMessage(getContext(), msg);
                        }
                        if (sepoStatesList != null) {
                            for (int i = 0; i < sepoStatesList.size(); i++) {
                                if (sCpData.getId_state() == sepoStatesList.get(i).get_id()) {
                                    spState.setSelection(i);
                                    putMuunicipality = true;
                                    break;
                                }
                            }
                        }
                    } else {
                        msg = "Código postal no encontrado, favor de validarlo";
                        CustomDialog.showMessage(getContext(), msg);
                    }
                }
                break;
        }
    }

    private void putMuunicipalityByCp() {
        if (sepoMuniList != null) {
            for (int i = 0; i < sepoMuniList.size(); i++) {
                if (sepoCP.get(0).getId_muni() == sepoMuniList.get(i).get_id()) {
                    spMuni.setSelection(i);
                    putColony = true;
                    break;
                }
            }
        }
    }

    private void putColonyByCp() {
        if (sepoColonyList != null && !moreThanOneCp) {
            for (int i = 0; i < sepoColonyList.size(); i++) {
                if (sepoCP.get(0).get_id() == sepoColonyList.get(i).get_id()) {
                    spColony.setSelection(i);
                    break;
                }
            }
        }
    }
}
