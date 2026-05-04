package be.nicolasdelbaer.forsakenmarket.exceptions.inventory;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;

public class BadItemOwnershipException extends ForsakenRuntimeException {
    public BadItemOwnershipException(String msg) {
        super(msg);
    }
}
