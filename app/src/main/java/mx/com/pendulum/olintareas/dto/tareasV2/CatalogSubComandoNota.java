package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import mx.com.pendulum.olintareas.db.TableNames;


/**
 * Created by evaleriano on 7/26/2017.
 */
@DatabaseTable(tableName = TableNames.CATALOG_SUB_COMANDO_NOTA)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogSubComandoNota extends CatalogComandoNota {


    @DatabaseField
    private String accionCode;

    public String getAccionCode() {
        return accionCode;
    }

    public void setAccionCode(String accionCode) {
        this.accionCode = accionCode;
    }
}
