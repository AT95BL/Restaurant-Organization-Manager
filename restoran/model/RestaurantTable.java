package restoran.model;

public class RestaurantTable {
    private int id;
    private int capacity;
    private String location;
    private String status;

    public RestaurantTable(int id, int capacity, String location, String status) {
        this.id = id; this.capacity = capacity;
        this.location = location; this.status = status;
    }

    public int getId()          { return id; }
    public int getCapacity()    { return capacity; }
    public String getLocation() { return location; }
    public String getStatus()   { return status; }

    @Override public String toString() {
        return "Stol " + id + " (" + capacity + " mjesta, " + location + ") — " + status;
    }
}
