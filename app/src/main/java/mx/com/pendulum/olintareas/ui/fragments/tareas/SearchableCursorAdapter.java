package mx.com.pendulum.olintareas.ui.fragments.tareas;


import android.content.Context;
import android.database.Cursor;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

public class SearchableCursorAdapter extends SimpleCursorAdapter {

    public SearchableCursorAdapter(Context context, int layout, Cursor c,
                                   String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    public int getPositionByName(String name, String columnName) {
        int position = -1;
        boolean found = false;
        Cursor cursor = getCursor();
        if (name != null && columnName != null && cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                int columnIndex = cursor.getColumnIndex(columnName);
                String code = null;
                while (!cursor.isAfterLast()) {
                    position++;
                    code = cursor.getString(columnIndex);
                    if (code.equals(name)) {
                        found = true;
                        break;
                    } else
                        cursor.moveToNext();
                }
            }
        }
        return found ? position : 0;
    }

    public int getPositionById(Long id, String columnName) {
        int position = -1;
        boolean found = false;
        Cursor cursor = getCursor();
        if (id != null && columnName != null && cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                int columnIndex = cursor.getColumnIndex(columnName);
                Long currId = null;
                while (!cursor.isAfterLast()) {
                    position++;
                    currId = cursor.getLong(columnIndex);
                    if (currId.equals(id)) {
                        found = true;
                        break;
                    } else
                        cursor.moveToNext();
                }
            }
        }
        return found ? position : 0;
    }


}
