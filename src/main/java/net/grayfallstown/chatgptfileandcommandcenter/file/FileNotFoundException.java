package net.grayfallstown.chatgptfileandcommandcenter.file;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(String message) {
        super(message);
    }
}