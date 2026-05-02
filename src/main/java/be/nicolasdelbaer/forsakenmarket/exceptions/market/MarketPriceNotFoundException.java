package be.nicolasdelbaer.forsakenmarket.exceptions.market;

public class MarketPriceNotFoundException extends RuntimeException {
    public MarketPriceNotFoundException(String msg) {
        super(msg);
    }
}
