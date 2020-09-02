package mx.com.pendulum.olintareas.dto.tareasV2;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import mx.com.pendulum.olintareas.db.TableNames;

@DatabaseTable(tableName = TableNames.CATALOG_CUENTAS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogCuentas {
    public final static String COL_ID = "_id";
    public final static String COL_ACOUNT_NAME = "accountName";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected Long _id;

    @DatabaseField
    private String accountName;


    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
