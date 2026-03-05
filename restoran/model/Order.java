package restoran.model;

public class Order {
    private int id;
    private String status;
    private String timestamp;
    private String note;
    private String employeeName;
    private String customerName;
    private double discountPct;
    private double paidAmount;
    private String paymentType;
    private int tableId;

    public Order() {}

    public int getId()              { return id; }
    public String getStatus()       { return status; }
    public String getTimestamp()    { return timestamp; }
    public String getNote()         { return note; }
    public String getEmployeeName() { return employeeName; }
    public String getCustomerName() { return customerName; }
    public double getDiscountPct()  { return discountPct; }
    public double getPaidAmount()   { return paidAmount; }
    public String getPaymentType()  { return paymentType; }
    public int getTableId()         { return tableId; }

    public void setId(int id)               { this.id = id; }
    public void setStatus(String s)         { this.status = s; }
    public void setTimestamp(String t)      { this.timestamp = t; }
    public void setNote(String n)           { this.note = n; }
    public void setEmployeeName(String e)   { this.employeeName = e; }
    public void setCustomerName(String c)   { this.customerName = c; }
    public void setDiscountPct(double d)    { this.discountPct = d; }
    public void setPaidAmount(double p)     { this.paidAmount = p; }
    public void setPaymentType(String pt)   { this.paymentType = pt; }
    public void setTableId(int t)           { this.tableId = t; }
}
