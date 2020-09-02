package mx.com.pendulum.olintareas.ui.fragments.alertdialogs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.developer.DialogDeveloperPassword;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.sync.constants.IUrls;
import mx.com.pendulum.utilities.Util;

public class InfoDialogFragment extends DialogFragment implements
        View.OnClickListener {
    private static final String USER = "user";
    private static final String NAME = "name";
    private static final String LOGIN = "login";
    private Interfaces.OnResponse response;
    private int request;
    private String user = null;
    private String name = null;
    private Long login = null;
    private int counter = 10;

    public static InfoDialogFragment newInstance(String user, String name,
                                                 Date login) {
        Bundle bundle = new Bundle();
        bundle.putString(USER, user);
        bundle.putString(NAME, name);
        bundle.putLong(LOGIN, login.getTime());
        InfoDialogFragment dialog = new InfoDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.OlinDialogWindow);
        if (getArguments() != null) {
            user = getArguments().getString(USER);
            name = getArguments().getString(NAME);
            login = getArguments().getLong(LOGIN);
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_info, container, false);
        TextView textView = view.findViewById(R.id.infoUsernameTextView);
        String usr = name + " - " + user.toUpperCase();
        textView.setText(usr);
        TextView infoVersionIP = view.findViewById(R.id.infoVersionIP);
        int color;
        if (login != null) {
            Date now = new Date();
            Date loginDate = new Date(login);
            int days = (int) ((now.getTime() - loginDate.getTime()) / (1000 * 60 * 60 * 24));
            if (days > 5)
                color = R.color.Red;
            else if (days > 2)
                color = R.color.Yellow;
            else
                color = R.color.Green;
            textView = view.findViewById(R.id.infoLoginDateTextView);
            textView.setText(new SimpleDateFormat().format(loginDate));
            if (getActivity() != null)
                textView.setTextColor(getActivity().getResources().getColor(color, null));
        }
        String urlWS = IUrls.BASE_URL_SYTEM + "\n";
        infoVersionIP.setText(urlWS);
        textView = view.findViewById(R.id.infoVersionTextView);
        if (getActivity() != null)
            textView.setText(Util.getCurrentApkVersion(getActivity()));
        textView = view.findViewById(R.id.infoEnvironmentTextView);
        if (!Properties.isReleaseApp) {
            textView.setText(getString(R.string.development));
            color = R.color.Red;
            textView.setTextColor(getResources().getColor(color));
        }
        else {
            textView.setText(getString(R.string.releaseapk));
            color = R.color.Green;
            textView.setTextColor(getResources().getColor(color));
        }
        Button buttonUnlock = view.findViewById(R.id.buttonDismiss);
        buttonUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
            }
        });
        view.findViewById(R.id.infoCompanyNameTextView).setOnClickListener(this);
        return view;
    }

    public void addResponse(Interfaces.OnResponse response, int request) {
        this.response = response;
        this.request = request;
    }

    @Override
    public void onClick(View v) {
        counter--;
        if (counter == 0) {
            new DialogDeveloperPassword(getActivity(), response, request).showDisclaimerPassword();
            dismissAllowingStateLoss();
        }
    }
}
