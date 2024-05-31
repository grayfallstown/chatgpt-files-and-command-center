package net.grayfallstown.chatgptfileandcommandcenter.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class FileTracker {
    private static final Logger logger = LoggerFactory.getLogger(FileTracker.class);

    public void addFilesRecursively(Git git, File directory, List<String> ignorePatterns) throws GitAPIException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String relativePath = getRelativePath(git.getRepository().getWorkTree(), file);
                if (shouldTrackFile(relativePath, ignorePatterns)) {
                    if (file.isDirectory()) {
                        addFilesRecursively(git, file, ignorePatterns);
                    } else {
                        git.add().addFilepattern(relativePath).call();
                        logger.info("Added file to tracking: {}", relativePath);
                    }
                }
            }
        }
    }

    public void untrackFiles(Git git, List<String> untrackList) throws GitAPIException {
        for (String filePath : untrackList) {
            git.rm().addFilepattern(filePath).call();
            logger.info("Removed file from tracking: {}", filePath);
        }
    }

    private boolean shouldTrackFile(String filePath, List<String> ignorePatterns) {
        for (String pattern : ignorePatterns) {
            if (matchesPattern(filePath, pattern)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesPattern(String filePath, String pattern) {
        // Simplified pattern matching for demonstration purposes
        String regex = pattern.replace("*", ".*");
        return filePath.matches(regex);
    }

    private String getRelativePath(File base, File file) {
        return base.toURI().relativize(file.toURI()).getPath();
    }
}
