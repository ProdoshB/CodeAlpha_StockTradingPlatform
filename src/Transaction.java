import java.time.LocalDateTime;

public record Transaction(String type, String symbol, int quantity, double price, LocalDateTime time) {

    public double total() {
        return quantity * price;
    }
}
