package restoran.model;

public class Reservation {
    private int id;
    private String date;
    private String time;
    private int duration;
    private String note;
    private String status;
    private int tableId;
    private int tableCapacity;
    private String tableLocation;
    private int customerId;
    private String customerName;
    private String customerPhone;

    public Reservation() {}

    public int getId()               { return id; }
    public String getDate()          { return date; }
    public String getTime()          { return time; }
    public int getDuration()         { return duration; }
    public String getNote()          { return note; }
    public String getStatus()        { return status; }
    public int getTableId()          { return tableId; }
    public int getTableCapacity()    { return tableCapacity; }
    public String getTableLocation() { return tableLocation; }
    public int getCustomerId()       { return customerId; }
    public String getCustomerName()  { return customerName; }
    public String getCustomerPhone() { return customerPhone; }

    public void setId(int id)               { this.id = id; }
    public void setDate(String d)           { this.date = d; }
    public void setTime(String t)           { this.time = t; }
    public void setDuration(int dur)        { this.duration = dur; }
    public void setNote(String n)           { this.note = n; }
    public void setStatus(String s)         { this.status = s; }
    public void setTableId(int t)           { this.tableId = t; }
    public void setTableCapacity(int c)     { this.tableCapacity = c; }
    public void setTableLocation(String l)  { this.tableLocation = l; }
    public void setCustomerId(int c)        { this.customerId = c; }
    public void setCustomerName(String c)   { this.customerName = c; }
    public void setCustomerPhone(String p)  { this.customerPhone = p; }
}
