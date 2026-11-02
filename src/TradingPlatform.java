import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Console UI for the Stock Trading Platform simulation.
 */
public class TradingPlatform {
    private static final Scanner in = new Scanner(System.in);
    private static final Market market = new Market();
    private static final Portfolio portfolio = new Portfolio();
    private static final Storage storage = new Storage(Path.of("portfolio.dat"));

    public static void main(String[] args) {
        System.out.println("=== CodeAlpha Stock Trading Platform (simulation) ===");
        try {
            if (storage.load(market, portfolio)) {
                System.out.println("Loaded your saved portfolio (day " + market.getDay() + ").");
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not read saved data, starting fresh: " + e.getMessage());
        }
        if (portfolio.getHistory().isEmpty()) {
            portfolio.recordSnapshot(market.getDay(), market);
        }

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.printf("Day %d | Cash: Rs.%.2f%n", market.getDay(), portfolio.getCash());
            System.out.println("1. View market");
            System.out.println("2. Buy stock");
            System.out.println("3. Sell stock");
            System.out.println("4. View portfolio");
            System.out.println("5. Transaction history");
            System.out.println("6. Advance to next day");
            System.out.println("7. Portfolio performance over time");
            System.out.println("0. Save & exit");
            switch (readLine("Choose an option: ")) {
                case "1" -> showMarket();
                case "2" -> trade(true);
                case "3" -> trade(false);
                case "4" -> showPortfolio();
                case "5" -> showTransactions();
                case "6" -> nextDay();
                case "7" -> showPerformance();
                case "0" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
        try {
            storage.save(market, portfolio);
            System.out.println("Portfolio saved. Goodbye!");
        } catch (IOException e) {
            System.out.println("Could not save portfolio: " + e.getMessage());
        }
    }

    private static void showMarket() {
        System.out.printf("%-6s %-22s %10s %9s%n", "Symbol", "Company", "Price", "Change");
        for (Stock s : market.all()) {
            System.out.printf("%-6s %-22s %10.2f %+8.2f%%%n", s.getSymbol(), s.getName(), s.getPrice(),
                    s.getChangePercent());
        }
    }

    private static void trade(boolean buying) {
        showMarket();
        Stock stock = market.getStock(readLine("Symbol: "));
        if (stock == null) {
            System.out.println("Unknown symbol.");
            return;
        }
        try {
            int qty = Integer.parseInt(readLine("Quantity: "));
            if (buying) {
                portfolio.buy(stock, qty);
                System.out.printf("Bought %d x %s at Rs.%.2f%n", qty, stock.getSymbol(), stock.getPrice());
            } else {
                portfolio.sell(stock, qty);
                System.out.printf("Sold %d x %s at Rs.%.2f%n", qty, stock.getSymbol(), stock.getPrice());
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a whole number.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showPortfolio() {
        if (portfolio.getHoldings().isEmpty()) {
            System.out.println("You do not own any stocks yet.");
        } else {
            System.out.printf("%-6s %5s %10s %10s %12s %10s%n", "Symbol", "Qty", "Avg cost", "Price", "Value",
                    "P/L");
            for (Holding h : portfolio.getHoldings()) {
                double price = market.getStock(h.getSymbol()).getPrice();
                double value = price * h.getQuantity();
                double pl = value - h.getAverageCost() * h.getQuantity();
                System.out.printf("%-6s %5d %10.2f %10.2f %12.2f %+10.2f%n", h.getSymbol(), h.getQuantity(),
                        h.getAverageCost(), price, value, pl);
            }
        }
        double total = portfolio.totalValue(market);
        System.out.printf("Cash: Rs.%.2f | Holdings: Rs.%.2f | Total: Rs.%.2f%n", portfolio.getCash(),
                portfolio.holdingsValue(market), total);
        System.out.printf("Overall return: %+.2f%%%n",
                (total - Portfolio.STARTING_CASH) / Portfolio.STARTING_CASH * 100);
    }

    private static void showTransactions() {
        if (portfolio.getTransactions().isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.printf("%-17s %-5s %-6s %5s %10s %12s%n", "Time", "Type", "Symbol", "Qty", "Price", "Total");
        for (Transaction t : portfolio.getTransactions()) {
            System.out.printf("%-17s %-5s %-6s %5d %10.2f %12.2f%n", t.time().format(fmt), t.type(), t.symbol(),
                    t.quantity(), t.price(), t.total());
        }
    }

    private static void nextDay() {
        market.advanceDay();
        portfolio.recordSnapshot(market.getDay(), market);
        System.out.println("Market moved to day " + market.getDay() + ".");
        showMarket();
    }

    private static void showPerformance() {
        System.out.println("Portfolio value over time (20 # = starting value, each # = 0.5% change):");
        for (Portfolio.Snapshot s : portfolio.getHistory()) {
            double ratio = s.value() / Portfolio.STARTING_CASH;
            String bar = "#".repeat((int) Math.max(0, Math.round(20 + (ratio - 1) * 100 * 2)));
            System.out.printf("Day %3d  Rs.%12.2f  %+6.2f%%  %s%n", s.day(), s.value(), (ratio - 1) * 100, bar);
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!in.hasNextLine()) {
            return "0";
        }
        return in.nextLine().trim();
    }
}
