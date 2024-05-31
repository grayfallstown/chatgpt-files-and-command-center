package net.grayfallstown.chatgptfileandcommandcenter.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DeleteFileOperation extends FileOperation {

    private final String path;

    public DeleteFileOperation(String path) {
        this.path = path;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public void execute() throws IOException {
        Files.deleteIfExists(Paths.get(path));
    }
}
