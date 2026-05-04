package be.nicolasdelbaer.forsakenmarket.exceptions.market;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenException;

public class UndefinedMarketPriceException extends ForsakenException {
    public UndefinedMarketPriceException(String msg) {
        super(msg);
    }
}
