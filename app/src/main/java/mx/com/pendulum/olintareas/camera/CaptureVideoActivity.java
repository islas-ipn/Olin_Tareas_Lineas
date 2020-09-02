package mx.com.pendulum.olintareas.camera;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.adapter.GalleryTakePhotoAdapter;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.recycler.OnStartDragListener;
import mx.com.pendulum.utilities.recycler.SimpleItemTouchHelperCallback;

public class CaptureVideoActivity extends AppCompatActivity implements Interfaces.OnResponse, View.OnClickListener, OnStartDragListener {

    public static final String KEY_FILE_NAME = "key_file_name";
    public static final String KEY_FILE_PATH = "key_file_path";
    public static final String KEY_NUMBER_OF_VIDEOS = "key_number_of_videos";
    public static final String KEY_ALREADY_TAKEN = "key_already_taken";
    public static final String KEY_FREE_SPACES = "key_free_spaces";
    public static final String KEY_MAX_SIZE = "key_max_size";
    public static final String KEY_QUESTION = "key_question";
    public static final String KEY_INTENT_OBJECT = "key_intent_object";
    public static final String KEY_INTENT_ANSWER = "key_intent_answer";
    public static final String KEY_INTENT_ANSWER2 = "key_intent_answer2";
    public static final String KEY_IMAGE_RESIZE = "key_isImageResize";
    public static final String KEY_DATA = "key_data";
    public static final int DIALOG_RESPONSE = 426;
    public static final int DIALOG_RESPONSE_RECORDGIN = 427;
    public static final int DIALOG_RESPONSE_GALERIA_2 = 428;
    private final int CAMERA_RESPONSE = 12345;

