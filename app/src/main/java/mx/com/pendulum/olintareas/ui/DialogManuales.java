package mx.com.pendulum.olintareas.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.complementos.ManualDTO;
import mx.com.pendulum.olintareas.dto.complementos.ManualTypeDTO;
import mx.com.pendulum.olintareas.ui.adapter.ManualExpandableAdapter;
import mx.com.pendulum.utilities.http.DownloadServicePDF;

public class DialogManuales extends DialogFragment implements ExpandableListView.OnChildClickListener {

    public static final String DATA = "dataKey";
    private ArrayList<ManualTypeDTO> list;

    public static DialogManuales newInstance(ArrayList<ManualTypeDTO> list) {
        DialogManuales frag = new DialogManuales();
        Bundle args = new Bundle();
        args.putSerializable(DATA, list);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        list = (ArrayList<ManualTypeDTO>) getArguments().getSerializable(DATA);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_message_ok_box, null);

        ManualExpandableAdapter adapter = new ManualExpandableAdapter(getActivity(), list);

        ExpandableListView listView = view.findViewById(R.id.expandManual);
        listView.setAdapter(adapter);
        listView.setOnChildClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder
                .setMessage("Selecciona.")
                .setView(view)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
        Dialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));


        return dialog;

    }


    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

//        Toast.makeText(getActivity(), list.get(groupPosition).getManualList().get(childPosition).getNombre_manual(), Toast.LENGTH_SHORT).show();

        ManualDTO item = list.get(groupPosition).getManualList().get(childPosition);

//        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(item.getPublic_path()));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setPackage("com.android.chrome");
//        try {
//            getActivity().startActivity(intent);
//        } catch (ActivityNotFoundException ex) {
//            // Chrome browser presumably not installed so allow user to choose instead
//            intent.setPackage(null);
//            getActivity().startActivity(intent);
//        }

        Intent intent = new Intent(getActivity(), DownloadServicePDF.class);
        intent.putExtra("url", item.getPublic_path());
        intent.putExtra("nombre", item.getNombre_manual());
        getActivity().startService(intent);


        dismiss();

        return false;
    }
}
