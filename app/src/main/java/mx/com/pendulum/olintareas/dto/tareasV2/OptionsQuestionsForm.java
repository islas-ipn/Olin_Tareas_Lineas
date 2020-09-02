package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import mx.com.pendulum.olintareas.db.TableNames;


@DatabaseTable(tableName = TableNames.OPTIONS_QUESTIONS_FORM)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OptionsQuestionsForm {
    public final static String COL_ID = "_id";
    public final static String COLQUESTIOND = "questions";
    public final static String COL_ID_OPTION = "idOption";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected Long _id;

    @DatabaseField(columnName = COL_ID_OPTION)
    private Long idOption;

    @DatabaseField(columnName = COLQUESTIOND)
    private String questions;


    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public Long getIdOption() {
        return idOption;
    }

    public void setIdOption(Long idOption) {
        this.idOption = idOption;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(List<Questions> questions) {
        String tmp = "";

        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
//            Gson gson = new Gson();
            tmp = gson.toJson(questions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.questions = tmp;
    }
}
