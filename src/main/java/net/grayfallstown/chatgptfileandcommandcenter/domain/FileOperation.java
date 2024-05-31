package net.grayfallstown.chatgptfileandcommandcenter.domain;

import java.io.IOException;

public abstract class FileOperation {
    public abstract File getFile();
    public abstract void execute() throws IOException;
}
