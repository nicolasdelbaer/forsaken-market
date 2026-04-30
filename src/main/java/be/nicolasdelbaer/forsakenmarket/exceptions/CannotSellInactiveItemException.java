package be.nicolasdelbaer.forsakenmarket.exceptions;

public class CannotSellInactiveItemException extends Exception {
    public CannotSellInactiveItemException(String msg) {
        super(msg);
    }
}
