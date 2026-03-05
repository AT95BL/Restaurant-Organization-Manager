package restoran.model;

public class Discount {
    private int id;
    private String code;
    private double percentage;

    public Discount(int id, String code, double percentage) {
        this.id = id; this.code = code; this.percentage = percentage;
    }

    public int getId()            { return id; }
    public String getCode()       { return code; }
    public double getPercentage() { return percentage; }

    @Override public String toString() { return code + " (" + percentage + "%)"; }
}
