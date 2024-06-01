package net.grayfallstown.chatgptfileandcommandcenter.project;

public class UnknownProjectException extends RuntimeException {
    public UnknownProjectException(String message) {
        super(message);
    }
}