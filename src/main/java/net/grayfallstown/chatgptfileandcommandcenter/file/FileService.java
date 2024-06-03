package net.grayfallstown.chatgptfileandcommandcenter.file;

import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    public void writeFile(String path, String content, ProjectConfig projectConfig) {
        try {
            Path filePath = validatePathInsideWorkingDir(path, projectConfig);
            FileUtils.forceMkdirParent(filePath.getParent().toFile());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            Files.write(filePath, content.getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            logger.info("File written: {}", path);
        } catch (IOException e) {
            logger.error("Failed to write file: {}", path, e);
            throw new FileOperationException("Failed to write file: " + path + ": " + e.getMessage(), e);
        }
    }

    public void deleteFile(String path, ProjectConfig projectConfig) {
        try {
            Path filePath = validatePathInsideWorkingDir(path, projectConfig);
            Files.delete(filePath);
            logger.info("File deleted: {}", path);
        } catch (IOException e) {
            logger.error("Failed to delete file: {}", path, e);
            throw new FileOperationException("Failed to delete file: " + path + ": " + e.getMessage(), e);
        }
    }

    public void moveFile(String from, String to, ProjectConfig projectConfig) {
        try {
            Path fromPath = validatePathInsideWorkingDir(from, projectConfig);
            Path toPath = validatePathInsideWorkingDir(to, projectConfig);
            FileUtils.forceMkdirParent(toPath.getParent().toFile());
            if (toPath.toFile().exists()) {
                Files.delete(toPath);
            }
            Files.move(fromPath, toPath);
            logger.info("File moved from {} to {}", from, to);
        } catch (IOException e) {
            logger.error("Failed to move file from {} to {}", from, to, e);
            throw new FileOperationException("Failed to move file from " + from + " to " + to + ": " + e.getMessage(), e);
        }
    }

    public String readFile(String path, ProjectConfig projectConfig) {
        try {
            Path filePath = validatePathInsideWorkingDir(path, projectConfig);
            String content = new String(Files.readAllBytes(filePath));
            logger.info("File read: {}", path);
            return content;
        } catch (IOException e) {
            logger.error("Failed to read file: {}", path, e);
            throw new FileOperationException("Failed to read file: " + path + ": " + e.getMessage(), e);
        }
    }

    public String listFiles(String path, boolean recursive, boolean ignoreGitIgnore, boolean foldersOnly, ProjectConfig projectConfig) {
        try {
            Path dirPath = validatePathInsideWorkingDir(path, projectConfig);
            StringBuilder fileList = new StringBuilder();
            Files.walk(dirPath, recursive ? Integer.MAX_VALUE : 1)
                .filter((filterPath) -> {
                    if (foldersOnly) {
                        return Files.isDirectory(filterPath);
                    } else {
                        return Files.isRegularFile(filterPath) || Files.isDirectory(filterPath);
                    }
                })
                .forEach(p -> fileList.append(p.toString()).append("\n"));
            logger.info("Files listed in directory: {}", path);
            return fileList.toString();
        } catch (IOException e) {
            logger.error("Failed to list files in directory: {}", path, e);
            throw new FileOperationException("Failed to list files in directory: " + path + ": " + e.getMessage(), e);
        }
    }

    private Path validatePathInsideWorkingDir(String path, ProjectConfig projectConfig) {
        Path filePath;
        try {
            filePath = Paths.get(path).normalize();
            if (!filePath.isAbsolute()) {
                filePath = Paths.get(projectConfig.getWorkingDir(), path).normalize();
            }
            Path workingDirPath = Paths.get(projectConfig.getWorkingDir()).normalize();
            if (!filePath.startsWith(workingDirPath)) {
                throw new FileOperationException("Operation not allowed outside of working directory: " + path);
            }
        } catch (InvalidPathException e) {
            throw new FileOperationException("Invalid path: " + path, e);
        }
        return filePath;
    }
}
