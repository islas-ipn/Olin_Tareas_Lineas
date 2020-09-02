package mx.com.pendulum.olintareas.dto.tareasV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.table.DatabaseTable;

import mx.com.pendulum.olintareas.db.TableNames;

/**
 * Created by evaleriano on 8/2/2017.
 */
@DatabaseTable(tableName = TableNames.NOTE_RESPONSE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteResponseDTO  extends ResponseTask{


}
