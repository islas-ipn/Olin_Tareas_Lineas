package mx.com.pendulum.olintareas.db.dao;

public class SepoMuni {

    private String description;
    private String name;
    private int _id;
    private int id_state;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int get_id() { return _id; }
    public void set_id(int _id) { this._id = _id; }

    public int getId_state() { return id_state; }
    public void setId_state(int id_state) { this.id_state = id_state; }
}