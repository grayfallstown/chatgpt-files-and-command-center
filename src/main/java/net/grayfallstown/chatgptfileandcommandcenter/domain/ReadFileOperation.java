package net.grayfallstown.chatgptfileandcommandcenter.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadFileOperation extends FileOperation {

    private final String path;
    private File file;

    public ReadFileOperation(String path) {
        this.path = path;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void execute() throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(path));
        file = new File();
        file.setPath(path);
        file.setContent(new String(content));
    }
}
