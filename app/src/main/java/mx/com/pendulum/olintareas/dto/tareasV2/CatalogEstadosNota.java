package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.table.DatabaseTable;

import mx.com.pendulum.olintareas.db.TableNames;


/**
 * Created by evaleriano on 7/26/2017.
 */
@DatabaseTable(tableName = TableNames.CATALOG_GET_ESTADO_NOTA)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogEstadosNota extends CatalogPrioridades {
}
