package be.nicolasdelbaer.forsakenmarket.exceptions.market;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenException;

public class CannotSellInactiveItemException extends ForsakenException {
    public CannotSellInactiveItemException(String msg) {
        super(msg);
    }
}
