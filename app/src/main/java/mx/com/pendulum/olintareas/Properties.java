package mx.com.pendulum.olintareas;

import android.os.Environment;

import java.util.Objects;

import mx.com.pendulum.olintareas.config.util.ContextApplication;

public class Properties {

    public static final String TAG_DEVELOP = "<<-- TAG - Tareas -->> ";
    public static final boolean isReleaseApp = true;
    public static final boolean isGlassFishApp = false; // Apuntar a Glassfish, es necesario isReleaseApp en true
    public static final boolean isDev88App = false; // Apuntar al 88, es necesario isReleaseApp en false
    public static final boolean isTempWSRelease = false; // Apunta los WS lentos a producción
    public static final String SD_FILES_DIR = Objects.requireNonNull(
            ContextApplication.getAppContext().getExternalFilesDir(null)).getAbsolutePath();
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String FILE_SEPERATOR = System.getProperty("file.separator");
    public static final String SD_CARD_IMAGES_DIR = SD_FILES_DIR + FILE_SEPERATOR + ".images";
    public static final String SD_CARD_VIDEOS_DIR = SD_FILES_DIR + FILE_SEPERATOR + ".videos";
    private static final String SD_FILES_DIR_RESP = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    public static final String SD_CARD_IMAGES_DIR_RESP = SD_FILES_DIR_RESP + FILE_SEPERATOR + ".respaldoOlin";
    public static final String SD_CARD_IMAGES_DIR_BACKUP_TSK = SD_FILES_DIR_RESP + FILE_SEPERATOR + ".respaldoTsk";
    public static final String SD_JSON_COBR_RESP = "/json/";
    public static final String pakage_tareas = "mx.com.pendulum.olintareas";
    public static final String pakage_olin = "mx.com.pendulum.olin";
    public static final String pakage_legal = "mx.com.pendulum.olinlegal";
    public static final String pakage_ubicuo = "mx.com.pendulum.ubicuo";
    public static final String APP_NAME = "olin_tareas";
    public static final String APP_NAME_OLIN = "olin";
    public static final String APP_NAME_LEGAL = "olin_legal";
    public static final String APP_NAME_UBICUO = "oubicuo";
    public static final String NAME_JSON_TAREAS_RESP = "jsonTareasOlinBackUp.txt";
    public static final String NAME_JSON_NOTAS_RESP = "jsonNotasOlinBackUp.txt";
    public static final String NAME_JSON_NOTAS2_RESP = "jsonNotas2OlinBackUp.txt";
    public static final String NAME_JSON_EPOCH_RESP = "jsonEpochDateOlinBackUp.txt";
    public static final String NAME_JSON_FILE_RESP = "jsonFileUploadOlinBackUp.txt";
    public static final String NAME_JSON_ANSWERS_RESP = "jsonAnswerOlinBackUp.txt";
    public static final String NAME_JSON_TEMPORAL_FORM_RESP = "jsonTemporalFormOlinBackUp.txt";
    public static final String NAME_JSON_SEGUIMIENTO_RESP = "jsonSeguimientoOlinBackUp.txt";
    public static final String NAME_JSON_RESPONSE_TSK_RESP = "jsonResponseTskOlinBackUp.txt";
    public static final String NAME_JSON_NOTE_RSP_RESP = "jsonNoteRespOlinBackUp.txt";

    public static final boolean EXECUTE_WIFI_TEST = true;
    public static final String DATE_TIME_FORMAT_FILE = "yyyy_MM_dd_HH_mm_ss";
    public static final String DATE_TIME_FORMAT_DB = "yyyy-MM-dd HH:mm:ss";
    public static final String PHONE_FILES_DIR = ContextApplication.getAppContext().getFilesDir().getAbsolutePath();
    public static final String pakageFile_managet = "com.mobisystems.office";
    public static final String pakageUbicuo = "mx.com.pendulum.ubicuo";
    public static final String pathPlay = "https://play.google.com/store/apps/details?id=com.mobisystems.office";

    public static final String PERMISSION_INTERNAL = "interno";
    public static final int TIME_UOT = 50000;
    public static final int SOKECT_TIME_UOUT = 50000;
    public static final int VIDEO_TIME_UOUT = 30000; // 5 Minutos en milisegundos
    public static final int SPINNER_OPTION_SELECT_INT = 99999999;
    public static final String SPINNER_OPTION_SELECT_STRING = "SELECCIONAR";

    //DataBase
    public static final String pathDataBase = "";
    private static final String pathDataBaseExportBase = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String pathDataBaseExportFolder = "/respaldoTareas/";
    public static final String pathFilesExportFolder = pathDataBaseExportBase + pathDataBaseExportFolder + "files/";

    // STRINGS DESDE OTAS APPS
    public static final String EXTRA_IS_FROM_ANOTHER_APP = "isFromAnotherApp";
    public static final String EXTRA_IS_QUANITY_NEEDED = "isQuantityNeeded";
    public static final String EXTRA_USER_LOGIN = "user";
    public static final String EXTRA_PASS_LOGIN = "pass";
    public static final String CALL_FROM_PACKAGE_NAME = "packageName";
    public static final String EXTRA_JSON_SEG = "seguimientoTareas";
    public static final String EXTRA_SERIAL_SEG = "seguimientoObject";
    public static final String EXTRA_LOG_OUT_OPTION = "LOG_OUT_OPTION";
    public static final String SHARED_FROM_ANOTHER_APP = "from_another_app";
    public static final String SHARED_IS_FINISHED_TASK = "from_another_app";
    public static final String QUANITY_TASKS_WS = "task_quantity_ws";
}
