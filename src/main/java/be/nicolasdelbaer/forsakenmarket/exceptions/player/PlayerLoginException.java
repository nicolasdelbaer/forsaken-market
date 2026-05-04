package be.nicolasdelbaer.forsakenmarket.exceptions.player;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenException;

public class PlayerLoginException extends ForsakenException {
    public PlayerLoginException(String message) {
        super(message);
    }
}
