package net.grayfallstown.chatgptfileandcommandcenter.history;

public class GitOperationException extends RuntimeException {
    public GitOperationException(String message) {
        super(message);
    }

    public GitOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}