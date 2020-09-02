package mx.com.pendulum.olintareas.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.camera.CaptureVideoActivity;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.tareas.views.ViewFile_upload;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.recycler.ItemTouchHelperAdapter;
import mx.com.pendulum.utilities.recycler.ItemTouchHelperViewHolder;
import mx.com.pendulum.utilities.recycler.OnStartDragListener;

public class GalleryTakeVideoAdapter extends RecyclerView.Adapter<GalleryTakeVideoAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<String> items;
    private DisplayImageOptions options;
    private Context context;
    private boolean isGrid;
    private final OnStartDragListener mDragStartListener;
    private long startTime = 0;
    private long endTime = 0;
    private Interfaces.OnResponse response;
    private int request;

    public GalleryTakeVideoAdapter(Context context, OnStartDragListener dragStartListener, ArrayList<String> items, boolean isGrid) {
        this.context = context;
        this.items = items;
        this.isGrid = isGrid;
        options = Tools.getImageLoaderOptions(20, R.drawable.page);
        mDragStartListener = dragStartListener;
    }

    public void setOnResponseListener(Interfaces.OnResponse response, int request) {
        this.response = response;
        this.request = request;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int id;
        if (isGrid) {
            id = R.layout.row_take_photo_gallery_grid;
        } else {
            id = R.layout.row_take_photo_gallery;
        }
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(id, null);
        return new ViewHolder(itemLayoutView);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String fileName = items.get(position);
        if (!isGrid) {
            holder.ivDelete.setVisibility(View.VISIBLE);
        }
        File photoFile = new File(fileName);
        String image;
        if (photoFile.exists()) {
            image = Uri.fromFile(photoFile).toString();
            String ext = Tools.getFileExt(image).toUpperCase();
            switch (ext) {
                case "JPG":
                case "PNG":
                case "JPEG":
                case "AVI":
                    break;
                default:
                    image = "assets://" + "file_ok.png";
                    break;
            }
        } else image = "drawable://" + R.drawable.page;
        ImageLoader.getInstance().displayImage(image, holder.ivImage, options);
        holder.rowMaximize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (response != null) {
                    response.onResponse(request, items.get(holder.getAdapterPosition()));
                }
            }
        });
        holder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGrid) {
                    if (holder.ll.getVisibility() == View.VISIBLE) {
                        holder.ll.setVisibility(View.GONE);
                        holder.ivDelete.setVisibility(View.GONE);
                    } else {
                        holder.ll.setVisibility(View.VISIBLE);
                        holder.ivDelete.setVisibility(View.VISIBLE);
                        fadeOut(holder.ll);
                        fadeOut(holder.ivDelete);
                    }
                } else {
                    final String filePath = items.get(holder.getAdapterPosition());
                    String ext = Tools.getFileExt(filePath).toUpperCase();
                    if (ext.equalsIgnoreCase("AVI")) {
                        if (response != null)
                            response.onResponse(CaptureVideoActivity.VIDEO_PREVIEW, filePath);
                    } else
                        CustomDialog.dialogImage(context, new Interfaces.OnResponse() {
                            @Override
                            public void onResponse(int handlerCode, Object o) {
                                notifyItemChanged(holder.getAdapterPosition());
                            }
                        }, 123, items.get(holder.getAdapterPosition()));
                }
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(holder.getAdapterPosition());
            }
        });
        holder.rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate(90, holder.getAdapterPosition());
            }
        });
        holder.rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate(-90, holder.getAdapterPosition());
            }
        });
        holder.ivImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    startTime = ev.getEventTime();
                } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                    endTime = ev.getEventTime();
                } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                    endTime = 0;
                }
                if (endTime - startTime > 500) {
                    mDragStartListener.onStartDrag(holder);
                    startTime = 0;
                    endTime = 0;
                }
                return false;
            }
        });
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    private void rotate(int angle, int position) {
        final File file = new File(items.get(position));
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
        notifyItemChanged(position);
    }

    private void fadeOut(final View view) {
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

    public void remove(int position) {
        String str = items.get(position);
        File file = new File(str);
        String fileName = file.getName();
        String[] array = fileName.split(ViewFile_upload.SEPATATOR);
        int rowPosition = Integer.parseInt(array[2]);
        try {
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());
            if (response != null) {
                response.onResponse(request, rowPosition);
            }
        } catch (Exception e) {
            notifyDataSetChanged();
        }
        notifyDataSetChanged();
        if (file.exists()) {
            if (file.delete()) {
                Log.d("TAG -->", "Archivo Eliminado");
            }
        }
    }

    public void add(String str, RecyclerView rv) {

        items.add(str);
        notifyItemChanged(items.size());
        rv.getLayoutManager().scrollToPosition(items.size() - 1);
    }

    public void add(String str, int position) {
        items.add(position, str);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition == 9999 && toPosition == 9999) {
            notifyDataSetChanged();
            return true;
        }
        /*ArrayList<String> list = getItems();
        String from = list.get(fromPosition);
        String to = list.get(toPosition);
        String fromName = from.substring(from.lastIndexOf("/") + 1);
        String toName = to.substring(from.lastIndexOf("/") + 1);
        String[] arrayFrom = fromName.split(ViewFile_upload.SEPATATOR);
        String[] arrayTo = toName.split(ViewFile_upload.SEPATATOR);
        StringBuilder name = new StringBuilder(from.substring(0, from.lastIndexOf("/")));
        for (int i = 0; i < arrayFrom.length; i++) {
            if (i == 2) name.append(toPosition).append(ViewFile_upload.SEPATATOR);
            else if (i == (arrayFrom.length - 1)) name.append(arrayFrom[i]);
            else name.append(arrayFrom[i]).append(ViewFile_upload.SEPATATOR);
        }
        list.set(fromPosition, name.toString());
        name = new StringBuilder(to.substring(0, to.lastIndexOf("/")));
        for (int i = 0; i < arrayTo.length; i++) {
            if (i == 2) name.append(toPosition).append(ViewFile_upload.SEPATATOR);
            else if (i == (arrayTo.length - 1)) name.append(arrayTo[i]);
            else name.append(arrayTo[i]).append(ViewFile_upload.SEPATATOR);
        }
        list.set(toPosition, name.toString());
        items = list;
        Solo Faltaria renombrar los archivos xD*/
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public ImageView ivDelete;
        ImageView ivImage;
        LinearLayout ll;
        ImageButton rotateLeft;
        ImageButton rotateRight;
        ImageButton rowMaximize;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            ivImage = itemLayoutView.findViewById(R.id.rowIv);
            ivDelete = itemLayoutView.findViewById(R.id.rowDelete);
            ll = itemLayoutView.findViewById(R.id.rowGalleryll);
            rotateLeft = itemLayoutView.findViewById(R.id.rowGalleryTurnLeft);
            rotateRight = itemLayoutView.findViewById(R.id.rowGalleryTurnRight);
            rowMaximize = itemLayoutView.findViewById(R.id.rowMaximize);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}