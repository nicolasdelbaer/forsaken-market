package be.nicolasdelbaer.forsakenmarket.exceptions.player;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenException;

public class PlayerNotFoundException extends ForsakenException {
    public PlayerNotFoundException(String msg) {
        super(msg);
    }
}