    public static final int VIDEO_PREVIEW = 99999;
    public static final int CLOSE_VIDEO_PREVIEW = 99998;
    public static final int EXPAND_SCREEN = 3450;
    public static final int BEGIN_RECORD = 7593;
    private ItemTouchHelper mItemTouchHelper;
    private CameraVideoFragment cameraVideoFragment;
    GalleryTakePhotoAdapter adapter;
    private boolean isFullScreen = false;
    private boolean isRecording = false;
    private boolean isPreview = false;
    private Questions question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creat_document_activity);
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            if (arguments.get(KEY_FILE_NAME) == null) {
                throw new IllegalArgumentException("FALTA NOMBRE DEL ARCHIVO");
            }
            if (arguments.get(KEY_FILE_PATH) == null) {
                throw new IllegalArgumentException("FALTA DIRECCIÓN DE LA RUTA");
            }

            Bundle args = new Bundle();
            args.putString(KEY_FILE_NAME, arguments.getString(KEY_FILE_NAME));
            args.putString(KEY_FILE_PATH, arguments.getString(KEY_FILE_PATH));
            args.putInt(KEY_NUMBER_OF_VIDEOS, arguments.getInt(KEY_NUMBER_OF_VIDEOS, -1));
            args.putInt(KEY_ALREADY_TAKEN, arguments.getInt(KEY_ALREADY_TAKEN, 0));
            args.putIntegerArrayList(KEY_FREE_SPACES, arguments.getIntegerArrayList(KEY_FREE_SPACES));
            int capacity = 0;
            if (arguments.getInt(KEY_NUMBER_OF_VIDEOS) != -1)
                capacity = arguments.getInt(KEY_NUMBER_OF_VIDEOS);
            args.putLong(KEY_MAX_SIZE, arguments.getLong(KEY_MAX_SIZE, 0));
            question = (Questions) arguments.get(KEY_QUESTION);
            if (question != null) {
                if (question.getObject() == null)
                    question.setObject(arguments.get(CaptureVideoActivity.KEY_INTENT_OBJECT));
                if (question.getAnswer() == null)
                    question.setAnswer(arguments.get(CaptureVideoActivity.KEY_INTENT_ANSWER));
                if (question.getAnswer2() == null)
                    question.setAnswer2(arguments.get(CaptureVideoActivity.KEY_INTENT_ANSWER2));
            }
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            cameraVideoFragment = CameraVideoFragment.newInstance(this, CAMERA_RESPONSE, args);
            cameraVideoFragment.internSpacesFree = new ArrayList<>(capacity);
            cameraVideoFragment.spacesFree = arguments.getIntegerArrayList(KEY_FREE_SPACES);
            Tools.fragmentChooser(R.id.cameraContainer,
                    cameraVideoFragment,
                    getFragmentManager(),
                    CameraVideoFragment.class.getSimpleName());
            GalleryTakePhotoFragment galleryTakePhotoFragment =
                    GalleryTakePhotoFragment.newInstance();
            Tools.fragmentChooser(R.id.galleryContainer,
                    galleryTakePhotoFragment,
                    getFragmentManager(),
                    GalleryTakePhotoFragment.class.getSimpleName());
            galleryTakePhotoFragment.setOnResponse(this, 23);
            adapter = new GalleryTakePhotoAdapter(this, this, getItems(), true);
            String str = "Juicio " + getIntent().getExtras().getString(KEY_FILE_NAME);
            ((TextView) findViewById(R.id.text)).setText(str);
            RecyclerView rv = findViewById(R.id.rvF);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
            rv.setLayoutManager(layoutManager);
            rv.setAdapter(adapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(this, adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(rv);
            adapter.setOnResponseListener(this, 894649);
            findViewById(R.id.save).setOnClickListener(this);
            findViewById(R.id.save2).setOnClickListener(this);
            findViewById(R.id.back).setOnClickListener(this);
            findViewById(R.id.back2).setOnClickListener(this);
            findViewById(R.id.llContador).setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<String> getItems() {
        GalleryTakePhotoFragment frag = (GalleryTakePhotoFragment) getFragmentManager()
                .findFragmentByTag(GalleryTakePhotoFragment.class.getSimpleName());
        if (frag == null) {
            return new ArrayList<>();
        }
        return frag.getItems();
    }

    private void actualizarContador() {
        TextView tv = findViewById(R.id.contador);
        GalleryTakePhotoFragment frag =
                (GalleryTakePhotoFragment) getFragmentManager().
                        findFragmentByTag(GalleryTakePhotoFragment.class.getSimpleName());
        int cantidad = frag.getItems().size();
        String str = cantidad + "";
        tv.setText(str);
    }

    private void putViewVisible(boolean visible) {
        if (visible) {
            findViewById(R.id.galleryContainer).setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            param.addRule(RelativeLayout.ABOVE, R.id.galleryContainer);
            findViewById(R.id.cameraContainer).setLayoutParams(param);
        } else {
            findViewById(R.id.galleryContainer).setVisibility(View.GONE);
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            param.removeRule(RelativeLayout.ABOVE);
            findViewById(R.id.cameraContainer).setLayoutParams(param);
        }
    }

    private void hideViewsInRecord(boolean hide) {
        if (hide) {
            findViewById(R.id.save).setVisibility(View.GONE);
            findViewById(R.id.save2).setVisibility(View.GONE);
            findViewById(R.id.back).setVisibility(View.GONE);
            findViewById(R.id.back2).setVisibility(View.GONE);
        } else {
            findViewById(R.id.save).setVisibility(View.VISIBLE);
            findViewById(R.id.save2).setVisibility(View.VISIBLE);
            findViewById(R.id.back).setVisibility(View.VISIBLE);
            findViewById(R.id.back2).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResponse(int handlerCode, Object o) {
        switch (handlerCode) {
            case DIALOG_RESPONSE_RECORDGIN:
                if ((Boolean) o) {
                    cameraVideoFragment.stopRecording(false);
                    isRecording = false;
                    stopRecordgin();
                }
                break;
            case BEGIN_RECORD:
                findViewById(R.id.llContador).setVisibility(View.GONE);
                hideViewsInRecord(true);
                isRecording = true;
                break;
            case EXPAND_SCREEN:
                if (isFullScreen) {
                    putViewVisible(true);
                    isFullScreen = false;
                } else {
                    putViewVisible(false);
                    isFullScreen = true;
                }
                break;
            case CLOSE_VIDEO_PREVIEW:
                putViewVisible(true);
                hideViewsInRecord(false);
                isPreview = false;
                break;
            case VIDEO_PREVIEW:
                putViewVisible(false);
                hideViewsInRecord(true);
                cameraVideoFragment.mOutputFilePath = o.toString();
                cameraVideoFragment.prepareViews();
                isPreview = true;
                break;
            case 23:
                actualizarContador();
                cameraVideoFragment.internSpacesFree.add((int) o);
                break;
            case 894649:
                if (o instanceof String) {
                    String fileName = (String) o;
                    CustomDialog.dialogThubtail(this, fileName);
                } else {
                    GalleryTakePhotoFragment frag =
                            (GalleryTakePhotoFragment) getFragmentManager().
                                    findFragmentByTag(GalleryTakePhotoFragment.class.getSimpleName());
                    frag.itemsWereModified();
                }
                break;
            case CAMERA_RESPONSE:
                findViewById(R.id.llContador).setVisibility(View.VISIBLE);
                if (o != null) {
                    File file = (File) o;
                    GalleryTakePhotoFragment frag = (GalleryTakePhotoFragment) getFragmentManager().findFragmentByTag(GalleryTakePhotoFragment.class.getSimpleName());
                    frag.itemWasAdded(file.toString());
                    actualizarContador();
                }
                stopRecordgin();
                break;
            case DIALOG_RESPONSE:
                boolean bool = (boolean) o;
                if (bool) {
                    setResult(RESULT_CANCELED);
                    deleteCapturedFiles();
                    super.onBackPressed();
                }
                break;
            case DIALOG_RESPONSE_GALERIA_2:
                if ((boolean) o) {
                    Intent intent = new Intent();
                    intent.putExtra(KEY_DATA, getItems());
                    Object object = question.getObject();
                    Object answer = question.getAnswer();
                    Object answer2 = question.getAnswer2();
                    if (!(object instanceof Serializable)) {
                        intent.putExtra(CameraActivity.KEY_INTENT_OBJECT, (Parcelable) question.getObject());
                        question.setObject(null);
                    }
                    if (!(answer instanceof Serializable)) {
                        intent.putExtra(CameraActivity.KEY_INTENT_ANSWER, (Parcelable) question.getAnswer());
                        question.setAnswer(null);
                    }
                    if (!(answer2 instanceof Serializable)) {
                        intent.putExtra(CameraActivity.KEY_INTENT_ANSWER2, (Parcelable) question.getAnswer2());
                        question.setAnswer2(null);
                    }
                    intent.putExtra(KEY_QUESTION, question);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    private void stopRecordgin() {
        if (isFullScreen) {
            putViewVisible(true);
            isFullScreen = false;
        }
        hideViewsInRecord(false);
        isRecording = false;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteCapturedFiles() {
        for (String str : getItems()) {
            File file = new File(str);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBackPressed() {
        if (isRecording) {
            CustomDialog.dialogChoice(this, this, DIALOG_RESPONSE_RECORDGIN,
                    "¿Seguro que desea cancelar la grabación?", "El video será eliminado");
        } else if (isPreview) {
            if (cameraVideoFragment != null) {
                cameraVideoFragment.finishPreview();
                putViewVisible(true);
                hideViewsInRecord(false);
            }
            isPreview = false;
        } else {
            if (getItems().size() > 0) {
                CustomDialog.dialogChoice(this, this, DIALOG_RESPONSE,
                        "¿Seguro que desea salir?", "Toda la información será eliminada");
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back2:
                RelativeLayout rl = findViewById(R.id.rl);
                LinearLayout ll = findViewById(R.id.ll);
                rl.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                GalleryTakePhotoFragment frag =
                        (GalleryTakePhotoFragment) getFragmentManager().
                                findFragmentByTag(GalleryTakePhotoFragment.class.getSimpleName());
                frag.itemsWereModified();
                //actualizarContador();
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.save:
            case R.id.save2:
                if (getItems().size() > 0) {
                    CustomDialog.dialogChoice(this, this, DIALOG_RESPONSE_GALERIA_2, null, "¿Desea guardar los videos capturados?");
                } else {
                    CustomDialog.showDisclaimer(this, "Para guardar debe capturar al menos un video", null);
                }
                break;
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
