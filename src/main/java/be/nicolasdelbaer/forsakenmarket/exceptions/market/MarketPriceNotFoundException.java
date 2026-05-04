package be.nicolasdelbaer.forsakenmarket.exceptions.market;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;

public class MarketPriceNotFoundException extends ForsakenRuntimeException {
    public MarketPriceNotFoundException(String msg) {
        super(msg);
    }
}
