package mx.com.pendulum.olintareas.db.dao;

public class SepoCP {

    private String name;
    private int id_state;
    private int id_muni;
    private int _id;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getId_state() { return id_state; }
    public void setId_state(int id_state) { this.id_state = id_state; }

    public int getId_muni() { return id_muni; }
    public void setId_muni(int id_muni) { this.id_muni = id_muni; }

    public int get_id() { return _id; }
    public void set_id(int _id) { this._id = _id; }
}