package be.nicolasdelbaer.forsakenmarket.exceptions.auth;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenException;

public class EmailAlreadyUsedException extends ForsakenException {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
