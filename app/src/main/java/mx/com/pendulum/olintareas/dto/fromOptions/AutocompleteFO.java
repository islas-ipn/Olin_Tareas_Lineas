package mx.com.pendulum.olintareas.dto.fromOptions;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.dto.tareasV2.Autocomplete;

@DatabaseTable(tableName = TableNames.CATALOG_FORM_AUTOCOMPLETE_FO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutocompleteFO extends Autocomplete implements Serializable {

}
