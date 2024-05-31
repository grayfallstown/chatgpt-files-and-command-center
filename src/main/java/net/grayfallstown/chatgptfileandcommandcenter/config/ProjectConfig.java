package net.grayfallstown.chatgptfileandcommandcenter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Data
@Configuration
@ConfigurationProperties(prefix = "project")
public class ProjectConfig {
    private String apiKey;
    private String workingDir;
    private String systemDescription;
    private String dir;

    public void setWorkingDir(String workingDir) {
        validateDirectoryExists(workingDir, "Working directory");
        this.workingDir = workingDir;
    }

    public void setDir(String dir) {
        validateDirectoryExists(dir, "Project directory");
        this.dir = dir;
    }

    private void validateDirectoryExists(String dirPath, String dirName) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException(dirName + " does not exist or is not a directory: " + dirPath);
        }
    }

    @PostConstruct
    public void validateConfiguration() throws IOException {
        validateDirectoryExists(this.dir, "Project directory");
        validateDirectoryExists(this.workingDir, "Working directory");

        // Read API key from apikey.txt
        File apiKeyFile = new File(dir, "apikey.txt");
        if (!apiKeyFile.exists()) {
            generateAndSaveApiKey(apiKeyFile);
        }
        this.apiKey = new String(Files.readAllBytes(apiKeyFile.toPath())).trim();
    }

    private void generateAndSaveApiKey(File apiKeyFile) throws IOException {
        String apiKey = UUID.randomUUID().toString();
        Files.write(apiKeyFile.toPath(), apiKey.getBytes());
    }

    public void regenerateApiKey() throws IOException {
        File apiKeyFile = new File(dir, "apikey.txt");
        generateAndSaveApiKey(apiKeyFile);
        this.apiKey = new String(Files.readAllBytes(apiKeyFile.toPath())).trim();
    }
}
