package be.nicolasdelbaer.forsakenmarket.exceptions.player;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;

public class PlayerNotFoundException extends ForsakenRuntimeException {
    public PlayerNotFoundException(String msg) {
        super(msg);
    }
}
