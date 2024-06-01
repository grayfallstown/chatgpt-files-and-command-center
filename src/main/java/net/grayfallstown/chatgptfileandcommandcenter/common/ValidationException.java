package net.grayfallstown.chatgptfileandcommandcenter.common;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
