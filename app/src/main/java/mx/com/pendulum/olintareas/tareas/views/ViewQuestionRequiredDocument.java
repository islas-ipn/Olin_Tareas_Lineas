package mx.com.pendulum.olintareas.tareas.views;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.camera.CameraActivity;
import mx.com.pendulum.olintareas.camera.CaptureVideoActivity;
import mx.com.pendulum.olintareas.config.util.ContextApplication;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Document;
import mx.com.pendulum.olintareas.dto.tareasV2.FileUploadDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.Obj;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.adapter.GalleryTakePhotoAdapter;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;
import mx.com.pendulum.olintareas.ui.dialog.SignatureDialog;
import mx.com.pendulum.olintareas.ui.dialog.tareas.AddressDialog;
import mx.com.pendulum.olintareas.ui.fragments.tareas.AddNotaV2Fragment;
import mx.com.pendulum.olintareas.ui.fragments.tareas.AddTareaV2Fragment;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.recycler.SimpleItemTouchHelperCallback;
import mx.com.pendulum.utilities.views.CustomTextView;

import static android.app.Activity.RESULT_OK;

class ViewQuestionRequiredDocument implements Interfaces.OnResponse {

    private Context context;
    private DynamicFormAdapter adapter;
    private static final String KEY_DATA = "key_data";
    private static final String KEY_QUESTION = "key_question";
    private static final String REULT_CODE = "REULT_CODE";

