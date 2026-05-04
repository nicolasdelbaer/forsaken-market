package be.nicolasdelbaer.forsakenmarket.exceptions.inventory;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;

public class CannotDiscardItemException extends ForsakenRuntimeException {
    public CannotDiscardItemException(String msg) {
        super(msg);
    }
}
