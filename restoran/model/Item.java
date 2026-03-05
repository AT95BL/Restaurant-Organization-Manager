package restoran.model;

public class Item {
    private int id;
    private String name;
    private double price;
    private boolean onMenu;
    private String description;
    private int categoryId;
    private String categoryName;
    private String picture;

    public Item() {}
    public Item(int id, String name, double price, boolean onMenu,
                String description, int categoryId, String categoryName, String picture) {
        this.id = id; this.name = name; this.price = price;
        this.onMenu = onMenu; this.description = description;
        this.categoryId = categoryId; this.categoryName = categoryName;
        this.picture = picture;
    }

    public int getId()              { return id; }
    public String getName()         { return name; }
    public double getPrice()        { return price; }
    public boolean isOnMenu()       { return onMenu; }
    public String getDescription()  { return description; }
    public int getCategoryId()      { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getPicture()      { return picture; }

    public void setId(int id)               { this.id = id; }
    public void setName(String name)        { this.name = name; }
    public void setPrice(double price)      { this.price = price; }
    public void setOnMenu(boolean onMenu)   { this.onMenu = onMenu; }
    public void setDescription(String d)    { this.description = d; }
    public void setCategoryId(int cid)      { this.categoryId = cid; }
    public void setCategoryName(String cn)  { this.categoryName = cn; }
    public void setPicture(String p)        { this.picture = p; }

    @Override public String toString() { return name + String.format(" (%.2f KM)", price); }
}
