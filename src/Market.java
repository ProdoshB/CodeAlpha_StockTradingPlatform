import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Simulated stock market. Prices are fictional and move randomly each simulated day.
 */
public class Market {
    private final Map<String, Stock> stocks = new LinkedHashMap<>();
    private final Random random = new Random();
    private int day = 0;

    public Market() {
        add(new Stock("TCS", "Tata Consultancy", 3900));
        add(new Stock("INFY", "Infosys", 1500));
        add(new Stock("RELI", "Reliance Industries", 2900));
        add(new Stock("HDFC", "HDFC Bank", 1650));
        add(new Stock("WIPR", "Wipro", 450));
    }

    private void add(Stock s) {
        stocks.put(s.getSymbol(), s);
    }

    public Stock getStock(String symbol) {
        return stocks.get(symbol.trim().toUpperCase());
    }

    public Collection<Stock> all() {
        return stocks.values();
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    /** Advances one day; each price moves randomly by up to +/-5%. */
    public void advanceDay() {
        day++;
        for (Stock s : stocks.values()) {
            double move = (random.nextDouble() - 0.5) * 0.10;
            double newPrice = Math.max(1.0, s.getPrice() * (1 + move));
            s.updatePrice(Math.round(newPrice * 100.0) / 100.0);
        }
    }
}
