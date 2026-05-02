package be.nicolasdelbaer.forsakenmarket.exceptions.inventory;

public class BadItemOwnershipException extends Exception {
    public BadItemOwnershipException(String msg) {
        super(msg);
    }
}
