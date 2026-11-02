public class Stock {
    private final String symbol;
    private final String name;
    private double price;
    private double previousClose;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.previousClose = price;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    public double getChangePercent() {
        return previousClose == 0 ? 0 : (price - previousClose) / previousClose * 100;
    }

    /** Moves to a new price, remembering the old one as the previous close. */
    public void updatePrice(double newPrice) {
        this.previousClose = this.price;
        this.price = newPrice;
    }

    /** Used when loading saved data. */
    public void restore(double price, double previousClose) {
        this.price = price;
        this.previousClose = previousClose;
    }

    public double getPreviousClose() { return previousClose; }
}
