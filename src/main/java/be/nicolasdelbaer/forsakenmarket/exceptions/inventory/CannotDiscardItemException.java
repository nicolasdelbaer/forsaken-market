package be.nicolasdelbaer.forsakenmarket.exceptions.inventory;

public class CannotDiscardItemException extends Exception {
    public CannotDiscardItemException(String msg) {
        super(msg);
    }
}
