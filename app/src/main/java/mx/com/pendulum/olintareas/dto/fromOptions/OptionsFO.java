package mx.com.pendulum.olintareas.dto.fromOptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;

import mx.com.pendulum.olintareas.db.TableNames;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;

@DatabaseTable(tableName = TableNames.CATALOG_FORM_OPTIONS_FO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OptionsFO extends Options implements Serializable {

    private ArrayList<Options> obtainObject(ArrayList<OptionsFO> listFO) {
        return new ArrayList<Options>(listFO);
    }

}
