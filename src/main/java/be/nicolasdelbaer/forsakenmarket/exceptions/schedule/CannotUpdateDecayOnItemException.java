package be.nicolasdelbaer.forsakenmarket.exceptions.schedule;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenException;

public class CannotUpdateDecayOnItemException extends ForsakenException {
    public CannotUpdateDecayOnItemException(String msg) {
        super(msg);
    }
}
