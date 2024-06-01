package net.grayfallstown.chatgptfileandcommandcenter.file;

import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    public void writeFile(String path, String content, ProjectConfig projectConfig) {
        try {
            Path filePath = getValidatedPath(path, projectConfig);
            Files.write(filePath, content.getBytes());
            logger.info("File written: {}", path);
        } catch (IOException | InvalidPathException e) {
            logger.error("Failed to write file: {}", path, e);
            throw new FileOperationException("Failed to write file: " + path + ": " + e.getMessage());
        }
    }

    public void deleteFile(String path, ProjectConfig projectConfig) {
        try {
            Path filePath = getValidatedPath(path, projectConfig);
            Files.delete(filePath);
            logger.info("File deleted: {}", path);
        } catch (IOException | InvalidPathException e) {
            logger.error("Failed to delete file: {}", path, e);
            throw new FileOperationException("Failed to delete file: " + path + ": " + e.getMessage());
        }
    }

    public void moveFile(String from, String to, ProjectConfig projectConfig) {
        try {
            Path fromPath = getValidatedPath(from, projectConfig);
            Path toPath = getValidatedPath(to, projectConfig);
            Files.move(fromPath, toPath);
            logger.info("File moved from {} to {}", from, to);
        } catch (IOException | InvalidPathException e) {
            logger.error("Failed to move file from {} to {}", from, to, e);
            throw new FileOperationException("Failed to move file from " + from + " to " + to + ": " + e.getMessage());
        }
    }

    public String readFile(String path, ProjectConfig projectConfig) {
        try {
            Path filePath = getValidatedPath(path, projectConfig);
            String content = new String(Files.readAllBytes(filePath));
            logger.info("File read: {}", path);
            return content;
        } catch (IOException | InvalidPathException e) {
            logger.error("Failed to read file: {}", path, e);
            throw new FileOperationException("Failed to read file: " + path + ": " + e.getMessage());
        }
    }

    public String listFiles(String path, boolean recursive, boolean ignoreGitIgnore, ProjectConfig projectConfig) {
        try {
            logger.info("projectConfig: {}", projectConfig);
            Path dirPath = getValidatedPath(path, projectConfig);
            StringBuilder fileList = new StringBuilder();
            Files.walk(dirPath, recursive ? Integer.MAX_VALUE : 1)
                .filter(Files::isRegularFile)
                .forEach(p -> fileList.append(p.toString()).append("\n"));
            logger.info("Files listed in directory: {}", path);
            return fileList.toString();
        } catch (IOException | InvalidPathException e) {
            logger.error("Failed to list files in directory: {}", path, e);
            throw new FileOperationException("Failed to list files in directory: " + path + ": " + e.getMessage());
        }
    }

    private Path getValidatedPath(String path, ProjectConfig projectConfig) {
        Path filePath = Paths.get(path).normalize();
        if (projectConfig.isRestrictToWorkingDir()) {
            Path workingDirPath = Paths.get(projectConfig.getWorkingDir()).normalize().toAbsolutePath();
            Path absoluteFilePath = filePath.toAbsolutePath();

            if (!absoluteFilePath.startsWith(workingDirPath)) {
                throw new InvalidPathException(path, "Operation not allowed outside of working directory");
            }
        }
        return filePath;
    }
}
