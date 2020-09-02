package mx.com.pendulum.olintareas.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.tareasV2.NotaDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.SeguimientoTarea;
import mx.com.pendulum.olintareas.dto.tareasV2.TareaDTO;
import mx.com.pendulum.olintareas.ui.parents.FragmentParent;

public class FragmentInfoAll_ extends FragmentParent {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setProgressBarIndeterminateVisibility(true);
        //private static final String TAG = FragmentInfoAll_.class.getSimpleName();
        StringBuilder pageView = new StringBuilder();
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        String style = "<style>"
                + ".outer {position:relative}"
                + ".inner {overflow-x:scroll; overflow-y:visible; width:auto;}"
                + "</style>";
        pageView.append("<html><head>").append(style).append("</head><body>");
        pageView.append(getResumen());
        pageView.append("</body> </html>");
        webView.loadDataWithBaseURL(null, pageView.toString(), "text/html", "UTF-8", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_all, container, false);
        webView = view.findViewById(R.id.wb_info);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        ActionBar a = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        if (a != null){
//            a.setTitle("Resumen de actividades");
//        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.clear();
    }

    @Override
    public void setHasOptionsMenu(boolean hasMenu) {
        super.setHasOptionsMenu(true);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    private String getColorString(int resColor) {
        int greenColor = ContextCompat.getColor(getActivity(), resColor);
        String color = "#" + Integer.toHexString(greenColor).toUpperCase();
        return color.replace("#FF", "#");
    }

    public String getResumen() {
        StringBuilder resumen = new StringBuilder();
        UserDatabaseHelper helper = UserDatabaseHelper.getHelper(getActivity());
        String styleA = "style=\"background-color: " + getColorString(R.color.color_500) + "; color: white; padding: 8px; text-align: center\"";
        String styleB = "style=\"background-color: " + getColorString(R.color.color_100) + "; color: black; padding: 8px; text-align: center; white-space: nowrap \"";
        try {
            resumen.append("<p><h3>Resumen de actividades</h3></p>");
            resumen.append("<div id=\"sright\" style=\"float: right; cursor: pointer; margin-bottom: 5px; margin-right: 5px;\"><b> >>> </b></div>"
                    + "<div style=\"clear:both;width:100%;\">" + "<div class=\"outer\">" + "<div class=\"inner\">");
            resumen.append("<p><h4>Tareas Creadas</h4></p>");
            resumen.append("<table style=\"border-collapse: 1\"><tr>" + "<th ")
                    .append(styleA).append("> Credito/Juicio </th>").append("<th ")
                    .append(styleA).append("> Categoria </th>").append("<th ")
                    .append(styleA).append("> Sub Categoria </th>").append("<th ")
                    .append(styleA).append("> No. Seguimientos </th>").append("</tr>");
            String nextRoeBeg = "<tr>";
            String align = "<td " + styleB + ">";
            Dao<TareaDTO, Long> tareaDao = helper.getDao(TareaDTO.class);
            Dao<NotaDTO, Long> notDao = helper.getDao(NotaDTO.class);
            List<TareaDTO> allTsk = tareaDao.queryBuilder().where().eq(TareaDTO.COL_UPDATED, true).query();
            TareaDTO tarea;
            int delete = 0;
            for (int i = 0; i < allTsk.size(); i++) {
                tarea = allTsk.get(i);
                List<TareaDTO> creditTsk = tareaDao.queryBuilder().where().eq(TareaDTO.COL_CREDIT_NUMBER, tarea.getCredito()).query();
                for (TareaDTO tsk : creditTsk) {
                    resumen.append(nextRoeBeg);
                    List<NotaDTO> notasTsk = notDao.queryBuilder().where().eq(NotaDTO.COL_ID_TAREA, tarea.get_id()).and().eq(NotaDTO.COL_UPDATED, true).query();
                    resumen.append(align).append(tsk.getCredito().isEmpty() ? "n/a" : tsk.getCredito()).append("</td>")
                            .append(align).append(tsk.getAsunto().isEmpty() ? "n/a" : tsk.getAsunto()).append("</td>")
                            .append(align).append(tsk.getSubClasifica().isEmpty() ? "n/a" : tsk.getSubClasifica()).append("</td>")
                            .append(align).append(notasTsk != null ? notasTsk.size() : 0).append("</td>").append("</tr>");
                }
                for (int j = 0; j < allTsk.size(); j++) {
                    if (allTsk.get(j).getCredito().equalsIgnoreCase(tarea.getCredito())) {
                        allTsk.remove(j);
                        j--;
                        delete++;
                    }
                }
                if (delete > 0) {
                    i--;
                    delete = 0;
                }
            }
            resumen.append("</table></div></div><br>");
            resumen.append("<div id=\"sright\" style=\"float: right; cursor: pointer; margin-bottom: 5px; margin-right: 5px;\"><b> >>> </b></div>"
                    + "<div style=\"clear:both;width:100%;\">" + "<div class=\"outer\">" + "<div class=\"inner\">");
            resumen.append("<p><h4>Tareas Asignadas</h4></p>");
            resumen.append("<table style=\"border-collapse: 1\"><tr>" + "<th ")
                    .append(styleA).append("> Credito/Juicio </th>").append("<th ")
                    .append(styleA).append("> Categoria </th>").append("<th ")
                    .append(styleA).append("> Sub Categoria </th>").append("<th ")
                    .append(styleA).append("> No. Seguimientos </th>").append("</tr>");
            align = "<td " + styleB + ">";
            String nextRowEnd = "</tr>";
            Dao<SeguimientoTarea, Long> segDao = helper.getDao(SeguimientoTarea.class);
            List<SeguimientoTarea> allTskInternet = segDao.queryBuilder().query();
            SeguimientoTarea tskInternet;
            delete = 0;
            for (int i = 0; i < allTskInternet.size(); i++) {
                tskInternet = allTskInternet.get(i);
                List<NotaDTO> notasTsk = notDao.queryBuilder().where().eq(NotaDTO.COL_ID_TAREA, tskInternet.getIdtarea()).and().eq(NotaDTO.COL_UPDATED, true).query();
                resumen.append(nextRoeBeg)
                        .append(align).append(tskInternet.getCredito().isEmpty() ? "n/a" : tskInternet.getCredito()).append("</td>")
                        .append(align).append(tskInternet.getAsunto().isEmpty() ? "n/a" : tskInternet.getAsunto()).append("</td>")
                        .append(align).append(tskInternet.getSubClasifica() != null ? (tskInternet.getSubClasifica().isEmpty() ? "n/a" : tskInternet.getSubClasifica()) : "n/a").append("</td>")
                        .append(align).append(notasTsk != null ? notasTsk.size() : 0).append("</td>").append("</tr>");
                for (int j = 0; j < allTsk.size(); j++) {
                    if (allTsk.get(j).getCredito().equalsIgnoreCase(tskInternet.getCredito())) {
                        allTsk.remove(j);
                        j--;
                        delete++;
                    }
                }
                if (delete > 0) {
                    i--;
                    delete = 0;
                }
                resumen.append(nextRowEnd);
            }
            resumen.append("</table></div></div><br>");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }
        return resumen.toString();
    }
}
