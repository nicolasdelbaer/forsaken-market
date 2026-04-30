package be.nicolasdelbaer.forsakenmarket.exceptions.auth;

public class EmailAlreadyUsedException extends Exception {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
