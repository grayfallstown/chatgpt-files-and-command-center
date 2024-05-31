package net.grayfallstown.chatgptfileandcommandcenter.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WriteFileOperation extends FileOperation {

    private final File file;

    public WriteFileOperation(String path, String content) {
        this.file = new File();
        this.file.setPath(path);
        this.file.setContent(content);
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void execute() throws IOException {
        Files.write(Paths.get(file.getPath()), file.getContent().getBytes());
    }
}
