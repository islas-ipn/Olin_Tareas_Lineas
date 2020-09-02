package mx.com.pendulum.olintareas.dto.fromOptions;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.dto.SubClasifica.RelOptionQuery;

@DatabaseTable(tableName = TableNames.CATALOG_FORM_REL_OPT_QRY_FO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelOptionQueryFO extends RelOptionQuery implements Serializable {
}
