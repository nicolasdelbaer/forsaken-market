package be.nicolasdelbaer.forsakenmarket.exceptions.market;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;

public class UndefinedMarketPriceException extends ForsakenRuntimeException {
    public UndefinedMarketPriceException(String msg) {
        super(msg);
    }

    public UndefinedMarketPriceException() {
        super("Market Price not found, missing init?");
    }
}
