package mx.com.pendulum.olintareas.ui.dialog.tareas;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.views.CustomCheckedTextView;

public class FilesDialog {
    private Context context;


    private Options selectedOption;
    private int selectedOptionPos = -1;
    private AlertDialog alert;

    public FilesDialog(final Context context, final Interfaces.OnResponse<Options> response, final int request, List<Options> options) {

        Tools.hideSoftKeyboard((Activity) context);
        this.context = context;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = View.inflate(context, R.layout.dialog_dynamic_form_list, null);
        ListView list = (ListView) view.findViewById(R.id.listView);

        //  String[] names = new String[list.];


        final ArrayAdapter<Options> adapter = new ArrayAdapter<Options>(context, R.layout.simple_list_item_single_choice, options) {
            @NonNull
            @Override
            public View getView(int pos, @Nullable View view, @NonNull ViewGroup parent) {
                Options op = getItem(pos);
                view = super.getView(pos, view, parent);
                ((CustomCheckedTextView) view).setText(op.getOption() == null ? "" : op.getOption());
                ((CustomCheckedTextView) view).setSingleLine(true);


                if (selectedOptionPos == pos) {
                    ((CustomCheckedTextView) view).setChecked(true);
                } else {
                    ((CustomCheckedTextView) view).setChecked(false);
                }
                return view;
            }

        };

        list.setAdapter(adapter);

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) list.getLayoutParams();

        int margin = 150;
        mlp.setMargins(Tools.pxToDp(context, margin), 0, Tools.pxToDp(context, margin), 0);
        list.setLayoutParams(mlp);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                int count = parent.getChildCount();

                selectedOptionPos = position;
                selectedOption = (Options) parent.getItemAtPosition(position);
                if (selectedOption.getId() == -1) {
                    //       selectedOptionPos = -1;
                    selectedOption = null;
                }
                adapter.notifyDataSetChanged();

                ((CustomCheckedTextView) view).setChecked(true);

                if (response != null)
                    response.onResponse(request, selectedOption);
                alert.dismiss();

            }
        });


        builder.setView(view);
        builder.setTitle(context.getString(R.string.selecione));

//        builder.setPositiveButton("Aceptar", null);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (response != null)
                    response.onResponse(request, null);
                dialog.dismiss();
            }
        });

        alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Typeface font = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.font_bold));
                AlertDialog alertDialog = (AlertDialog) dialog;
                Button button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                button.setTypeface(font);


                button = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                button.setTypeface(font);

                int textViewId = alertDialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
                TextView tv = (TextView) alertDialog.findViewById(textViewId);
                tv.setTextColor(ContextCompat.getColor(context, R.color.color_800));
                tv.setTypeface(font);

//                button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
//                button.setTypeface(font);
//                button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (selectedOption == null) {
//                            Toast.makeText(getContext(), "Por favor seleccione una opcion", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        if (response != null)
//                            response.onResponse(request, selectedOption);
//                        dialog.dismiss();
//
//                    }
//                });
            }
        });


    }

    private Context getContext() {
        return context;
    }

    public void showDialog() {
        alert.show();
    }

}
