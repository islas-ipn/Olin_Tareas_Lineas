package mx.com.pendulum.olintareas.db;

import android.database.Cursor;
import androidx.core.content.ContextCompat;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.config.util.ContextApplication;

public class UtilQuery {

    public static final String QUERY_LOGIN_DATE = "SELECT User_Session.fecha_login , User_Data.nombre, User_Data.username ,User_Session.session_id  FROM User_Session , User_Data";


    public static final String QUERY_HEADER_RESUMEN_ACTIVITYS_ALL =
            "SELECT\n" +
                    "		credit,\n" +
                    "		ifnull(num_cierre, 0) num_cierre,\n" +
                    "		ifnull(num_gest, 0) num_gest,\n" +
                    "		ifnull(num_gua, 0) num_gua,\n" +
                    "		ifnull(num_lw, 0) num_lw,\n" +
                    "		ifnull(num_eml, 0) num_eml,\n" +
                    "		ifnull(num_tel, 0) num_tel,\n" +
                    "		ifnull(num_dir, 0) num_dir,\n" +
                    "		ifnull(num_schem, 0) num_schem,\n" +
                    "		ifnull(num_task, 0) num_task,\n" +
                    "		ifnull(num_note, 0) num_note,\n" +
                    "		ifnull(num_photo, 0) num_photo,\n" +
                    "		ifnull(num_negociaciones, 0) num_negociaciones\n" +
                    "		FROM\n" +
                    "		(\n" +
                    "			SELECT\n" +
                    "				credit.credit,\n" +
                    "				num_cierre,\n" +
                    "				num_gest,\n" +
                    "				num_gua,\n" +
                    "				num_lw,\n" +
                    "				num_eml,\n" +
                    "				num_tel,\n" +
                    "				num_dir,\n" +
                    "				num_schem,\n" +
                    "				num_task,\n" +
                    "				num_note,\n" +
                    "				num_photo,\n" +
                    "				num_negociaciones\n" +
                    "			FROM\n" +
                    "				credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					CloseProbability.credit,\n" +
                    "					count(CloseProbability.credit) num_cierre\n" +
                    "				FROM\n" +
                    "					CloseProbability\n" +
                    "				WHERE CloseProbability.updated=1\n" +
                    "				GROUP BY\n" +
                    "					CloseProbability.credit\n" +
                    "			) AS num_cierre ON credit.credit = num_cierre.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Collection_Management.credit,\n" +
                    "					count(Collection_Management.updated) num_gest\n" +
                    "				FROM\n" +
                    "					Collection_Management\n" +
                    "				WHERE\n" +
                    "					Collection_Management.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Collection_Management.credit\n" +
                    "			) AS num_gest ON credit.credit = num_gest.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Guarantee.credit,\n" +
                    "					count(Guarantee.updated) num_gua\n" +
                    "				FROM\n" +
                    "					Guarantee\n" +
                    "				WHERE\n" +
                    "					Guarantee.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Guarantee.credit\n" +
                    "			) AS num_gua ON credit.credit = num_gua.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Lw.credit,\n" +
                    "					count(Lw.credit) num_lw\n" +
                    "				FROM\n" +
                    "					Lw\n" +
                    "				WHERE \n" +
                    "					Lw.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Lw.credit\n" +
                    "			) AS num_lw ON credit.credit = num_lw.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Email.credit,\n" +
                    "					count(Email.updated) num_eml\n" +
                    "				FROM\n" +
                    "					Email\n" +
                    "				WHERE\n" +
                    "					Email.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Email.credit\n" +
                    "			) AS num_eml ON credit.credit = num_eml.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Telephone.credit,\n" +
                    "					count(Telephone.updated) num_tel\n" +
                    "				FROM\n" +
                    "					Telephone\n" +
                    "				WHERE\n" +
                    "					Telephone.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Telephone.credit\n" +
                    "			) AS num_tel ON credit.credit = num_tel.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Address_Guarantee.credit,\n" +
                    "					count(Address_Guarantee.updated) num_dir\n" +
                    "				FROM\n" +
                    "					Address_Guarantee\n" +
                    "				WHERE\n" +
                    "					Address_Guarantee.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Address_Guarantee.credit\n" +
                    "			) AS num_dir ON credit.credit = num_dir.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Collection_Management.credit,\n" +
                    "					count(Collection_Management.updated) num_schem\n" +
                    "				FROM\n" +
                    "					Collection_Management\n" +
                    "				WHERE\n" +
                    "					Collection_Management.updated = 1\n" +
                    "				AND\n" +
                    "					Collection_Management.codigo_accion = 'OE'\n" +
                    "				GROUP BY\n" +
                    "					Collection_Management.credit\n" +
                    "			) AS num_schem ON credit.credit = num_schem.credit\n" +
                    "			LEFT JOIN(\n" +
                    "				SELECT\n" +
                    "					Task.credito credit,\n" +
                    "					count(Task.updated) num_task\n" +
                    "				FROM\n" +
                    "					Tsk_TareaDTO Task\n" +
                    "				WHERE\n" +
                    "					Task.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Task.credito\n" +
                    "			) AS num_task ON credit.credit = num_task.credit\n" +
                    "			LEFT JOIN(\n" +
                    "				SELECT\n" +
                    "					Note.credito credit,\n" +
                    "					count(Note.updated) num_note\n" +
                    "				FROM\n" +
                    "					Tsk_NotaDTO Note\n" +
                    "				WHERE\n" +
                    "					Note.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Note.credito\n" +
                    "			) AS num_note ON credit.credit = num_note.credit\n" +
                    "			LEFT JOIN( \n" +
                    "				SELECT \n" +
                    "					fo.credit,\n" +
                    "					count(fo.credit )  num_photo\n" +
                    "				FROM \n" +
                    "					Lw_Facilidades fa\n" +
                    "				inner join \n" +
                    "					Lw_Fotos_Facilidades fo  \n" +
                    "				on \n" +
                    "					fa.id_fotos_credito = fo.id_fotos_credito \n" +
                    "				and \n" +
                    "					fa.updated = 1\n" +
                    "				and \n" +
                    "					fo.updated = 1\n" +
                    "				GROUP BY \n" +
                    "					fo.credit   \n" +
                    "			) AS num_photo ON credit.credit = num_photo.credit\n" +
                    "			LEFT JOIN(\n" +
                    "                SELECT \n" +
                    "                AmortizacionOut.cuenta,\n" +
                    "                        count(AmortizacionOut.updated) num_negociaciones\n" +
                    "                        FROM \n" +
                    "                        AmortizacionOut\n" +
                    "                        WHERE AmortizacionOut.updated = 1\n" +
                    "                        Group By AmortizacionOut.cuenta\n" +
                    "			) AS num_negociaciones ON credit.credit = num_negociaciones.cuenta\n" +
                    "		) AS tempo \n" +
                    "		WHERE num_cierre NOTNULL\n" +
                    "		OR num_gest NOTNULL\n" +
                    "		OR num_eml NOTNULL\n" +
                    "		OR num_gua NOTNULL\n" +
                    "		OR num_lw NOTNULL\n" +
                    "		OR num_tel NOTNULL\n" +
                    "		OR num_dir NOTNULL\n" +
                    "		OR num_schem NOTNULL\n" +
                    "		OR num_task NOTNULL\n" +
                    "		OR num_note NOTNULL\n" +
                    "		OR num_photo NOTNULL\n" +
                    "        OR num_negociaciones NOTNULL\n" +
                    "		union\n" +
                    "		SELECT\n" +
                    "		credit,\n" +
                    "		ifnull(num_cierre, 0) num_cierre,\n" +
                    "		ifnull(num_gest, 0) num_gest,\n" +
                    "		ifnull(num_gua, 0) num_gua,\n" +
                    "		ifnull(num_lw, 0) num_lw,\n" +
                    "		ifnull(num_eml, 0) num_eml,\n" +
                    "		ifnull(num_tel, 0) num_tel,\n" +
                    "		ifnull(num_dir, 0) num_dir,\n" +
                    "		ifnull(num_schem, 0) num_schem,\n" +
                    "		ifnull(num_task, 0) num_task,\n" +
                    "		ifnull(num_note, 0) num_note,\n" +
                    "		ifnull(num_photo, 0) num_photo,\n" +
                    "		ifnull(num_negociaciones,0) num_negociaciones\n" +
                    "		FROM\n" +
                    "		(\n" +
                    "			SELECT\n" +
                    "				task.credit,\n" +
                    "				num_cierre,\n" +
                    "				num_gest,\n" +
                    "				num_gua,\n" +
                    "				num_lw,\n" +
                    "				num_eml,\n" +
                    "				num_tel,\n" +
                    "				num_dir,\n" +
                    "				num_schem,\n" +
                    "				num_task,\n" +
                    "				num_note,\n" +
                    "				num_photo,\n" +
                    "				num_negociaciones\n" +
                    "			FROM\n" +
                    "				Tsk_TareaDTO task\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					CloseProbability.credit,\n" +
                    "					count(CloseProbability.credit) num_cierre\n" +
                    "				FROM\n" +
                    "					CloseProbability\n" +
                    "				WHERE CloseProbability.updated=1\n" +
                    "				GROUP BY\n" +
                    "					CloseProbability.credit\n" +
                    "			) AS num_cierre ON task.credit = num_cierre.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Collection_Management.credit,\n" +
                    "					count(Collection_Management.updated) num_gest\n" +
                    "				FROM\n" +
                    "					Collection_Management\n" +
                    "				WHERE\n" +
                    "					Collection_Management.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Collection_Management.credit\n" +
                    "			) AS num_gest ON task.credit = num_gest.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Guarantee.credit,\n" +
                    "					count(Guarantee.updated) num_gua\n" +
                    "				FROM\n" +
                    "					Guarantee\n" +
                    "				WHERE\n" +
                    "					Guarantee.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Guarantee.credit\n" +
                    "			) AS num_gua ON task.credit = num_gua.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Lw.credit,\n" +
                    "					count(Lw.credit) num_lw\n" +
                    "				FROM\n" +
                    "					Lw\n" +
                    "				WHERE \n" +
                    "					Lw.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Lw.credit\n" +
                    "			) AS num_lw ON task.credit = num_lw.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Email.credit,\n" +
                    "					count(Email.updated) num_eml\n" +
                    "				FROM\n" +
                    "					Email\n" +
                    "				WHERE\n" +
                    "					Email.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Email.credit\n" +
                    "			) AS num_eml ON task.credit = num_eml.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Telephone.credit,\n" +
                    "					count(Telephone.updated) num_tel\n" +
                    "				FROM\n" +
                    "					Telephone\n" +
                    "				WHERE\n" +
                    "					Telephone.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Telephone.credit\n" +
                    "			) AS num_tel ON task.credit = num_tel.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Address_Guarantee.credit,\n" +
                    "					count(Address_Guarantee.updated) num_dir\n" +
                    "				FROM\n" +
                    "					Address_Guarantee\n" +
                    "				WHERE\n" +
                    "					Address_Guarantee.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Address_Guarantee.credit\n" +
                    "			) AS num_dir ON task.credit = num_dir.credit\n" +
                    "			LEFT JOIN (\n" +
                    "				SELECT\n" +
                    "					Collection_Management.credit,\n" +
                    "					count(Collection_Management.updated) num_schem\n" +
                    "				FROM\n" +
                    "					Collection_Management\n" +
                    "				WHERE\n" +
                    "					Collection_Management.updated = 1\n" +
                    "				AND\n" +
                    "					Collection_Management.codigo_accion = 'OE'\n" +
                    "				GROUP BY\n" +
                    "					Collection_Management.credit\n" +
                    "			) AS num_schem ON task.credit = num_schem.credit\n" +
                    "			LEFT JOIN(\n" +
                    "				SELECT\n" +
                    "					Task.credit,\n" +
                    "					count(Task.updated) num_task\n" +
                    "				FROM\n" +
                    "					Tsk_TareaDTO Task\n" +
                    "				WHERE\n" +
                    "					Task.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Task.credit\n" +
                    "			) AS num_task ON task.credit = num_task.credit\n" +
                    "			LEFT JOIN(\n" +
                    "				SELECT\n" +
                    "					Note.credit,\n" +
                    "					count(Note.updated) num_note\n" +
                    "				FROM\n" +
                    "					Tsk_NotaDTO Note\n" +
                    "				WHERE\n" +
                    "					Note.updated = 1\n" +
                    "				GROUP BY\n" +
                    "					Note.credit\n" +
                    "			) AS num_note ON task.credit = num_note.credit\n" +
                    "			LEFT JOIN( \n" +
                    "				SELECT \n" +
                    "					fo.credit,\n" +
                    "					count(fo.credit )  num_photo\n" +
                    "				FROM \n" +
                    "					Lw_Facilidades fa\n" +
                    "				inner join \n" +
                    "					Lw_Fotos_Facilidades fo  \n" +
                    "				on \n" +
                    "					fa.id_fotos_credito = fo.id_fotos_credito \n" +
                    "				and \n" +
                    "					fa.updated = 1\n" +
                    "				and \n" +
                    "					fo.updated = 1\n" +
                    "				GROUP BY \n" +
                    "					fo.credit   \n" +
                    "			) AS num_photo ON task.credit = num_photo.credit\n" +
                    "			LEFT JOIN(\n" +
                    "                SELECT \n" +
                    "                AmortizacionOut.cuenta,\n" +
                    "                        count(AmortizacionOut.updated) num_negociaciones\n" +
                    "                        FROM \n" +
                    "                        AmortizacionOut\n" +
                    "                        WHERE AmortizacionOut.updated = 1\n" +
                    "                        Group By AmortizacionOut.cuenta\n" +
                    "			) AS num_negociaciones ON task.credit = num_negociaciones.cuenta\n" +
                    "		) AS tempo \n" +
                    "		WHERE num_cierre NOTNULL\n" +
                    "		OR num_gest NOTNULL\n" +
                    "		OR num_eml NOTNULL\n" +
                    "		OR num_gua NOTNULL\n" +
                    "		OR num_lw NOTNULL\n" +
                    "		OR num_tel NOTNULL\n" +
                    "		OR num_dir NOTNULL\n" +
                    "		OR num_schem NOTNULL\n" +
                    "		OR num_task NOTNULL\n" +
                    "		OR num_note NOTNULL\n" +
                    "		OR num_photo NOTNULL\n" +
                    "		OR num_negociaciones NOTNULL";

