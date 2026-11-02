import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A user's cash, holdings, transaction log and value history.
 */
public class Portfolio {
    public static final double STARTING_CASH = 100000;

    public record Snapshot(int day, double value) {}

    private double cash = STARTING_CASH;
    private final Map<String, Holding> holdings = new LinkedHashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private final List<Snapshot> history = new ArrayList<>();

    public double getCash() { return cash; }
    public Collection<Holding> getHoldings() { return holdings.values(); }
    public List<Transaction> getTransactions() { return transactions; }
    public List<Snapshot> getHistory() { return history; }

    public void buy(Stock stock, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        double cost = stock.getPrice() * qty;
        if (cost > cash) {
            throw new IllegalArgumentException(
                    String.format("Insufficient funds: need Rs.%.2f but only Rs.%.2f available.", cost, cash));
        }
        cash -= cost;
        Holding h = holdings.get(stock.getSymbol());
        if (h == null) {
            holdings.put(stock.getSymbol(), new Holding(stock.getSymbol(), qty, stock.getPrice()));
        } else {
            h.addShares(qty, stock.getPrice());
        }
        transactions.add(new Transaction("BUY", stock.getSymbol(), qty, stock.getPrice(), LocalDateTime.now()));
    }

    public void sell(Stock stock, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        Holding h = holdings.get(stock.getSymbol());
        if (h == null || h.getQuantity() < qty) {
            int owned = h == null ? 0 : h.getQuantity();
            throw new IllegalArgumentException("You only own " + owned + " share(s) of " + stock.getSymbol() + ".");
        }
        cash += stock.getPrice() * qty;
        h.removeShares(qty);
        if (h.getQuantity() == 0) {
            holdings.remove(stock.getSymbol());
        }
        transactions.add(new Transaction("SELL", stock.getSymbol(), qty, stock.getPrice(), LocalDateTime.now()));
    }

    public double holdingsValue(Market market) {
        double total = 0;
        for (Holding h : holdings.values()) {
            total += h.getQuantity() * market.getStock(h.getSymbol()).getPrice();
        }
        return total;
    }

    public double totalValue(Market market) {
        return cash + holdingsValue(market);
    }

    public void recordSnapshot(int day, Market market) {
        history.add(new Snapshot(day, totalValue(market)));
    }

    // ----- used when loading saved data -----
    public void restoreCash(double cash) { this.cash = cash; }
    public void restoreHolding(Holding h) { holdings.put(h.getSymbol(), h); }
    public void restoreTransaction(Transaction t) { transactions.add(t); }
    public void restoreSnapshot(Snapshot s) { history.add(s); }
}
