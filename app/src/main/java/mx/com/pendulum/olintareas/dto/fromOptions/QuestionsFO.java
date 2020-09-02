package mx.com.pendulum.olintareas.dto.fromOptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;

@DatabaseTable(tableName = TableNames.CATALOG_FROM_QUESTIONS_FO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionsFO extends Questions {}



