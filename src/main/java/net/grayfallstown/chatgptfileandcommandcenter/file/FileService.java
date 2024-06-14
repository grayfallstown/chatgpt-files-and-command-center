package net.grayfallstown.chatgptfileandcommandcenter.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.ignore.IgnoreNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.grayfallstown.chatgptfileandcommandcenter.history.GitSyncService;
import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private FileAPIConfig fileAPIConfig;

    @Autowired
    private GitSyncService gitSyncService;

    public void writeFile(String path, String content, ProjectConfig projectConfig) {
        try {
            Path filePath = validatePathInsideWorkingDir(path, projectConfig);
            FileUtils.forceMkdirParent(filePath.toFile());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            logger.info("filepath: {}, path: {}", filePath, path);
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
            throw new FileOperationException("Failed to move file from " + from + " to " + to + ": " + e.getMessage(),
                    e);
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

    public String listFiles(String path, boolean recursive, boolean ignoreGitIgnore, boolean foldersOnly,
            ProjectConfig projectConfig) {
        try {
            Path dirPath = validatePathInsideWorkingDir(path, projectConfig);
            StringBuilder fileList = new StringBuilder();
            IgnoreNode ignoreNode = gitSyncService.getIgnoreNode(dirPath);
            String responseTooLargeErrror = "\n\nERROR: File list too large, output truncated";
            int limit = fileAPIConfig.getResponseSizeLimit() - responseTooLargeErrror.length();
            logger.debug("response limit {}", limit);
            List<Path> foundPaths = Files.walk(dirPath, recursive ? Integer.MAX_VALUE : 1)
                    .filter((filterPath) -> {
                        if (filterPath.toString().contains(File.separator + ".git" + File.separator)) {
                            return false;
                        }
                        if (!ignoreGitIgnore) {
                            if (gitSyncService.isGitIgnored(dirPath, filterPath, ignoreNode)) {
                                return false;
                            }
                        }
                        if (foldersOnly) {
                            return Files.isDirectory(filterPath);
                        } else {
                            return Files.isRegularFile(filterPath) || Files.isDirectory(filterPath);
                        }
                    }).collect(Collectors.toList());

            Path workingDir = Paths.get(projectConfig.getWorkingDir());
            for (Path foundPath : foundPaths) {
                String filePath;
                if (isPathInsideWorkingDir(foundPath, workingDir)) {
                    Path relativePath = workingDir.toAbsolutePath().relativize(foundPath.toAbsolutePath());
                    filePath = "./" + relativePath.toString();
                } else {
                    filePath = path;
                }
                fileList.append(filePath).append("\n");
                if (fileList.length() + filePath.length() > limit) {
                    fileList.append(responseTooLargeErrror);
                    logger.warn("reached configured limit for listfiles, truncating");
                    break;
                }
            }

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
            if (!isPathInsideWorkingDir(filePath, projectConfig.getWorkingDir())) {
                throw new FileOperationException("Operation not allowed outside of working directory: " + path);
            }
        } catch (InvalidPathException e) {
            throw new FileOperationException("Invalid path: " + path, e);
        }
        return filePath;
    }

    private boolean isPathInsideWorkingDir(Path path, String workingDir) {
        return isPathInsideWorkingDir(path, Paths.get(workingDir));
    }

    private boolean isPathInsideWorkingDir(Path path, Path workingDir) {
        return path.startsWith(workingDir.normalize());
    }
}
