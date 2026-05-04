package be.nicolasdelbaer.forsakenmarket.exceptions.inventory;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenException;

public class CannotDiscardItemException extends ForsakenException {
    public CannotDiscardItemException(String msg) {
        super(msg);
    }
}