    public static final String getLogin_date() {
        StringBuilder resumen = new StringBuilder();
        UserDatabaseHelper userDatabaseHelper = UserDatabaseHelper
                .getHelper(ContextApplication.getAppContext());
        String styleA = "style=\"background-color: " + getColorString(R.color.color_500) + "; color: white; padding: 8px; text-align: center\"";
        String styleB = "style=\"background-color: " + getColorString(R.color.color_100) + "; color: black; padding: 8px; text-align: center; white-space: nowrap \"";
        Cursor cursor = null;
        try {
            try {
                cursor = userDatabaseHelper.getReadableDatabase().rawQuery(QUERY_LOGIN_DATE, null);

                if (cursor.moveToFirst()) {

                    String tmp = cursor.getString(3);
                    resumen.append("<table  style=\"border-collapse: 1\">");
                    resumen.append("<tr><th " + styleA + "> Sesión </th>"
                            + "<td " + styleB + ">" + cursor.getString(3)
                            + "<br></td></tr>");
                    resumen.append("<tr><th " + styleA + "> Usuario </th>"
                            + "<td " + styleB + ">" + cursor.getString(2) + "<br></td></tr>");
                    resumen.append("<tr><th " + styleA + "> Nombre </th>"
                            + "<td " + styleB + ">" + cursor.getString(1) + "<br></td></tr>");
                    resumen.append("<tr><th " + styleA + "> Fecha de login </th>"
                            + "<td " + styleB + ">" + cursor.getString(0) + "<br></td></tr>");
                    resumen.append("<tr><th " + styleA + "> Fecha de inicio de logout </th>"  //Fecha de inicio de logout
                            + "<td " + styleB + ">" + getDateLogOut() + "</td> </tr>");
                    resumen.append("</table>");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } finally {
            userDatabaseHelper.close();
            userDatabaseHelper = null;
        }
        return resumen.toString();
    }

    private static String getDateLogOut() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(System.currentTimeMillis()));
    }

    private static String getColorString(int resColor) {
        int greenColor = ContextCompat.getColor(ContextApplication.getAppContext(), resColor);
        String color = "#" + Integer.toHexString(greenColor).toUpperCase();
        return color.replace("#FF", "#");
    }
}
