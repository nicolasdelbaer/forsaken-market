package be.nicolasdelbaer.forsakenmarket.exceptions;

public class PlayerInsufficientFundsException extends Exception {
    public PlayerInsufficientFundsException(String message) {
        super(message);
    }
}
