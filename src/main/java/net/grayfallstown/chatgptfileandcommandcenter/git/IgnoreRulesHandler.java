package net.grayfallstown.chatgptfileandcommandcenter.git;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class IgnoreRulesHandler {
    private static final Logger logger = LoggerFactory.getLogger(IgnoreRulesHandler.class);

    public List<String> loadGitIgnore(String workingDirPath) throws IOException {
        Path gitignorePath = Path.of(workingDirPath, ".gitignore");
        List<String> patterns = new ArrayList<>();
        File gitignoreFile = gitignorePath.toFile();

        if (gitignoreFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(gitignoreFile), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    patterns.add(line.trim());
                }
            }
            logger.info(".gitignore rules loaded from {}", gitignoreFile.getPath());
        } else {
            logger.info("No .gitignore file found in {}", workingDirPath);
        }

        return patterns;
    }
}
