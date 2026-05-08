package be.nicolasdelbaer.forsakenmarket.exceptions.inventory;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;

public class AlreadyBoughtException extends ForsakenRuntimeException {
    public AlreadyBoughtException(String message) {
        super(message);
    }
}
