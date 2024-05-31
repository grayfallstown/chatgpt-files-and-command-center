package net.grayfallstown.chatgptfileandcommandcenter.web;

import net.grayfallstown.chatgptfileandcommandcenter.config.ProjectConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final ProjectConfig projectConfig;

    @Autowired
    public FileService(ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    public void writeFile(String path, String content) throws IOException {
        Path filePath = Paths.get(projectConfig.getWorkingDir(), path);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content);
    }

    public void deleteFile(String path) throws IOException {
        Path filePath = Paths.get(projectConfig.getWorkingDir(), path);
        Files.deleteIfExists(filePath);
    }

    public void moveFile(String from, String to) throws IOException {
        Path fromPath = Paths.get(projectConfig.getWorkingDir(), from);
        Path toPath = Paths.get(projectConfig.getWorkingDir(), to);
        Files.createDirectories(toPath.getParent());
        Files.move(fromPath, toPath);
    }

    public String readFile(String path) throws IOException {
        Path filePath = Paths.get(projectConfig.getWorkingDir(), path);
        return Files.readString(filePath);
    }

    public String listFiles(String path, boolean recursive, boolean ignoreGitIgnore) throws IOException {
        Path dirPath = Paths.get(projectConfig.getWorkingDir(), path).toAbsolutePath().normalize();
        List<Path> files;

        if (recursive) {
            files = Files.walk(dirPath)
                    .filter(p -> !Files.isDirectory(p))
                    .collect(Collectors.toList());
        } else {
            files = Files.list(dirPath)
                    .filter(p -> !Files.isDirectory(p))
                    .collect(Collectors.toList());
        }

        if (!ignoreGitIgnore) {
            // Apply .gitignore rules if required
            // For simplicity, assume a method filterGitIgnoredFiles exists
            files = filterGitIgnoredFiles(dirPath, files);
        }

        return files.stream()
                .map(Path::toString)
                .collect(Collectors.joining("\n"));
    }

    private List<Path> filterGitIgnoredFiles(Path dirPath, List<Path> files) {
        // Implement logic to filter files based on .gitignore rules
        // This is a placeholder method
        return files;
    }
}
