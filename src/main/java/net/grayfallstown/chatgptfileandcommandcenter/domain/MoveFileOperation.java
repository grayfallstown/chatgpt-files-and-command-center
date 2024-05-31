package net.grayfallstown.chatgptfileandcommandcenter.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MoveFileOperation extends FileOperation {

    private final String fromPath;
    private final String toPath;

    public MoveFileOperation(String fromPath, String toPath) {
        this.fromPath = fromPath;
        this.toPath = toPath;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public void execute() throws IOException {
        Files.move(Paths.get(fromPath), Paths.get(toPath));
    }
}
