public class Holding {
    private final String symbol;
    private int quantity;
    private double averageCost;

    public Holding(String symbol, int quantity, double averageCost) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averageCost = averageCost;
    }

    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getAverageCost() { return averageCost; }

    public void addShares(int qty, double price) {
        averageCost = (averageCost * quantity + price * qty) / (quantity + qty);
        quantity += qty;
    }

    public void removeShares(int qty) {
        quantity -= qty;
    }
}
