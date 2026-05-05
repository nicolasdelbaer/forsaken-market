package be.nicolasdelbaer.forsakenmarket.exceptions.inventory;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;

public class CollectionNotFoundException extends ForsakenRuntimeException {

    public CollectionNotFoundException(String message) {
        super(message);
    }
}
