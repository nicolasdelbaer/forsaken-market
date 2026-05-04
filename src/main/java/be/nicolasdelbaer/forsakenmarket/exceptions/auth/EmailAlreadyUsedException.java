package be.nicolasdelbaer.forsakenmarket.exceptions.auth;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;

public class EmailAlreadyUsedException extends ForsakenRuntimeException {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
