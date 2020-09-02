package mx.com.pendulum.olintareas.camera;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
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

@SuppressWarnings("rawtypes")
public class CameraActivity extends AppCompatActivity implements Serializable, Interfaces.OnResponse, View.OnClickListener, OnStartDragListener {

    public static final String KEY_FILE_NAME = "key_file_name";
    public static final String KEY_FILE_PATH = "key_file_path";
    public static final String KEY_NUMBER_OF_PHOTOS = "key_number_of_photos";
    //public static final String QUESTION_TYPE_SELECTED = "question_type_selected";
    public static final String QUESTION_TYPE_IMAGE_RESIZE = "question_desc_selected";
    public static final String KEY_ALREADY_TAKEN = "key_already_taken";
    public static final String KEY_FREE_SPACES = "key_free_spaces";
    public static final String KEY_QUESTION = "key_question";
    public static final String KEY_INTENT_OBJECT = "key_intent_object";
    public static final String KEY_INTENT_ANSWER = "key_intent_answer";
    public static final String KEY_INTENT_ANSWER2 = "key_intent_answer2";
    public static final String KEY_IMAGE_RESIZE = "key_isImageResize";
    public static final String KEY_DATA = "key_data";
    public static final int DIALOG_RESPONSE = 426;
    public static final int DIALOG_RESPONSE_GALERIA_2 = 428;
    private static final int CAMERA_RESPONSE = 12345;
    private ItemTouchHelper mItemTouchHelper;
    private GalleryTakePhotoAdapter adapter;
    private CameraTakePhotoFragment cameraTakePhotoFragment;
    private Questions question;
    private boolean justOnePicture = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creat_document_activity);
        Bundle arguments = getIntent().getExtras();
        if (arguments.get(KEY_FILE_NAME) == null) {
            throw new IllegalArgumentException("FALTA NOMBRE DEL ARCHIVO");
        }
        if (arguments.get(KEY_FILE_PATH) == null) {
            throw new IllegalArgumentException("FALTA DIRECCIÓN DE LA RUTA");
        }
        Bundle args = new Bundle();
        int nPictures = arguments.getInt(KEY_NUMBER_OF_PHOTOS, -1);
        if (nPictures == 1)
            justOnePicture = true;
        else
            justOnePicture = false;
        args.putString(KEY_FILE_NAME, arguments.getString(KEY_FILE_NAME));
        args.putString(KEY_FILE_PATH, arguments.getString(KEY_FILE_PATH));
        args.putInt(KEY_NUMBER_OF_PHOTOS, nPictures);
        args.putInt(KEY_ALREADY_TAKEN, arguments.getInt(KEY_ALREADY_TAKEN, 0));
        args.putBoolean(QUESTION_TYPE_IMAGE_RESIZE, arguments.getBoolean(QUESTION_TYPE_IMAGE_RESIZE));
        args.putIntegerArrayList(KEY_FREE_SPACES, arguments.getIntegerArrayList(KEY_FREE_SPACES));
        question = (Questions) arguments.get(KEY_QUESTION);
        if (question != null) {
            if (question.getObject() == null)
                question.setObject(arguments.get(CaptureVideoActivity.KEY_INTENT_OBJECT));
            if (question.getAnswer() == null)
                question.setAnswer(arguments.get(CaptureVideoActivity.KEY_INTENT_ANSWER));
            if (question.getAnswer2() == null)
                question.setAnswer2(arguments.get(CaptureVideoActivity.KEY_INTENT_ANSWER));
        }
        cameraTakePhotoFragment =CameraTakePhotoFragment.newInstance(this, CAMERA_RESPONSE, args);
        cameraTakePhotoFragment.spacesFree = arguments.getIntegerArrayList(KEY_FREE_SPACES);
        int capacity = 0;
        if (arguments.getInt(KEY_NUMBER_OF_PHOTOS) != -1)
            capacity = arguments.getInt(KEY_NUMBER_OF_PHOTOS);
        /*INIICIALIZAMOS LOS OBJETOS DE LA CAMPARA*/
        cameraTakePhotoFragment.internSpacesFree = new ArrayList<>(capacity);
        GalleryTakePhotoFragment galleryTakePhotoFragment = GalleryTakePhotoFragment.newInstance();

        Tools.fragmentChooser(R.id.cameraContainer,cameraTakePhotoFragment,getFragmentManager(),CameraTakePhotoFragment.class.getSimpleName());
        Tools.fragmentChooser(R.id.galleryContainer,galleryTakePhotoFragment,getFragmentManager(),GalleryTakePhotoFragment.class.getSimpleName());
        galleryTakePhotoFragment.setOnResponse(this, 23);
        adapter = new GalleryTakePhotoAdapter(this, this, getItems(), true);

        ((TextView) findViewById(R.id.text)).setText("Juicio " + getIntent().getExtras().getString(KEY_FILE_NAME));
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
    }

    @SuppressLint("SetTextI18n")
    private void actualizarContador() {
        TextView tv = findViewById(R.id.contador);
        GalleryTakePhotoFragment frag =(GalleryTakePhotoFragment) getFragmentManager().findFragmentByTag(GalleryTakePhotoFragment.class.getSimpleName());
        int cantidad = frag.getItems().size();
        tv.setText(cantidad + "");
        if (justOnePicture) {
            if (cantidad == 1) {
                saveData(true);
            }
        }
    }

    @Override
    public void onResponse(int handlerCode, Object o) {
        switch (handlerCode) {
            case 23:
                actualizarContador();
                cameraTakePhotoFragment.internSpacesFree.add((int) o);
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
                if (o != null) {
                    if (o instanceof Boolean) {
                        if (getItems().size() > 0) {
                            goToGallery();
                        } else {
                            CustomDialog.showDisclaimer(this, "Debe tomar al menos una fotografía para ver la galeriía.", null);
                        }
                    } else {
                        File file = (File) o;
                        GalleryTakePhotoFragment frag = (GalleryTakePhotoFragment) getFragmentManager().findFragmentByTag(GalleryTakePhotoFragment.class.getSimpleName());
                        /*Esta condición es para quitar el bug de triple clic*/
                        if (!frag.itemExiste(file.toString())) {
                            frag.itemWasAdded(file.toString());
                            android.media.ExifInterface exif;
                            try {
                                exif = new android.media.ExifInterface(file.getAbsolutePath());
                                int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                                if (orientation == 0)
                                    orientation = 1;
                                exif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation + "");
                                exif.saveAttributes();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            actualizarContador();
                        }
                    }
                }
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
                saveData((boolean) o);
                break;
        }
    }

    private void saveData(boolean save) {
        if (save) {
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
    }

    public void goToGallery() {
        RelativeLayout rl = findViewById(R.id.rl);
        LinearLayout ll = findViewById(R.id.ll);
        rl.setVisibility(View.GONE);
        ll.setVisibility(View.VISIBLE);
        adapter.setItems(getItems());
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
        if (getItems().size() > 0) {
            CustomDialog.dialogChoice(this, this, DIALOG_RESPONSE, "¿Seguro que desea salir?", "Toda la información será eliminada");
        } else {
            super.onBackPressed();
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
                GalleryTakePhotoFragment frag =(GalleryTakePhotoFragment) getFragmentManager().findFragmentByTag(GalleryTakePhotoFragment.class.getSimpleName());
                frag.itemsWereModified();
                actualizarContador();
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.save:
            case R.id.save2:
                if (getItems().size() > 0) {
                    CustomDialog.dialogChoice(this, this, DIALOG_RESPONSE_GALERIA_2, null, "¿Desea guardar las fotografías capturadas?");
                } else {
                    CustomDialog.showDisclaimer(this, "Para guardar debe capturar al menos una fotografía", null);
                }
                break;
        }
    }

    private ArrayList<String> getItems() {
        GalleryTakePhotoFragment frag = (GalleryTakePhotoFragment) getFragmentManager().findFragmentByTag(GalleryTakePhotoFragment.class.getSimpleName());
        if (frag == null) {
            return new ArrayList<>();
        }
        return frag.getItems();
    }

    /*public int itemLength(){
        GalleryTakePhotoFragment frag = (GalleryTakePhotoFragment) getFragmentManager().findFragmentByTag(GalleryTakePhotoFragment.class.getSimpleName());
        if (frag == null) {
            return 0;
        }
        return frag.getItems().size();
    }*/

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}