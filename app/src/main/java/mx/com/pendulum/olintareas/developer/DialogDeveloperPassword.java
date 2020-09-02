package mx.com.pendulum.olintareas.developer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.UtilPhone;

public class DialogDeveloperPassword {
    private static final String TAG = "DEV. ";

    private Activity activity;
    private Interfaces.OnResponse response;
    private int request;
    private String selectedOption = developerOptions[0];
    private static final String[] developerOptions = {
            "Respaldar BD"
            , "Respaldar Imagenes"
            , "Servidores"
            , "renombrar .images"
            , "logout sin horario"
            , "Enviar correo"
            , "Versión de Ubicuo"
            , "Importar DB User"
            , "Importar DB Catalog"
            , "Importar Imagenes"
    };


    public DialogDeveloperPassword(Activity activity, Interfaces.OnResponse response, int request) {
        this.activity = activity;
        this.response = response;
        this.request = request;
    }

    public static int calculateValue(int s){
        return Math.abs(Math.round((2 * s * s) - (4 * s) + 100));
    }

    public void showDisclaimerPassword() {
        if (Tools.isDebug()) {
            showDeveloperOptions();
            return;
        }
        Random random = new Random();
        final int s = random.nextInt(900) + 100;
        AlertDialog.Builder alert = new AlertDialog.Builder(activity, R.style.AlertDialogStyle);
        final EditText edittext = new EditText(activity);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setCancelable(false);
        alert.setTitle("Inserta password de administrador ( " + s + " ): ");
        alert.setView(edittext);
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Tools.hideSoftKeyboard(activity);
                String pass = edittext.getText().toString();
                boolean shouldPass;
                try {
                    int password = Integer.parseInt(pass);
                    shouldPass = password == calculateValue(s);
                } catch (Exception e) {
                    shouldPass = false;
                }
                if (shouldPass) {
                    showDeveloperOptions();
                } else {
                    Toast.makeText(activity, "Password incorecto.", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void showDeveloperOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Opciones:");
        builder.setSingleChoiceItems(developerOptions, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedOption = developerOptions[which];
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                developerActions();
                dialog.dismiss();
            }
        });


        AlertDialog alert = builder.create();
        alert.show();
    }


    private void developerActions() {
        if (selectedOption.equals(developerOptions[0])) {//EXPORT DB
            Toast.makeText(activity, developerOptions[0], Toast.LENGTH_SHORT).show();
            DeveloperUtils.respaladarBD(activity);
        } else if (selectedOption.equals(developerOptions[1])) { // Exportar Imagenes
            Toast.makeText(activity, developerOptions[1], Toast.LENGTH_SHORT).show();
            moveFiles(1); // 1 = Export -- 2 = Import
        } else if (selectedOption.equals(developerOptions[2])) {// Ver a que direccion apunta
            Toast.makeText(activity, developerOptions[2], Toast.LENGTH_SHORT).show();
            DeveloperUtils.showServersAddress(activity);
        } else if (selectedOption.equals(developerOptions[3])) { // Cambiar directorio Imagenes
            renameImagesFile();
        } else if (selectedOption.equals(developerOptions[4])) { // LogOut sin Horario
            logoutSinHorario();
        } else if (selectedOption.equals(developerOptions[5])) { // Enviar correos
            sendLogMail();
        } else if (selectedOption.equals(developerOptions[6])) { // Version Ubicuo
            String vName = Tools.getUbicuoVersionName(activity);
            int vCode = Tools.getUbicuoVersionCode(activity);
            showDialog("Versión instalada de Ubicuo -->" + vName
                    + " <-- Version de codigo de Ubicuo --> " + vCode);
        } else if (selectedOption.equals(developerOptions[7])) {
            Toast.makeText(activity, developerOptions[7], Toast.LENGTH_SHORT).show();
            DeveloperUtils.importDB(activity, "user.db");
        } else if (selectedOption.equals(developerOptions[8])) {
            Toast.makeText(activity, developerOptions[8], Toast.LENGTH_SHORT).show();
            DeveloperUtils.importDB(activity, "catalog.db");
        } else if (selectedOption.equals(developerOptions[9])) {
            Toast.makeText(activity, developerOptions[9], Toast.LENGTH_SHORT).show();
            moveFiles(2); // 1 = Export -- 2 = Import
        }
    }

