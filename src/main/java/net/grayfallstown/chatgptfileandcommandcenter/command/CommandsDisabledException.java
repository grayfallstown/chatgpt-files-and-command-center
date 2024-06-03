package net.grayfallstown.chatgptfileandcommandcenter.command;

public class CommandsDisabledException extends RuntimeException {
    public CommandsDisabledException(String message) {
        super(message);
    }
}
