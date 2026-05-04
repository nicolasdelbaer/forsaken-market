package be.nicolasdelbaer.forsakenmarket.exceptions.market;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenException;

public class MarketItemDoesNotExistException extends ForsakenException {
    public MarketItemDoesNotExistException(String s) {
        super(s);
    }
}