    private void logoutSinHorario() {
        if (response != null)
            response.onResponse(request, "logout");
    }

    private void showDialog(String message) {
        CustomDialog.showDisclaimer(activity, message, new Interfaces.OnResponse() {
            @Override
            public void onResponse(int handlerCode, Object o) {
            }
        });
    }

    private void sendLogMail() {
        String backupDBPath = Environment.getExternalStorageDirectory()
                + File.separator + "respaldo" + activity.getString(R.string.app_name);
        File logFile = new File(Properties.SD_FILES_DIR, "log.file");
        File dbUser = new File(backupDBPath, "user.db");
        File dbCatalog = new File(backupDBPath, "catalog.db");
        String[] to = new String[]{"rauFlores@pendulum.com.mx", "jislas@pendulum.com.mx", "rbarrera@pendulum.com.mx", "emujica@pendulum.com.mx"};
        String subject = "LOG " + UtilPhone.getAcountGmail();
        String message = "Informacion del usuario: " + UtilPhone.getAcountGmail() + "\n\n";
        Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE);
        email.putExtra(Intent.EXTRA_EMAIL, to);
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        ArrayList<Uri> uris = new ArrayList<>();
        if (logFile.exists())
            uris.add(Uri.fromFile(logFile));
        if (dbUser.exists())
            uris.add(Uri.fromFile(dbUser));
        if (dbCatalog.exists())
            uris.add(Uri.fromFile(dbCatalog));
        email.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        email.setType("message/rfc822");
        activity.startActivity(Intent.createChooser(email, "Escoje un cliente de correo electrónico :"));
    }

    private void renameImagesFile() {
        File file = new File(Properties.SD_FILES_DIR);
        File[] files = file.listFiles();
        boolean isHiddenFile = false;
        for (File f : files) {
            if (f.getName().contains(".images")) {
                isHiddenFile = true;
                break;
            }
        }
        File oldFolder;
        File newFolder;
        String resultName;
        if (isHiddenFile) {
            oldFolder = new File(Properties.SD_FILES_DIR, ".images");
            newFolder = new File(Properties.SD_FILES_DIR, "images");
            resultName = "images";
        } else {
            oldFolder = new File(Properties.SD_FILES_DIR, "images");
            newFolder = new File(Properties.SD_FILES_DIR, ".images");
            resultName = ".images";
        }
        Log.i(TAG, "isHidden " + isHiddenFile);
        boolean success = oldFolder.renameTo(newFolder);
        if (success) {
            Toast.makeText(activity, TAG + "nombre cambiado a " + resultName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, TAG + "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveFiles(int optionMoveFiles) {
        File srcDir = null;
        File destDir = null;
        switch (optionMoveFiles) {
            case 1: // Export Files
                srcDir = new File(Properties.SD_CARD_IMAGES_DIR);
                destDir = new File(Properties.pathFilesExportFolder);
                break;
            case 2: // Import Files
                srcDir = new File(Properties.pathFilesExportFolder);
                destDir = new File(Properties.SD_CARD_IMAGES_DIR);
                break;
        }
        try {
            if (srcDir != null && srcDir.exists()) {
                if (!destDir.exists()) {
                    Log.i("Directorio Creado", " --> " + destDir.mkdirs());
                }
                File[] files = srcDir.listFiles();
                for (File f : files) {
                    String fileName = f.getName();
                    File destFile = new File(destDir + Properties.FILE_SEPERATOR + fileName);
                    copy(f, destFile);
                }
            }
            Toast.makeText(activity, TAG + "Files Respaldados", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.i("Exception", e.toString());
        }
    }

    private static void copy(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}