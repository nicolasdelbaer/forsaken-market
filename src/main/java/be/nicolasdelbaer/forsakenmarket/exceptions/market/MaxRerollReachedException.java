package be.nicolasdelbaer.forsakenmarket.exceptions.market;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;

public class MaxRerollReachedException extends ForsakenRuntimeException {
    public MaxRerollReachedException(String msg) {
        super(msg);
    }
}
