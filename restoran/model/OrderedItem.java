package restoran.model;

public class OrderedItem {
    private int id;
    private int quantity;
    private double price;
    private String itemName;

    public OrderedItem(int id, int quantity, double price, String itemName) {
        this.id = id; this.quantity = quantity;
        this.price = price; this.itemName = itemName;
    }

    public int getId()          { return id; }
    public int getQuantity()    { return quantity; }
    public double getPrice()    { return price; }
    public String getItemName() { return itemName; }
}
