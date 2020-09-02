package mx.com.pendulum.olintareas.dto.tareasV2;

import java.io.Serializable;
import java.util.ArrayList;


public class Section implements Serializable {

    private Long id;
    private String sectionName;
    private ArrayList<Questions> questions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public ArrayList<Questions> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Questions> questions) {
        this.questions = questions;
    }
}
