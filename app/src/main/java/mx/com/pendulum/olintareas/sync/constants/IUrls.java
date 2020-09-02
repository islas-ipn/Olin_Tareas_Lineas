package mx.com.pendulum.olintareas.sync.constants;

import mx.com.pendulum.olintareas.Properties;

public class IUrls {

    private static final String IP_PUBLIC_DEV_88 = "192.168.0.9:8080";
    private static final String IP_PUBLIC_PROD = "olin.pendulum.com.mx:59008";
    private static final String IP_PUBLIC_GLSH = "olin.pendulum.com.mx:59008"; // Glassfish
    //private static final String IP_PUBLIC_DEV = "192.168.1.115:8080"; // Lap SWITCH Pendulum
    //private static final String IP_PUBLIC_DEV = "192.168.1.112:8080"; // PC SWITCH Pendulum
    //private static final String IP_PUBLIC_DEV = "10.73.108.35:8080"; // Lap Pendulum
    //private static final String IP_PUBLIC_DEV = "10.73.140.80:59008"; // PC Pendulum
    private static final String IP_PUBLIC_DEV = "192.168.100.12:8080"; // Lap HOUSE WIFI
    //private static final String IP_PUBLIC_DEV = "192.168.100.23:8080"; // Lap HOUSE CABLE
    private static final String SCHEME_SLL = "http"; // produccion
    private static final String SCHEME_GLSH = "http"; // glassfish
    private static final String SCHEME_DEV = "http"; // developer
    private static final String SCHEME = Properties.isReleaseApp ? (Properties.isGlassFishApp ? SCHEME_GLSH : SCHEME_SLL) : SCHEME_DEV;
    private static final String SERVER = Properties.isReleaseApp ? (Properties.isGlassFishApp ? IP_PUBLIC_GLSH : IP_PUBLIC_PROD) : Properties.isDev88App ? IP_PUBLIC_DEV_88 : IP_PUBLIC_DEV;
    private static final String SCHEME_OLIN = Properties.isReleaseApp ? SCHEME_SLL : SCHEME_DEV;
    private static final String SERVER_OLIN = Properties.isReleaseApp ? IP_PUBLIC_PROD : Properties.isDev88App ? IP_PUBLIC_DEV_88 : IP_PUBLIC_DEV;
    private static final String CONTEXT_ROOT = "/olin/";
    private static final String TWO_POINTS = "://";

    private static final String TEMP_SERVER = SCHEME_DEV + TWO_POINTS + IP_PUBLIC_DEV + CONTEXT_ROOT;

    private static final String BASE_URL_OLIN = SCHEME_OLIN + TWO_POINTS + SERVER_OLIN + CONTEXT_ROOT;
    public static final String BASE_URL = SCHEME + TWO_POINTS + SERVER + CONTEXT_ROOT;
    private static final String BASE_URL_TEMP_RELEASE = SCHEME_SLL + TWO_POINTS + IP_PUBLIC_PROD + CONTEXT_ROOT;
    private static final String BASE_URL_2 = SCHEME + TWO_POINTS + SERVER + CONTEXT_ROOT;
    public static final String SERVER_TIME = BASE_URL + IResources.SERVER_TIME;
    public static final String BASE_URL_SYTEM = BASE_URL;
    public static final String CONTEXT_USER = "user";
    static final String CONTEXT_ID_FORM = "idForm";
    public static final String CONTEXT_SEPERATOR = "/";
    private static final String CONTEXT_CATALOG = "catalogos";
    private static final String CONTEXT_ALICACION_COBRANZA = "COBRANZA";
    public static final String I_LW = BASE_URL_2 + IResources.I_LW;
    public static final String VALIDATE_VERSION = BASE_URL + IResources.VERSION;
    //mail resumen
    public static final String USER_LOGOUT = BASE_URL + IResources.LOGOUT;
    public static final String I_EMAIL = BASE_URL + IResources.EMAIL;
    public static final String ALL_VERSION_APPS = BASE_URL_OLIN + IResources.ALL_VERSION_APPS;
    //public static final String USER_LOGINLDAP = BASE_URL + IResources.LOGINLDAP;
    public static final String USER_LOGINLDAP = BASE_URL_OLIN + IResources.LOGINLDAP;

    //COMPLEMENTOS
    public static final String COMPLEMENTOS = BASE_URL + IResources.COMPLEMENTOS;
    public static final String MANUALEES = BASE_URL + IResources.MANUALES;
    //tareas V2 login
    public static final String CATALOG_FORM = BASE_URL + IResources.CATALOG_FORM;
    public static final String SUB_CATALOG_FORM = BASE_URL + IResources.SUB_CATALOG_FORM;
    public static final String CATALOG_NOTE_FORM = BASE_URL + IResources.CATALOG_NOTE_FORM;
    public static final String CATALOG_ACTIVIDAD = BASE_URL + IResources.CATALOG_ACTIVIDAD;
    public static final String CATALOG_GET_TIPOS_TAREA = BASE_URL + IResources.CATALOG_GET_TIPOS_TAREA;
    public static final String CATALOG_GET_PRIORIDADES = BASE_URL + IResources.CATALOG_GET_PRIORIDADES;
    public static final String CATALOG_GET_SUBTIPOS = BASE_URL + IResources.CATALOG_GET_SUBTIPOS;
    public static final String CATALOG_GET_ESTADO_NOTA = BASE_URL + IResources.CATALOG_GET_ESTADO_NOTA;
    public static final String CATALOG_CUENTAS = BASE_URL + IResources.CATALOG_CUENTAS;

