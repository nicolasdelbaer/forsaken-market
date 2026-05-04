package be.nicolasdelbaer.forsakenmarket.exceptions.player;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;

public class PlayerInsufficientFundsException extends ForsakenRuntimeException {
    public PlayerInsufficientFundsException(String message) {
        super(message);
    }
}
