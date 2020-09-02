package mx.com.pendulum.olintareas.sync.constants;

public class IResources {
    static final String LOGOUT = "logout_tareas";
    static final String LOGINLDAP = "login_LDAP";
    public static final String VERSION = "version/olin_tareas";
    static final String I_LW = "iLwCobranza";
    //complementos
    static final String COMPLEMENTOS = "getComplementos";
    static final String MANUALES = "getManuales";
    public static final String EMAIL = "mail_tareas";
    static final String SERVER_TIME = "timeServer";
    //TAREAS V2"
    static final String CATALOG_FORM = "tareasV2/catalogForm";
    static final String SUB_CATALOG_FORM = "tareasV2/catalogForm/" + IUrls.CONTEXT_ID_FORM;
    static final String CATALOG_NOTE_FORM = "tareasV2/getNoteQuestions/" + IUrls.CONTEXT_USER;
    static final String CATALOG_ACTIVIDAD = "tareasV2/catalogActividad";
    //String CATALOG_GET_TIPOS_TAREA = "tareasV2/getTiposTarea";
    static final String CATALOG_GET_TIPOS_TAREA = "tareasV2/getTiposNegociosTsk/" + IUrls.CONTEXT_USER + "/";
    static final String CATALOG_GET_PRIORIDADES = "tareasV2/getPrioridades";
    static final String CATALOG_GET_SUBTIPOS = "tareasV2/getSubtipos";
    static final String CATALOG_GET_ESTADO_NOTA = "tareasV2/getCatalogEstadosNota";
    static final String CATALOG_CUENTAS = "tareasV2/getAccountsName";
    static final String CATALOG_CREDITS_USER = "tareasV2/getCreditsByUser/"  + IUrls.CONTEXT_USER;
    static final String CATALOG_CASES_USER = "tareasV2/getCasesByUser/"  + IUrls.CONTEXT_USER;
    static final String RESPONSABLES_DTO = "tareasV2/getResponsables";
    static final String SEGUIMIENTO_TAREA = "tareasV2/getSeguimientoTareas/" + IUrls.CONTEXT_USER + "/2";
    static final String SEGUIMIENTO_TAREA_RESPONSE = "tareasV2/getSeguiQiestionReponse/" + IUrls.CONTEXT_USER + "/";
    static final String NOTE_RESPONSE = "tareasV2/getQuestionResponseNoteOlin/" + IUrls.CONTEXT_USER + "/";
    static final String SEGUIMIENTO_NOTA_RESPONSE = "tareasV2/getSeguimientoNota/" + IUrls.CONTEXT_USER + "/";
    static final String OPTION_QUESTION_FORM = "tareasV2/getOptionQuestions";
    static final String CAT_COMANDOS_ALL = "/comandos/";
    //tareas v2 upload
    static final String U_TAREA = "tareasV2/addTarea";
    static final String U_NOTA = "tareasV2/addNota";
    // Sub Clasificacion
    static final String CATALOG_SUB_CATEGORIES = "tareasV2/getSubClasificaTarea/" + IUrls.CONTEXT_USER + "/";
    static final String CATALOG_GET_SUB_REL_CAT_QNS = "tareasV2/getRelacionSubClasificaQuestion/" + IUrls.CONTEXT_USER + "/";
    static final String CATALOG_SUB_QUESTIONS = "tareasV2/getQuestionSubClasifica/" + IUrls.CONTEXT_USER + "/";
    static final String CATALOG_SUB_QUES_DOCUMENT = "tareasV2/getDocumentQuestionSubClasifica/" + IUrls.CONTEXT_USER + "/";
    static final String CATALOG_SUB_QUES_OPTIONS = "tareasV2/getOptionsQuestionSubClasifica/" + IUrls.CONTEXT_USER + "/";
    static final String CATALOG_SUB_REL_OPTIONS = "tareasV2/getRelOptionsQuery/" + IUrls.CONTEXT_USER + "/";
    static final String CATALOG_SUB_OPT_AUTOCOMPLETE = "tareasV2/getAutocompleteOlin/" + IUrls.CONTEXT_USER + "/";
    // Options Questions
    static final String CATALOG_RELATION_OPT_QRY_FO = "tareasV2/getRelOptionsQuestionsQuery/" + IUrls.CONTEXT_USER + "/";
    static final String CATALOG_RELATION_OPT_QNS_FO = "tareasV2/getRelOptionQuestion/" + IUrls.CONTEXT_USER + "/";
    static final String CATALOG_QUESTIONS_FO = "tareasV2/getRelOptionQuestionQuestion/" + IUrls.CONTEXT_USER + "/";
    static final String CATALOG_OPTIONS_FO = "tareasV2/getOptionsRelOptionQuestionSub/" + IUrls.CONTEXT_USER + "/";
    static final String CATALOG_DOCUMENTS_FO = "tareasV2/getDocumentRelOptionQuestion/" + IUrls.CONTEXT_USER + "/";
    static final String CATALOG_AUTOCOMPLETE_FO = "tareasV2/getRelOptionQuestionAutocompleteOlin/" + IUrls.CONTEXT_USER + "/";
    //static final String QUESTION_RESOLUTION = "tareasV2/getResolutionQuestions/" + IUrls.CONTEXT_USER + "/";
    static final String ALL_VERSION_APPS = "allVersionApps";
}
