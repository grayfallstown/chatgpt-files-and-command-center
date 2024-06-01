package net.grayfallstown.chatgptfileandcommandcenter.file;

import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FileService {

    public void writeFile(String path, String content, ProjectConfig projectConfig) throws IOException {
        java.nio.file.Path filePath = Paths.get(projectConfig.getDir(), path);
        Files.write(filePath, content.getBytes());
    }

    public void deleteFile(String path, ProjectConfig projectConfig) throws IOException {
        java.nio.file.Path filePath = Paths.get(projectConfig.getDir(), path);
        Files.delete(filePath);
    }

    public void moveFile(String from, String to, ProjectConfig projectConfig) throws IOException {
        java.nio.file.Path fromPath = Paths.get(projectConfig.getDir(), from);
        java.nio.file.Path toPath = Paths.get(projectConfig.getDir(), to);
        Files.move(fromPath, toPath);
    }

    public String readFile(String path, ProjectConfig projectConfig) throws IOException {
        java.nio.file.Path filePath = Paths.get(projectConfig.getDir(), path);
        return new String(Files.readAllBytes(filePath));
    }

    public String listFiles(String path, boolean recursive, boolean ignoreGitIgnore, ProjectConfig projectConfig) throws IOException {
        // Implementation of listFiles based on the requirements
        // For simplicity, we can return a string representation of files in the directory
        java.nio.file.Path dirPath = Paths.get(projectConfig.getDir(), path);
        StringBuilder fileList = new StringBuilder();
        Files.walk(dirPath, recursive ? Integer.MAX_VALUE : 1)
            .filter(Files::isRegularFile)
            .forEach(p -> fileList.append(p.toString()).append("\n"));
        return fileList.toString();
    }
}
