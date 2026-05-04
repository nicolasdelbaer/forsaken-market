package be.nicolasdelbaer.forsakenmarket.exceptions.market;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;

public class CannotSellInactiveItemException extends ForsakenRuntimeException {
    public CannotSellInactiveItemException(String msg) {
        super(msg);
    }
}
