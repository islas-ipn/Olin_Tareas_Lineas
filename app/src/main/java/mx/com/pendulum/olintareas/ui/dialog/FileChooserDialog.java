package mx.com.pendulum.olintareas.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.utilities.views.CustomTextView;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class FileChooserDialog {
    private static final String PARENT_DIR = " ../";

    private final Context context;
    private CustomTextView tvTitle;
    private ListView list;
    private Dialog dialog;
    private File currentPath;

    // filter on file extension
    private String[] extension = null;


    private void setExtension(String extension) {
        this.extension = (extension == null) ? null :
                extension.split(",");
    }

    // file selection event handling
    public interface FileSelectedListener {
        void fileSelected(File file);
    }

    public FileChooserDialog setFileListener(FileSelectedListener fileListener) {
        this.fileListener = fileListener;
        return this;
    }

    private FileSelectedListener fileListener;

    public FileChooserDialog(final Context context, String extensions) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.setCancelable(false);
        View view = View.inflate(context, R.layout.dialog_dynamic_form_file_chooser, null);

        list = view.findViewById(R.id.listView);
        tvTitle = view.findViewById(R.id.title);
        CustomTextView tvExtension = view.findViewById(R.id.tvExtension);

        if (extensions != null)
            if (!extensions.isEmpty()) {
                tvExtension.setText(extensions);
                setExtension(extensions);
                tvExtension.setVisibility(View.VISIBLE);
            }


        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileListener != null) {
                    fileListener.fileSelected(null);
                }
                dialog.dismiss();
            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int which, long id) {
                String fileChosen = (String) list.getItemAtPosition(which);
                File chosenFile = getChosenFile(fileChosen);

                if (Environment.getExternalStorageDirectory().getParent().equals(chosenFile.getAbsolutePath())) {
                    Toast.makeText(context, "No hay mas directorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (chosenFile.isDirectory()) {
                    refresh(chosenFile);
                } else {
                    if (fileListener != null) {
                        int size = (int) chosenFile.length();

                        if (size <= 1) {
                            return;
                        }

                        fileListener.fileSelected(chosenFile);
                    }
                    dialog.dismiss();
                }
            }
        });


        dialog.setContentView(view);
        dialog.setTitle("Seleccione un archivo");
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        refresh(Environment.getExternalStorageDirectory());
    }

    public void showDialog() {
        dialog.show();
    }


    /**
     * Sort, filter and display the files for the given path.
     */
    private void refresh(File path) {
        this.currentPath = path;
        if (path.exists()) {

            File[] dirs = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.isDirectory() && file.canRead());
                }
            });
            File[] files = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {


                    if (extension == null) {
                        return !file.isDirectory() && file.canRead();
                    }
                    boolean isValidExtension = false;
                    for (String ext : extension) {
                        if (file.getName().toLowerCase().endsWith(ext.toLowerCase())
                                || ext.toLowerCase().equals("all")) {
                            isValidExtension = true;
                            break;
                        }
                    }

                    int size = (int) file.length();

                    return size > 1 && isValidExtension && !file.isDirectory() && file.canRead();

                }
            });

            if (dirs == null)
                return;

            String tmp = path.getAbsolutePath().replace(Environment.getExternalStorageDirectory().getAbsolutePath(), "sdcard") + "/";

            // tmp += currentPath;

            tvTitle.setText(tmp);

            // convert to an array
            int i = 0;
            String[] fileList;
            if (path.getParentFile() == null) {
                fileList = new String[dirs.length + files.length];
            } else {
                fileList = new String[dirs.length + files.length + 1];
                fileList[i++] = PARENT_DIR;
            }
            Arrays.sort(dirs);
            Arrays.sort(files);
            for (File dir : dirs) {
                fileList[i++] = dir.getName() + "/";
            }
            for (File file : files) {
                fileList[i++] = file.getName();
            }

            // refresh the user interface
            //  dialog.setTitle(currentPath.getPath());
            list.setAdapter(new ArrayAdapter(context, R.layout.simple_list_item_1, fileList) {
                @NonNull
                @Override
                public View getView(int pos, View view, @NonNull ViewGroup parent) {
                    view = super.getView(pos, view, parent);
                    ((CustomTextView) view).setSingleLine(true);
                    return view;
                }
            });
        }
    }


    /**
     * Convert a relative filename into an actual File object.
     */
    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) {
            return currentPath.getParentFile();
        } else {
            return new File(currentPath, fileChosen);
        }
    }

}
