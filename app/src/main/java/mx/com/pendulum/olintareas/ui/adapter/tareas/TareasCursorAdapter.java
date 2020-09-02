package mx.com.pendulum.olintareas.ui.adapter.tareas;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;

import mx.com.pendulum.utilities.Tools;

import static mx.com.pendulum.olintareas.Properties.SD_CARD_IMAGES_DIR;

public class TareasCursorAdapter  extends SimpleCursorAdapter implements View.OnClickListener, View.OnLongClickListener {

    private AbsListView alv;
    private boolean isWrapToContent;
    private int rowSize;
    private Context context;

    public TareasCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
        TareasAdapter();
    }

    public void TareasAdapter() {

    }

    public TareasCursorAdapter(Context context, int layout) {
        super(context, layout, null, new String[]{}, new int[]{}, 0);
        TareasAdapter();
    }


    public void wrapToContent(int rowSize, AbsListView alv) {
        isWrapToContent = true;
        this.rowSize = rowSize;
        this.alv = alv;
    }

    @Override
    public Cursor swapCursor(Cursor c) {

        if (alv != null && isWrapToContent) {
            try {

                int size;
                if (c != null) {
                    size = c.getCount();
                } else {
                    size = 0;
                }
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) alv.getLayoutParams();

                if (alv instanceof GridView) {
                    int numColumns = ((GridView) alv).getNumColumns();
                    size = (int) (size / numColumns);
                }

                params.height = Tools.dpToPx(context, rowSize) * size;
                alv.setLayoutParams(params);

            } catch (Exception e) {
                return super.swapCursor(null);
            }
        }


        return super.swapCursor(c);
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

    public int getPositionByName(String name, String columnName) {
        System.out.println("name " + name + "   comuno name " + columnName);
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

    public Context getContext() {
        return context;
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public View getViewByPosition(int pos) {
        final int firstListItemPosition = alv.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + alv.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return alv.getAdapter().getView(pos, null, alv);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return alv.getChildAt(childIndex);
        }
    }


    @Override
    public void onClick(View v) {

    }


    @Override
    public boolean onLongClick(View v) {
        return false;
    }


    public void fadeOut(final View view) {
        view.postDelayed(new Runnable() {
            public void run() {
                if (view.getVisibility() == View.VISIBLE) {

                    AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.0f);
                    animation1.setDuration(500);
                    animation1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            view.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    view.startAnimation(animation1);

                }

            }
        }, 3000);
    }

    public void rotate(int angle, String fileName, int position, GridView gv) {
        final File file = new File(SD_CARD_IMAGES_DIR, fileName);
        ExifInterface exif;
        try {
            exif = new ExifInterface(file.getAbsolutePath());

            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);


            switch (exifOrientation) {

                case 1:
                    if (angle > 0)
                        exifOrientation = 6;
                    else
                        exifOrientation = 8;

                    break;
                case 3:
                    if (angle > 0)
                        exifOrientation = 8;
                    else
                        exifOrientation = 6;

                    break;

                case 6:
                    if (angle > 0)
                        exifOrientation = 3;
                    else
                        exifOrientation = 1;

                    break;
                case 8:
                    if (angle > 0)
                        exifOrientation = 1;
                    else
                        exifOrientation = 3;

                    break;
            }

            exif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation + "");
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //reloads only one item
        int visiblePosition = gv.getFirstVisiblePosition();
        View view = gv.getChildAt(position - visiblePosition);
        gv.getAdapter().getView(position, view, gv);

    }


    public static class Viewholder {
        public View view1;
        public View view2;
        public View view3;
        public View view4;
        public View view5;
    }
}