package be.nicolasdelbaer.forsakenmarket.exceptions.inventory;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenException;

public class BadItemOwnershipException extends ForsakenException {
    public BadItemOwnershipException(String msg) {
        super(msg);
    }
}
