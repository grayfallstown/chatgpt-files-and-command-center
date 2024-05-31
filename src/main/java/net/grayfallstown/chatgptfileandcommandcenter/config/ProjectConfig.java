package net.grayfallstown.chatgptfileandcommandcenter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

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
    public void validateConfiguration() {
        validateDirectoryExists(this.dir, "Project directory");
        validateDirectoryExists(this.workingDir, "Working directory");
    }
}
