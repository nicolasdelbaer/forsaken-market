package be.nicolasdelbaer.forsakenmarket.exceptions.market;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;

public class MarketItemDoesNotExistException extends ForsakenRuntimeException {
    public MarketItemDoesNotExistException(String s) {
        super(s);
    }
}
