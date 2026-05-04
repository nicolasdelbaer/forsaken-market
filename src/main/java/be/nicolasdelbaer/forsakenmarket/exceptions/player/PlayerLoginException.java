package be.nicolasdelbaer.forsakenmarket.exceptions.player;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;

public class PlayerLoginException extends ForsakenRuntimeException {
    public PlayerLoginException(String message) {
        super(message);
    }
}
