package mx.com.pendulum.olintareas.db.dao;

public class SepoColony {

    private String name;
    private int _id;
    private int id_state;
    private int id_municipality;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int get_id() { return _id; }
    public void set_id(int _id) { this._id = _id; }

    public int getId_state() { return id_state; }
    public void setId_state(int id_state) { this.id_state = id_state; }

    public int getId_municipality() { return id_municipality; }
    public void setId_municipality(int id_municipality) { this.id_municipality = id_municipality; }
}