package be.nicolasdelbaer.forsakenmarket.exceptions.market;

public class CannotSellInactiveItemException extends Exception {
    public CannotSellInactiveItemException(String msg) {
        super(msg);
    }
}