    ViewQuestionRequiredDocument(Context context, DynamicFormAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    public View getView() {
        LinearLayout container = new LinearLayout(context);
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        container.setOrientation(LinearLayout.VERTICAL);
        return container;
    }

    void configView(List<Obj> questionContainerDocsList, View convertView,
                    LinearLayout linearLayout, Questions question, Fragment fragment,
                    int itemPosition, AnswerDTO pendingAnswer, boolean isImageResize) {
        Document doc = question.getDocument();
        if (doc == null)
            return;
        BigDecimal maxDocuments = BigDecimal.valueOf(doc.getMax());
        linearLayout.removeAllViews();
        String type = question.getType();
        switch (type) {
            case "SIGN":
                signConfig(maxDocuments.intValue(), linearLayout, questionContainerDocsList, question);
                break;
            case "RAW":
            case "VIDEO":
            case "IMAGE":
            case "FILE_UPLOAD":
                imageConfig(linearLayout, questionContainerDocsList, question);
                break;
            default:
                break;
        }
    }

    private void imageConfig(LinearLayout linearLayout, List<Obj> questionContainerDocsList,
                             Questions question) {
        View view = linearLayout.findViewById(0);
        if (view == null) {
            View v = View.inflate(
                    context, R.layout.row_dynamic_form_question_required_document, null);
            initViewFile(questionContainerDocsList, v, question);
            linearLayout.addView(v);
        } else
            initViewFile(questionContainerDocsList, view, question);
    }

    private void signConfig(long maxDocuments, LinearLayout linearLayout, List<Obj>
            questionContainerDocsList, Questions question) {
        for (int i = 0; i < maxDocuments; i++) {
            View view = linearLayout.findViewById(i);
            if (view == null) {
                View v = View.inflate(
                        context, R.layout.row_dynamic_form_question_required_document, null);
                v.setId(i);
                initViewFile(questionContainerDocsList, v, question);
                linearLayout.addView(v);
            } else {
                initViewFile(questionContainerDocsList, view, question);
            }
        }
    }

    @SuppressWarnings("unused")
    private void initView(List<Obj> questionContainerDocsList, final View convertView, View view,
                          final Questions question, final int rowPosition, final Fragment fragment,
                          final int itemPosition, final AnswerDTO pendingAnswer,
                          final boolean isImageResize) {

        final TextView tvQuestionDocumentExtension =
                view.findViewById(R.id.tvQuestionDocumentExtension);
        final CustomTextView tvQuestionDocumentAnswer =
                view.findViewById(R.id.tvQuestionDocumentAnswer);
        final View addedQuestionDocument = view.findViewById(R.id.addedQuestionDocument);
        final ImageView addQuestionDocument = view.findViewById(R.id.addQuestionDocument);
        final View ivQuestionDocumentDelete = view.findViewById(R.id.ivQuestionDocumentDelete);
        final View flQuestionDocument = view.findViewById(R.id.flQuestionDocument);
        final ImageView ivImageCaptured = view.findViewById(R.id.ivImageCaptured);
        final CustomTextView tvFileSelected =
                view.findViewById(R.id.tvQuestionDocumentAnswerSelected);

        final Obj obj = new Obj();
        Object tag = ivImageCaptured.getTag();
        if (tag instanceof String) {
            String path = (String) tag;
            if (!path.isEmpty()) {
                Questions q = (Questions) view.getTag();
                if (q.getId().equals(question.getId())) {
                    ParentViewMain.loadImageInImageView(ivImageCaptured, path, ParentViewMain.FILE);
                    setAddVisible(true, addQuestionDocument, addedQuestionDocument,
                            ivQuestionDocumentDelete, ivImageCaptured, flQuestionDocument,
                            tvQuestionDocumentAnswer, tvFileSelected);
                } else {
                    setAddVisible(false, addQuestionDocument, addedQuestionDocument,
                            ivQuestionDocumentDelete, ivImageCaptured, flQuestionDocument,
                            tvQuestionDocumentAnswer, tvFileSelected);
                }
            } else {
                setAddVisible(false, addQuestionDocument, addedQuestionDocument,
                        ivQuestionDocumentDelete, ivImageCaptured, flQuestionDocument,
                        tvQuestionDocumentAnswer, tvFileSelected);
            }
        } else {
            setAddVisible(false, addQuestionDocument, addedQuestionDocument,
                    ivQuestionDocumentDelete, ivImageCaptured, flQuestionDocument,
                    tvQuestionDocumentAnswer, tvFileSelected);
        }
        questionContainerDocsList.add(obj);
        int defaultImage = R.drawable.dynamic_file_add;
        switch (question.getType()) {
            /*case "SIGN":
                defaultImage = R.drawable.dynamic_default_signature;
                break;*/
            case "SIGN":
            case "IMAGE":
            case "FILE_UPLOAD":
            case "RAW":
            case "VIDEO":
                break;
            /*case "IMAGE":
                defaultImage = R.drawable.dynamic_add_image;
                break;*/
        }
        addQuestionDocument.setImageDrawable(ContextCompat.getDrawable(context, defaultImage));
        if (ivQuestionDocumentDelete != null)
            ivQuestionDocumentDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvQuestionDocumentExtension.setText("");
                    tvQuestionDocumentAnswer.setText("");
                    tvQuestionDocumentAnswer.setTag(null);
                    obj.setObj(null);
                    question.setAnswer(null);
                    question.setObject(null);
                    ivImageCaptured.setTag("");
                    Obj obj = question.getQuestionContainerDocsList().get(rowPosition);
                    String path = String.valueOf(obj);
                    File fileToDelete = new File(path);
                    if (fileToDelete.delete())
                        Log.d("TAG", "Error al eliminar archivo");
                    question.getQuestionContainerDocsList().remove(rowPosition);
                    //questionContainerDocsList
                    setAddVisible(false, addQuestionDocument, addedQuestionDocument,
                            ivQuestionDocumentDelete, ivImageCaptured, flQuestionDocument,
                            tvQuestionDocumentAnswer, tvFileSelected);
                }
            });

        /*view.findViewById(R.id.rvQuestionDocumentAnwser)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        question.setError(false);
                        if ("SIGN".equals(question.getType())) {
                            getSign(convertView, question, ivImageCaptured, tvQuestionDocumentAnswer,
                                    obj, addQuestionDocument, addedQuestionDocument,
                                    ivQuestionDocumentDelete, flQuestionDocument, tvFileSelected);
                        }
                    }
                });*/
    }

    private View rvQuestionDocumentAnwser;
    private RecyclerView rvTakePhoto;

    private void initViewFile(List<Obj> questionContainerDocsList, View view,
                              final Questions question) {
        final View rvQuestionDocumentAnwser = view.findViewById(R.id.rvQuestionDocumentAnwser);
        final RecyclerView rvTakePhoto = view.findViewById(R.id.rvTakePhoto);
        this.rvQuestionDocumentAnwser = rvQuestionDocumentAnwser;
        final Obj obj = new Obj();
        Object tag = rvQuestionDocumentAnwser.getTag();
        if (tag instanceof String) {
            String path = (String) tag;
            if (!path.isEmpty()) {
                Questions q = (Questions) view.getTag();
                if (q.getId().equals(question.getId())) {
                    /*ParentViewMain.loadImageInImageView(
                            ivImageCaptured, path, ParentViewMain.FILE);*/
                    setAddVisible(true, rvQuestionDocumentAnwser, rvTakePhoto);
                } else {
                    setAddVisible(false, rvQuestionDocumentAnwser, rvTakePhoto);
                }
            } else {
                setAddVisible(false, rvQuestionDocumentAnwser, rvTakePhoto);
            }
        } else {
            setAddVisible(true, rvQuestionDocumentAnwser, rvTakePhoto);
        }
        obtainPathsFromAnswers(question);
        ArrayList<String> pathsDocuments = obtainPthsFromObject(question);
        /*if (pathsDocuments.size() == 0)
            pathsDocuments = obtainPathsFromAnswers(question);*/
        GalleryTakePhotoAdapter adapterGallery = new GalleryTakePhotoAdapter(
                context, null, pathsDocuments, false);
        adapterGallery.setOnResponseListener(this, 23);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(
                context, LinearLayoutManager.HORIZONTAL, false);
        rvTakePhoto.setLayoutManager(layoutManager);
        rvTakePhoto.setAdapter(adapterGallery);
        questionContainerDocsList.add(obj);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(
                context, adapterGallery);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvTakePhoto);
        //adapterGallery.setOnResponseListener(this, 894649);
        this.rvTakePhoto = rvTakePhoto;
    }

    private ArrayList<String> obtainPthsFromObject(Questions question) {
        ArrayList<Obj> obj = question.getQuestionContainerDocsList();
        ArrayList<String> listDocuments = new ArrayList<>();
        for (Obj o : obj) {
            if (o == null) continue;
            if (o.getObj() == null) continue;
            if (!(o.getObj() instanceof String)) continue;
            String str = (String) o.getObj();
            File file = new File(str);
            if (file.exists())
                listDocuments.add(str);
        }
        return listDocuments;
    }

    private void obtainPathsFromAnswers(Questions questions) {
        AnswerDTO answer;
        if (questions.getObject() instanceof AnswerDTO)
            answer = (AnswerDTO) questions.getObject();
        else return;
        Collection<FileUploadDTO> files;
        if (answer.getFiles() != null)
            files = answer.getFiles();
        else return;
        String nFile;
        ArrayList<Obj> objList = new ArrayList<>();
        Obj obj;
        for (FileUploadDTO file : files) {
            if (file.getLw() != null && !file.getLw().getNombre_archivo().equals("")) {
                if (answer.getType().equalsIgnoreCase("VIDEO") ||
                        answer.getType().equalsIgnoreCase("RAW"))
                    nFile = Properties.SD_CARD_VIDEOS_DIR + "/" + file.getLw().getNombre_archivo();
                else
                    nFile = Properties.SD_CARD_IMAGES_DIR + "/" + file.getLw().getNombre_archivo();
                File f = new File(nFile);
                if (f.exists()) {
                    obj = new Obj();
                    obj.setObj(nFile);
                    objList.add(obj);
                }
            }
        }
        if (objList.size() > 0) {
            if (questions.getQuestionContainerDocsList().size() == 0) {
                objList.add(new Obj());
                questions.setQuestionContainerDocsList(objList);
                questions.setObject(null);
            }
        }
    }

    ArrayList<Integer> findFreePosition(Questions question) {
        ArrayList<Obj> obj = question.getQuestionContainerDocsList();
        ArrayList<Integer> freeSpaces = new ArrayList<>();
        int i = 0;
        for (Obj o : obj) {
            if (o == null) {
                freeSpaces.add(i);
                i++;
                continue;
            }
            if (o.getObj() == null) {
                freeSpaces.add(i);
                i++;
                continue;
            }
            if (!(o.getObj() instanceof String)) {
                freeSpaces.add(i);
                i++;
                continue;
            }
            String str = (String) o.getObj();
            File file = new File(str);
            if (!file.exists()) {
                freeSpaces.add(i);
                i++;
                continue;
            }
            i++;
        }
        return freeSpaces;
    }

    int obtainQuestionDocumentsSize(Questions question) {
        ArrayList<Obj> obj = question.getQuestionContainerDocsList();
        int size = 0;
        for (Obj o : obj) {
            if (o == null) continue;
            if (o.getObj() == null) continue;
            if (!(o.getObj() instanceof String)) continue;
            String str = (String) o.getObj();
            File file = new File(str);
            if (file.exists())
                size++;
        }
        return size;
    }

    Questions cleanQuestionContainerDocsList(Questions question) {
        ArrayList<Obj> obj = question.getQuestionContainerDocsList();
        for (Obj o : obj) {
            if (o == null) continue;
            if (o.getObj() == null) continue;
            if (!(o.getObj() instanceof String)) continue;
            String str = (String) o.getObj();
            File file = new File(str);
            if (!file.exists())
                o.setObj(null);
        }
        return question;
    }

    void invoqueActionFromParent(int option, Questions question, Fragment fragment,
                                 boolean isImageResize, int itemPosition, int maxFiles,
                                 int alreadyTaken, ArrayList<Integer> freSpaces,
                                 final View convertView, int rowPosition) {
        if (option == 2) {
            getFromFiles(question, rvTakePhoto, rvQuestionDocumentAnwser, itemPosition,
                    alreadyTaken, freSpaces, fragment);
        } else {
            String type = question.getType().toUpperCase();
            switch (type) {
                case "SIGN":
                    getSign(convertView, question, fragment, itemPosition, rowPosition);
                    break;
                case "VIDEO":
                case "RAW":
                    getVideoFromCamera(fragment, itemPosition, isImageResize, maxFiles,
                            alreadyTaken, freSpaces, question.getDocument().getSize(),
                            question, convertView);
                    break;
                default:
                    getImageFromCamera(fragment, itemPosition, isImageResize, maxFiles,
                            alreadyTaken, freSpaces, question, convertView);
                    break;
            }
        }
    }

    void invoqueActionAddressFromParent(final Questions question, final Fragment fragment, int itemPosition,
                                        final View convertView, int rowPosition) {
        new AddressDialog(context, new Interfaces.OnResponse<Object>() {
            @Override
            public void onResponse(int handlerCode, Object requestString) {
                if (requestString instanceof Integer) {
                    if (fragment instanceof AddNotaV2Fragment) {
                        // Guardar id domicilio
                        AddNotaV2Fragment notaFrg;
                        notaFrg = (AddNotaV2Fragment) fragment;
                        Intent intent = new Intent();
                        intent.putExtra(KEY_DATA, String.valueOf(requestString));
                        question.setAnswer(requestString);
                        intent.putExtra(KEY_QUESTION, question);
                        intent.putExtra(REULT_CODE, RESULT_OK);
                        notaFrg.addressTaken(intent);
                    } else if (fragment instanceof AddTareaV2Fragment) {
                        // Guardar id domicilio
                        AddTareaV2Fragment tareaFrg;
                        tareaFrg = (AddTareaV2Fragment) fragment;
                        Intent intent = new Intent();
                        intent.putExtra(KEY_DATA, String.valueOf(requestString));
                        question.setAnswer(requestString);
                        intent.putExtra(KEY_QUESTION, question);
                        intent.putExtra(REULT_CODE, RESULT_OK);
                        tareaFrg.addressTaken(intent);
                    }
                }
            }
        }, 0, itemPosition, rowPosition).showDialog();

    }

    private void getFromFiles(final Questions questions, final View rvQuestionDocumentAnwser,
                              final View rvTakePhoto, final int itemPosition, final int alreadyTaken,
                              final ArrayList<Integer> freeSpaces, final Fragment fragment) {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        String extensions = questions.getDocument().getExtension().toUpperCase();
        String[] extT;
        String[] ext = null;
        if (!extensions.equalsIgnoreCase("ALL")) {
            extT = extensions.split(",");
            ext = new String[extT.length + extT.length];
            for (int i = 0; i < extT.length; i++) {
                ext[i] = "." + extT[i].toUpperCase();
                ext[extT.length + i] = extT[i].toLowerCase();
            }
        }
        properties.extensions = ext;
        final FilePickerDialog dialog = new FilePickerDialog(context, properties);
        dialog.setNegativeBtnName("Cancelar");
        dialog.setPositiveBtnName("Seleccionar");
        dialog.setTitle("Seleccionar Archivo");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                try {
                    String pathOrigin = files[0];
                    String dest = copyFileToPrivateFolder(pathOrigin, itemPosition,
                            alreadyTaken, freeSpaces);
                    setAddVisible(true, rvQuestionDocumentAnwser, rvTakePhoto);
                    updateQuestion(dest, questions, rvQuestionDocumentAnwser, rvTakePhoto);
                    if (adapter != null) {
                        Intent data = new Intent();
                        ArrayList<String> fileArray = new ArrayList<>();
                        fileArray.add(dest);
                        data.putExtra(KEY_DATA, fileArray);
                        String ext = Tools.getFileExt(dest).toUpperCase();
                        switch (ext) {
                            case "JPG":
                            case "PNG":
                            case "JPEG":
                                adapter.setImagePathItem(RESULT_OK, data);
                                break;
                            default:
                                adapter.setFilePathItem(RESULT_OK, data);
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Tag", "Algo salio mal al copiar el archivo" + e.getMessage());
                }
            }
        });
        dialog.show();
    }

    void setUpdate(Object update, LinearLayout ll, Questions question) {
        Log.i("", "");
        if (ll == null) {
            Log.e("qwerty", "ll es NULL");
            //Log.e("qwerty   werty","prueba");
            return;
        } //TODO PONE IMAGENES
        if (update instanceof Intent) {
            Intent data = (Intent) update;
            String pt;

            data.hasExtra(KEY_DATA);
            ArrayList<String> paths = data.getStringArrayListExtra(KEY_DATA);
            if (paths == null) {
                pt = data.getStringExtra(KEY_DATA);
                paths = new ArrayList<>();
                paths.add(pt);
            }
            for (String path : paths) {
                if (path != null) {
                    update(path, ll, question);
                }
                for (int i = 0; i < question.getQuestionContainerDocsList().size(); i++) {
                    Obj obj = question.getQuestionContainerDocsList().get(i);
                    if (obj == null) {
                        continue;
                    }
                    if (obj.getObj() == null) {
                        continue;
                    }
                    if (!(obj.getObj() instanceof String)) {
                        continue;
                    }
                    String str = (String) obj.getObj();
                    if (str.equals(path)) {
                        continue;
                    }
                    update(str, ll, question);
                }
            }
        } else if (update instanceof AnswerDTO) {
            Log.i("", "");
            AnswerDTO ans = (AnswerDTO) update;
            Collection<FileUploadDTO> files = ans.getFiles();
            for (FileUploadDTO file : files) {
                File archivo;
                if (ans.getType().equalsIgnoreCase("VIDEO") ||
                        ans.getType().equalsIgnoreCase("RAW"))
                    archivo = new File(Properties.SD_CARD_VIDEOS_DIR,
                            file.getLw().getNombre_archivo());
                else
                    archivo = new File(Properties.SD_CARD_IMAGES_DIR,
                            file.getLw().getNombre_archivo());
                String path = archivo.getAbsolutePath();
                update(path, ll, question);
            }
        } else if (update instanceof String && question.getType().equals("SIGN")) {
            String path = (String) update;
            View view = ll.getChildAt(0);
            CustomTextView tvQuestionDocumentAnswer =
                    view.findViewById(R.id.tvQuestionDocumentAnswer);
            View addedQuestionDocument = view.findViewById(R.id.addedQuestionDocument);
            ImageView addQuestionDocument = view.findViewById(R.id.addQuestionDocument);
            View ivQuestionDocumentDelete = view.findViewById(R.id.ivQuestionDocumentDelete);
            View flQuestionDocument = view.findViewById(R.id.flQuestionDocument);
            ImageView ivImageCaptured = view.findViewById(R.id.ivImageCaptured);
            CustomTextView tvFileSelected =
                    view.findViewById(R.id.tvQuestionDocumentAnswerSelected);
            ParentViewMain.loadImageInImageView(ivImageCaptured, path, ParentViewMain.FILE);
            ivImageCaptured.setTag(path);
            view.setTag(question);
            question.setAnswer(path);
            setAddVisible(true, addQuestionDocument, addedQuestionDocument,
                    ivQuestionDocumentDelete, ivImageCaptured, flQuestionDocument,
                    tvQuestionDocumentAnswer, tvFileSelected);
            /*Obj obj = question.getQuestionContainerDocsList().get(0);
            obj.setObj(path);
        }*/
        }
    }

    private void update(String path, LinearLayout ll, Questions question) {
        if (ll == null) {
            //Log.e("qwerty", "ll es NULL");
            return;
        }
        File file = new File(path);
        String fileName = file.getName();
        String[] array = fileName.split(ViewFile_upload.SEPATATOR);
        //int itemPosition = Integer.parseInt(array[1]);
        if (array[0].equals("image")) {
            try {
                int rowPosition = Integer.parseInt(array[2]);
                View view = ll.getChildAt(rowPosition);
                if (view != null) {
                    CustomTextView tvQuestionDocumentAnswer =
                            view.findViewById(R.id.tvQuestionDocumentAnswer);
                    View addedQuestionDocument = view.findViewById(R.id.addedQuestionDocument);
                    ImageView addQuestionDocument = view.findViewById(R.id.addQuestionDocument);
                    View ivQuestionDocumentDelete =
                            view.findViewById(R.id.ivQuestionDocumentDelete);
                    View flQuestionDocument = view.findViewById(R.id.flQuestionDocument);
                    ImageView ivImageCaptured = view.findViewById(R.id.ivImageCaptured);
                    ParentViewMain.loadImageInImageView(ivImageCaptured, path, ParentViewMain.FILE);
                    CustomTextView tvFileSelected =
                            view.findViewById(R.id.tvQuestionDocumentAnswerSelected);
                    ivImageCaptured.setTag(path);
                    /*if (path == null) {
                        Log.i("", "");
                    }*/
                    view.setTag(question);
                    question.setAnswer(path);
                    setAddVisible(true, addQuestionDocument, addedQuestionDocument,
                            ivQuestionDocumentDelete, ivImageCaptured, flQuestionDocument,
                            tvQuestionDocumentAnswer, tvFileSelected);
                }
                Obj obj = question.getQuestionContainerDocsList().get(rowPosition);
                obj.setObj(path);
            } catch (Exception ignored) {
            }
        } else if (array[0].contains("signature")) {
            try {
                View view = ll.getChildAt(0);
                if (view != null) {
                    CustomTextView tvQuestionDocumentAnswer =
                            view.findViewById(R.id.tvQuestionDocumentAnswer);
                    View addedQuestionDocument = view.findViewById(R.id.addedQuestionDocument);
                    ImageView addQuestionDocument = view.findViewById(R.id.addQuestionDocument);
                    View ivQuestionDocumentDelete =
                            view.findViewById(R.id.ivQuestionDocumentDelete);
                    View flQuestionDocument = view.findViewById(R.id.flQuestionDocument);
                    ImageView ivImageCaptured = view.findViewById(R.id.ivImageCaptured);
                    CustomTextView tvFileSelected =
                            view.findViewById(R.id.tvQuestionDocumentAnswerSelected);
                    ParentViewMain.loadImageInImageView(ivImageCaptured, path, ParentViewMain.FILE);
                    ivImageCaptured.setTag(path);
                    /*if (path == null) {
                        Log.i("", "");
                    }*/
                    view.setTag(question);
                    question.setAnswer(path);
                    setAddVisible(true, addQuestionDocument, addedQuestionDocument,
                            ivQuestionDocumentDelete, ivImageCaptured, flQuestionDocument,
                            tvQuestionDocumentAnswer, tvFileSelected);
                }
                Obj obj = question.getQuestionContainerDocsList().get(0);
                obj.setObj(path);
            } catch (Exception i) {
                Log.d("TAG", i.getMessage());
            }
        } else {
            //file capturado desde findFile
            try {
                int rowPosition = Integer.parseInt(array[array.length - 2]);
                View view = ll.getChildAt(rowPosition);
                if (view != null) {
                    CustomTextView tvQuestionDocumentAnswer =
                            view.findViewById(R.id.tvQuestionDocumentAnswer);
                    View addedQuestionDocument = view.findViewById(R.id.addedQuestionDocument);
                    ImageView addQuestionDocument = view.findViewById(R.id.addQuestionDocument);
                    View ivQuestionDocumentDelete =
                            view.findViewById(R.id.ivQuestionDocumentDelete);
                    View flQuestionDocument = view.findViewById(R.id.flQuestionDocument);
                    ImageView ivImageCaptured = view.findViewById(R.id.ivImageCaptured);
                    CustomTextView tvFileSelected =
                            view.findViewById(R.id.tvQuestionDocumentAnswerSelected);
                    String ext = Tools.getFileExt(path).toUpperCase();
                    switch (ext) {
                        case "JPG":
                        case "PNG":
                        case "JPEG":
                            ParentViewMain.loadImageInImageView(
                                    ivImageCaptured, path, ParentViewMain.FILE);
                            tvFileSelected.setText("");
                            break;
                        default:
                            ParentViewMain.loadIconNotImage(ivImageCaptured, "file_ok.png");
                            int separator = path.lastIndexOf("/") + 1;
                            int pointPosition = path.lastIndexOf(".");
                            String pathNew = path.substring(separator, pointPosition - 8);
                            pathNew += path.substring(pointPosition);
                            tvFileSelected.setText(pathNew);
                            break;
                    }
                    ivImageCaptured.setTag(path);
                    view.setTag(question);
                    question.setAnswer(path);
                    setAddVisible(true, addQuestionDocument, addedQuestionDocument,
                            ivQuestionDocumentDelete, ivImageCaptured, flQuestionDocument,
                            tvQuestionDocumentAnswer, tvFileSelected);
                }
                Obj obj = question.getQuestionContainerDocsList().get(rowPosition);
                obj.setObj(path);
            } catch (Exception i) {
                Log.d("TAG", i.getMessage());
            }
        }
    }

    private void updateQuestion(String path, Questions question,
                                View rvQuestionDocumentAnwser, View rvTakePhoto) {
        File file = new File(path);
        String fileName = file.getName();
        String[] array = fileName.split(ViewFile_upload.SEPATATOR);
        try {
            int rowPosition = Integer.parseInt(array[2]);
            question.setAnswer(path);
            setAddVisible(true, rvQuestionDocumentAnwser, rvTakePhoto);
            Obj obj = question.getQuestionContainerDocsList().get(rowPosition);
            obj.setObj(path);
        } catch (Exception ignored) {
        }
    }

    private void getImageFromCamera(Fragment fragment, int itemPosition,
                                    boolean isImageResize, long maxDoc, int alreadyTaken,
                                    ArrayList<Integer> freeSpaces, Questions question,
                                    View convertView) {

        String type = question.getType();
        String desc = question.getQuestion();
        boolean resizeIMAGE = false;
        if (type.equalsIgnoreCase("IMAGE") && desc.toUpperCase().contains("FOTO"))
            resizeIMAGE = true;
        int docMax = (int) maxDoc;
        convertView.findViewById(R.id.flError).setVisibility(View.GONE);
        String imageName = "image" + ViewFile_upload.SEPATATOR + itemPosition +
                ViewFile_upload.SEPATATOR;
        Intent intent = new Intent(ContextApplication.getAppContext(), CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_FILE_NAME, imageName);
        intent.putExtra(CameraActivity.KEY_FILE_PATH, Properties.SD_CARD_IMAGES_DIR);
        intent.putExtra(CameraActivity.QUESTION_TYPE_IMAGE_RESIZE, resizeIMAGE);
        intent.putExtra(CameraActivity.KEY_IMAGE_RESIZE, isImageResize);
        intent.putExtra(CameraActivity.KEY_NUMBER_OF_PHOTOS, docMax);
        intent.putExtra(CameraActivity.KEY_ALREADY_TAKEN, alreadyTaken);
        intent.putExtra(CameraActivity.KEY_FREE_SPACES, freeSpaces);
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
        intent.putExtra(CameraActivity.KEY_QUESTION, question);

        fragment.startActivityForResult(intent, DynamicFormAdapter.FILE_UPLOAD);
    }

    private void getVideoFromCamera(Fragment fragment, int itemPosition,
                                    boolean isImageResize, long maxDoc, int alreadyTaken,
                                    ArrayList<Integer> freeSpaces, Long size, Questions question,
                                    final View convertView) {
        int docMac = (int) maxDoc;
        convertView.findViewById(R.id.flError).setVisibility(View.GONE);
        String imageName = "video" + ViewFile_upload.SEPATATOR + itemPosition +
                ViewFile_upload.SEPATATOR;
        Intent intent = new Intent(ContextApplication.getAppContext(), CaptureVideoActivity.class);
        intent.putExtra(CaptureVideoActivity.KEY_FILE_NAME, imageName);
        intent.putExtra(CaptureVideoActivity.KEY_FILE_PATH, Properties.SD_CARD_VIDEOS_DIR);
        intent.putExtra(CaptureVideoActivity.KEY_IMAGE_RESIZE, isImageResize);
        intent.putExtra(CaptureVideoActivity.KEY_NUMBER_OF_VIDEOS, docMac);
        intent.putExtra(CaptureVideoActivity.KEY_ALREADY_TAKEN, alreadyTaken);
        intent.putExtra(CaptureVideoActivity.KEY_FREE_SPACES, freeSpaces);
        intent.putExtra(CaptureVideoActivity.KEY_MAX_SIZE, size);
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
        intent.putExtra(CaptureVideoActivity.KEY_QUESTION, question);
        fragment.startActivityForResult(intent, DynamicFormAdapter.VIDEO);
    }

    private void getSign(final View convertView, final Questions question, final Fragment fragment,
                         int itemPosition, int rowPosition) {
        convertView.findViewById(R.id.flError).setVisibility(View.GONE);
        new SignatureDialog(context, new Interfaces.OnResponse<Object>() {
            @Override
            public void onResponse(int handlerCode, Object o) {
                if (o instanceof String) {
                    String fileName = (String) o;
                    if (fragment instanceof AddNotaV2Fragment) {
                        AddNotaV2Fragment notaFrg;
                        notaFrg = (AddNotaV2Fragment) fragment;
                        Intent intent = new Intent();
                        ArrayList<String> fileNameArray = new ArrayList<>();
                        fileNameArray.add(fileName);
                        intent.putExtra(KEY_DATA, fileNameArray);
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
                        intent.putExtra(REULT_CODE, RESULT_OK);
                        notaFrg.signatureTaken(intent);
                    } else if (fragment instanceof AddTareaV2Fragment) {
                        AddTareaV2Fragment tareaFrg;
                        tareaFrg = (AddTareaV2Fragment) fragment;
                        Intent intent = new Intent();
                        ArrayList<String> fileNameArray = new ArrayList<>();
                        fileNameArray.add(fileName);
                        intent.putExtra(KEY_DATA, fileNameArray);
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
                        intent.putExtra(REULT_CODE, RESULT_OK);
                        tareaFrg.signatureTaken(intent);
                    }
                }
            }
        }, 0, "", itemPosition, rowPosition).showDialog();
    }

    /*private void getSign(final View convertView, final Questions question,
                         final ImageView ivImageCaptured, final TextView tvQuestionDocumentAnswer,
                         final Obj obj, final View addQuestionDocument,
                         final View addedQuestionDocument, final View ivQuestionDocumentDelete,
                         final View flQuestionDocument, final CustomTextView tvFileSelected) {
        new SignatureDialog(context, new Interfaces.OnResponse<Object>() {
            @Override
            public void onResponse(int handlerCode, Object o) {
                if (o instanceof String) {
                    convertView.findViewById(R.id.flError).setVisibility(View.GONE);
                    String fileName = (String) o;
                    ParentViewMain.loadImageInImageView(ivImageCaptured, fileName, ViewSign.FILE);
                    tvQuestionDocumentAnswer.setTag(fileName);
                    obj.setObj(fileName);
                    question.setAnswer(fileName);
                    setAddVisible(true, addQuestionDocument, addedQuestionDocument,
                            ivQuestionDocumentDelete, ivImageCaptured, flQuestionDocument,
                            tvQuestionDocumentAnswer, tvFileSelected);
                }
            }
        }, 0, "").showDialog();
    }*/

    private String copyFileToPrivateFolder(String sourcePath, int itemPosition,
                                           int rowPosition,
                                           ArrayList<Integer> freeSpaces) throws Exception {
        String destPath = Properties.SD_CARD_IMAGES_DIR;
        File src = new File(sourcePath);
        String fileName = src.getName();
        String extencion = Tools.getFileExt(fileName);
        String pos;
        if (freeSpaces != null)
            pos = ViewFile_upload.SEPATATOR + itemPosition + ViewFile_upload.SEPATATOR +
                    freeSpaces.get(0) + ViewFile_upload.SEPATATOR;
        else
            pos = ViewFile_upload.SEPATATOR + itemPosition + ViewFile_upload.SEPATATOR +
                    rowPosition + ViewFile_upload.SEPATATOR;
        fileName = fileName.replace("." + extencion, pos) + "." + extencion;
        File dst = new File(destPath, fileName);
        if (!dst.getParentFile().exists()) {
            boolean created = dst.getParentFile().mkdirs();
            if (!created) return "";
        }
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
        return dst.getAbsolutePath();
    }

    private void setAddVisible(boolean isAdded, View addQuestionDocument,
                               View addedQuestionDocument, View ivQuestionDocumentDelete,
                               View ivImageCaptured, View flQuestionDocument,
                               View tvQuestionDocumentAnswer, View tvFileSelected) {
        if (addQuestionDocument != null && addedQuestionDocument != null &&
                ivQuestionDocumentDelete != null && ivImageCaptured != null &&
                flQuestionDocument != null && tvQuestionDocumentAnswer != null)
            if (isAdded) {
                addQuestionDocument.setVisibility(View.GONE);
                ivQuestionDocumentDelete.setVisibility(View.VISIBLE);
                flQuestionDocument.setVisibility(View.GONE);
                addedQuestionDocument.setVisibility(View.VISIBLE);
                tvFileSelected.setVisibility(View.VISIBLE);
                tvQuestionDocumentAnswer.setVisibility(View.VISIBLE);
                /*if (isText) {
                    tvQuestionDocumentAnswer.setVisibility(View.VISIBLE);
                } else {
                    ivImageCaptured.setVisibility(View.VISIBLE);
                }*/
            } else {
                addQuestionDocument.setVisibility(View.VISIBLE);
                ivQuestionDocumentDelete.setVisibility(View.GONE);
                flQuestionDocument.setVisibility(View.VISIBLE);
                addedQuestionDocument.setVisibility(View.GONE);
                tvQuestionDocumentAnswer.setVisibility(View.GONE);
                ivImageCaptured.setVisibility(View.GONE);
                tvFileSelected.setVisibility(View.GONE);
            }
    }

    private void setAddVisible(boolean isAdded, View addedQuestionDocument,
                               View tvQuestionDocumentAnswer) {
        if (addedQuestionDocument != null && tvQuestionDocumentAnswer != null)
            if (isAdded) {
                addedQuestionDocument.setVisibility(View.VISIBLE);
                tvQuestionDocumentAnswer.setVisibility(View.VISIBLE);
            } else {
                addedQuestionDocument.setVisibility(View.GONE);
                tvQuestionDocumentAnswer.setVisibility(View.GONE);
            }
    }

    @Override
    public void onResponse(int handlerCode, Object o) {
        CustomDialog.dialogVideo(context, new Interfaces.OnResponse() {
            @Override
            public void onResponse(int handlerCode, Object o) {
                Log.d("Tag", "Chalalala");
            }
        }, 123, o.toString());
    }
}