    public static final String CATALOG_CREDITS_USER = BASE_URL + IResources.CATALOG_CREDITS_USER; // DESCOMENTAR PARA PROD
    public static final String CATALOG_CASES_USER = BASE_URL + IResources.CATALOG_CASES_USER; // DESCOMENTAR PARA PROD

    //public static final String CATALOG_CREDITS_USER = TEMP_SERVER + IResources.CATALOG_CREDITS_USER; // PRUEBA PARA EL RESTO DE WS EN PROD
    //public static final String CATALOG_CASES_USER = TEMP_SERVER + IResources.CATALOG_CASES_USER; // PRUEBA PARA EL RESTO DE WS EN PROD


    public static final String SEGUIMIENTO_TAREA = BASE_URL + IResources.SEGUIMIENTO_TAREA;
    public static final String SEGUIMIENTO_TAREA_RESPONSE = BASE_URL + IResources.SEGUIMIENTO_TAREA_RESPONSE;
    public static final String NOTE_RESPONSE = BASE_URL + IResources.NOTE_RESPONSE;
    public static final String SEGUIMIENTO_NOTA_RESPONSE = BASE_URL + IResources.SEGUIMIENTO_NOTA_RESPONSE;
    public static final String RESPONSABLES_DTO = BASE_URL + IResources.RESPONSABLES_DTO;
    public static final String OPTION_QUESTION_FORM = BASE_URL + IResources.OPTION_QUESTION_FORM;
    public static final String CATALOG_COMANDOS_ALL = BASE_URL + CONTEXT_CATALOG + IResources.CAT_COMANDOS_ALL + CONTEXT_USER + CONTEXT_SEPERATOR + CONTEXT_ALICACION_COBRANZA;
    //tarea V2 upload
    public static final String U_TAREA = BASE_URL + IResources.U_TAREA;
    public static final String U_NOTA = BASE_URL + IResources.U_NOTA;
    // Sub Categorias
    public static final String CATALOG_GET_SUB_CATEGORIES = BASE_URL + IResources.CATALOG_SUB_CATEGORIES;
    public static final String CATALOG_GET_SUB_REL_CAT_QNS = BASE_URL + IResources.CATALOG_GET_SUB_REL_CAT_QNS;
    public static final String CATALOG_GET_SUB_QUESTIONS = BASE_URL + IResources.CATALOG_SUB_QUESTIONS;
    public static final String CATALOG_GET_SUB_QUES_DOCUMENTS = BASE_URL + IResources.CATALOG_SUB_QUES_DOCUMENT;
    public static final String CATALOG_GET_SUB_QUES_OPTIONS = BASE_URL + IResources.CATALOG_SUB_QUES_OPTIONS;
    public static final String CATALOG_GET_SUB_REL_OPTIONS = BASE_URL + IResources.CATALOG_SUB_REL_OPTIONS;
    public static final String CATALOG_GET_SUB_OPT_AUTOCOMPLETE = BASE_URL + IResources.CATALOG_SUB_OPT_AUTOCOMPLETE;
    // Options Questions
    public static final String CATALOG_RELATION_OPT_QRY_FO = BASE_URL + IResources.CATALOG_RELATION_OPT_QRY_FO;
    public static final String CATALOG_RELATION_OPT_QNS_FO = BASE_URL + IResources.CATALOG_RELATION_OPT_QNS_FO;
    public static final String CATALOG_QUESTIONS_FO = BASE_URL + IResources.CATALOG_QUESTIONS_FO;
    public static final String CATALOG_OPTIONS_FO = BASE_URL + IResources.CATALOG_OPTIONS_FO;
    public static final String CATALOG_DOCUMENTS_FO = BASE_URL + IResources.CATALOG_DOCUMENTS_FO;
    public static final String CATALOG_AUTOCOMPLETE_FO = BASE_URL + IResources.CATALOG_AUTOCOMPLETE_FO;
    //Resolucion de questions
    //public static final String QUESTION_RESOLUTION = BASE_URL + IResources.QUESTION_RESOLUTION;
    public static final String CATALOG_FORM_TEMP = BASE_URL_TEMP_RELEASE + IResources.CATALOG_FORM;
    public static final String CATALOG_NOTE_FORM_TEMP = BASE_URL_TEMP_RELEASE + IResources.CATALOG_NOTE_FORM;
    public static final String OPTION_QUESTION_FORMT_TEMP = BASE_URL_TEMP_RELEASE + IResources.OPTION_QUESTION_FORM;
    public static final String CATALOG_COMANDOS_ALL_TEMP = BASE_URL_TEMP_RELEASE + CONTEXT_CATALOG + IResources.CAT_COMANDOS_ALL + CONTEXT_USER + CONTEXT_SEPERATOR + CONTEXT_ALICACION_COBRANZA;
    public static final String CATALOG_CREDITS_USER_TEMP = BASE_URL_TEMP_RELEASE + IResources.CATALOG_CREDITS_USER;
    public static final String CATALOG_CASES_USER_TEMP = BASE_URL_TEMP_RELEASE + IResources.CATALOG_CASES_USER;
}