package net.grayfallstown.chatgptfileandcommandcenter.command;

public class ShellNotFoundException extends RuntimeException {
    public ShellNotFoundException(String message) {
        super(message);
    }
}