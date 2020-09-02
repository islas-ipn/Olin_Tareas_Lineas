package mx.com.pendulum.olintareas.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import mx.com.pendulum.olintareas.R;

public class ProgressDialogFragment extends DialogFragment {
    public static final String TAG = ProgressDialogFragment.class
            .getSimpleName();

    private static final String DESCRIPTION = "description";

    private String description = null;


    public static ProgressDialogFragment newInstance(String description) {
        Bundle bundle = new Bundle();
        bundle.putString(DESCRIPTION, description);


        ProgressDialogFragment dialog = new ProgressDialogFragment();

        dialog.setArguments(bundle);
        dialog.setCancelable(false);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.OlinDialogWindow);

        description = getArguments().getString(DESCRIPTION);
    }
//
//    @Override
//    public void onDestroy() {
//        view = null;
//        super.onDestroy();
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog thisDialog = super.onCreateDialog(savedInstanceState);
        thisDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH
                        && event.getRepeatCount() == 0) {
                    return true; // Pretend we processed it
                }
                return false; // Any other keys are still processed as normal
            }
        });
        return thisDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_progress, container, false);
        if (description != null)
            ((TextView) view.findViewById(R.id.dlgProgressDescription))
                    .setText(description);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH
                        && event.getRepeatCount() == 0) {
                    return true; // Pretend we processed itEntryPointActivity
                }
                return false; // Any other keys are still processed as normal
            }
        });

        ProgressBar pb = view.findViewById(R.id.dlgProgressBar2);
        LinearLayout ll = view.findViewById(R.id.llPercent);
        pb.setVisibility(View.GONE);
        ll.setVisibility(View.GONE);

        return view;
    }


    @SuppressWarnings("ConstantConditions")
    public void setProgress(int progress, String description) {

        try {
            ProgressBar pb = getView().findViewById(R.id.dlgProgressBar2);
            LinearLayout ll = getView().findViewById(R.id.llPercent);
            TextView percent = getView().findViewById(R.id.percent);
            TextView tvDescription = getView().findViewById(R.id.description);
            pb.setVisibility(View.VISIBLE);
            ll.setVisibility(View.VISIBLE);
            pb.setProgress(progress);
            String str = progress + " %";
            percent.setText(str);
            if (!description.equals(""))
                tvDescription.setText(description);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
