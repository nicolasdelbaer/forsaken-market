package be.nicolasdelbaer.forsakenmarket.exceptions.player;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenException;

public class PlayerInsufficientFundsException extends ForsakenException {
    public PlayerInsufficientFundsException(String message) {
        super(message);
    }
}
