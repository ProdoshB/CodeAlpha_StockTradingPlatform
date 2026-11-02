import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * File I/O: saves and loads the market and portfolio as a simple pipe-separated text file.
 */
public class Storage {
    private final Path file;

    public Storage(Path file) {
        this.file = file;
    }

    public void save(Market market, Portfolio portfolio) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("DAY|" + market.getDay());
        lines.add("CASH|" + portfolio.getCash());
        for (Stock s : market.all()) {
            lines.add("PRICE|" + s.getSymbol() + "|" + s.getPrice() + "|" + s.getPreviousClose());
        }
        for (Holding h : portfolio.getHoldings()) {
            lines.add("HOLD|" + h.getSymbol() + "|" + h.getQuantity() + "|" + h.getAverageCost());
        }
        for (Transaction t : portfolio.getTransactions()) {
            lines.add("TXN|" + t.type() + "|" + t.symbol() + "|" + t.quantity() + "|" + t.price() + "|" + t.time());
        }
        for (Portfolio.Snapshot s : portfolio.getHistory()) {
            lines.add("HIST|" + s.day() + "|" + s.value());
        }
        Files.write(file, lines);
    }

    /** Returns true if saved data was found and loaded. */
    public boolean load(Market market, Portfolio portfolio) throws IOException {
        if (!Files.exists(file)) {
            return false;
        }
        for (String line : Files.readAllLines(file)) {
            if (line.isBlank()) continue;
            String[] p = line.split("\\|");
            switch (p[0]) {
                case "DAY" -> market.setDay(Integer.parseInt(p[1]));
                case "CASH" -> portfolio.restoreCash(Double.parseDouble(p[1]));
                case "PRICE" -> {
                    Stock s = market.getStock(p[1]);
                    if (s != null) s.restore(Double.parseDouble(p[2]), Double.parseDouble(p[3]));
                }
                case "HOLD" -> portfolio.restoreHolding(
                        new Holding(p[1], Integer.parseInt(p[2]), Double.parseDouble(p[3])));
                case "TXN" -> portfolio.restoreTransaction(new Transaction(p[1], p[2], Integer.parseInt(p[3]),
                        Double.parseDouble(p[4]), LocalDateTime.parse(p[5])));
                case "HIST" -> portfolio.restoreSnapshot(
                        new Portfolio.Snapshot(Integer.parseInt(p[1]), Double.parseDouble(p[2])));
                default -> { }
            }
        }
        return true;
    }
}
